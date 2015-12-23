package com.outtribe.dpost.template

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
 * from the body and subject, saved in template this matcher class creates the 
 * right (appropriate to e-mail consumer) body and subject, using parameters, that were submitted to
 * the storage by e-mail sender
 * 
 */
class MailTemplateHelper(val body:String, val subj:String){
    
    def produceBody(param: Map[String, String], templateName:String, lang:String, mailType:String) : String = {
        var body:String = this.body
        try{
	        if(body.length()>0){
	           param.foreach((el)=> body = body.replace("{$"+el._1+"}", el._2))
	        }
	        
        }catch{
            case e: Throwable => {
               Logger.error("template parsing error , body="+body, e)
            }
        }
        body
    }
    def produceSubj(param: Map[String, String]) : String = {
        var subj:String = this.subj
        try{
	        //remove Unicode marker
	        if(subj.length()>0 && 65279==subj.charAt(0).toInt){
	          subj = subj.substring(1)
	        }
	        if(subj.length()>0){
	           param.foreach((el)=> subj = subj.replace("{$"+el._1+"}", el._2))
	        }
        }catch{
            case e: Throwable => {
               Logger.error("template parsing error : subj"+subj, e)
            }
        }
        subj
    }
}