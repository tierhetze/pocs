package beep02.avenge

import scala.io.StdIn

/**
 *
 * @author Crazy Jerk
 *
 * The utility encodes all small (less than 10 Mb) and not executable files
 *  
 * in specified directory recursively
 * 
 * The output file, called avenge.txt has all information to rollback the changes(restore encoded files)
 * 
 * You better not to miss avenge.txt file!
 * 
 * Good luck!
 * 
 */

object Avenge {

 

  def main(args: Array[String]): Unit = {
      val bullets = Bandolier.load
      Shotgun fire bullets
  }

}