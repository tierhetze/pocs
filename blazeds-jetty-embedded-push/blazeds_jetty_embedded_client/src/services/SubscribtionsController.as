package services
{
	import flash.utils.Dictionary;
	
	import mx.messaging.Consumer;
	import mx.messaging.events.MessageEvent;
	import mx.messaging.events.MessageFaultEvent;
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.RemoteObject;

	public class SubscribtionsController
	{
		
		private var subscribtionService:RemoteObject = new RemoteObject();
		private var dictObservers:Dictionary = new Dictionary(); //key: userId.type, value: consumer
		
		public function SubscribtionsController()
		{
			subscribtionService.destination = "subscribtionService";
			subscribtionService.addEventListener(ResultEvent.RESULT, subscribeResult);
			
		}
		
		
		private function subscribeResult(e:ResultEvent):void{
			//TODO: how to process subscribe failure
			//somehow need report that to user
		}
		
		
		public function subscribe(userId:Number, type:String, messageHandler:Function, faultHandler:Function):void
		{
			var consumer:Consumer = new Consumer();
			consumer.destination = "PushDestination";
			consumer.subtopic="clients.client"+userId+"."+type;
			consumer.addEventListener (MessageEvent.MESSAGE, messageHandler);
			consumer.addEventListener (MessageFaultEvent.FAULT, faultHandler);
			consumer.subscribe();//start listening
			subscribtionService.subscribe(userId,type);//activate service
			dictObservers[userId+"."+type]=consumer;
		}
		
		
		public function unsubscribe(userId:Number, type:String):void
		{
			
			var consumer:Consumer =  dictObservers[userId+"."+type];
			if(consumer!=null){
				consumer.unsubscribe();//stop listening
				subscribtionService.unsubscribe(userId,type);//passivate service
				delete dictObservers[userId+"."+type];
			}
			
		}
		
	}
}