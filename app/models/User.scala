package models

import org.joda.time.DateTime
import scalikejdbc._

/**
 * Created by mattia on 02.07.15.
 */
case class User(id: Long, turkerId: String, firstSeenDateTime: DateTime)

object User extends SQLSyntaxSupport[User] {
  // override val tableName = "User"
  // By default, column names will be cached from meta data automatically when accessing this table for the first time.
  override val columns = Seq("id", "turker_id", "first_seen_date_time")

  def apply(p: ResultName[User])(rs: WrappedResultSet): User = new User(
    id = rs.long(p.id),
    turkerId = rs.string(p.turkerId),
    firstSeenDateTime = rs.jodaDateTime(p.firstSeenDateTime)
  )

  private val p = User.syntax("p")

  def find(id: Long)(implicit session: DBSession = AutoSession): Option[User] = withSQL {
    select.from(User as p).where.eq(p.id, id)
  }.map(User(p.resultName)).single.apply()

}