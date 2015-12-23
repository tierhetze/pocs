package com.outtribe.dpost.storage
import scala.collection.immutable.Queue
import controllers.Configuration
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
 * Not persisted storage for messages
 */
class MemoryStorage extends Storage{
    
    var maxSize      :Int = 100 
    var storageQueue : Queue[MessageData] = null
    private var problem : Boolean = false
      
    override def reset{
       problem       = false
       maxSize       =  Configuration.common_storage_size 
       storageQueue  =  Queue[MessageData]()
    }
    
    
      
    override def isProblem:Boolean={
       problem
    }
    /**
     * FIXME: take care of overloading event
     */
    override def storeMessage(template: String, body:Map[String, String], subj:Map[String, String], lang:String, toAddr: String):Boolean = {
         this.synchronized {
              if(storageQueue.size == maxSize){
                    Logger.warn("overloaded queue "+ maxSize)
                    problem = true
                    false
              }
              val message = new MessageData(0, template, body, subj,lang, toAddr)
              storageQueue = storageQueue.enqueue(message)
              problem = false
              true
         }
    }
  
    override def getMessagesToSend(limit:Int): List[MessageData]={
         this.synchronized {
              Logger.debug("the memory storage contains "+storageQueue.size+ " messages...")
              var result  = List[MessageData]()
              Iterator.iterate(storageQueue) { 
                 qi =>
                     val (e,q) = qi.dequeue
                     result = e::result
                     storageQueue = q
                     q
              }.takeWhile(! _.isEmpty).foreach(identity)
              
              problem = false
              result
         }
    }
    
    
}