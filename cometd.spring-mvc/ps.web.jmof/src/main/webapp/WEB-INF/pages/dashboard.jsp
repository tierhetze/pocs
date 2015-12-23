<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>

<head>
    
    <title>Japan Banker Dashboard</title>
    
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
    
    
    <!-- connection JS - comet-d connection created here -->
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/dashboard.js" id="dashboard"></script>
    
    <!--Start Widgets JSs -->
    <!-- Price widget -->
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/ebs-widgets/prices-widget.js" id="prices-widgets"></script>
    <!-- Deals widget -->
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/ebs-widgets/deals-widget.js" id="deals-widgets"></script>
    <!-- Trading-weather widget (combined example)-->
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/ebs-widgets/trading-weather-widget.js" id="trading-weather-widgets"></script>
    <!--End Widgets JSs -->
    
    <!-- Monitoring widget(for developers) -->
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/ebs-cometd-monitor.js" id="ebs-cometd-monitor"></script>
    
</head>

<body>

<table>
<tr>

<!--monitoring-->
<td valign="top" width="500" height="100%">
<%@include file="ebs-monitor.jsp" %>
</td>

<td valign="top" width="30" height="100%">&nbsp;</td>

<td width="*">

<!--main dashboard-->
<h1>Japan Banker Dashboard</h1>

<h2>${user.username} (${user.ip}), Konnichiwa! <img border="0" width="100" src="${pageContext.request.contextPath}/images/japan-guy.jpg"> </h2>	

<h3><a href="<c:url value="/app/j_spring_security_logout" />">Logout</a></h3>


<%@include file="ebs-widgets/prices-widget.jsp" %>
<BR>
<BR>
<%@include file="ebs-widgets/trading-weather-widget.jsp" %>
<BR>
<BR>
<%@include file="ebs-widgets/deals-widget.jsp" %>


</td>
</tr>
</table>

</body>

</html>
