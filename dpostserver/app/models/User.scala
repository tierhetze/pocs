package models


import play.api.Play.current
import play.api.Logger
import controllers.Configuration
import java.io.File
import java.util.Properties
import java.io.FileInputStream
import java.io.IOException
import scala.collection.mutable.HashMap
import collection.JavaConversions._ 



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

case class User(email: String, name: String, password: String)

object User {
  
  def getUsers ( loginConfiguration:Properties) : HashMap[String, (String, String)] = {
     val propsM = propertiesAsScalaMap(loginConfiguration)
     val propsMp = new HashMap[String, (String, String)]
     for(key<-propsM.keySet){
         try{
	         if(key.startsWith("users")){
	             val mail = key.substring("users.".length)
	             if(mail.trim().length()>0){
		             val valueTuple = propsM.get(key).get
		             val one = valueTuple.substring(0, valueTuple.indexOf("["))
		             val two = valueTuple.substring(valueTuple.indexOf("[")+1, valueTuple.indexOf("]")) 
		             Logger.debug("login adding:"+mail+"|"+one+"|"+two)
		             propsMp += (mail-> (one, two))
	             }
	         }
         }catch{
             case e: RuntimeException =>{
                 Logger.error("failed parse users config file entry", e)
             }
         }
     }
     propsMp
  }
  /**
   * Authenticate a User.
   */
  def authenticate(email: String, password: String): Option[User] = {
       val loginConfiguration = new Properties
       var is: FileInputStream = null
       try{
           val loginConfig = Configuration.loginConfigfilePath 
           val loginConfigFile = new File(loginConfig)
           if(loginConfigFile.exists){
              is = new FileInputStream(loginConfigFile)
              loginConfiguration.load(is);
              var user1: Option[User] = None
              val logins:HashMap[String, (String, String)] = getUsers(loginConfiguration)
              for(user<-logins.keys){
                  if(user1==None){
	                  val tupl = logins.get(user).get
	                  if(user.trim().equals(email.trim()) && tupl._2.trim().equals(password.trim())){
	                       Logger.debug("user found")
	                       user1 = Option(new User(user.trim(),tupl._1.trim(),tupl._2.trim()))
	                  }
                  }
              }
              return user1
           }
       }catch{
           case e:Throwable =>{
              Logger.error("error while reading user data for login ", e)
              return None
           }
       }finally{
           if(is!=null){
              try{
                  is.close
              }catch {
                  case e: IOException => Logger.error("trying close is on login configuration",e ) 
              }
           }
       }
       None
       
  }
   
  
  
  
}
