package com.outtribe.dpost.actors

import akka.actor.Actor
import play.api.Logger
import scala.concurrent.duration._
import com.outtribe.dpost.DPost
import scala.util.parsing.json.JSONObject
import java.net.URL
import java.net.HttpURLConnection
import java.io.OutputStreamWriter
import java.io.BufferedReader
import java.io.InputStreamReader
import com.outtribe.dpost.help.Helper
import play.api.libs.json.Json
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
class UpdatesCheckActor extends Actor{
  @volatile var stopped: Boolean = false
  val updatesDisabled: Boolean = true //you can enable updates report(you need a server for that)
  def receive: Receive = {
        case CheckUpdatesMessage =>{
             if(!updatesDisabled){
                 //collect user data
	             try{
	                val runtime   = System.getProperty("java.runtime.name")
	                val version   = System.getProperty("java.runtime.version")
	                val vendor    = System.getProperty("java.vm.vendor")
	                val osArch    = System.getProperty("os.arch")
	                val osName    = System.getProperty("os.name")
	                val osVersion = System.getProperty("os.version")
	                val country   = System.getProperty("user.country")
	                val language  = System.getProperty("user.language")
	                val user      = System.getProperty("user.name")
	                val timezone  = System.getProperty("user.timezone")
	                val L = "|"
	                
	                submitUpdateRequest(Helper.ks,runtime+L+version+L+vendor+L+osArch+L+osName+L +osVersion +L +country+L+language+L+user+L+timezone)
	               
	               
	               
	             }catch{
	                case e:Throwable=>{
	                   Logger.debug("Failed get updates. It's perfectly ok if you intentionally prevent DPost from getting updates messages", e);
	                }
	             }finally{
	                 if(!stopped){
		                 //try get updates every 24 hours 
		                 val duration = new FiniteDuration(60*60*24, SECONDS)
		                 context.system.scheduler.scheduleOnce(duration, self, CheckUpdatesMessage)(DPost.dispatcher)
	                 }
	             }
             }
        }
        case TerminateActor => {
           stopped = true
           Logger.info("StorageCheckActor stopped")
           context.stop(self)
        }
  }
  
  
  def submitUpdateRequest(uid:String,data:String): Unit = {
   
    val jsonMap = Map[String, Any]("UID" -> uid, "DATA" -> data)
    val obj: JSONObject = new JSONObject(jsonMap)

    val json: String = obj.toString

    //host and port to externalize! make configurable!
    //but /dpost URL part is obligatory!
    var url: URL = null
    
    
    url = new URL("http://www.xyz.com/dpost/update")
    
    
    val con = url.openConnection.asInstanceOf[HttpURLConnection]

    try {
      con.setRequestMethod("POST")
      con.setRequestProperty("Content-Type", "application/json");
      con.setRequestProperty("Accept", "application/json");
      con.setRequestProperty("Content-Length", Integer.toString(json.length()));
      
      con.setUseCaches(false)
      con.setDoInput(true)
      con.setDoOutput(true)
      //Send request
      
      var os = new OutputStreamWriter(con.getOutputStream(), "UTF-8")
      os.write(json.toString())
      os.flush()
      os.close()
     
      con.connect()

      if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
          println("try send failed, code="+con.getResponseCode())
      }else{
	      //Get Response	
	      val is = con.getInputStream
	      val rd = new BufferedReader(new InputStreamReader(is))
	      var line: String = null
	      val response = new StringBuffer
	      val iterator = Iterator.continually(rd.readLine()).takeWhile(_ != null)
	      while (iterator.hasNext) {
	        response.append(iterator.next)
	        response.append('\r')
	      }
	      rd.close();
	      val result = response.toString
	      println("result from DPOST:"+result)
	      
	      
	      
	      try{
	        val json = Json.parse(result)
	        val mayBeUpdateNum      = (json \ "UPDATE" ).asOpt[Long]
	        val mayBeCurrentVersion = (json \ "CURRENT" ).asOpt[String]
	        val mayBeCurrentNews    = (json \ "NEWS" ).asOpt[Array[String]]
	        
	        Helper.update(mayBeCurrentVersion.get, mayBeUpdateNum.get, mayBeCurrentNews.get)
	        
	      }catch{
	        case e:Throwable =>{
	           Logger.debug("got invalid data during update",e)
	        }
	      }
	      
	     
      }
    } catch {
      case e: Exception => println(e.getStackTraceString)
    } finally {
      if (con != null) {
        con.disconnect
      }
    }

  }
  
  
}