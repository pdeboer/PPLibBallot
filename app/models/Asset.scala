package models

import java.sql.SQLException

import anorm._
import anorm.SqlParser._
import com.mysql.jdbc.Blob
import org.joda.time.DateTime
import play.api.db.DB
import play.api.Play.current

/**
 * Created by mattia on 02.07.15.
 */
case class Asset(id: Pk[Long], byteArray: Array[Byte], contentType: String, questionId: Long, filename: String) extends Serializable

object AssetDAO {
  private val assetParser: RowParser[Asset] =
    get[Pk[Long]]("id") ~
      bytes("byte_array") ~
      get[String]("content_type") ~
      get[Long]("question_id") ~
      get[String]("filename") map {
      case id ~ byte_array ~ content_type ~ question_id ~ filename =>
        Asset(id, byte_array, content_type, question_id, filename)
    }

  /**
   * Attempt to convert a SQL value into a byte array.
   */
  private def valueToByteArrayOption(value: Any): Option[Array[Byte]] = {
    value match {
      case bytes: Array[Byte] => Some(bytes)
      case blob: Blob => try {
        Some(blob.getBytes(1, blob.length.asInstanceOf[Int]))
      }
      catch {
        case e: SQLException => None
      }
      case _ => None
    }
  }

  /**
   * Implicitly convert an Anorm row to a byte array.
   */
  def rowToByteArray: Column[Array[Byte]] = {
    Column.nonNull[Array[Byte]] { (value, meta) =>
      val MetaDataItem(qualified, nullable, clazz) = meta
      valueToByteArrayOption(value) match {
        case Some(bytes) => Right(bytes)
        case _ => Left(TypeDoesNotMatch("Cannot convert " + value + ":" + value.asInstanceOf[AnyRef].getClass + " to Byte Array for column " + qualified))
      }
    }
  }

  /**
   * Build a RowParser factory for a byte array column.
   */
  def bytes(columnName: String): RowParser[Array[Byte]] = {
    get[Array[Byte]](columnName)(rowToByteArray)
  }

  def findById(id: Long): Option[Asset] =
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM assets WHERE id = {id}").on(
        'id -> id
      ).as(assetParser.singleOpt)
    }

  def findByQuestionId(questionId: Long): List[Asset] =
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM assets WHERE question_id = {questionId}").on(
        'questionId -> questionId
      ).as(assetParser *)
    }

  def getAllIdByQuestionId(questionId: Long) : List[Long] = findByQuestionId(questionId).map(_.id.get)
}