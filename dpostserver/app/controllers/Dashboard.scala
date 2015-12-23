package controllers

import java.util.concurrent.TimeUnit
import scala.concurrent.stm.Ref
import com.outtribe.dpost.DPost
import models.DPostStatus
import play.api.Logger
import play.api.libs.Comet
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.Promise
import play.api.libs.iteratee.Enumerator
import play.api.mvc.Action
import play.api.mvc.Controller
import views.html
import com.outtribe.dpost.javamail.JavaMailSc
import com.outtribe.dpost.help.Helper
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
object Dashboard extends Controller with Secured{
  
  var dpost = DPost
  val totalMemory   = Runtime.getRuntime().totalMemory()/(1024*1024)
  // isConfigCreated:Boolean,
  // isStarted:Boolean,
  // isSending:Int /*0 - yes, 1 - no, 2 - not checked, 3 - warn*/
 
  def isConfigCreated(): Boolean={
     true
  }
  def isStarted(): Boolean={
     dpost.isDpostStarted
  }
  def isStorageProblem(): Boolean={
     dpost.isStorageProbem
  }
  def isSending(): Int={
     if(!isStorageProblem && (hasSendSuccess>0 && hasSendErrors==0)){
        return 0
     }
     if(!isStorageProblem && (hasSendSuccess>0 && hasSendErrors>0)){
        return 3
     }
     if(isStorageProblem || (hasSendSuccess==0 && hasSendErrors>0)){
        return 1
     }
     if(!isStorageProblem && (hasSendSuccess==0 && hasSendErrors==0)){
        return 2
     }
     2
  }
  
  def hasSendErrors(): Long = {
     JavaMailSc.failureCounter
  }
  def hasSendSuccess(): Long = {
     JavaMailSc.successCounter
  }
  
  def dashboard = IsAuthenticated { _ => _ =>
     val conFigCreated = isConfigCreated
     val started       = isStarted
     val sending       = isSending
     
     Ok(html.dashboard.dashboard(DPostStatus(conFigCreated, started, sending, totalMemory, hasSendSuccess,hasSendErrors,isStorageProblem)))
  }
  
  def startStop = Action {
     
    implicit request => {
      request.body.asFormUrlEncoded.get("startStop").headOption match {
          case Some("Start") => {
             Logger.info("pressed start dpost..")
		     dpost.startDpost
		     val conFigCreated = isConfigCreated
		     val started = isStarted
		     val sending = isSending
		     Ok(html.dashboard.dashboard(DPostStatus(conFigCreated, started, sending, totalMemory, hasSendSuccess,hasSendErrors,isStorageProblem)))
          }
          case Some("Stop") => {
             Logger.info("pressed stop dpost..")
		     dpost.stopDpost
		     val conFigCreated = isConfigCreated
		     val started = isStarted
		     val sending = isSending
		     Ok(html.dashboard.dashboard(DPostStatus(conFigCreated, started, sending, totalMemory, hasSendSuccess,hasSendErrors,isStorageProblem)))
          }
          case _ => BadRequest("This action is not allowed");
    
     }
    
    }
  }
  
 object SpeedOMeter {

  val unit = 60000

  private val counter = Ref((0,(0,java.lang.System.currentTimeMillis())))

  def countRequest() = {
    val current = java.lang.System.currentTimeMillis()
    counter.single.transform {
      case (precedent,(count,millis)) if current > millis + unit => (0, (1,current))
      case (precedent,(count,millis)) if current > millis + (unit/2) => (count, (1, current))
      case (precedent,(count,millis))  => (precedent,(count + 1, millis))
    }
  }

  def getSpeed = {
    val current = java.lang.System.currentTimeMillis()
    val (precedent,(count,millis)) = counter.single()
    val since = current-millis
    if(since <= unit) ((count + precedent) * 1000 * 60 * 60) / (since + unit/2)
    else 0
  }

}
  
  
  object Streams {

	  val getRequestsPerSecond = Enumerator.generateM{ 
	      Promise.timeout( 
	      {
	      val currentMillis = java.lang.System.currentTimeMillis()
	      Some(SpeedOMeter.getSpeed +":rps") 
	      },
	      1000, TimeUnit.MILLISECONDS )
	  }
	
	  val getHeap = Enumerator.generateM{ 
	    Promise.timeout(
	      Some((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024*1024) + ":memory"),
	      100, TimeUnit.MILLISECONDS)
	  }
	
	  val cpu = new models.CPU()
	
	  val getCPU = Enumerator.generateM{ 
	    Promise.timeout(
	      Some((cpu.getCpuUsage()*1000).round / 10.0 + ":cpu"),
	      100, TimeUnit.MILLISECONDS)
	  }
	  
	  val getSuccessCount = Enumerator.generateM{
	    Promise.timeout(
	      Some(hasSendSuccess+ ":successCounter"),
	      1000, TimeUnit.MILLISECONDS)
	  }
	  
	  val getFailureCount = Enumerator.generateM{ 
	    Promise.timeout(
	      Some(hasSendErrors+ ":failureCounter"),
	      1000, TimeUnit.MILLISECONDS)
	  }
	  
	  val getStatus = Enumerator.generateM{
	    Promise.timeout(
	      Some(isSending+ ":commonstatus"),
	      1000, TimeUnit.MILLISECONDS)
	  }
	  
	  val getStorageStatus = Enumerator.generateM{ 
	    Promise.timeout(
	      Some(isStorageProblem+ ":storagestatus"),
	      1000, TimeUnit.MILLISECONDS)
	  }
	  
	  def toStringUpdate():String={
	    val ustatus: (Boolean, String, Array[String]) =  Helper.getCurrent
	    var str = ""
	    if  (ustatus._1){
		    Logger.debug("getting new update status...")
		    str = str+
		    "<div id=\"updateStatusContainer\" class=\"updateStatusContainerClass\">\n"+
	        "<p class=\"newerDpostAvailableTitle\">There is a newer and better DPost available (" + ustatus._2 + ") <p>\n"+
	        "<p class=\"newerDpostAvailableSubTitle\">New in this version &#58;<p>\n"
	        for(N <-ustatus._3){
	           str = str+ "<p class=\"newerDpostAvailableNewsLine\">" + N + "<p>\n"
	        } 
	        str = str+"<div>\n"
	    }else{
	         str = str+ 
	         "<div id=\"updateStatusContainer\" class=\"updateStatusContainerClass\">\n"+
	         "<p class=\"newerDpostAvailableSubTitle\">Your version is most current &#58; (" + ustatus._2 + ")<p>\n"+
	         "<div>\n"
	    }
	    
	    str
	  }
	  
	  val getUpdateStatus = Enumerator.generateM{ 
	    Promise.timeout(
	      Some(toStringUpdate()+ ":updatestatus"),
	      5000, TimeUnit.MILLISECONDS)
	  }

}
  
  
  def monitoring = Action {
   Ok.stream(
     Streams.getRequestsPerSecond >-
     Streams.getCPU >-
     Streams.getHeap >-
     Streams.getSuccessCount >-
     Streams.getFailureCount >-
     Streams.getStatus >-
     Streams.getStorageStatus >-
     Streams.getUpdateStatus
     &>
     Comet( callback = "parent.message"))
  }
  
  

}