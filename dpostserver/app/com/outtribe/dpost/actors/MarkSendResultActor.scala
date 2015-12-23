package com.outtribe.dpost.actors
import akka.actor.Actor
import com.outtribe.dpost.storage.Storage
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
 */
class MarkSendResultActor (val storage: Storage ) extends Actor{
  
   def receive: Receive = {
        case m:MarkSendResultMessage => {
           Logger.debug("address storage to mark success "+m.id + " " + m.sent)
           storage.markSuccess(m.id, m.sent)
        }
        case TerminateActor => {
           Logger.info("MarkSendResultActor stopped")
           context.stop(self)
        }
   }
  
  
}