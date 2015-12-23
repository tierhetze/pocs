package com.outtribe.dpost.storage

import java.sql.{Connection, DriverManager, ResultSet}
import java.sql.SQLException
import scala.collection.immutable.List

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
 * message storage interface
 */
abstract class Storage{
    //bring the message into the store
    def storeMessage(template: String, body:Map[String, String], subj:Map[String, String], lang:String, toAddr: String):Boolean={false}
    
    //get messages to send
    def getMessagesToSend(limit:Int): List[MessageData]={null}
    
    def markSuccess(id: Long, sent: Boolean):Unit ={}
    
    def reset(){}
    
    def isProblem:Boolean
}




  

