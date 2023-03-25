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
    <li><a href="/commodities">Commodities</a></li>
    <li><a href="/buyList">Buy List</a></li>
    <li><a href="/credit">Add Credit</a></li>
    <li><a href="/logout">Log Out</a></li>
</ul>

</body>
</html>

