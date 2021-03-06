import play.api._
import com.outtribe.dpost.DPost
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
 * Main class, here the business starts
 * FIXME: create graceful interruption
*/
object Global extends GlobalSettings {
  
   
   override def onStart(app: Application) {
       Logger.info("Dpost has started")
       val wasGood = Configuration.init
       if(!wasGood)
         DPost.fileSystemProblem = true
       else
         DPost.fileSystemProblem = false
       
   }  
   override def onStop(app: Application) {
       Logger.info("Application shutdown...")
       Logger.info("Thank you for using DPost!")
       Logger.info(":)")
   } 
}