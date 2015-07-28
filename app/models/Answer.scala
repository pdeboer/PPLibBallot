package models

import anorm._
import anorm.SqlParser._
import org.joda.time.DateTime
import play.api.db.DB
import play.api.Play.current

/**
 * Created by mattia on 02.07.15.
 */
case class Answer(id: Pk[Long], questionId: Long, userId: Long, time: DateTime, answerJson: String) extends Serializable

object AnswerDAO {
  private val answerParser: RowParser[Answer] =
    get[Pk[Long]]("id") ~
      get[Long]("question_id") ~
      get[Long]("user_id") ~
      get[DateTime]("time") ~
      get[String]("answer_json") map {
      case id ~ question_id ~ user_id ~ time ~ answer_json =>
        Answer(id, question_id, user_id, time, answer_json)
    }

  def findById(id: Long): Option[Answer] =
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM answer WHERE id = {id}").on(
        'id -> id
      ).as(answerParser.singleOpt)
    }

  def findByUserId(userId: Long): List[Answer] =
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM answer WHERE user_id = {userId}").on(
        'userId -> userId
      ).as(answerParser *)
    }

  def create(questionId: Long, userId: Long, time: DateTime, answerJson: String): Option[Long] =
    DB.withConnection { implicit c =>
      SQL("INSERT INTO answer(question_id, user_id, time, answer_json) VALUES ({questionId}, {userId}, {time}, {answerJson})").on(
        'questionId -> questionId,
        'userId -> userId,
        'time -> time,
        'answerJson -> answerJson
      ).executeInsert()
    }

  def countUserAnswersForBatch(userId: Long, batchId: Long): Int = {
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM answer as a, question as q WHERE a.user_id = {userId} AND q.batch_id = {batchId} AND a.question_id = q.id").on(
        'userId -> userId,
        'batchId -> batchId
      ).as(answerParser *).size
    }
  }

  def countAnswersForQuestion(questionId: Long): Int = {
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM answer as a WHERE a.question_id = {questionId}").on(
        'questionId -> questionId
      ).as(answerParser *).size
    }
  }

  def existsAnswerForQuestionAndUser(userId: Long, questionId: Long): Boolean = {
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM answer WHERE question_id = {questionId} AND user_id = {userId} ").on(
        'userId -> userId,
        'questionId -> questionId
      ).as(answerParser *).size != 0
    }
  }

}