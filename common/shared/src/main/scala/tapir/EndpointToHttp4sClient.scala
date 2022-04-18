package tapir

import cats.Applicative
import cats.effect.Async
import cats.implicits.*
import fs2.Chunk
import org.http4s.*
import org.http4s.headers.`Content-Type`
import org.typelevel.ci.CIString
import sttp.capabilities.Streams
import sttp.model.ResponseMetadata
import sttp.tapir.Codec.PlainCodec
import sttp.tapir.client.ClientOutputParams
import sttp.tapir.internal.{Params, ParamsAsAny, RichEndpointOutput, SplitParams}
import sttp.tapir.{
  Codec,
  CodecFormat,
  DecodeResult,
  Endpoint,
  EndpointIO,
  EndpointInput,
  EndpointOutput,
  FileRange,
  Mapping,
  RawBodyType,
  StreamBodyIO,
  WebSocketBodyOutput
}

import java.io.{ByteArrayInputStream, InputStream}
import java.nio.ByteBuffer

final class EndpointToHttp4sClient() {

  def toHttp4sRequest[A, I, E, O, R, F[_]: Async](
    e: Endpoint[A, I, E, O, R],
    maybeUri: Option[Uri]
  ): A => I => (Request[F], Response[F] => F[DecodeResult[Either[E, O]]]) = { aParams => iParams =>
    val baseUri     = maybeUri.getOrElse(Uri.unsafeFromString("/"))
    val baseRequest = Request[F](uri = baseUri)
    val request0    = setInputParams[A, F](e.securityInput, ParamsAsAny(aParams), baseRequest)
    val request1    = setInputParams[I, F](e.input, ParamsAsAny(iParams), request0)

    def responseParser(response: Response[F]): F[DecodeResult[Either[E, O]]] = {
      parseHttp4sResponse(e).apply(response)
    }

    (request1, responseParser)
  }

  @scala.annotation.tailrec
  private def setInputParams[I, F[_]: Async](
    input: EndpointInput[I],
    params: Params,
    req: Request[F]
  ): Request[F] = {
    def value: I = params.asAny.asInstanceOf[I]

    input match {
      case EndpointInput.FixedMethod(m, _, _) =>
        req.withMethod(Method.fromString(m.method).getOrElse(throw new IllegalArgumentException("Fail")))
      case EndpointInput.FixedPath(p, _, _) => req.withUri(req.uri.addSegment(p))
      case EndpointInput.PathCapture(_, codec, _) =>
        val path = codec.asInstanceOf[PlainCodec[Any]].encode(value: Any)
        req.withUri(req.uri.addSegment(path))
      case EndpointInput.PathsCapture(codec, _) =>
        val pathFragments = codec.encode(value)
        val uri           = pathFragments.foldLeft(req.uri)(_.addSegment(_))
        req.withUri(uri)
      case EndpointInput.Query(name, codec, _) =>
        codec.encode(value) match {
          case values if values.nonEmpty => req.withUri(req.uri.withQueryParam(name, values))
          case _                         => req
        }
      case EndpointInput.Cookie(name, codec, _) =>
        codec.encode(value).foldLeft(req)(_.addCookie(name, _))
      case EndpointInput.QueryParams(codec, _) =>
        val uri = codec.encode(value).toMultiSeq.foldLeft(req.uri) { case (currentUri, (key, values)) =>
          currentUri.withQueryParam(key, values)
        }
        req.withUri(uri)
      case EndpointIO.Empty(_, _)              => req
      case EndpointIO.Body(bodyType, codec, _) => setBody(value, bodyType, codec, req)
      case EndpointIO.OneOfBody(variants, _)   => setInputParams(variants.head.body, params, req)
      case EndpointIO.StreamBodyWrapper(StreamBodyIO(streams, _, _, _, _)) =>
        throw new IllegalArgumentException("Streaming not supported in inlined library")
      case EndpointIO.Header(name, codec, _) =>
        val headers = codec.encode(value).map(value => Header.Raw(CIString(name), value): Header.ToRaw)
        req.putHeaders(headers*)
      case EndpointIO.Headers(codec, _) =>
        val headers = codec.encode(value).map(h => Header.Raw(CIString(h.name), h.value): Header.ToRaw)
        req.putHeaders(headers*)
      case EndpointIO.FixedHeader(h, _, _)           => req.putHeaders(Header.Raw(CIString(h.name), h.value))
      case EndpointInput.ExtractFromRequest(_, _)    => req // ignoring
      case a: EndpointInput.Auth[?, ?]               => setInputParams(a.input, params, req)
      case EndpointInput.Pair(left, right, _, split) => handleInputPair(left, right, params, split, req)
      case EndpointIO.Pair(left, right, _, split)    => handleInputPair(left, right, params, split, req)
      case EndpointInput.MappedPair(wrapped, codec) =>
        handleMapped(
          wrapped.asInstanceOf[EndpointInput.Pair[Any, Any, Any]],
          codec.asInstanceOf[Mapping[Any, Any]],
          params,
          req
        )
      case EndpointIO.MappedPair(wrapped, codec) =>
        handleMapped(
          wrapped.asInstanceOf[EndpointIO.Pair[Any, Any, Any]],
          codec.asInstanceOf[Mapping[Any, Any]],
          params,
          req
        )
    }
  }

