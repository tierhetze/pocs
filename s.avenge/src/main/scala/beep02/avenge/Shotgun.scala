package beep02.avenge

import java.io.File
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import akka.actor.Props
import beep02.avenge.destroy.FileKiller
import beep02.avenge.destroy.DirBrowser
import destroy.Messages._

object Shotgun {
  
  def system = ActorSystem("Revenge", ConfigFactory.load())

  def fire(bullets:(Option[File], Option[String], Option[String])): Unit = {
     val browser = system.actorOf(Props(new DirBrowser()), "browserActor")
     bullets match {
       case (Some(f), Some(s), None) =>  browser ! new StartDesrroy(bullets)
       case (None , None, Some(s)) =>  browser ! new StartRestore(bullets)
       case other => println("invalid input")
     }
     
  }

}