package domain
{

	[Bindable]
	[RemoteClass(alias="com.example.StockQuote")]
	public class StockQuote{
		private var _name:String;
		private var _price:int;
		
		public function get name():String{
			return _name;
		}
		public function get price():int{
			return _price;
		}
		public function set name(name:String):void{
			_name = name;
		}
		public function set price(price:int):void{
			_price = price;
		}
	}
	
}
