package controllers

import models.MainConfig
import play.Play
import play.api.data.Form
import play.api.data.Forms.boolean
import play.api.data.Forms.mapping
import play.api.data.Forms.number
import play.api.data.Forms.text
import play.api.mvc.Action
import play.api.mvc.Controller
import views.html
import play.api._
import java.io.File
import java.io.FileInputStream
import java.util.Properties
import java.io.FileNotFoundException
import java.io.IOException
import models.MainConfig
import org.apache.commons.dbcp.BasicDataSourceFactory
import com.outtribe.dpost.storage.PersistentMessageEntity
import com.googlecode.mapperdao.utils.Setup
import java.io.PrintWriter
import java.io.FileOutputStream
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
 * Main class, here the business starts
 * FIXME: create graceful interruption
*/
object Configuration extends Controller with Secured{
   
   val APP_NAME = "dpost"
   val RUNTIME_CONFIG_FILE = "dpost.properties"
   val LOGIN_CONFIG_FILE = "users.properties"
   val SMTP_ADDS_RUNTIME_CONFIG_FILE = "session.extra.properties"
   val KEY_FILE = "license.txt"
   val FILE_SEPARATOR = System.getProperty("file.separator")
   lazy val HOME : String = scala.util.Properties.envOrElse("DPOST_HOME", System.getProperty("user.dir")  + FILE_SEPARATOR + APP_NAME)
   
   
   lazy val loginConfigfilePath = HOME + FILE_SEPARATOR + "conf" + FILE_SEPARATOR + LOGIN_CONFIG_FILE
   lazy val runtimeConigFilePath = HOME + FILE_SEPARATOR + "conf" + FILE_SEPARATOR + RUNTIME_CONFIG_FILE
   lazy val runtimeConigSMTPAddsFilePath = HOME + FILE_SEPARATOR + "conf" + FILE_SEPARATOR + SMTP_ADDS_RUNTIME_CONFIG_FILE
   lazy val templatesPath = HOME + FILE_SEPARATOR + "templates"
   lazy val licensePath = HOME + FILE_SEPARATOR + "conf" + FILE_SEPARATOR + KEY_FILE
   private lazy val runtimeConfiguration = new Properties
   private lazy val defaultConfiguration = new Properties
   private lazy val runtimeSMTPAddedConfiguration = new Properties
   
   
   
   def init() : Boolean={
     runtimeConfiguration.clear()
     defaultConfiguration.clear()
     
     loadDefaultConfiguration
     val loadedRuntime = loadRuntimeConfiguration
     
     if(!loadedRuntime || !isRuntimeConfigurationMapNotEmpty){
        //create it from defaults
        val written = copyDefaultsInRuntime
        if(!written)
          return false
        loadRuntimeConfiguration
        if(!isRuntimeConfigurationMapNotEmpty){
          return false
        }
     }
     true
   }
   
   private def copyDefaultsInRuntime():Boolean = {
      try{
	      val newRuntimeConfig = new File(runtimeConigFilePath)
	      if(!newRuntimeConfig.exists()){
	        newRuntimeConfig.createNewFile()
	      }
	      val os = new FileOutputStream(newRuntimeConfig)
	      defaultConfiguration.store(os, null);
	      os.flush()
	      os.close()
	      return true
	  }catch{
         case e: Exception => {
             Logger.error("trying write runtime configuration",e ) 
         }
      }
	  false
   }
   
   private def isRuntimeConfigurationMapNotEmpty: Boolean = {
       runtimeConfiguration.size() > 0
   }
   
