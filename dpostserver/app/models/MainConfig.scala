package models

import scalax.io._
import java.io.PrintWriter
import java.io.File
import java.io.IOException
import java.io.FileNotFoundException
import play.api.Logger
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
case class MainConfig(
   //jmail
   jmail_auth_user: String,
   jmail_auth_password: String,
   jmail_from_name: String,
   jmail_from_address:String,
   jmail_session_property_mail_transport_protocol:String,
   jmail_session_property_mail_host: String,
   jmail_session_property_mail_smtp_auth: Boolean,
   jmail_session_property_mail_smtp_port: Int,
   jmail_session_property_mail_smtp_socketFactory_class: String,
   //db
   db_driverClassName: String,
   db_url: String,
   db_username: String,
   db_password:String,
   //common 
   common_storage_size: Int, 
   common_check_time_msc: Int,
   common_storageType:Int
   
   
   
   
   
){
  
  def writeToFile(runtimeConigFilePath: String): Boolean ={
      try{
	      val writer = new PrintWriter(new File(runtimeConigFilePath))
	      
	      //COMMON
	      writer.write("# Common Dpost config \n")
          
          if(common_storage_size != 0){
	         writer.write("common_storage_size="+common_storage_size+ "\n")
	      }
          if(common_check_time_msc != 0){
	         writer.write("common_check_time_msc="+common_check_time_msc+ "\n")
	      }
          
          if(common_storageType != 0){
	         writer.write("common_storageType="+common_storageType+ "\n")
	      }
          
          //DB
          writer.write("# Data Base config \n")
	      if(db_driverClassName != null){
	         writer.write("db_driverClassName="+db_driverClassName+ "\n")
	      }
          if(db_url != null){
	         writer.write("db_url="+db_url+ "\n")
	      }
          if(db_username != null){
	         writer.write("db_username="+db_username+ "\n")
	      }
          if(db_password != null){
	         writer.write("db_password="+db_password+ "\n")
	      }
          
	      
	      //jmail
	      writer.write("# JMail config \n")
	      
	      if(jmail_auth_user != null){
	         writer.write("jmail.auth.user="+jmail_auth_user+ "\n")
	      }
	      if(jmail_auth_password != null){
	         writer.write("jmail.auth.password="+jmail_auth_password+ "\n")
	      }
	      if(jmail_from_name != null){
	         writer.write("jmail.from.name="+jmail_from_name+ "\n")
	      }
	      if(jmail_from_address != null){
	         writer.write("jmail.from.address="+jmail_from_address+ "\n")
	      }
          if(jmail_session_property_mail_transport_protocol != null){
	         writer.write("jmail.session.property.mail.transport.protocol="+jmail_session_property_mail_transport_protocol+ "\n")
	      }
          if(jmail_session_property_mail_host != null){
	         writer.write("jmail.session.property.mail.host="+jmail_session_property_mail_host+ "\n")
	      }
          
	      writer.write("jmail.session.property.mail.smtp.auth="+jmail_session_property_mail_smtp_auth+ "\n")
	      
          if(jmail_session_property_mail_smtp_port != 0){
	         writer.write("jmail.session.property.mail.smtp.port="+jmail_session_property_mail_smtp_port+ "\n")
	      }
          if(jmail_session_property_mail_smtp_socketFactory_class != null){
	         writer.write("jmail.session.property.mail.smtp.socketFactory.class="+jmail_session_property_mail_smtp_socketFactory_class+ "\n")
	      }
          
          writer.write("# Note, that additional smtp properties can be added in the file: conf/session.extra.properties, more about that in installation.pdf")
          
          
          
          writer.flush
	      writer.close
	      return true
      }catch{
         case e: IOException => Logger.error("trying write runtime configuration",e ) 
         case e: FileNotFoundException => Logger.error("trying write runtime configuration",e )
      }
      false
  }
  
}



