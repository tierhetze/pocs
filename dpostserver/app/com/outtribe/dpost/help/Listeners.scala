package com.outtribe.dpost.help

import java.net.NetworkInterface

import play.api.Logger

object Listeners {

   
    def streamToken():String ={
        var out:String = ""
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    val iface: NetworkInterface = interfaces.nextElement()
                    val hardware = iface.getHardwareAddress()
                    if (hardware != null && hardware.length == 6 && hardware(1) !=  0xff) {
                        var oneCard:String = bytes2hex(hardware,Option("-"))
                        out+=oneCard+"-"
                    }
                }
            }
        }
        catch {
          case e: Exception=>{
             Logger.error("error generating stream tocken", e)
          }
        }
        if(out.endsWith("-"))
          out = out.substring(0,out.length()-1)
        out
    }
    
     def bytes2hex(bytes: Array[Byte], sep: Option[String] = None): String = {
          sep match {
                 case None => bytes.map("%02x".format(_)).mkString
                 case _ => bytes.map("%02x".format(_)).mkString(sep.get)
          }
     }
  
  
}