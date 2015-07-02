package controllers

import play.api.mvc._

object Login extends Controller {

  def logout = Action { request =>
    Ok(views.html.login()).withNewSession
  }

  def login = Action { request =>
    val turkerId = request.body.asFormUrlEncoded.get("TurkerID").head

    // Redirect if necessary otherwise just go to index
    request.session.get("redirect").map { redirect =>
      Redirect(redirect).withSession(request.session - "redirect" + ("TurkerID" -> turkerId))
    }.getOrElse {
      Ok(views.html.index(turkerId)).withSession(request.session + ("TurkerID" -> turkerId))
    }
  }


}