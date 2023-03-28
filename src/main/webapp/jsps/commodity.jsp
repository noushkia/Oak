<%@ page import="com.oak.application.Server" %>
<%@ page import="com.oak.domain.Commodity" %>
<%@ page import="com.oak.domain.Comment" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
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
    <span>username: <%=Server.getInstance().getServiceLayer().getCurrentUser().getUsername()%></span>
    <br>
    <%
        Integer commodityId = Integer.valueOf(request.getParameter("commodityId"));
        Commodity commodity = Server.getInstance().getServiceLayer().getCommodityService().getCommodityById(commodityId);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    %>
    <ul>
      <li id="id">Id: <%=commodity.getId()%></li>
      <li id="name">Name: <%=commodity.getName()%></li>
      <li id="providerName">Provider Name: <%=Server.getInstance().getServiceLayer().getProviderService().getProviderById(commodity.getProviderId()).getName()%></li>
      <li id="price">Price: <%=commodity.getPrice()%></li>
      <li id="categories">Categories: <%=String.valueOf(commodity.getCategories())%></li>
      <li id="rating">Rating: <%=commodity.getRating()%></li>
      <li id="inStock">In Stock: <%=commodity.getInStock()%></li>
    </ul>

    <label>Your Comment:</label>
    <form action="" method="post">
      <input type="text" name="comment" value="" />
      <input type="hidden" id="form_action" name="action" value="comment">
      <button type="submit">Add Comment</button>
    </form>
    <br>
    <form action="" method="POST">
      <label>Rate(between 1 and 10):</label>
      <input type="number" id="quantity" name="quantity" min="1" max="10">
      <input type="hidden" id="form_action" name="action" value="rate">
      <button type="submit">Rate</button>
    </form>
    <br>
    <form action="" method="POST">
    <input type="hidden" id="form_action" name="action" value="add">
      <button type="submit">Add to BuyList</button>
    </form>
    <br />
    <table>
      <caption><h2>Comments</h2></caption>
      <tr>
        <th>Email</th>
        <th>Comment</th>
        <th>Date</th>
        <th>Likes</th>
        <th>Dislikes</th>
      </tr>
      <% for (Comment comment : commodity.getUserComments()) { %>
      <tr>
        <td><%=comment.getUserEmail()%></td>
        <td><%=comment.getText()%></td>
        <td><%=dateFormat.format(comment.getDate())%></td>
        <td>
          <form action="" method="POST">
            <label for=""><%=comment.getVotes(1)%></label>
            <input type="hidden" id="form_action" name="action" value="vote">
            <input type="hidden" id="form_comment_id" name="comment_id" value=<%=comment.getId()%>>
            <input type="hidden" id="form_vote" name="vote" value="1">
            <button type="submit">Like</button>
          </form>
        </td>
        <td>
          <form action="" method="POST">
            <label for=""><%=comment.getVotes(-1)%></label>
            <input type="hidden" id="form_action" name="action" value="vote">
            <input type="hidden" id="form_comment_id" name="comment_id" value=<%=comment.getId()%>>
            <input type="hidden" id="form_vote" name="vote" value="-1">
            <button type="submit">Dislike</button>
          </form>
        </td>
      </tr>
      <% } %>
    </table>
    <br><br>
    <table>
      <caption><h2>Suggested Commodities</h2></caption>
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
        <% for (Commodity suggestedCommodity : Server.getInstance().getServiceLayer().getCommodityService().getSuggestedCommodities(commodityId)) { %>
        <tr>
            <td><%=suggestedCommodity.getId()%></td>
            <td><%=suggestedCommodity.getName()%></td>
            <td><%=Server.getInstance().getServiceLayer().getProviderService().getProviderById(suggestedCommodity.getProviderId()).getName()%></td>
            <td><%=suggestedCommodity.getPrice()%></td>
            <td><%=String.valueOf(suggestedCommodity.getCategories())%></td>
            <td><%=suggestedCommodity.getRating()%></td>
            <td><%=suggestedCommodity.getInStock()%></td>
            <td><a href="/commodities/<%=suggestedCommodity.getId()%>">Link</a></td>
        </tr>
        <% } %>
    </table>
  </body>
</html>
