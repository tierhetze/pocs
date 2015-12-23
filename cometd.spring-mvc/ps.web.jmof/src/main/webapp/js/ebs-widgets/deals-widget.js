(function($)
{

        $.fn.EbsDealsWidget = function(cometd) {
	    
        	
            var pushController = new EbsPushController();
	    	
	    	var dealsSubscribtion1 = null;
	    	var dealsSubscribtion2 = null;
	    	
	    	$('#deals_joinButton').click(function() { pushController.join($('#deals_username').val()); });
	    	$('#deals_disconnectButton').click(function() { pushController.disconnect($('#deals_username').val()); });
	    	
	    	
	    	
	    	function unsubscribe(nick){
	    	    cometd.unsubscribe(dealsSubscribtion1);
	    	    cometd.unsubscribe(dealsSubscribtion2);
	    	    $('#deals_eurusd').empty();
	    	    $('#deals_usdjpy').empty();
	        }
	    	
	    	function subscribe(nick){
	    	        //subscribe to get prices for the user
		    	    dealsSubscribtion1 = cometd.subscribe(
		    	          '/user/' + nick+ '/deals/eurusd', 
		    	          function (message){
		    	        	  var data = message.data;
		    	        	  if(!!data){
		    	        		  var deals = data.deals;
		    	        		  if(!!deals){
		    	        			  var deal1=deals[0];
		    	        			  var deal2=deals[1];
		    	        			  var deal3=deals[2];
		    	        			  
		    	        			  var str1 = deal1.pair+': '+deal1.amount;
		    	        			  var str2 = deal2.pair+': '+deal2.amount;
    	        			          var str3 = deal3.pair+': '+deal3.amount;
		    	        			  
			    	                  $('#deals_eurusd').empty();
			    	                  $('#deals_eurusd').append('<div>' + str1 +'<BR>'+ str2 + '<BR>' + str3  +'</div>');
		    	        		  }
		    	              }
		    	          }
		    	    );
		    	    
		    	  //subscribe to get prices for the user
		    	    dealsSubscribtion2 = cometd.subscribe(
		    	          '/user/' + nick+ '/deals/usdjpy', 
		    	          function (message){
		    	        	  var data = message.data;
		    	        	  if(!!data){
		    	        		  var deals = data.deals;
		    	        		  if(!!deals){
		    	        			  var deal1=deals[0];
		    	        			  var deal2=deals[1];
		    	        			  var deal3=deals[2];
		    	        			  
		    	        			  var str1 = deal1.pair+': '+deal1.amount;
		    	        			  var str2 = deal2.pair+': '+deal2.amount;
    	        			          var str3 = deal3.pair+': '+deal3.amount;
		    	        			  
			    	                  $('#deals_usdjpy').empty();
			    	                  $('#deals_usdjpy').append('<div>' + str1 +'<BR>'+ str2 + '<BR>' + str3  +'</div>');
		    	        		  }
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
	            
	        }//end EbsPushController
        };
		

})(jQuery);