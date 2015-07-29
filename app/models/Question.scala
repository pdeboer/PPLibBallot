package models

import anorm._
import anorm.SqlParser._
import org.joda.time.DateTime
import play.api.db.DB
import play.api.Play.current

/**
 * Created by mattia on 02.07.15.
 */
case class Question(id: Pk[Long], html: String, batchId: Long, createTime: DateTime, uuid: String)

object QuestionDAO {
  private val questionParser: RowParser[Question] =
    get[Pk[Long]]("id") ~
      get[String]("html") ~
      get[Long]("batch_id") ~
      get[DateTime]("create_time") ~
      get[String]("uuid") map {
      case id ~ html ~batch_id ~create_time ~uuid =>
        Question(id, html, batch_id, create_time, uuid)
    }


  def findById(id: Long): Option[Question] =
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM question WHERE id = {id}").on(
        'id -> id
      ).as(questionParser.singleOpt)
    }

  def findIdByUUID(uuid: String) : Long = {
    try {
      DB.withConnection { implicit c =>
        SQL("SELECT * FROM question WHERE uuid = {uuid}").on(
          'uuid -> uuid
        ).as(questionParser.singleOpt).get.id.get
      }
    } catch {
      case e: Exception => -1
    }
  }

}