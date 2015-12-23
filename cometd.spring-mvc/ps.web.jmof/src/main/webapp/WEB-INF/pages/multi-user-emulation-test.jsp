<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>

<head>
    
    <title>Multi user emulation test</title>
    
    <!-- CSS file for widgets design -->
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/widgets.css">
    
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/org/cometd/cometd-namespace.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/org/cometd/cometd-header.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/org/cometd/cometd-amd.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/org/cometd/cometd-json.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/org/cometd/Transport.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/org/cometd/TransportRegistry.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/org/cometd/LongPollingTransport.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/org/cometd/WebSocketTransport.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/org/cometd/RequestTransport.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/org/cometd/CallbackPollingTransport.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/org/cometd/Utils.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/org/cometd/Cometd.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/org/cometd/TimeStampExtension.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/org/cometd/TimeSyncExtension.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/org/cometd/AckExtension.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/org/cometd/ReloadExtension.js"></script>
    
    
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery-1.9.1.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery.cometd.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery.cometd-ack.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery.cookie.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery.cometd-reload.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery.cometd-timestamp.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery.cometd-timesync.js"></script>
    
    <script type="text/javascript">
        var config = {
            contextPath: '${pageContext.request.contextPath}'
        };
    </script>
    
</head>
<body>
	<table>
	<tr>
		<td valign="top" width="500" height="100%">
		
		
		<!-- jsp start -->
		<% 
		int from = Integer.valueOf((String)request.getAttribute("testusersfrom"));
		int to   = Integer.valueOf((String)request.getAttribute("testusersto"));
		%>
		<h1>Multi-user emulation test, users: <%=from%>:<%=to%></h1>
		<table>
		<%
		
		for(int i=from; i < to; i++){ %>
		<tr>
		<td>
		<div id="price_widget<%=i%>">
            <div id="widget_title<%=i%>">Prices Widget(<%=i%>)</div>
            <br>
		    <div id="eurusd<%=i%>"></div>
		    <br>
		    <div id="usdjpy<%=i%>"></div>
		</div>
		</td>
		<tr>
		<%
		}%>
		</table>
		<!-- jsp end  -->
		
		
		<script type="text/javascript">
		//define server URL, that supports, this instance
	    var cometdURL = $(location).attr('protocol') + "//" + $(location).attr('host') + $(config).attr('contextPath') +"/cometd";
	    var myComets = new Array();
	    
	    
		     (function($)
				{
				
	        	    for (var i=<%=from%>; i < <%=to%>; i++){
	        	 
						//get new client comet-d instance
					    var cometd = new $.Cometd('dashboard'+i);
					    
					    myComets[i] = cometd;
					    
					    
					    //configure the client comet-d instance
					    cometd.configure({
					        url: cometdURL,
					        maxBackoff: 3000,
					        maxConnections:2
					    });
					    
					    //enable also websocket (long polling is enabled by default)
					    cometd.websocketEnabled = true;
					    
					    //set JS console level of the log
					    cometd.setLogLevel('error');
					    
	        	    }
				    
				    //on document ready
				    $(document).ready(function()
				    {
				    	
				    	function subscribe(nick, i, type, symbol){
			    	        //subscribe to get prices for the user
				    	    myComets[i].subscribe(
				    	          '/user/' + nick+ '/' + type, 
				    	          function (message){
				    	        	  var id    = message.id;
				    	        	  var bid   = message.data.bid;
				    	              var offer = message.data.offer;
				    	              var pair  = message.data.pair;
				    	              var sent  = message.data.sent;
				    	              var p1 = message.data.par1;
				    	              var p2 = message.data.par2;
				    	              var p3 = message.data.par3;
				    	              var p4 = message.data.par4;
				    	              var p5 = message.data.par5;
				    	              var p6 = message.data.par6;
				    	              var p7 = message.data.par7;
				    	              var p8 = message.data.par8;
				    	              var p9 = message.data.par9;
				    	              var p10 = message.data.par10;
				    	              var p11 = message.data.par11;
				    	              var p12 = message.data.par12;
				    	              var p13 = message.data.par13;
				    	              var p14 = message.data.par14;
				    	              var p15 = message.data.par15;
				    	              var p16 = message.data.par16;
				    	              var p17 = message.data.par17;
				    	              var p18 = message.data.par18;
				    	              var p19 = message.data.par19;
				    	              var p20 = message.data.par20;
				    	              var p21 = message.data.par21;
				    	              var p22 = message.data.par22;
				    	              var p23 = message.data.par23;
				    	              var p24 = message.data.par24;
				    	              var p25 = message.data.par25;
				    	              var p26 = message.data.par26;
				    	              var p27 = message.data.par27;
				    	              var p28 = message.data.par28;
				    	              var p29 = message.data.par29;
				    	              var p30 = message.data.par30;
				    	              var p31 = message.data.par31;
				    	              var p32 = message.data.par32;
				    	              var p33 = message.data.par33;
				    	              var p34 = message.data.par34;
				    	              var p35 = message.data.par35;
				    	             
				    	              
				    	              
				    	              var mseconds = new Date().getTime();
				    	              var diff = mseconds-sent;
				    	              
				    	              if(!!pair){
				    	            	  try{
					    	                  $('#' + symbol + i).empty();
					    	                  $('#' + symbol + i).append('<div>[' + id + ']-- ' + pair + ':'+bid+'<>'+offer+ ',t='+ diff + ' ' + p1 + ' ' + p2 +' '+  p3+' ' + p4 + ' ' + p5 +'  </div>' );
				    	            	  }catch(error){
				    	            		  
				    	            	  }
				    	              }
				    	          }
				    	    );
				        }
				    	
				    	function EbsPushController()
				        {
				           
				            this.join = function(username, i, type, symbol)
				            {
				            
					            subscribe(username, i, type, symbol);
					                
				            
				            };
				        }//EbsPushController
				    	
				    	
				    	
				        
                        
                        
                        
				        //handshake all users
				        setTimeout(function handshakes(){
					    	for (var i=<%=from%>; i < <%=to%>; i++){
					    		//ask for connection from the comet-d
						    	myComets[i].handshake();
						    }
					    }, 2000);
				        //subscribe to eur usd
				        setTimeout(function sub1(){
					    	for (var i=<%=from%>; i < <%=to%>; i++){
				        		var pushController = new EbsPushController();
						    	pushController.join('us'+i, i, 'price/eurusd', 'eurusd');
						    }
				        }, 6000);
				        //subscribe to usd jpy
				        setTimeout(function sub2(){
					    	for (var i=<%=from%>; i < <%=to%>; i++){
					    		var pushController = new EbsPushController();
						    	pushController.join('us'+i, i, 'price/usdjpy', 'usdjpy');
						    }
				        }, 12000);
				    	
				    	
				        
				    });//end $(document).ready(function()
				    		
	        	   
				
				})(jQuery);
	         
		     
		    </script>
		</td>
	</tr>
	</table>
</body>

</html>