   private def loadRuntimeConfiguration: Boolean = {
     var is: FileInputStream = null
     try { 
	     val rutimeConfigFile = new File(runtimeConigFilePath)
	     if(rutimeConfigFile.exists){
	          is = new FileInputStream(rutimeConfigFile)
	          runtimeConfiguration.load(is);
	          return true
	     }
     } catch {
        case e: IOException => Logger.error("trying load runtime configuration",e ) 
        case e: FileNotFoundException => Logger.error("trying load runtime configuration",e )
     } finally{
         if(is!=null){
            try{
               is.close
            }catch {
               case e: IOException => Logger.error("trying close is on runtime configuration",e ) 
            }
         }
     }
     false
   }
   private def loadDefaultConfiguration:Unit = {
        //--JMAIL
        val jmail_auth_user = Play.application().configuration().getString("jmail.auth.user")
        defaultConfiguration.put("jmail.auth.user",jmail_auth_user)
        val jmail_auth_password= Play.application().configuration().getString("jmail.auth.password")
        defaultConfiguration.put("jmail.auth.password",jmail_auth_password)
        val jmail_from_name= Play.application().configuration().getString("jmail.from.name")
        defaultConfiguration.put("jmail.from.name",jmail_from_name)
        val jmail_from_address= Play.application().configuration().getString("jmail.from.address")
        defaultConfiguration.put("jmail.from.address",jmail_from_address)
        val jmail_session_property_mail_transport_protocol= Play.application().configuration().getString("jmail.session.property.mail.transport.protocol")
        defaultConfiguration.put("jmail.session.property.mail.transport.protocol",jmail_session_property_mail_transport_protocol)
        val jmail_session_property_mail_host= Play.application().configuration().getString("jmail.session.property.mail.host")
        defaultConfiguration.put("jmail.session.property.mail.host",jmail_session_property_mail_host)
        val jmail_session_property_mail_smtp_auth= Play.application().configuration().getString("jmail.session.property.mail.smtp.auth")
        defaultConfiguration.put("jmail.session.property.mail.smtp.auth",jmail_session_property_mail_smtp_auth)
        val jmail_session_property_mail_smtp_port= Play.application().configuration().getString("jmail.session.property.mail.smtp.port")
        defaultConfiguration.put("jmail.session.property.mail.smtp.port",jmail_session_property_mail_smtp_port)
        val jmail_session_property_mail_smtp_socketFactory_class= Play.application().configuration().getString("jmail.session.property.mail.smtp.socketFactory.class")
        defaultConfiguration.put("jmail.session.property.mail.smtp.socketFactory.class",jmail_session_property_mail_smtp_socketFactory_class)
        
        //----DB
        
        val db_driverClassName= Play.application().configuration().getString("db_driverClassName")
        defaultConfiguration.put("db_driverClassName",db_driverClassName)
        val db_url= Play.application().configuration().getString("db_url")
        defaultConfiguration.put("db_url",db_url)
        val db_username= Play.application().configuration().getString("db_username")
        defaultConfiguration.put("db_username",db_username)
        val db_password= Play.application().configuration().getString("db_password")
        defaultConfiguration.put("db_password",db_password)
        
        //---COMMON
        
        
        val common_storage_size= Play.application().configuration().getString("common_storage_size")
        defaultConfiguration.put("common_storage_size",common_storage_size)
        
        val common_check_time_msc= Play.application().configuration().getString("common_check_time_msc")
        defaultConfiguration.put("common_check_time_msc",common_check_time_msc)
        
        val common_storageType= Play.application().configuration().getString("common_storageType")
        defaultConfiguration.put("common_storageType",common_storageType)
   }
   
  
  
