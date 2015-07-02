package models

import anorm._
import anorm.SqlParser._
import org.joda.time.DateTime
import play.api.db.DB
import play.api.Play.current

/**
 * Created by mattia on 02.07.15.
 */
case class Question(id: Pk[Long], html: String, outputCode: Long, batchId: Long, createTime: DateTime)

object QuestionDAO {
  private val questionParser: RowParser[Question] =
    get[Pk[Long]]("id") ~
      get[String]("html") ~
      get[Long]("output_code") ~
      get[Long]("batch_id") ~
      get[DateTime]("create_time") map {
      case id ~ html ~output_code ~batch_id ~create_time =>
        Question(id, html, output_code, batch_id, create_time)
    }

  def findById(id: Long): Option[Question] =
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM question WHERE id = {id}").on(
        'id -> id
      ).as(questionParser.singleOpt)
    }
}