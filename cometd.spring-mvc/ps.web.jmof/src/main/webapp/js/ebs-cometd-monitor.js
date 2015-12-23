(function($)
{

        $.fn.EbsMoniorWidget = function(cometd, subscribedArray) {
	    
        	
        	           cometd.addListener('/meta/subscribe', function(message)
        		       {
        	        	   
        	        	   subscribedArray.push(message);
        	        	   
        	        	   $('#subscribe_table').html("");
        	        	   
        	        	   
        	        	   var table = $('<table></table>').addClass('tbl');
        	        	   
        	        	   for ( var i = subscribedArray.length-1; i >= 0; i = i -1 ) {
        	        		   
        	        		    if(subscribedArray.length -1 - i < 10){
        	        		    	
	        	        		    var chName     =  subscribedArray[i].subscription;
	        	        		    var chErr      =  subscribedArray[i].error;
	        	        		    var messId     =  subscribedArray[i].id;
	        	        		    var chSuccess  =  subscribedArray[i].successful;
	        	        		    var ee = (typeof chErr === "undefined"?'':',err:'+chErr);
	        	        		    var row = $('<tr></tr>').addClass('tbl_rwa').text('messId='+messId + '>>>'+ chName + '(success:'+ chSuccess +')'+ee  );
	        	        		    table.append(row);
	        	        		    
        	        		    }
        	        		    
        	        	   }
        	        	   
        	        	  
                           $('#subscribe_table').append(table);
        	        	   
        	        	
        		       });
        	
        	           
        	           
        	           
        	           
        	           
           
        };
		

})(jQuery);