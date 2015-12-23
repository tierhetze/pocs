package beep02.avenge.destroy

import akka.actor.Actor
import Messages._
import akka.event.LoggingReceive


object Tracking {
   def track(m:Track):Stop={
      while(hasIncomplete(m.map)){
           Thread.sleep(100);
           println("tracking...")
      }
      new Stop(m.key, m.dir)
   }
   
   def hasIncomplete(map: scala.collection.mutable.Map[String, (Int, Int)]):Boolean={
        val list = map.valuesIterator.toList
        for(e<-list){
          if(e._1==(-1)){
            return true
          }
        }
        false
   }
}

class Tracking extends Actor{
    def receive = LoggingReceive{
      case m:Track => sender ! Tracking.track(m)
      case other => println("invalid message")
   }
}