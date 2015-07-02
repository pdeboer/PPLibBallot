package controllers

import models._
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
  def showQuestion(questionId: String) = Action { request =>
    request.session.get("TurkerID").map { user =>
      //TODO: get the answers of the turker in the batch group
      val question = QuestionDAO.findById(questionId.toLong)
      if(question.isDefined){
        val batch = BatchDAO.findById(question.get.batchId)
        if(batch.get.allowedAnswersPerTurker > AnswerDAO.findByUserId(UserDAO.findByTurkerId(user).get.id.get).size){
          Ok(views.html.question(user, question.get.html,
            "<input type=\"button\" value=\"Yes\" style=\"width:10%;\">" +
              "<input type=\"button\" value=\"No\" style=\"width:10%;\">"))
        } else {
          Ok("You already answered enough question from this batch. Try another hit.")
        }
      } else {
        Ok("Cannot find question with id: " + questionId)
      }

    }.getOrElse {
      Ok(views.html.login()).withSession("redirect"-> ("/showQuestion/"+questionId))
    }
  }

  /**
   * Store an answer in the database.
   * This method extract the answer of a question and stores it in the database. After storing the answer the user will
   * be redirected to a conclusion page where a code is displayed in order to get the reward.
   * @return
   */
  def storeAnswer = Action { request =>
    Ok("Storing answer...")
  }

}