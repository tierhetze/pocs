<%@ Page Language="vb" AutoEventWireup="false" CodeBehind="WebForm1.aspx.vb" Inherits="web.dpost.csharp.client.test.WebForm1" %>
<%@ Import Namespace="dpost.csharp.client" %>
<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml">
<head runat="server">

<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title></title>
</head>

<body>
    <form id="form1" runat="server">
    <div>
        <%
            
            'here I skip imaginary DB connection.
            'from DB I may get parameters to send in e-mail
            'In rest of the code, I populate the maps by these parameters
            
            'init subject attributes map
            Dim subjMap As Dictionary(Of String, String) = New Dictionary(Of String, String) From
            {{"username", "Peter"}}
        
            'init body attributes map
            Dim bodyMap As Dictionary(Of String, String) = New Dictionary(Of String, String) From
            {{"username", "Peter"}, {"product", "Samsung Note II"}, {"salesemail", "peter@peter.com"}}
            
            'Here I ensure that DPost Client is singleton in application scope
            'I see concurrency issue possibility here in such Lazy initialization
            'So:
            'I recommend insted: init this Application attribute ("dpostClient") during app init (make it not Lazy and explicit on application start) 
            'then only get dpostClient form Application context when required and by that avoid concurrency issues completelly
            Dim dpostClient As DpostClient = System.Web.HttpContext.Current.Application.Get("dpostClient")
            If (dpostClient Is Nothing) Then
                dpostClient = New DpostClient("localhost", 9000) 'Dpost server host and port
                System.Web.HttpContext.Current.Application.Add("dpostClient",dpostClient)
            End If
                
            'Call Dpost client Library to send the mail
            'Parameters:
            'subject attributes map
            'body attributes map
            'recipient address
            'template name
            'template language
            'return value: SUCCESS - means e-mail submit succeed, FAILURE - means failure during submit
            Dim result As String = dpostClient.sendMail(subjMap, bodyMap, "yourclient@gmail.com", "welcome", "en-US")
            Response.Write("The result is: " & result)
       %>
    </div>
    </form>
</body>
</html>
