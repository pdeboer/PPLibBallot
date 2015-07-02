package models

import scalikejdbc._

/**
 * Created by mattia on 02.07.15.
 */
case class Batch(id: Long, allowedAnswersPerTurker: Int)

object Batch extends SQLSyntaxSupport[Batch] {
  // override val tableName = "batch"
  // By default, column names will be cached from meta data automatically when accessing this table for the first time.
  override val columns = Seq("id", "allowed_answers_per_turker")

  def apply(p: ResultName[Batch])(rs: WrappedResultSet): Batch = new Batch(
    id = rs.long(p.id),
    allowedAnswersPerTurker = rs.int(p.allowedAnswersPerTurker)
  )

  private val p = Batch.syntax("p")

  def find(id: Long)(implicit session: DBSession = AutoSession): Option[Batch] = withSQL {
    select.from(Batch as p).where.eq(p.id, id)
  }.map(Batch(p.resultName)).single.apply()

}
