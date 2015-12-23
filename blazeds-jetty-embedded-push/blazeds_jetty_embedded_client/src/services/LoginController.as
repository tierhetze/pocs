package services
{
	import mx.rpc.events.ResultEvent;
	import mx.rpc.remoting.RemoteObject;

	public class LoginController
	{
		
		private var loginService:RemoteObject = new RemoteObject();
		
		public function LoginController()
		{
			loginService.destination = "loginService";
		}
		
		
		public function login(userName:String, loginResult:Function):void{
			loginService.addEventListener(ResultEvent.RESULT, loginResult);
			loginService.login(userName);
		}
	}
}