package com.outtribe.dpost.storage
import controllers.Configuration
import com.googlecode.mapperdao.MapperDao
import com.googlecode.mapperdao.Query._
import java.util.Date
import com.googlecode.mapperdao.QueryConfig
import com.googlecode.mapperdao.jdbc.Transaction
import Transaction._
import com.googlecode.mapperdao.SurrogateLongId
import play.api._
import com.googlecode.mapperdao.QueryDao
import org.apache.commons.dbcp.BasicDataSourceFactory
import com.googlecode.mapperdao.utils.Setup
import javax.sql.DataSource


/**
 * Copyright (C) 2013 Peter Kovgan
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
 */
class DBStorage extends Storage{
     
  
      val DELIMITER                = "==="
      
      //DB message statuses  
      val STATUS_READY     = 0
      val STATUS_INPROCESS = 1
      val STATUS_SENT      = 2
      val STATUS_FAILED    = 3
      
      var oneTakeMax: Int = 0
      var tx : Transaction = null
      var messagesDao: DBMessageDao = null
      var queryDao : QueryDao = null
      
      private var dataSource: DataSource = null
     
      
      
      private var problem : Boolean = false
      
      override def isProblem:Boolean={
        problem
      }
      
      override def reset{
        
           try{
             
               problem = false
             
               dataSource = BasicDataSourceFactory.createDataSource(Configuration.getDbProperties)
               
               val entities   = List(PersistentMessageEntity)
               
               var (jdbc, mapperDaos, queryDaos, txManagers) = Setup.postGreSql(dataSource, entities)
             
               oneTakeMax = Configuration.common_messages_to_send_request_limit
	      
	           tx = Transaction.highest(txManagers)
	      
	           messagesDao = new DBMessageDao {
	                 val (mapperDao, queryDao, txManager) = (mapperDaos , queryDaos, txManagers)
	           }
	      
	           queryDao  = queryDaos
	           
	           problem = false
           }catch{
              case e:Exception => {
                  problem = true
                  Logger.error("reset storage failed",e)
              }
           }
      }
  
      override def storeMessage(template: String, body:Map[String, String], subj:Map[String, String], lang:String, toAddr: String):Boolean = {
            try{
	            val storableMessage = new PersistentMessageData(template, body.mkString(DELIMITER), subj.mkString(DELIMITER), lang, toAddr, STATUS_READY, new Date)
	            tx{()=>
	                
	                
			        val inserted = messagesDao.create(storableMessage)
			        problem = false
			        inserted
			        
	            }
            }catch {
                
                case e: com.googlecode.mapperdao.exceptions.PersistException => {
                    Logger.error("insert failed", e)
                    problem = true
                }
                case e: Exception => {
                    Logger.error("insert failed", e)
                    problem = true
                }
            }
		    true
	  }
	  
	  override def getMessagesToSend(limit:Int): List[MessageData]={
	         val pe = PersistentMessageEntity
	         var result = List[MessageData]()
	         try{
		         tx{()=>
			         val q  = select from pe where pe.status === STATUS_READY orderBy (PersistentMessageEntity.touchDate, desc)
		             val entityList = queryDao.query(QueryConfig.pagination(1, oneTakeMax),q)
		             for(d <- entityList) {
		                  val body = d.body
		                  val subj = d.subj
		                  
		                  val bodyMap = body.split(DELIMITER).map(_ split " -> ") collect { case Array(k, v) => (k, v) } toMap
		                  val subjMap = subj.split(DELIMITER).map(_ split " -> ") collect { case Array(k, v) => (k, v) } toMap
		                  
		                  val message = new MessageData(d.id, d.template, bodyMap, subjMap, d.lang,d.toAddr)
		                  result = message::result
		                  
		                  //change status
		                  d.status = STATUS_INPROCESS
		                  d.touchDate = new Date
		                  val updated = messagesDao.update(d)
		                  updated 
		                  
	                 }
			         
		         }
		         problem = false
	         }catch {
	            case e: com.googlecode.mapperdao.exceptions.PersistException => {
                    Logger.error("select/update failed",e)
                     problem = true
                }
                case e: Exception => {
                    Logger.error("select/update failed",e)
                     problem = true
                }
             }
             
	         Logger.debug("the DB storage contains "+result.size+ " messages...")
             result
	  }
	  
      
	  override def markSuccess(id: Long, sent: Boolean){
	     val pe = PersistentMessageEntity
	     try{
		     tx{
		        ()=>
		        val q  = select from pe where pe.id === id
		        val el = queryDao.querySingleResult(q)
		        val message = el.get
		        
		        if(sent)
		           message.status = STATUS_SENT
		        else
		           message.status = STATUS_FAILED
		           
		        message.touchDate = new Date
		        val updated = messagesDao.update(message)
		        problem = false
		        
		     }
	     }catch {
            case e: com.googlecode.mapperdao.exceptions.PersistException => {
                Logger.error("markSuccess failed",e)
                 problem = true
            }
            case e: Exception => {
                Logger.error("markSuccess failed",e)
                 problem = true
            }
         }
	  }
  

}


