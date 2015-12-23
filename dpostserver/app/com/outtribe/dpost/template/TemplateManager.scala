package com.outtribe.dpost.template
import scala.collection.mutable.HashMap
import java.io.File
import scala.io.Codec
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
 * Mail templates managed by this class
 */
class TemplateManager {
    TemplateManager
    
    /**
    * load templates, creating objects for them, object must be a matcher for the specific parameters list
    */
    def findTemplate(templateKey:String, langKey:String) : MailTemplateHelper = {
        TemplateManager.templatesMap.get((templateKey,langKey)).get
    }
}

object TemplateManager{
    
    Logger.info("the template manager is starting")

    //path to the template folder
    val baseFolder : String = Configuration.HOME + System.getProperty("file.separator") +"templates"

    //collect templates here
    var templatesMap: Map[(String, String), MailTemplateHelper] = new scala.collection.immutable.HashMap[(String, String), MailTemplateHelper]

    readTemplatesFromDirectory




    def readTemplatesFromDirectory(){
          val templateFiles: Array[File] = new java.io.File(baseFolder).listFiles.filter(_.getName.endsWith(".dpt.html"))
          if(templateFiles==null || templateFiles.size==0){
              Logger.error("there are no templates found in the " + baseFolder)
          }else{
              templateFiles.foreach(addTemplateFromFile)
          }
    }


    /**
     *adding template content to the in memory map
     */
    def addTemplateFromFile(file: File){

        val name = file.getName
        Logger.info("loaded template " + name)

        if(name==null || name.indexOf('-') < 1 || name.length < 8 /* at least 1 letter for the template name and 2 letters for language, dash in the middle, and .dpt.html*/){
            Logger.error("template "+name + " has a badly formed name")
            return
        }

        val templateKey = name.substring(0, name.indexOf('-'))
        val langKey     = name.substring(name.indexOf('-') + 1, name.indexOf(".dpt.html"))

        Logger.info("template's ( " + templateKey + " " + langKey + " ) name has passed the formatting part" )

        val source = scala.io.Source.fromFile(file)(Codec.UTF8)
        
        var i = 0
        var subj = ""
        var body = ""
          
        for {  
              (line) <- source.getLines  
        } 
        {
          
          if(i==0){
            subj = line.toString()
          }else{
            body = body + line.toString() + "\n" 
          }
          
          i+=1
          
        }

          
        

        templatesMap += ((templateKey, langKey) -> new MailTemplateHelper(body,subj))

    }

}

