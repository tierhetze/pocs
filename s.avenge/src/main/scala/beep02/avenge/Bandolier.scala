package beep02.avenge

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import scala.io.StdIn
import java.util.UUID
import java.util.Random

object Bandolier {
  
  val validkeypattern = "(^[a-zA-Z0-9_]{6,100}$)".r
   
   
  
  def load(): (Option[File], Option[String], Option[String])={
    
    println()
    println("If you want create problems, type 1 followed by <Enter>(no worries, you can still exit later)")
    println("If you want restore files, type 2 followed by <Enter>")
    println()
    
    val choiceRd = StdIn.readInt
    
    choiceRd match{
      case 2 => goodPath
      case 1 => badPath
      case other => {
         println("You have chosen an unsupported option \'"+other+"\' we stop here, if you are drunk, get sober and try another time")
         System.exit(0)
         null
      }
    }
    
  }
   
  def goodPath(): (Option[File], Option[String], Option[String])={
     println("Hello, they got you! Or you just tested the thing and want your stuff back..")
     println("Enter the path to the avenge.txt file(you must have it to restore all files you have changed) and press <Enter>" )
     val avengeFile = StdIn.readLine
     (None, None, Option(avengeFile))
  }
    
  def badPath(): (Option[File], Option[String], Option[String])={
    val theKey = generate24Key
   
    println("automatically generated sec key = "+theKey)
    
    println("Now you need to choose the target directory on your disk\n")
    
    
    println("Copy and paste the path to the directory..press <Enter>\n")
    val thePath = StdIn.readLine()
    if(Files.isDirectory(Paths.get(thePath))){
         println("OK, You are up to compromise everything you have in this directory:\n"+thePath)
         println("As output you will get the file (avenge.txt), that might help you to rollback the changes you done\n(however, I can give you no guarantee of that - shit happens!)")
    }else{
         println("You can not be trusted, your input is not a directory, are you drunk? Exiting...")
         System.exit(0)
    }
    println("")
    println("")
    println("\nIf you take complete responsibility for the following type 1 <Enter>, in this case you can not blame/sue/bother the author")
    println("")
    println("\nIf you hesitate type 2 <Enter>, this is the last chance to quit!\n")
    println("")
    println("")
    println("Note, the change in the directory "+thePath+" may be fatal and not restorable!")
    println("")
    
    
    
    val choice2 = StdIn.readInt
    val response2 = choice2 match {
      case 1        => "Oh, I see you are serious, go on!"
      case 2        => "Fuck you! You are pussy!"
      case anyother => "Are you an idiot or some fucking putin supporter? You were asked to choose 1 or 2! Fuck your " + anyother + " !"
    }
    println("\n"+response2 + "\n")
    if (choice2 != 1) {
      println("\nExiting...Go to hell! You are pussy!\n")
      System.exit(0)
    }
    
    (Option(new File(thePath)),Option(theKey), None)
  } 
  
  def generate24Key():String={
       var key = ""
       val r = new Random
       for ( x <- 1 to 24 ) {
          key = key + (r.nextInt(26) + 'a').toChar  
       }
       key
   }

}