   val mainForm: Form[MainConfig] = Form(
     // Define a mapping that will handle MainConfig values
     mapping(
      //jmail   
     "jmail_auth_user" -> text(minLength = 3, maxLength = 50),
     "jmail_auth_password" -> text(minLength = 3, maxLength = 50),
     "jmail_from_name" -> text(minLength = 1, maxLength = 50),
     "jmail_from_address"-> text(minLength = 1, maxLength = 50),
     "jmail_session_property_mail_transport_protocol"-> text(minLength = 1, maxLength = 10),
     "jmail_session_property_mail_host"-> text(minLength = 1, maxLength = 100),
     "jmail_session_property_mail_smtp_auth" -> boolean,
     "jmail_session_property_mail_smtp_port" -> number(min = 1, max = 49151),
     "jmail_session_property_mail_smtp_socketFactory_class"-> text(minLength = 0, maxLength = 100),
      //db
     "db_driverClassName"-> text(minLength = 1, maxLength = 100),
     "db_url" -> text(minLength = 1, maxLength = 100),
     "db_username"-> text(minLength = 1, maxLength = 20),
     "db_password"-> text(minLength = 1, maxLength = 20),
     
      //common
      "common_storage_size" -> number(min = 10, max = 2000),
      "common_check_time_msc" -> number(min = 1000, max = 20000),
     
      "common_storageType" -> number(min = 1, max = 2)
      
      
      
     ) (MainConfig.apply)(MainConfig.unapply)
  )
  
  
  private def loadRuntimeConfigForm: play.api.data.Form[models.MainConfig] = {
     
     //--JMAIL
     
     val jmail_auth_user = runtimeConfiguration.getProperty("jmail.auth.user")
     
     val jmail_auth_password = runtimeConfiguration.getProperty("jmail.auth.password")
     
     val jmail_from_name = runtimeConfiguration.getProperty("jmail.from.name")
     
     val jmail_from_address = runtimeConfiguration.getProperty("jmail.from.address")
     
     val jmail_session_property_mail_transport_protocol = runtimeConfiguration.getProperty("jmail.session.property.mail.transport.protocol")
     
     val jmail_session_property_mail_host = runtimeConfiguration.getProperty("jmail.session.property.mail.host")
     
     val st_jmail_session_property_mail_smtp_auth =  runtimeConfiguration.getProperty("jmail.session.property.mail.smtp.auth")
     var jmail_session_property_mail_smtp_auth : Boolean = true
     if(st_jmail_session_property_mail_smtp_auth!=null){
        jmail_session_property_mail_smtp_auth = st_jmail_session_property_mail_smtp_auth.toBoolean
     }
     
     var st_jmail_session_property_mail_smtp_port= runtimeConfiguration.getProperty("jmail.session.property.mail.smtp.port")
     var jmail_session_property_mail_smtp_port : Int = 0
     if(st_jmail_session_property_mail_smtp_port!=null){
        jmail_session_property_mail_smtp_port = st_jmail_session_property_mail_smtp_port.toInt
     }
     
     val jmail_session_property_mail_smtp_socketFactory_class = runtimeConfiguration.getProperty("jmail.session.property.mail.smtp.socketFactory.class")
     
     //--DB
     
     val db_driverClassName = runtimeConfiguration.getProperty("db_driverClassName")
     
     val db_url = runtimeConfiguration.getProperty("db_url")
     
     val db_username = runtimeConfiguration.getProperty("db_username")
     
     val db_password = runtimeConfiguration.getProperty("db_password")
     
     //--COMMON
     
     var st_common_storage_size= runtimeConfiguration.getProperty("common_storage_size")
     var common_storage_size : Int = 100
     if(st_common_storage_size!=null){
        common_storage_size = st_common_storage_size.toInt
     }
     
     var st_common_check_time_msc= runtimeConfiguration.getProperty("common_check_time_msc")
     var common_check_time_msc : Int = 5000
     if(st_common_check_time_msc!=null){
        common_check_time_msc = st_common_check_time_msc.toInt
     }
     
     
    
     
     
     var st_common_storageType= runtimeConfiguration.getProperty("common_storageType")
     var common_storageType : Int = 1
     if(st_common_storageType!=null){
        common_storageType = st_common_storageType.toInt
     }
     
     val runtimeForm = mainForm.fill(MainConfig(jmail_auth_user,jmail_auth_password,jmail_from_name,jmail_from_address,jmail_session_property_mail_transport_protocol,
         jmail_session_property_mail_host,jmail_session_property_mail_smtp_auth,jmail_session_property_mail_smtp_port,
         jmail_session_property_mail_smtp_socketFactory_class,
         db_driverClassName,db_url,db_username,db_password,
         common_storage_size,common_check_time_msc,
         common_storageType))
         
     runtimeForm
   }
  
  
  
