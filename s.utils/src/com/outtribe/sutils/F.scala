package com.outtribe.sutils
import java.io._
import java.nio.charset.Charset

/**
* Copyright (C) 2014 Peter Kovgan
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
* 
* File utility
* 
*/
object F {

  /**
   * This method copies or moves the source text file from place to place, replacing specified content by another content in each line
   * Note: Replacement is optional, the utility can just move or copy the file
   * @param srcPath              - source file
   * @param destPath             - destination file   - optional, if not specified, then extension .copy will be used
   * @param whatReplacePhrase    - phrase to replace  - optional, null - means nothing to replace
   * @param byWhatReplacePhrase  - replacement phrase - optional, null - means nothing to replace
   * @param charset              - charset used in the file - optional, if not specified, then used default OS charset
   * @param deleteSource         - optional, if not specified, then source file will be preserved(file copied), if true - source deleted(file moved)
   * @returns nothing, but new file created in the destPath
   * 
   *
   */
  def copyMoveReplace(srcPath: String, destPath: String = null, whatReplacePhrase: String = null, byWhatReplacePhrase: String = null, charset: Charset = Charset.defaultCharset(), deleteSource: Boolean=false) {
    
    val source =  new File(srcPath)
    if(!source.exists()){
        println("specified file "+srcPath +" is not found, abort operation")
        return
    }
    
    val pattern  = Option(whatReplacePhrase)
    val cover    = Option(byWhatReplacePhrase)
    val destFile = Option(destPath).getOrElse(srcPath+".copy")
    
    use(new FileInputStream(srcPath)) { in =>
      use(new FileOutputStream(destFile)) { out =>
        while (in.available() > 0) {
          val bis = new ByteArrayOutputStream()
          Iterator.continually(in.read()).takeWhile(lineEndChar(_)).foreach { bis.write(_) }
          val inputString = new String(bis.toByteArray(), charset)
          val resultString = (pattern, cover) match {
            case (s: Some[String], p: Some[String]) => inputString.replaceAll(whatReplacePhrase, byWhatReplacePhrase)
            case _ => inputString
          }
          use(bis) { _ =>
            out.write(resultString.getBytes(charset))
          }
        }
      }
    }
    
    if(deleteSource){
      source.delete();
    }
    
  }

  /**
   * return true if char is end of line or end of file mark
   */
  def lineEndChar(char: Int): Boolean = {
    (char != -1 && char != 13)
  }

  /**
  *  
  *  control structure, able to get streams as an input, apply some operation on stream and close it
  *  
  */
  def use[T <: { def close(): Unit }](closable: T)(block: T => Unit) {
    try {
      block(closable)
    } finally {
      closable.close()
    }
  }

}