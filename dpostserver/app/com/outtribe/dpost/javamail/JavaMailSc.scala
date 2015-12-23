package com.outtribe.dpost.javamail
import java.lang.Boolean
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import java.util.Properties
import javax.mail.PasswordAuthentication
import javax.mail.PasswordAuthentication
import javax.mail.Authenticator
import controllers.Configuration
import com.outtribe.dpost.storage.MessageData
import com.outtribe.dpost.DPost
import java.io.FileInputStream
import play.api._
import controllers.Configuration


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
 * responsible to send an email
 * currently supports only gmail kind
 * 
 */
object JavaMailSc{
  
  val MAIL_TYPE_TEXT: String = "text/plain"
  val MAIL_TYPE_HTML: String = "text/html"
  val MAIL_ENCODING: String = "charset=UTF-8"
  var session: Session = null
  var successCounter:Long = 0
  var failureCounter:Long = 0
  
  def reset(){
    session = null
    allProperties
    successCounter = 0
    failureCounter = 0
  }
  
  def allProperties:Properties={
      Configuration.getMailerProperties
  }
  def smtpAddProperties:Properties={
      Configuration.getSMTPAddMailerProperties
  }

  def matchTemplateSend(message: MessageData): Boolean = {

    val template = DPost.tm.findTemplate(message.template, message.lang)
    
    var mailType = MAIL_TYPE_TEXT
    
    val bodyInTemplate = template.body
    
    if(bodyInTemplate.contains("<html") || bodyInTemplate.contains("<HTML") || bodyInTemplate.contains("<table") || bodyInTemplate.contains("<TABLE") || bodyInTemplate.contains("<BODY") || bodyInTemplate.contains("<body")){
         mailType = MAIL_TYPE_HTML
    }
    
    
    if (template == null) {
      Logger.error("there is no template " + template )
      false
    }
    
    val body = template.produceBody(message.body, message.template,message.lang, mailType)
    val subj = template.produceSubj(message.subj)
    val addrTo = message.toAddr
    
    sendMail(addrTo, subj, body, mailType)
    
  }

  def sendMail(toAddr: String, subj: String, body: String, mailType:String) : Boolean = {
    
      try {
          if (session == null) {
             this.synchronized {
               if (session == null) {
                   session = init
               }
             }
          }
      
          val transport = session.getTransport()
	      val message = new MimeMessage(session)
	      message.setFrom(new InternetAddress(allProperties.getProperty("jmail.from.address"), allProperties.getProperty("jmail.from.name"), MAIL_ENCODING))
	      message.setSubject(subj)
	      message.setContent(body, mailType + ";" + MAIL_ENCODING)
	      message.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(toAddr))
	      transport.connect()
	      transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO))
	      transport.close()
	      
	      increaseSuccess
      
      } catch {
         case e: Throwable => {
            Logger.error("failed send message to "+toAddr + " subj "+subj, e )
            increaseFailure
            return false
         }
         
      }
      true
  }
  
  def resetCouneters(){
    successCounter=0
    failureCounter=0
  }
  
  def increaseSuccess(){
    if (successCounter < Long.MaxValue-1){
       successCounter+=1
    }else{
       Logger.info("success counter reset")
       successCounter = 1
    }
    
  }
  def increaseFailure(){
    if (failureCounter < Long.MaxValue-1){
       failureCounter+=1
    }else{
       Logger.info("failure counter reset")
       failureCounter = 1
    }
  }

  def init(): Session = {
    
    val props = new Properties()
    val enume = allProperties.propertyNames()
    
    while(enume.hasMoreElements()){
        val key:String =  enume.nextElement().toString()
        val value:String = allProperties.getProperty(key)
        
        Logger.debug("viewed sender properties: key="+key+",value="+value)
        
        
        
        if(value==null || value.trim().equals("")){
            ;
        }else{
            if(key.startsWith("jmail.session.property.")){
                 Logger.debug("added sender properties: key="+key+",value="+value)
	             props.put(key.replace("jmail.session.property.", ""), value)
	        }
        }
    }
    //load additional smtp properties
    val enume1 = smtpAddProperties.propertyNames()
    if(enume1.hasMoreElements()){
        while(enume1.hasMoreElements()){
           val key:String =  enume1.nextElement().toString()
           val value:String = smtpAddProperties.getProperty(key)
           Logger.debug("viewed additional sender properties: key="+key+",value="+value)
           if(value==null || value.trim().equals("")){
               ;
           }else{
               if(key.startsWith("jmail.session.property.")){
                  Logger.debug("added additional sender properties: key="+key+",value="+value)
	              props.put(key.replace("jmail.session.property.", ""), value)
	           }
           }
        }
    }
    

    Session.getDefaultInstance(props, new javax.mail.Authenticator() {
      override def getPasswordAuthentication(): PasswordAuthentication = {
        return new PasswordAuthentication(allProperties.getProperty("jmail.auth.user"), allProperties.getProperty("jmail.auth.password"))
      }
    })
  }

}