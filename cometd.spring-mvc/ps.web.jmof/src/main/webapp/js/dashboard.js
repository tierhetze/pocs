(function($)
{

	/**
	 * See to have idea about js client configuration
	 * here: http://cometd.org/node/49
	 * here http://cometd.org/documentation/2.x/cometd-javascript/configuration
	 * Note, client attempts to reconnect server and does it with increased period: 1000, 2000, 3000 msc...etc
	 * When client reaches 60000 msc interval it stops to attempt to connect the server
	 * It's all configurable
	 */
	
	//for monitoring only, you can skip it at all 
	var subscribedArray = [];
	 
    
    //get new client comet-d instance
    var cometd = new $.Cometd('dashboard');
    
    //define server URL, that supports, this instance
    var cometdURL = $(location).attr('protocol') + "//" + $(location).attr('host') + $(config).attr('contextPath') +"/cometd";
    
    //configure the client comet-d instance
    cometd.configure({
        url: cometdURL,
        maxBackoff: 3000,
        maxConnections:6
    });
    
    //enable also websocket (long polling is enabled by default)
    cometd.websocketEnabled = true;
    
    //set JS console level of the log
    cometd.setLogLevel('debug');
    
    //on document ready
    $(document).ready(function()
    {
    	
    	//ask for connection from the comet-d
    	cometd.handshake();
    	
    	//-----------------------------------------------------------------------------------///
    	//
    	//             start attach widgets to this instance of the comet-d
    	//             each widget will use the same cometd connection, 
    	//             that we create in this dashboard
    	//
    	//-----------------------------------------------------------------------------------///
    	
    	//prices widget
    	$(document).EbsPricesWidget(cometd);
        
        //deals widget
        $(document).EbsDealsWidget(cometd);
        
        //trading weather (combined) widget
        $(document).TradingWeatherWidget(cometd);
        
        //just monitoring listeners separated in another JS: ebs-cometd-monitor.js
        $(document).EbsMoniorWidget(cometd, subscribedArray);
        
    	
    });

})(jQuery);
