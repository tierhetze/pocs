package controllers

import play.api.mvc.Action
import play.api.mvc.Controller
import views.html
import models.TemplatesInfo
import java.io.File
import java.io.FilenameFilter
import play.api.Logger
import java.util.Date
import java.text.DateFormat


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
object Templates extends Controller with Secured{
  def templates = IsAuthenticated { _ => _ =>
    
     val info : TemplatesInfo = new TemplatesInfo(Configuration.templatesPath,"<subject>-<language>-<region>.dpt.html")
     val rutimeTemplateDir = new File(Configuration.templatesPath)
     val filter:FilenameFilter = new  FilenameFilter(){
         def accept(dir: File, name: String): Boolean={
             nameIsGood(name)
         }
         def nameIsGood(name:String):Boolean={
            try{
            if(!name.endsWith(".dpt.html")){
              Logger.error("bad template "+name)
              return false
            }
            if(name.indexOf("-")<0){
               Logger.error("bad template "+name)
               return false
               val name1 = name.substring(name.indexOf("-")+1)
               if(name1.indexOf("-")<0){
                   Logger.error("bad template "+name)
                   return false
               }
               val name2 = name.substring(name1.indexOf("-")+1)
               if(name2.indexOf("-")<0){
                   Logger.error("bad template "+name)
                   return false
               }
            }
            }catch{
              case e:Exception => {
                 Logger.error("bad template "+name,e)
                 return false
              }
            }
            true
         }
     }
     val templates = rutimeTemplateDir.list(filter)
     
     
     if(templates.size > 0){
       for( x <- templates ){
          try{
          val name:String = x.asInstanceOf[String]
          Logger.info("template :"+name)
          val subject = name.substring(0, name.indexOf("-"))
          val i1 = name.indexOf("-")
          val rest = name.substring(i1+1)
          val language = rest.substring(0, rest.indexOf("-") )
          val i2 = rest.indexOf("-")
          val rest2 = rest.substring(i2+1)
          val region = rest2.substring(0, rest2.indexOf(".") )
          
          val  thisFile: File = new File(Configuration.templatesPath+Configuration.FILE_SEPARATOR+name)
          val time = thisFile.lastModified
          val date = new Date(time)
          
          val df:DateFormat = DateFormat.getDateInstance
          
          info.add(subject,language,region,df.format(date))
          
          }catch{
            case e:Exception=>{
              Logger.error("template name parsing failure", e)
            }
          }
          
       }  
     }else{
        Logger.warn("there is no templates")
     }
    
     Ok(html.templates.templates(info))
  }
}