package models

import org.joda.time.DateTime
import scalikejdbc._

/**
 * Created by mattia on 02.07.15.
 */
case class Answer(id: Long, questionId: Long, userId: Long, time: DateTime, answerJson: String)

object Answer extends SQLSyntaxSupport[Answer] {
  // override val tableName = "Answer"
  // By default, column names will be cached from meta data automatically when accessing this table for the first time.
  override val columns = Seq("id", "question_id", "user_id", "time", "answer_json")

  def apply(p: ResultName[Answer])(rs: WrappedResultSet): Answer = new Answer(
    id = rs.long(p.id),
    questionId = rs.long(p.questionId),
    userId =  rs.long(p.userId),
    time = rs.jodaDateTime(p.time),
    answerJson = rs.string(p.answerJson)
  )

  private val p = Answer.syntax("p")

  def find(id: Long)(implicit session: DBSession = AutoSession): Option[Answer] = withSQL {
    select.from(Answer as p).where.eq(p.id, id)
  }.map(Answer(p.resultName)).single.apply()

}