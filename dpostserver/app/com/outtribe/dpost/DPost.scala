package com.outtribe.dpost

import com.outtribe.dpost.actors.CheckStorageMessage
import com.outtribe.dpost.actors.MarkSendResultActor
import com.outtribe.dpost.actors.SendMessageActor
import com.outtribe.dpost.actors.StorageCheckActor
import com.outtribe.dpost.actors.TerminateActor
import com.outtribe.dpost.javamail.JavaMailSc
import com.outtribe.dpost.storage.DBStorage
import com.outtribe.dpost.storage.MemoryStorage
import com.outtribe.dpost.storage.Storage
import com.outtribe.dpost.template.TemplateManager
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.dispatch.MessageDispatcher
import controllers.Configuration
import play.api.Logger
import com.outtribe.dpost.actors.UpdatesCheckActor
import com.outtribe.dpost.actors.CheckUpdatesMessage
import scala.concurrent.duration._


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



object DPost{
  private var _system : ActorSystem  = null
  
  var tm: TemplateManager            = null
  var storage: Storage               = null
  var check: ActorRef                = null
  var senderActor: ActorRef          = null
  var markActor: ActorRef            = null
  var trySendActor:ActorRef          = null
  var updatesActor:ActorRef          = null
  var dpostStarted = false
  var fileSystemProblem = false

  def isStorageProbem(): Boolean={
     if(storage!=null)
       storage.isProblem
     else
       false
  }
  
  def isDpostStarted():Boolean={
     dpostStarted
  }
  
  def dispatcher():MessageDispatcher={
      _system.dispatcher
  }
  
  def startDpost() {
    
    
    
    try{
        _system = ActorSystem("DpostActors")
        
        Logger.info("DPOST is starting...loading templates...")
      
	    tm = new TemplateManager
	    
	    Logger.info("configuring storage...")
	    
	    Configuration.common_storageType match {
	      
		  case 1 => storage = new MemoryStorage
		  case 2 => storage = new DBStorage
		  
		}
	    storage.reset
        
        //httpServer  = new DpostServer(storage)
	    markActor = _system.actorOf(Props(new MarkSendResultActor(storage)), name = "MarkSendResultActor")
        
        senderActor = _system.actorOf(Props(new SendMessageActor(markActor)), name = "SendMessageActor")
        
        check = _system.actorOf(Props(new StorageCheckActor(storage, senderActor)), name = "StorageCheckActor")
	    
        // trySendActor = _system.actorOf(Props[TryPost], name = "TryPostActor")
        
        Logger.info("starting actors...")
        
        JavaMailSc.reset
	   
	    check ! CheckStorageMessage
	    
	    updatesActor = _system.actorOf(Props(new UpdatesCheckActor()), name = "UpdatesCheckActor")
	    
	    val duration = new FiniteDuration(10, SECONDS)
        _system.scheduler.scheduleOnce(duration, updatesActor, CheckUpdatesMessage)(DPost.dispatcher)
	    
	    printSuccessfulStart
	    
	    //trySendActor ! TestMessage
	    
	    dpostStarted = true
    
    }catch{
        
        case e: Exception => Logger.error("Dpost start has been interrupted with exception ", e)
        
    }
    
  }
  
  def stopDpost(){
     markActor ! TerminateActor
     senderActor ! TerminateActor
     check ! TerminateActor
     updatesActor ! TerminateActor
     Thread.sleep(500)
     _system.shutdown
     dpostStarted = false
  }
  
  def printSuccessfulStart(){
    
    val startMessage = 
        
",............................................................................................................\n"+
".                                                                                                           .\n"+
".                                                                                                           .\n"+
".                                                                                                           .\n"+
".                                                                                                           .\n"+
".                            B@@@@#s    :@@@@@G,    r@@@@A.    :A@@@@H  @@@@@@@@:                           .\n"+
".                            @@@S&@@@:  ;@@9i#@@;  @@@Sr#@@i  s@@2,:X@  3hA@@#2G.                           .\n"+
".                            @@s   A@@  ,@@  .@@s 5@@    S@@  ;@@@Gr       @@                               .\n"+
".                            @@i   S@@  .@@@@@@;  i@@    ;@@    ,3@@@@,    @@.                              .\n"+
".                            @@@;rM@@:  .@@        @@@;,i@@S  X#.  ;@@;    @@;                              .\n"+
".                            G@@@@#;     @@         r@@@@h    :@@@@@3.     @@,                              .\n"+
".                                                                                                           .\n"+
".                                                                                                           .\n"+
".                         .                                                                                 .\n"+
".                      h@@@@@5 @@@@@@@H   ,@@@    ;@@@@@@. ;@@@@@@@  @@@@@@; .@@@@@@r                       .\n"+
".                     ,@@;       .@@      @@@@3   :@@  @@M    @@5    @@G,:;   @@  :@@5                      .\n"+
".                      r@@@@@.    @@     s@9 @@   .@@A@@&     A@;    @@@M@@   @@   ;@@                      .\n"+
".                      ,   @@@    @@     @@#M@@@  .@@ H@B     B@i    @@:      @@  ,@@r                      .\n"+
".                     ,@@@#@B     @@    H@;   @@r  @@  r@@,   G@i    A@@@@@;  @@@@@M.                       .\n"+
".                                                                                                           .\n"+
".                                                                                                           .\n"+
".............................................................................................................\n"+
"\n"
  println(startMessage)
  Logger.warn(startMessage)
    
  }
}












