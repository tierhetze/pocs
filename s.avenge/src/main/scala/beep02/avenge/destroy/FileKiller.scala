package beep02.avenge.destroy

import akka.actor.Actor
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import Messages._
import java.io.IOException
import javax.crypto.spec.SecretKeySpec
import javax.crypto.spec.IvParameterSpec
import scala.collection.immutable.StringOps
import javax.crypto.Cipher
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import akka.event.LoggingReceive

class FileKiller extends Actor {
   
    def receive = LoggingReceive{
      //http://www.artima.com/pins1ed/case-classes-and-pattern-matching.html
      case task:Task if task.kill==1 => {println("sender"+sender); val res=FileKiller.kill((task.f,task.s), self.toString()); println("res="+res); task.a ! res }
      case task:Task if task.kill==0 => task.a ! FileKiller.restore((task.f,task.s), self.toString(), task.length)
      case other => println("invalid message")
      
   }
   
}

//http://stackoverflow.com/questions/1205135/how-to-encrypt-string-in-java
//http://stackoverflow.com/questions/2225214/scala-script-to-copy-files
object FileKiller{
  
  private def iv:String = "HaruMamb";
  private def ivBytes  =  new StringOps(iv).getBytes
  private def ivSpec   =  new IvParameterSpec(ivBytes)
  
  import scala.language.reflectiveCalls
 
  @throws(classOf[IOException])
  def copyWithEncrypt(from: String, to: String, keyword:String ) :Int = {
    val cipher   =  Cipher.getInstance("DESede/CBC/PKCS5Padding")
    val keyBytes =  new StringOps(keyword).getBytes
    val key      =  new SecretKeySpec(keyBytes, "DESede")
    cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec)
    //create encrypted file
    val inputbuffer =  Files.readAllBytes(Paths.get(from))
    val encr = encryption(inputbuffer,keyword,cipher)
    Files.write(Paths.get(to), encr._1)
    Files.delete(Paths.get(from))
    Files.move(Paths.get(to),Paths.get(from))
    encr._2
  }
  
  @throws(classOf[IOException])
  def copyWithDecrypt(from: String, to: String, keyword:String, length:Int ) {
    val cipher   =  Cipher.getInstance("DESede/CBC/PKCS5Padding")
    val keyBytes =  new StringOps(keyword).getBytes
    val key      =  new SecretKeySpec(keyBytes, "DESede")
    cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)
    val inputbuffer = Files.readAllBytes(Paths.get(from))
    val decrypted = decryption(inputbuffer, keyword, cipher, length);
    Files.write(Paths.get(to), decrypted)
    Files.delete(Paths.get(from))
    Files.move(Paths.get(to),Paths.get(from))
  }
  
  
  def encryption(buffer: Array[Byte], key: String, cipher: Cipher): (Array[Byte], Int)={
        val encrypted= new Array[Byte](cipher.getOutputSize(buffer.length))
        var enc_len:Int = cipher.update(buffer, 0, buffer.length, encrypted, 0)
        enc_len = enc_len + cipher.doFinal(encrypted, enc_len)
        (encrypted, enc_len)
  }
  
  def decryption(buffer: Array[Byte], key: String, cipher: Cipher, length:Int): Array[Byte]={
        val decrypted = new Array[Byte](cipher.getOutputSize(length));
        var dec_len = cipher.update(buffer, 0,  length, decrypted, 0);
        dec_len = dec_len + cipher.doFinal(decrypted, dec_len);
        println("dec_len="+dec_len+", output.length="+decrypted.length)
        if(decrypted.length>dec_len){
            decrypted.slice(0, dec_len)  
        }else{
            decrypted  
        }
        
  }
  
  def kill(bullet:(File, String), who:String):DestroyReport={
        println(who + " kills file "+bullet._1.getAbsolutePath)
        val temp = bullet._1.getAbsolutePath+".temp"
        var len = 0
        try{
            len = copyWithEncrypt(bullet._1.getAbsolutePath,temp,bullet._2)
        }catch{
            case e: RuntimeException =>  {
                e.printStackTrace
                return new DestroyReport(bullet._1.getAbsolutePath,0,0) 
            }
            
        }
        return new DestroyReport(bullet._1.getAbsolutePath,1, len) 
  }
  
  def restore(bullet:(File, String), who:String, length:Int):RestoreReport={
        println(who + " restores file "+bullet._1.getAbsolutePath + ", length="+length)
        val temp = bullet._1.getAbsolutePath+".temp"
        try{
            copyWithDecrypt(bullet._1.getAbsolutePath,temp,bullet._2, length)
        }catch{
            case e: RuntimeException =>  {
                e.printStackTrace
                return  new RestoreReport(bullet._1.getAbsolutePath,0) 
            }
            
        }
        return new RestoreReport(bullet._1.getAbsolutePath,1) 
  }
  
}

