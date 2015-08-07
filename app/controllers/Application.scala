package controllers

import models._
import org.joda.time.DateTime
import play.Configuration
import play.api.mvc._

import scala.util.Random
import scala.util.parsing.json.JSONObject

object Application extends Controller {

  def index = Action { request =>
    request.session.get("TurkerID").map { user =>
      Ok(views.html.index(user))
    }.getOrElse {
      Ok(views.html.login())
    }
  }

  def showAsset(id: Long) = Action { request =>
    request.session.get("TurkerID").map { user =>
      val asset = AssetDAO.findById(id)
      if (asset.isDefined) {
        Ok(asset.get.byteArray).as(asset.get.contentType)
      } else {
        UnprocessableEntity("There exists no asset with id: " + id)
      }
    }.getOrElse {
      Ok(views.html.login())
    }
  }

  /**
   * Show a question.
   * The method below counts the number of answers already sent by the turker and loads the question
   * from the database.
   *
   * If this number is equal to the maximal number of allowed answer per turker per batch, then the turker will be
   * redirected to a warning page.
   *
   * @param uuid
   * @return
   */
  def showQuestion(uuid: String) = Action { request =>

    val questionId = QuestionDAO.findIdByUUID(uuid)

    request.session.get("TurkerID").map { user =>
      // get the answers of the turker in the batch group
      val userFound = UserDAO.findByTurkerId(user)

      if(userFound.isDefined && isUserAllowedToAnswer(questionId, userFound.get.id.get)){
        Ok(views.html.question(user, QuestionDAO.findById(questionId).get, AssetDAO.getAllIdByQuestionId(questionId), AssetDAO.findByQuestionId(questionId).head.filename))
      } else if(userFound.isDefined) {
        Unauthorized("This question has already been answered")
      } else {
        Ok(views.html.login()).withSession("redirect" -> (Configuration.root().getString("assetPrefix") + "/showQuestion/" + uuid))
      }
    }.getOrElse {
      Ok(views.html.login()).withSession("redirect" -> (Configuration.root().getString("assetPrefix") + "/showQuestion/" + uuid))
    }

  }

  def isUserAllowedToAnswer(questionId: Long, userId: Long): Boolean = {
    val question = QuestionDAO.findById(questionId)
    // The question exists and there is no answer yet accepted in the DB
    if(question.isDefined && !AnswerDAO.existsAcceptedAnswerForQuestionId(questionId)){
      val batch = BatchDAO.findById(question.get.batchId)
      if(batch.get.allowedAnswersPerTurker == 0) {
        true
      }else {
        if(batch.get.allowedAnswersPerTurker > AnswerDAO.countUserAcceptedAnswersForBatch(userId, question.get.batchId)){
          true
        } else {
          false
        }
      }
    }else {
      false
    }
  }

  /**
   * Store an answer in the database.
   * This method extract the answer of a question and stores it in the database. After storing the answer the user will
   * be redirected to a conclusion page where a code is displayed in order to get the reward.
   * @return
   */
  def storeAnswer = Action { request =>
    request.session.get("TurkerID").map { user =>
      try {

        val questionId = request.getQueryString("questionId").mkString.toLong

        val outputCode = Math.abs(new Random(new DateTime().getMillis).nextLong())

        if(isUserAllowedToAnswer(questionId, UserDAO.findByTurkerId(user).get.id.get)){

          val answer: JSONObject = JSONObject.apply(request.queryString.map(m => { (m._1, m._2.mkString(",")) } ))

          AnswerDAO.create(questionId, UserDAO.findByTurkerId(user).get.id.get, new DateTime, answer.toString(), outputCode)

          Ok(views.html.code(user, outputCode)).withSession(request.session)
        } else {
          Unauthorized("This question has already been answered.")
        }
      } catch {
        case e: Exception => {
          e.printStackTrace()
          Unauthorized("Invalid request format.")
        }
      }
    }.getOrElse {
      Ok(views.html.login())
    }
  }

}