  private def loadDefaultsFromApplcationConfig: play.api.data.Form[models.MainConfig] = {
     //--JMAIL
     val jmail_auth_user = Play.application().configuration().getString("jmail.auth.user")
     val jmail_auth_password= Play.application().configuration().getString("jmail.auth.password")
     val jmail_from_name= Play.application().configuration().getString("jmail.from.name")
     val jmail_from_address= Play.application().configuration().getString("jmail.from.address")
     val jmail_session_property_mail_transport_protocol= Play.application().configuration().getString("jmail.session.property.mail.transport.protocol")
     val jmail_session_property_mail_host= Play.application().configuration().getString("jmail.session.property.mail.host")
     val jmail_session_property_mail_smtp_auth= Play.application().configuration().getBoolean("jmail.session.property.mail.smtp.auth")
     val jmail_session_property_mail_smtp_port= Play.application().configuration().getInt("jmail.session.property.mail.smtp.port")
     val jmail_session_property_mail_smtp_socketFactory_class= Play.application().configuration().getString("jmail.session.property.mail.smtp.socketFactory.class")
     
     //--DB
     
     val db_driverClassName= Play.application().configuration().getString("db_driverClassName")
     val db_url= Play.application().configuration().getString("db_url")
     val db_username= Play.application().configuration().getString("db_username")
     val db_password= Play.application().configuration().getString("db_password")
     
     //--COMMON
     val common_storage_size= Play.application().configuration().getInt("common_storage_size")
     val common_check_time_msc= Play.application().configuration().getInt("common_check_time_msc")
     val common_storageType= Play.application().configuration().getInt("common_storageType")
     
     
     
     val initialForm = mainForm.fill(MainConfig(jmail_auth_user,jmail_auth_password,jmail_from_name,jmail_from_address,jmail_session_property_mail_transport_protocol,
         jmail_session_property_mail_host,jmail_session_property_mail_smtp_auth,jmail_session_property_mail_smtp_port,
         jmail_session_property_mail_smtp_socketFactory_class,
         db_driverClassName,db_url,db_username,db_password,
         common_storage_size,common_check_time_msc,
         common_storageType))
         
     initialForm
   }
  
  
  
   private def  saveInRuntimeConfigFile(mainConfig: MainConfig){
       mainConfig.writeToFile(runtimeConigFilePath)
   }
   
   private def deleteRutimeConfigurationFile(): Boolean ={
       try{
          val file = new File(runtimeConigFilePath)
          if(file.exists)
             return new File(runtimeConigFilePath).delete
          else
             return true  
       }catch{
         case e: IOException => Logger.error("trying delete runtime configuration",e ) 
         case e: FileNotFoundException => Logger.error("delete load runtime configuration",e )
       }
       false
   }
   
   
   /**
   * Display an empty form.
   */
  def form = IsAuthenticated { _ => _ =>
    
	    if(!isRuntimeConfigurationMapNotEmpty) {
	         /*TODO: make HELP for that*/
	         Ok("System problem, please read the manual to resolve it")
	    }
	    val runtimeConfigForm = loadRuntimeConfigForm
	    
	    
	    Ok(html.configure.form(runtimeConfigForm, Dashboard.isStarted))
    
    
  }
  
  /**
   * Handle form submission.
   */
  def result = Action { 
    implicit request =>
    mainForm.bindFromRequest.fold(
      // Form has errors, redisplay itis
      errors => BadRequest(html.configure.form(errors, Dashboard.isStarted)),
      // We got a valid MainConfig value, display the summary
      mainConfig => {
        
        request.body.asFormUrlEncoded.get("applyconfig").headOption match {
          case Some("Update") => {
            saveInRuntimeConfigFile(mainConfig)
            loadRuntimeConfiguration
            if(isRuntimeConfigurationMapNotEmpty){
               Ok(html.configure.form(loadRuntimeConfigForm, Dashboard.isStarted))
            }else{
               Ok("System problem, runtime configuration is not ready")
            }
          }
          case Some("Reset") => {
             if(deleteRutimeConfigurationFile){
                 val wasGood = init
                 if(!wasGood){
                     DPost.fileSystemProblem = true
                     BadRequest("You need delete that file manually\nto reset\n"+runtimeConigFilePath);
                 }else{
                     DPost.fileSystemProblem = false
	                 if(isRuntimeConfigurationMapNotEmpty){
	                    Ok(html.configure.form(loadRuntimeConfigForm, Dashboard.isStarted))
	                 }else{
	                    Ok("System problem, runtime configuration is not ready")
	                 }
                 }
             }
             else
                BadRequest("You need delete that file manually\nto reset\n"+runtimeConigFilePath);
          }
          case _ => BadRequest("This action is not allowed");
        }
      }
    )
   }
  
   
   def common_storage_size: Int = {
       val storageSize  = runtimeConfiguration.getProperty("common_storage_size")
       if(storageSize != null){
         storageSize.toInt
       }
       else {
         10
       }
    }
    
