<%@ page import="com.oak.application.Server" %>
<%@ page import="com.oak.domain.Commodity" %>
<%@ page import="com.oak.domain.BuyList" %>
<%@ page import="com.oak.domain.User" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>User</title>
    <style>
        li {
            padding: 5px
        }

        table {
            width: 100%;
            text-align: center;
        }
    </style>
</head>
<body>
<a href="${pageContext.request.contextPath}/">Home</a>
<%
    User currentUser = Server.getInstance().getServiceLayer().getCurrentUser();
    BuyList buyList = currentUser.getBuyList();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
%>
<ul>
    <li id="username">Username: <%=currentUser.getUsername()%>
    </li>
    <li id="email">Email: <%=currentUser.getEmail()%>
    </li>
    <li id="birthDate">Birth Date: <%=dateFormat.format(currentUser.getBirthDate())%>
    </li>
    <li id="address"><%=currentUser.getAddress()%>
    </li>
    <li id="credit">Credit: <%=currentUser.getCredit()%>
    </li>
    <li>Current Buy List Total Price: <%=buyList.calculateTotalCredit()%> </li>
    <li>Current Buy List Discount Price: <%=buyList.calculateDiscountCredit()%> </li>
    <li>Current Buy List Final Price: <%=buyList.calculateFinalCredit()%> </li>
    <li>
        <a href="${pageContext.request.contextPath}/credit">Add Credit</a>
    </li>
    <li>
        <form action="" method="POST">
            <label>Submit & Pay</label>
            <button type="submit" name="action" value="pay">Pay</button>
        </form>
    </li>
</ul>
<table>
    <caption>
        <h2>Buy List</h2>
    </caption>
    <tr>
        <th>Id</th>
        <th>Name</th>
        <th>Provider Name</th>
        <th>Price</th>
        <th>Categories</th>
        <th>Rating</th>
        <th>In Stock</th>
        <th>Links</th>
        <th>Remove From Buy List</th>
    </tr>
    <% for (Commodity commodity : currentUser.getBuyListCommodities()) { %>
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
        <td>
            <form action="" method="POST">
                <input type="hidden" id="form_commodity_id" name="commodity_id" value=<%=commodity.getId()%>>
                <button type="submit" name="action" value="remove">Remove</button>
            </form>
        </td>
    </tr>
    <% } %>
</table>

<label>Enter Your Discount Code:</label>
<form action="" method="post">
    <label>
        <input type="text" name="code" value=""/>
    </label>
    <button type="submit" name="action" value="discount">Add Discount</button>
</form>
</body>
</html>