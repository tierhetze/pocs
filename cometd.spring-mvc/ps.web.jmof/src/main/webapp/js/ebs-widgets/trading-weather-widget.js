(function($)
{

        $.fn.TradingWeatherWidget = function(cometd) {
	        
        	var pushController = new EbsPushController();
	    	
	    	var priceSubscribtion1 = null;
	    	var priceSubscribtion2 = null;
	    	var weatherSubscribtion= null;
	    	
	    	$('#weather_prices_joinButton').click(function() { pushController.join($('#weather_prices_username').val()); });
	    	$('#weather_prices_disconnectButton').click(function() { pushController.disconnect($('#weather_prices_username').val()); });
	    	
	    	
	    	
	    	function unsubscribe(nick){
	    	    cometd.unsubscribe(priceSubscribtion1);
	    	    cometd.unsubscribe(priceSubscribtion2);
	    	    cometd.unsubscribe(weatherSubscribtion);
	    	    $('#weather_prices_eurusd').empty();
	    	    $('#weather_prices_usdjpy').empty();
	    	    $('#weather').empty();
	        }
	    	
	    	
	    	
	    	function subscribe(nick){
	    	        //subscribe to get prices for the user
		    	    priceSubscribtion1 = cometd.subscribe(
		    	          '/user/' + nick+ '/price/eurusd', 
		    	          function (message){
		    	        	  var id    = message.id;
		    	        	  var bid   = message.data.bid;
		    	              var offer = message.data.offer;
		    	              var pair  = message.data.pair;
		    	              if(!!pair){
		    	                  $('#weather_prices_eurusd').empty();
		    	                  $('#weather_prices_eurusd').append('<div>[' + id + ']-- ' + pair + ':'+bid+'<>'+offer+'</div>');
		    	              }
		    	          }
		    	    );
		    	    
		    	  //subscribe to get prices for the user
		    	    priceSubscribtion2 = cometd.subscribe(
		    	          '/user/' + nick+ '/price/usdjpy', 
		    	          function (message){
		    	        	  var id    = message.id;
		    	        	  var bid   = message.data.bid;
		    	              var offer = message.data.offer;
		    	              var pair  = message.data.pair;
		    	              if(!!pair){
		    	                  $('#weather_prices_usdjpy').empty();
		    	                  $('#weather_prices_usdjpy').append('<div>[' + id + ']-- ' + pair + ':'+bid+'<>'+offer+'</div>');
		    	              }
		    	              
		    	          }
		    	    );
		    	    
		    	    weatherSubscribtion =cometd.subscribe(
		    	          '/user/' + nick+ '/weather', 
		    	          function (message){
		    	        	  
		    	        	  var town   = message.data.town;
		    	              var temperature = message.data.temperature;
		    	              var air  = message.data.air;
		    	              
		    	              if(!!temperature){
		    	                  $('#weather').empty();
		    	                  $('#weather').append('<div>' + town + ':<BR>'+temperature+'<BR>('+air+')</div>');
		    	              }
		    	              
		    	          }
		    	    );
		    	    
		    }
	    	
	    	
	    	/**
	    	 * 
	    	 * This one may be moved to a common location
	    	 * Keep here , because I want more independence for each widget
	    	 * 
	    	 */
	    	function EbsPushController()
	        {
	           
	           var _username;
	           
	        
	           this.join = function(username)
	           {
	            
		             _username = username;
		             
		             if (!_username)
		             {
		                alert('Please enter a username');
		                return;
		             }
		             
		             if (!_username || !_username.length) return;
	                 
	                 subscribe(_username);
		                
	            
	            };
	            
	            this.disconnect = function(username)
	            {
	            
		             _username = username;
		             
		             if (!_username)
		             {
		                alert('Please enter a username');
		                return;
		             }
		             
		             if (!_username || !_username.length) return;
	                 
	                 unsubscribe(_username);
		                
	            
	            };
	            
	        }//EbsPushController
			
			
			

	    };
		

        
    	
   
    	
    	
})(jQuery);