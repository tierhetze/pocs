package com.outtribe.dpost.help

import java.io.BufferedInputStream
import java.io.FileInputStream
import java.security.MessageDigest
import controllers.Configuration
import play.api.Logger
import java.io.File
import scala.io.Source



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
object Helper{
  
   

   
var version:String="1-0-0-1"
  
   
   
  
   private var CURRENT  = version
   private var NUM:Long = 0
   private var NEWS:Array[String] = Array("DPost is up to date")
   private var newerAvailable: Boolean = false
   
   def update(current:String, num:Long, news:Array[String]){
        if(num > NUM){
             this.synchronized{
	             NUM = num
	             if(!newerAvailable && !current.equals(CURRENT)){
	                 newerAvailable  = true
	                 CURRENT  = current
	                 NEWS     = news
	             }
	         }
        }
   }
   
   def getCurrent():(Boolean, String, Array[String])={
        this.synchronized{
             Logger.debug("get update page request")
             val na = newerAvailable
             val cu = CURRENT
             val ne = new Array[String](NEWS.length)
             var c = 0
             for(N<-NEWS){
               ne(c)=N
               c+=1
             }
             Logger.debug("get update page request:"+na+"|"+cu)
             (na,cu,ne)
        }
   }
  
   
   
   
   def ks():String={
     version+"-"+p
   }
   
   def p():String={
      var avProc =  1000
      var macSeq = "NA"
      try{
        avProc = Runtime.getRuntime().availableProcessors()
        macSeq = Listeners.streamToken
      }catch{
        case e:Throwable=>{
            Logger.debug("system data retr. problem", e)
        }
      }
      avProc+"-"+macSeq
   }
   
   
   
}