    def common_check_time_msc: Int = {
       val checkTimeMsc  = runtimeConfiguration.getProperty("common_check_time_msc")
       if(checkTimeMsc != null) {
           checkTimeMsc.toInt
       }
       else{
           5000
       }
    }
    
    //how JSON message data received by the server
    
    
   

    //internal parameters
    /**TODO: add this property to configuration **/
    def common_messages_to_send_request_limit : Int = {
       val messagesToCheckRequestLimit = runtimeConfiguration.getProperty("common_messages_to_send_request_limit")
       if(messagesToCheckRequestLimit != null){
          messagesToCheckRequestLimit.toInt
       }
       else {
          100
       }
    }
    
    def common_storageType: Int = {
      val storageType = runtimeConfiguration.getProperty("common_storageType")
      if(storageType != null){
         storageType.toInt
      }
      else{
         1
      }//in memory
    }
    
    def STORAGE_TYPE_TITLE: String = {
      
      if(common_storageType == 1){
         "IN MEMORY"
      }
      else{
         "DATA BASE"
      }
    }
    
    
    
  
    
    def getDbProperties: Properties={
       val dbProperties = new Properties
       dbProperties.put("driverClassName", runtimeConfiguration.getProperty("db_driverClassName"))
       dbProperties.put("url", runtimeConfiguration.getProperty("db_url"))
       dbProperties.put("username", runtimeConfiguration.getProperty("db_username"))
       dbProperties.put("password", runtimeConfiguration.getProperty("db_password"))
       dbProperties
    }
    
    
    def getSMTPAddMailerProperties:Properties={
         val addSmtpConfig = new Properties
         var is: FileInputStream = null
	     try { 
		     val rutimeConfigFile = new File(runtimeConigSMTPAddsFilePath)
		     if(rutimeConfigFile.exists){
		          is = new FileInputStream(rutimeConfigFile)
		          addSmtpConfig.load(is);
		     }
	     } catch {
	        case e: IOException => Logger.error("trying load added smtp configuration",e ) 
	        case e: FileNotFoundException => Logger.error("trying load added smtp configuration",e )
	     } finally{
	         if(is!=null){
	            try{
	               is.close
	            }catch {
	               case e: IOException => Logger.error("trying load added smtp configuration, error in is closing",e ) 
	            }
	         }
	     }
	     addSmtpConfig
	}
    
    def getMailerProperties: Properties={
       val mailProperties:Properties = new Properties
       mailProperties.put("jmail.auth.user", runtimeConfiguration.getProperty("jmail.auth.user"))
       mailProperties.put("jmail.auth.password", runtimeConfiguration.getProperty("jmail.auth.password"))
       mailProperties.put("jmail.from.name", runtimeConfiguration.getProperty("jmail.from.name"))
       mailProperties.put("jmail.from.address", runtimeConfiguration.getProperty("jmail.from.address"))
       mailProperties.put("jmail.session.property.mail.transport.protocol", runtimeConfiguration.getProperty("jmail.session.property.mail.transport.protocol"))
       mailProperties.put("jmail.session.property.mail.host", runtimeConfiguration.getProperty("jmail.session.property.mail.host"))
       mailProperties.put("jmail.session.property.mail.smtp.auth", runtimeConfiguration.getProperty("jmail.session.property.mail.smtp.auth"))
       mailProperties.put("jmail.session.property.mail.smtp.port", runtimeConfiguration.getProperty("jmail.session.property.mail.smtp.port"))
       mailProperties.put("jmail.session.property.mail.smtp.socketFactory.class", runtimeConfiguration.getProperty("jmail.session.property.mail.smtp.socketFactory.class"))
       mailProperties
    }
}