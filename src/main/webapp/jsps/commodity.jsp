<%@ page import="com.oak.application.Server" %>
<%@ page import="com.oak.domain.Commodity" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <title>Commodity</title>
    <style>
        li {
            padding: 5px;
        }

        table {
            width: 100%;
            text-align: center;
        }
    </style>
</head>
<body>
<a href="${pageContext.request.contextPath}/">Home</a>
<p id="username">username: <%=Server.getInstance().getServiceLayer().getCurrentUser().getUsername()%>
</p>
<br>
<%
    Integer commodityId = Integer.valueOf(request.getParameter("commodityId"));
    Commodity commodity = Server.getInstance().getServiceLayer().getCommodityService().getCommodityById(commodityId);
%>
<ul>
    <li>Id: <%=commodity.getId()%>
    </li>
    <li>Name: <%=commodity.getName()%>
    </li>
    <li>Provider: <%=Server.getInstance().getServiceLayer().getProviderService().getProviderById(commodity.getProviderId()).getName()%>
    </li>
    <li>Price: <%=commodity.getPrice()%>
    </li>
    <li>Categories: <%=String.valueOf(commodity.getCategories())%>
    </li>
    <li>Rating: <%=commodity.getRating()%>
    </li>
    <li>Stock: <%=commodity.getInStock()%>
    </li>
</ul>

<label>Add Your Comment:</label>
<form action="comment/<%=commodity.getId()%>" method="POST">
    <input type="text" name="comment" value=""/>
    <button type="submit">Submit</button>
</form>
<br>
<form action="rate/<%=commodity.getId()%>" method="POST">
    <label>
        Rate(between 1 and 10):
        <input type="number" id="score" name="score" min="1" max="10">
    </label>
    <button type="submit">Rate</button>
</form>
<br>
<form action="add/<%=commodity.getId()%>" method="POST">
    <button type="submit">Add to BuyList</button>
</form>
<br/>
<table>
    <caption><h2>Comments</h2></caption>
    <tr>
        <th>username</th>
        <th>comment</th>
        <th>date</th>
        <th>likes</th>
        <th>dislikes</th>
    </tr>
    <%--    Todo: get comments--%>
</table>
<br><br>
<table>
    <caption><h2>Suggested Commodities</h2></caption>
    <%--    Todo: get suggested comms--%>
</table>
</body>
</html>
