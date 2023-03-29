<%@ page import="com.oak.application.Server" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Home</title>
</head>
<body>
<ul>
    <li id="username">Username: <%=Server.getInstance().getServiceLayer().getCurrentUser().getUsername()%></li>
    <li id="credit">Credit: <%=Server.getInstance().getServiceLayer().getCurrentUser().getCredit()%></li>
    <li><a href="${pageContext.request.contextPath}/commodities">Commodities</a></li>
    <li><a href="${pageContext.request.contextPath}/buyList">Buy List</a></li>
    <li><a href="${pageContext.request.contextPath}/credit">Add Credit</a></li>
    <li><a href="${pageContext.request.contextPath}/logout">Log Out</a></li>
</ul>

</body>
</html>

