package com.outtribe.dpost.actors
import akka.actor.Actor
import com.outtribe.dpost.storage.MessageData
import com.outtribe.dpost.storage.Storage
import controllers.Configuration
import akka.actor.ActorRef
import play.api._
import scala.concurrent.duration._
import scala.concurrent.duration.TimeUnit
import com.outtribe.dpost.DPost


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
 * checks messages in a storage and invokes itself till not interrupted by the PauseCheckMessage(true)
 * interruption may be reversed by the PauseCheckMessage(false)
 */
class StorageCheckActor(val storage: Storage, val senderActor: ActorRef) extends Actor {
  
     @volatile var pauseCheck: Boolean = false
     @volatile var stopped: Boolean = false
     def receive: Receive = {
        case CheckStorageMessage => {
          Logger.debug("received command check storage")
          
          val timeStart = System.currentTimeMillis
          try {

            if (!pauseCheck) {
              val messages = storage.getMessagesToSend(Configuration.common_messages_to_send_request_limit)
              //pass messages to the sender actor
              if (messages.size > 0) {
                messages.foreach(m => senderActor ! EmailMessage(m))
              }
            }

          } catch {
            case e: Exception => Logger.error("failed get messages to send", e)
          } finally {
            
            var pause:Long = Configuration.common_check_time_msc - (System.currentTimeMillis - timeStart) //FIXME: configuration for pause time
            if (pause < 0) pause = Configuration.common_check_time_msc
            
            if(!stopped){
               val duration = new FiniteDuration(pause, MILLISECONDS)
               context.system.scheduler.scheduleOnce(duration, self, CheckStorageMessage)(DPost.dispatcher)
            }
          }
        }
        case PauseCheckMessage(true) => {
          Logger.debug("received command pause checking")
          pauseCheck = true;
        }
        case PauseCheckMessage(false) => {
          Logger.debug("received command continue checking")
          pauseCheck = false;
        }
        case TerminateActor => {
           stopped = true
           Logger.info("StorageCheckActor stopped")
           context.stop(self)
        }
     }
}