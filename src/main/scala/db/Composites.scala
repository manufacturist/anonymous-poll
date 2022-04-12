package db

import doobie.Meta
import doobie.enumerated.JdbcType
import doobie.h2.Instances
import doobie.util.meta.*
import entity.*
import monix.newtypes.*

trait Composites extends MetaConstructors with MetaInstances with Instances:

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
    unliftedStringArrayType.timap(_.toList.map(Text(_)))(_.map(_.value).toArray)
