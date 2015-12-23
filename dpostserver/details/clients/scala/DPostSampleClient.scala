import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

import scala.util.parsing.json.JSONObject


/**
 * @author peterk
 * 
 * You are allowed to use this client to the best of your abilities.
 * 
 * Please extend and/or improve it.
 * 
 * DPOST client is better to be singleton
 * 
 * What we do here:
 * 
 * -get parameters of the e-mail from imaginary DB (not written here)
 * -populate maps
 * -create JSON and send it to DPOST
 * 
 * For more information about DPOST clients, please read clients.pdf
 * 
 * 
 * 
 */
object DPostSampleClient extends App {

  override def main(args: Array[String]) {
          //I assume here you collect variables (name->value) from the DB of your site
          //then you populate maps
          //then you send
          val subjMap = Map[String, String]("username" -> "Peter")
          val bodyMap = Map[String, String]("username" -> "Peter", "product" -> "Samsung Note II", "salesemail" -> "peter@peter.com")
          DPostSampleClient.scalaSubmitMessage(subjMap, bodyMap,"yourclient@gmail.com","welcome","de-DE")
  }
  
  def scalaSubmitMessage(subjMap:Map[String, String],bodyMap:Map[String, String],toMail:String,template:String,lang:String): Unit = {
    val jsonMap = Map[String, Any]("toMail" -> toMail, "template" -> template, "lang" -> lang, "subjMap" -> new JSONObject(subjMap), "bodyMap" -> new JSONObject(bodyMap))
    val obj: JSONObject = new JSONObject(jsonMap)

    val json: String = obj.toString

    println("input before we send:"+ json)
    //TODO:
    //host and port to externalize! make configurable!
    //but /dpost URL part is obligatory!
    val url: URL = new URL("http://127.0.0.1:9000/dpost")
    val con = url.openConnection.asInstanceOf[HttpURLConnection]

    try {
      con.setRequestMethod("POST")
      con.setRequestProperty("Content-Type", "application/json");
      con.setRequestProperty("Accept", "application/json");
      con.setRequestProperty("Content-Length", Integer.toString(json.length()));
      
      con.setUseCaches(false)
      con.setDoInput(true)
      con.setDoOutput(true)
      //Send request
      
      var os = new OutputStreamWriter(con.getOutputStream(), "UTF-8")
      os.write(json.toString())
      os.flush()
      os.close()
     
      con.connect()

      if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
          println("try send failed, code="+con.getResponseCode())
      }else{
	      //Get Response	
	      val is = con.getInputStream
	      val rd = new BufferedReader(new InputStreamReader(is))
	      var line: String = null
	      val response = new StringBuffer
	      val iterator = Iterator.continually(rd.readLine()).takeWhile(_ != null)
	      while (iterator.hasNext) {
	        response.append(iterator.next)
	        response.append('\r')
	      }
	      rd.close();
	      
	      val result = response.toString
	      println("result from DPOST:"+result)
	      
	      if(result.trim().equals("SUCCESS")){
	          println("Successfully submitted to DPOST!")
	      }
      }
    } catch {
      case e: Exception => println(e.getStackTraceString)
    } finally {
      if (con != null) {
        con.disconnect
      }
    }

  }
}
