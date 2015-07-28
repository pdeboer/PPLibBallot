package controllers

import models._
import org.joda.time.DateTime
import play.Configuration
import play.api.Logger
import play.api.mvc._

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
      if(isUserAllowedToAnswer(questionId, UserDAO.findByTurkerId(user).get.id.get)){
        Ok(views.html.question(user, QuestionDAO.findById(questionId).get, AssetDAO.getAllIdByQuestionId(questionId)))
      } else {
        Unauthorized("You already answered enough question from this batch. Try another hit.")
      }
    }.getOrElse {
      Ok(views.html.login()).withSession("redirect"-> (Configuration.root().getString("application.context", "")+"/showQuestion/"+uuid))
    }

  }

  def isUserAllowedToAnswer(questionId: Long, userId: Long): Boolean = {
    val question = QuestionDAO.findById(questionId)
    // The question exists and there is no answer yet for the current user
    if(question.isDefined && AnswerDAO.isUserAllowedToAnswerQuestion(userId, questionId)){
      val batch = BatchDAO.findById(question.get.batchId)
      if(batch.get.allowedAnswersPerTurker == 0) {
        true
      }else {
        if(batch.get.allowedAnswersPerTurker > AnswerDAO.countUserAnswersForBatch(userId, question.get.batchId)){
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
        val questionId = request.body.asFormUrlEncoded.get("questionId").mkString.toLong

        if(isUserAllowedToAnswer(questionId, UserDAO.findByTurkerId(user).get.id.get)){

          val answer: JSONObject = JSONObject.apply(request.body.asFormUrlEncoded.get.map(m => { (m._1, m._2.mkString(",")) } ))

          AnswerDAO.create(questionId, UserDAO.findByTurkerId(user).get.id.get, new DateTime, answer.toString())

          Ok(views.html.code(user, QuestionDAO.findById(questionId).get.outputCode)).withSession(request.session)
        } else {
          Unauthorized("You already answered this question. Try another hit.")
        }
      } catch {
        case e: Exception => Unauthorized("Invalid request format.")
      }
    }.getOrElse {
      Ok(views.html.login())
    }
  }

}