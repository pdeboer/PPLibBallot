package controllers

import models._
import org.joda.time.DateTime
import play.api.mvc._

object Application extends Controller {

  def index = Action { request =>
    request.session.get("TurkerID").map { user =>
      Ok(views.html.index(user))
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
   * @param questionId
   * @return
   */
  def showQuestion(questionId: Long) = Action { request =>
    request.session.get("TurkerID").map { user =>
      // get the answers of the turker in the batch group
      if(isUserAllowedToAnswer(questionId, UserDAO.findByTurkerId(user).get.id.get)){
        Ok(views.html.question(user, QuestionDAO.findById(questionId).get))
      } else {
        Unauthorized("You already answered enough question from this batch. Try another hit.")
      }
    }.getOrElse {
      Ok(views.html.login()).withSession("redirect"-> ("/showQuestion/"+questionId))
    }
  }

  def isUserAllowedToAnswer(questionId: Long, userId: Long): Boolean = {
    val question = QuestionDAO.findById(questionId)
    if(question.isDefined){
      val batch = BatchDAO.findById(question.get.batchId)
      if(batch.get.allowedAnswersPerTurker > AnswerDAO.findByUserId(userId).size){
        true
      } else {
        false
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
      val questionId = request.body.asFormUrlEncoded.get("questionId").mkString.toLong
      if(isUserAllowedToAnswer(questionId, UserDAO.findByTurkerId(user).get.id.get)){

        val answer = request.body.asFormUrlEncoded.get("answer").mkString
        AnswerDAO.create(questionId, UserDAO.findByTurkerId(user).get.id.get, new DateTime, answer)

        Ok(views.html.code(user, QuestionDAO.findById(questionId).get.outputCode)).withSession(request.session)
      } else {
        Unauthorized("You already answered this question. Try another hit.")
      }
    }.getOrElse {
      Ok(views.html.login())
    }
  }

}