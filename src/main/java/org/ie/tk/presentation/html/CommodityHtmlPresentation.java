package org.ie.tk.presentation.html;

import io.javalin.http.Handler;

import org.ie.tk.application.service.ServiceLayer;
import org.ie.tk.domain.Comment;
import org.ie.tk.domain.Commodity;

import org.ie.tk.exception.Commodity.CommodityNotFound;
import org.ie.tk.exception.Commodity.InvalidRating;
import org.ie.tk.exception.User.UserNotFound;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommodityHtmlPresentation extends HtmlPresentation {
    public static String marshalCommodityObject(Commodity commodity) throws IOException {
        File input = new File(COMMODITY_TEMPLATE_PATH);
        Document doc = Jsoup.parse(input, "UTF-8");
        String htmlString = doc.html();
        htmlString = marshalCommodityEntry(htmlString, commodity);

        String commentTableRow = """
                <tr>
                    <td>$username</td>
                    <td>$comment</td>
                    <td>$date</td>
                    <td>
                        <form action="/voteComment/$id/1" method="POST">
                            <label>$likes</label>
                            <button type="submit">like</button>
                            <input type="hidden" name="username" value=""/>
                        </form>
                    </td>
                    <td>
                        <form action="/voteComment/$id/-1" method="POST">
                            <label>$dislikes</label>
                            <button type="submit">dislike</button>
                            <input type="hidden" name="username" value=""/>
                        </form>
                    </td>
                </tr>
                """;

        StringBuilder tableRows = new StringBuilder();

        for (Comment userComment : commodity.getUserComments()) {
            String row = commentTableRow.replaceAll("\\$username", userComment.getUserEmail())
                    .replaceAll("\\$comment", userComment.getText())
                    .replaceAll("\\$date", userComment.getDate().toString())
                    .replaceAll("\\$likes", userComment.getVotes(1).toString())
                    .replaceAll("\\$dislikes", userComment.getVotes(-1).toString())
                    .replaceAll("\\$id", String.valueOf(userComment.getId()));
            tableRows.append(row);
        }

        htmlString = htmlString.replaceAll("\\$comments", tableRows.toString());

        return htmlString;
    }


    public CommodityHtmlPresentation(ServiceLayer serviceLayer) {
        super(serviceLayer);
    }

    public static String marshalCommodities(List<Commodity> commoditiesList) throws IOException {
        File input = new File(COMMODITIES_TEMPLATE_PATH);
        Document doc = Jsoup.parse(input, "UTF-8");
        String htmlString = doc.html();

        String commodityTableRow = """
                <tr>
                    <td>$id</td>
                    <td>$name</td>
                    <td>$providerId</td>
                    <td>$price</td>
                    <td>$categories</td>
                    <td>$rating</td>
                    <td>$inStock</td>
                    <td><a href="/commodities/$id">Page</a></td>
                </tr>
                """;

        StringBuilder tableRows = new StringBuilder();

        for (Commodity commodity : commoditiesList) {
            String row = commodityTableRow.replaceAll("\\$id", String.valueOf(commodity.getId()))
                    .replaceAll("\\$name", commodity.getName())
                    .replaceAll("\\$providerId", String.valueOf(commodity.getProviderId()))
                    .replaceAll("\\$price", String.valueOf(commodity.getPrice()))
                    .replaceAll("\\$categories", String.valueOf(commodity.getCategories()))
                    .replaceAll("\\$rating", String.valueOf(commodity.getRating()))
                    .replaceAll("\\$inStock", String.valueOf(commodity.getInStock()));
            tableRows.append(row);
        }

        htmlString = htmlString.replace("$commodities", tableRows.toString());

        return htmlString;
    }


    public Handler getCommodities = ctx -> {
        try {
            List<Commodity> commodities = new ArrayList<>(serviceLayer.getCommodityService().getCommoditiesList());

            String response = marshalCommodities(commodities);

            ctx.html(response);
        } catch (Exception exception) {
            ctx.redirect("/notFound");
        }
    };

    public Handler getCommoditiesByCategory = ctx -> {
        try {
            String category = ctx.pathParamAsClass("categories", String.class).get();
            List<Commodity> commodities = new ArrayList<>(serviceLayer.getCommodityService().getCommoditiesByCategory(category));

            String response = marshalCommodities(commodities);

            ctx.html(response);
        } catch (Exception exception) {
            ctx.redirect("/notFound");
        }
    };

    public Handler getCommoditiesByPriceRange = ctx -> {
        try {
            Integer startPrice = ctx.pathParamAsClass("start_price", Integer.class).get();
            Integer endPrice = ctx.pathParamAsClass("end_price", Integer.class).get();
            List<Commodity> commodities = new ArrayList<>(serviceLayer.getCommodityService().getCommoditiesByPrice(startPrice, endPrice));

            String response = marshalCommodities(commodities);

            ctx.html(response);
        } catch (Exception exception) {
            ctx.redirect("/notFound");
        }
    };

    public Handler getCommodityById = ctx -> {
        try {
            Integer commodityId = ctx.pathParamAsClass("commodity_id", Integer.class).get();
            Commodity commodity = serviceLayer.getCommodityService().getCommodityById(commodityId);

            String response = marshalCommodityObject(commodity);

            ctx.html(response);
        } catch (CommodityNotFound commodityNotFound) {
            ctx.redirect("/notFound");
        }
    };

    public Handler rateCommodity = ctx -> {
        try {
            String username = ctx.pathParamAsClass("username", String.class).get();
            Integer commodityId = ctx.pathParamAsClass("commodity_id", Integer.class).get();
            Integer rating = ctx.pathParamAsClass("rate", Integer.class).get();

            serviceLayer.getCommodityService().rateCommodity(username, commodityId, String.valueOf(rating));
        } catch (CommodityNotFound | UserNotFound e) {
            ctx.redirect("/notFound");
        } catch (InvalidRating invalidRating) {
            ctx.redirect("/forbidden");

        }
    };

    public Handler voteComment = ctx -> {
        try {
            String username;
            Integer commentId = ctx.pathParamAsClass("comment_id", Integer.class).get();
            Integer vote = ctx.pathParamAsClass("vote", Integer.class).get();

            // TODO: 17.03.23 How to get username/email for voting on comments?
            if (ctx.method().equals("GET")) {
                username = ctx.queryParamAsClass("username", String.class).get();
            } else {
                username = ctx.formParamAsClass("username", String.class).get();
            }

            // TODO: 17.03.23 Username or user email?
            serviceLayer.getCommentService().voteComment(username, commentId, vote);
            ctx.redirect("/success");
        } catch (UserNotFound userNotFound) {
            ctx.redirect("/notFound");
        }
    };

}