  private def setBody[R, T, CF <: CodecFormat, F[_]: Async](
    value: T,
    bodyType: RawBodyType[R],
    codec: Codec[R, T, CF],
    req: Request[F]
  ): Request[F] = {
    val encoded: R = codec.encode(value)

    val newReq = bodyType match {
      case RawBodyType.StringBody(charset) =>
        val entityEncoder = EntityEncoder.stringEncoder[F](Charset.fromNioCharset(charset))
        req.withEntity(encoded.asInstanceOf[String])(entityEncoder)
      case RawBodyType.ByteArrayBody =>
        req.withEntity(encoded.asInstanceOf[Array[Byte]])
      case RawBodyType.ByteBufferBody =>
        val entityEncoder = EntityEncoder.chunkEncoder[F].contramap(Chunk.byteBuffer)
        req.withEntity(encoded.asInstanceOf[ByteBuffer])(entityEncoder)
      case RawBodyType.InputStreamBody =>
        val entityEncoder = EntityEncoder.inputStreamEncoder[F, InputStream]
        req.withEntity(Applicative[F].pure(encoded.asInstanceOf[InputStream]))(entityEncoder)
      case RawBodyType.FileBody =>
        throw new IllegalArgumentException("FileBody not supported in inlined library")
      case _: RawBodyType.MultipartBody =>
        throw new IllegalArgumentException("Multipart body isn't supported yet")
    }
    val contentType =
      `Content-Type`.parse(codec.format.mediaType.toString()).getOrElse(throw new IllegalArgumentException("Fail"))

    newReq.withContentType(contentType)
  }

  private def handleInputPair[I, F[_]: Async](
    left: EndpointInput[?],
    right: EndpointInput[?],
    params: Params,
    split: SplitParams,
    currentReq: Request[F]
  ): Request[F] = {
    val (leftParams, rightParams) = split(params)

    val req2 = setInputParams(left.asInstanceOf[EndpointInput[Any]], leftParams, currentReq)
    setInputParams(right.asInstanceOf[EndpointInput[Any]], rightParams, req2)
  }

  private def handleMapped[II, T, F[_]: Async](
    tuple: EndpointInput[II],
    codec: Mapping[T, II],
    params: Params,
    req: Request[F]
  ): Request[F] =
    setInputParams(
      tuple.asInstanceOf[EndpointInput[Any]],
      ParamsAsAny(codec.encode(params.asAny.asInstanceOf[II])),
      req
    )

  private def parseHttp4sResponse[A, I, E, O, R, F[_]: Async](
    e: Endpoint[A, I, E, O, R]
  ): Response[F] => F[DecodeResult[Either[E, O]]] = { response =>
    val code = sttp.model.StatusCode(response.status.code)

    val parser = if code.isSuccess then responseFromOutput[F](e.output) else responseFromOutput[F](e.errorOutput)
    val output = if code.isSuccess then e.output else e.errorOutput

    // headers with cookies
    val headers = response.headers.headers.map(h => sttp.model.Header(h.name.toString, h.value)).toVector

    parser(response).map { responseBody =>
      val params = clientOutputParams(output, responseBody, ResponseMetadata(code, response.status.reason, headers))
      params.map(_.asAny).map(p => if code.isSuccess then Right(p.asInstanceOf[O]) else Left(p.asInstanceOf[E]))
    }
  }

  private def responseFromOutput[F[_]: Async](out: EndpointOutput[?]): Response[F] => F[Any] = { response =>
    bodyIsStream(out) match {
      case Some(streams) =>
        throw new IllegalArgumentException("Only Fs2Streams streaming is supported")
      case None =>
        out.bodyType
          .map[F[Any]] {
            case RawBodyType.StringBody(charset) =>
              response.body.compile.toVector.map(bytes => new String(bytes.toArray, charset).asInstanceOf[Any])
            case RawBodyType.ByteArrayBody =>
              response.body.compile.toVector.map(_.toArray).map(_.asInstanceOf[Any])
            case RawBodyType.ByteBufferBody =>
              response.body.compile.toVector.map(_.toArray).map(java.nio.ByteBuffer.wrap).map(_.asInstanceOf[Any])
            case RawBodyType.InputStreamBody =>
              response.body.compile.toVector.map(_.toArray).map(new ByteArrayInputStream(_)).map(_.asInstanceOf[Any])
            case RawBodyType.FileBody =>
              throw new IllegalArgumentException("FileBody not supported in inlined library")
            case RawBodyType.MultipartBody(_, _) =>
              throw new IllegalArgumentException("Multipart bodies aren't supported in responses")
          }
          .getOrElse[F[Any]](((): Any).pure[F])
    }
  }

  private def bodyIsStream[I](out: EndpointOutput[I]): Option[Streams[?]] = {
    out.traverseOutputs { case EndpointIO.StreamBodyWrapper(StreamBodyIO(streams, _, _, _, _)) =>
      Vector(streams)
    }.headOption
  }

  private val clientOutputParams = new ClientOutputParams {
    override def decodeWebSocketBody(o: WebSocketBodyOutput[?, ?, ?, ?, ?], body: Any): DecodeResult[Any] =
      DecodeResult.Error("", new IllegalArgumentException("WebSocket aren't supported yet"))
  }
}
