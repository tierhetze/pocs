package com.outtribe.dpost.actors
import akka.actor.Actor
import com.outtribe.dpost.javamail.JavaMailSc
import akka.actor.ActorRef
import play.api._


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
 * Actor, responsible for sending messages from the storage
 * It is working in react fashion, which enables more than 1 thread utilization
 */
class SendMessageActor(val markActor: ActorRef) extends Actor{
  
  def receive: Receive = {
        case m:EmailMessage => {
           Logger.debug("going to match template"+m.message.template+"."+m.message.lang+" ,thread"+Thread.currentThread().hashCode())
           val sent = JavaMailSc.matchTemplateSend(m.message)
           markActor ! new MarkSendResultMessage(sent, m.message.id)
        }
        case TerminateActor => {
           Logger.info("SendMessageActor stopped")
           context.stop(self)
        }
   }
}