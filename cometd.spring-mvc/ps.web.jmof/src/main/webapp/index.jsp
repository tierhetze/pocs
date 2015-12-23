<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
    String redirectURL = request.getContextPath()+ "/app/login";
    response.sendRedirect(redirectURL);
%>

