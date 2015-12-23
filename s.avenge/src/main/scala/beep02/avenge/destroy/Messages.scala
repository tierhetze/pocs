package beep02.avenge.destroy

import java.io.File
import scala.collection.mutable.Map
import akka.actor.ActorRef

object Messages {
  
  case class StartDesrroy (bullets:(Option[File], Option[String], Option[String]))
  case class StartRestore (bullets:(Option[File], Option[String], Option[String]))
  case class DestroyReport(path:String, status:Int, length:Int)
  case class RestoreReport(path:String, status:Int)
  case class Task (kill:Int, f: File, s: String, a:ActorRef, length:Int)
  case class Track(map:Map[String, (Int, Int)], key: Option[String], dir:Option[File])
  case class Stop(key: Option[String], dir:Option[File])
  object Continue

}