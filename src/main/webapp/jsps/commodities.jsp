<%@ page import="com.oak.application.Server" %>
<%@ page import="com.oak.domain.Commodity" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Commodities</title>
    <style>
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
<br><br>
<form action="" method="POST">
    <label>
        Search:
        <input type="text" name="search" value="">
    </label>
    <button type="submit" name="action" value="search_by_category">Search By Category</button>
    <button type="submit" name="action" value="search_by_name">Search By Name</button>
    <button type="submit" name="action" value="clear">Clear Search</button>
</form>
<br><br>
<form action="" method="POST">
    <label>Sort By:</label>
    <button type="submit" name="action" value="sort_by_rating">Rating</button>
    <button type="submit" name="action" value="sort_by_price">Price</button>
</form>
<br><br>
<table>
    <tr>
        <th>Id</th>
        <th>Name</th>
        <th>Provider Name</th>
        <th>Price</th>
        <th>Categories</th>
        <th>Rating</th>
        <th>In Stock</th>
        <th>Links</th>
    </tr>
    <% for (Commodity commodity : Server.getInstance().getServiceLayer().getCommodityService().getCommoditiesList()) { %>
    <tr>
        <td><%=commodity.getId()%>
        </td>
        <td><%=commodity.getName()%>
        </td>
        <td><%=Server.getInstance().getServiceLayer().getProviderService().getProviderById(commodity.getProviderId()).getName()%>
        </td>
        <td><%=commodity.getPrice()%>
        </td>
        <td><%=String.valueOf(commodity.getCategories())%>
        </td>
        <td><%=commodity.getRating()%>
        </td>
        <td><%=commodity.getInStock()%>
        </td>
        <td><a href="/commodities/<%=commodity.getId()%>">Link</a></td>
    </tr>
    <% } %>
</table>
</body>
</html>