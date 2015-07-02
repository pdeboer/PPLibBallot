package models

import org.joda.time.DateTime
import scalikejdbc._

/**
 * Created by mattia on 02.07.15.
 */
case class Question(id: Long, html: String, outputCode: Long, batchId: Long, createTime: DateTime)

object Question extends SQLSyntaxSupport[Question] {
  // override val tableName = "question"
  // By default, column names will be cached from meta data automatically when accessing this table for the first time.
  override val columns = Seq("id", "html", "output_code", "batch_id", "create_time")

  def apply(p: ResultName[Question])(rs: WrappedResultSet): Question = new Question(
    id = rs.long(p.id),
    html = rs.string(p.html),
    outputCode = rs.long(p.outputCode),
    batchId = rs.long(p.batchId),
    createTime = rs.jodaDateTime(p.createTime)
  )

  private val p = Question.syntax("p")

  def find(id: Long)(implicit session: DBSession = AutoSession): Option[Question] = withSQL {
    select.from(Question as p).where.eq(p.id, id)
  }.map(Question(p.resultName)).single.apply()

}
