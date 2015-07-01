package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action { request =>
    request.session.get("TurkerID").map { user =>
      Ok(views.html.index(user))
    }.getOrElse {
      Ok(views.html.login())
    }
  }

  def logout = Action { request =>
    Ok(views.html.login()).withNewSession
  }

  def login = Action { request =>
    val turkerId = request.body.asFormUrlEncoded.get("TurkerID").head
    Ok(views.html.index(turkerId)).withSession("TurkerID" -> turkerId)
  }

}