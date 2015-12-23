package controllers

import play.api._
import play.api.mvc._
import views._
import com.outtribe.dpost.DPost
import play.api.data.Form
import play.api.data.Forms._
import models.User
/**
 * Copyright (C) 2013 Peter Kovgan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Main class, here the business starts
 * FIXME: create graceful interruption
*/
object Application extends Controller with Secured{
  
  def index = IsAuthenticated { _ => _ =>
    if(DPost.fileSystemProblem){
        val directory = Configuration.HOME
        val home = scala.util.Properties.envOrElse("DPOST_HOME", "")
        var ext = ""
        var what = "current"
        if(home.equals("")){
           what = "automatic"
           ext = "Note: DPOST_HOME environment variable is not set, your <USER_DIR>"+Configuration.FILE_SEPARATOR+"dpost used automatically instead"
        }
        Ok("Unable to start. Check that your <DPOST_HOME>"+Configuration.FILE_SEPARATOR+"conf directory exists and write access there allowed. Your "+what+" DPOST_HOME is: \n"+directory + "\n"+ext)
    }else{
        Ok(html.index())
    }
  }
  
  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
      case (email, password) => User.authenticate(email, password).isDefined
    })
  )

  /**
   * Login page.
   */
  def login = Action { implicit request =>
    Ok(html.login.login(loginForm))
  }
  
  /**
   * Handle login form submission.
   */
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.login.login(formWithErrors)),
      user => Redirect(routes.Application.index).withSession("email" -> user._1)
    )
  }
  
  /**
   * Logout and clean the session.
   */
  def logout = Action {
    Redirect(routes.Application.login).withNewSession.flashing(
      "success" -> "You've been logged out"
    )
  }
  
  
  
}

trait Secured {
  
  /**
   * Retrieve the connected user email.
   */
  private def username(request: RequestHeader) = request.session.get("email")

  /**
   * Redirect to login if the user in not authorized.
   */
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.login)
  
  // --
  
  /** 
   * Action for authenticated users.
   */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }

  

}