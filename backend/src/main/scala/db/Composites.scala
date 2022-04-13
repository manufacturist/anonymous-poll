package db

import doobie.Meta
import doobie.enumerated.JdbcType
import doobie.h2
import doobie.util.invariant.{NullableCellRead, NullableCellUpdate}
import doobie.util.meta.{Meta, *}
import entity.*
import monix.newtypes.*

import scala.reflect.ClassTag

trait Composites extends MetaConstructors with MetaInstances with h2.Instances:

  // TODO: Ask #doobie (typelevel) on discord. Copied from h2.Instances, but not sure why it doesn't work with
  // TODO: ResultSet.getObject (h2.Instances), since it's also mentioned
  // TODO: here http://www.h2database.com/html/datatypes.html#array_type:
  //
  // Use PreparedStatement.setArray(..) or PreparedStatement.setObject(.., new Object[] {..}) to store values, and
  // ResultSet.getObject(..) or ResultSet.getArray(..) to retrieve the values.
  //
  // TODO: h2.Instances of boxed / unboxed pairs implemented via Meta.advanced.other[Array[Object]] => Uses ResultSet.getObject
  private def arrayMeta[A >: Null <: AnyRef: ClassTag]: Meta[Array[A]] =
    val raw = Meta.Advanced
      .array[Object]("I'm not sure why this is not used in the JdbcConnection.createArrayOf? :confused:", "ARRAY")
      .timap[Array[A]](a => if a == null then null else a.map(_.asInstanceOf[A]))(a =>
        if a == null then null else a.map(_.asInstanceOf[Object])
      )

    def checkNull[B >: Null](a: Array[B], e: Exception): Array[B] =
      if a == null then null else if a.contains(null) then throw e else a

    raw.timap(checkNull(_, NullableCellRead))(checkNull(_, NullableCellUpdate))

  given StringArrayMeta: Meta[Array[String]] = arrayMeta[String]

  given [Base, New](using
    newBuilder: HasBuilder.Aux[New, Base],
    newExtractor: HasExtractor.Aux[New, Base],
    baseMeta: Meta[Base]
  ): Meta[New] =
    baseMeta.imap(base => newBuilder.build(base).getOrElse(throw new IllegalArgumentException("Dead throw")))(
      newExtractor.extract
    )

  given Meta[java.time.OffsetDateTime] =
    Basic.one[java.time.OffsetDateTime](
      jdbcType = JdbcType.TimestampWithTimezone,
      jdbcSourceSecondary = List(JdbcType.Char, JdbcType.VarChar, JdbcType.LongVarChar, JdbcType.Date, JdbcType.Time),
      get = _.getObject(_, classOf[java.time.OffsetDateTime]),
      put = _.setObject(_, _),
      update = _.updateObject(_, _)
    )

  given Meta[QuestionType] =
    StringMeta.imap(QuestionType.valueOf)(_.toString)

  given Meta[List[Text]] =
    StringArrayMeta.timap(_.toList.map(Text(_)))(_.map(_.value).toArray)
