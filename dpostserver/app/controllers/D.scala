package controllers

import play.api._

import play.api.mvc._
import views._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import java.util.Date
import com.outtribe.dpost.storage.Storage
import com.outtribe.dpost.DPost
import play.api.libs.iteratee.Enumerator
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

/**
 * 
 * E-mail endpoint
 * 
 * Dpost clients send JSON encoded e-mails through this interface
 * 
 * 
 */
object D extends Controller {
  val SUCCESS: String = "SUCCESS"
  val FAILURE: String = "FAILURE"
  
  def dpost = Action(parse.json) {
    
    request =>{
    
    Dashboard.SpeedOMeter.countRequest
    
    val storage:Storage  = DPost.storage     
    var result  = SUCCESS
    var accepted = false
    try{
    
	    val json: JsValue = request.body
	    
	
	    val customReads: Reads[(Map[String, String], Map[String, String], String, String, String)] =
	        (JsPath \ "subjMap").read[Map[String, String]] and
	        (JsPath \ "bodyMap").read[Map[String, String]] and
	        (JsPath \ "toMail").read[String] and
	        (JsPath \ "template").read[String] and
	        (JsPath \ "lang").read[String] tupled
	
	      customReads.reads(json).fold(
	      invalid = { errors => {
	               Logger.debug(errors.asInstanceOf[String]) 
	               result = FAILURE
	           }
	      },
	      valid = { res =>
	        {
	          val (subjMap, bodyMap, toMail, template, lang): (Map[String, String], Map[String, String], String, String, String) = res
	          Logger.debug("request came: send mail to" + toMail + " " + template + " " + lang)
	
	          val perTest = scala.util.Properties.envOrElse("dpost.performance", null)
	
	          if (perTest != null) {
	            val heapSize = Runtime.getRuntime.totalMemory
	            Logger.warn(new Date().toString() + " " + heapSize);
	          }
	
	          
	          
	
	          
	          if (toMail == null || template == null || lang == null) {
	            Logger.error("bad format mail data on server: " + json)
	          } else {
	            accepted = storage.storeMessage(template, bodyMap, subjMap, lang, toMail)
	          }
	
	          if (accepted) {
	            result = SUCCESS
	          } else {
	            result = FAILURE
	          }
	        }
	
	      })
    
    }catch{
      case e:Throwable =>{
         Logger.error("Failed process dpost request", e)
         if(accepted==false)
            result = FAILURE
         else
            result = SUCCESS
      }
    }
    
    SimpleResult(
         header = ResponseHeader(200, Map(CONTENT_TYPE -> "text/html;charset=utf-8")), 
         body = Enumerator(result)
      )
    }
    
  }
  
  
  
}