package org.ie.tk.presentation.html;

import io.javalin.http.Handler;

import org.ie.tk.application.service.ServiceLayer;
import org.ie.tk.domain.Comment;
import org.ie.tk.domain.Commodity;

import org.ie.tk.exception.Commodity.CommodityNotFound;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommodityHtmlPresentation extends HtmlPresentation {
    public CommodityHtmlPresentation(ServiceLayer serviceLayer) {
        super(serviceLayer);
    }

    public static String marshalCommodityObject(Commodity commodity) throws IOException {
        File input = new File(COMMODITY_TEMPLATE_PATH);
        Document doc = Jsoup.parse(input, "UTF-8");
        String htmlString = doc.html();
        htmlString = htmlString.replace("$id", commodity.getId().toString());
        htmlString = htmlString.replace("$name", commodity.getName());
        htmlString = htmlString.replace("$providerId", commodity.getProviderId().toString());
        htmlString = htmlString.replace("$price", commodity.getPrice().toString());
        htmlString = htmlString.replace("$categories", commodity.getCategories().toString());
        htmlString = htmlString.replace("$rating", commodity.getRating().toString());
        htmlString = htmlString.replace("$inStock", commodity.getInStock().toString());

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


        StringBuilder comments = new StringBuilder();

        for (Comment userComment : commodity.getUserComments()) {
            comments.append(commentTableRow);
            comments = new StringBuilder(comments.toString().replace("$username", userComment.getUserEmail()));
            comments = new StringBuilder(comments.toString().replace("$comment", userComment.getText()));
            comments = new StringBuilder(comments.toString().replace("$date", userComment.getDate().toString()));
            comments = new StringBuilder(comments.toString().replace("$likes", userComment.getVotes(1).toString()));
            comments = new StringBuilder(comments.toString().replace("$disLikes", userComment.getVotes(-1).toString()));
        }

        htmlString = htmlString.replace("$comments", comments.toString());

        return htmlString;
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
                        </tr>""";

        StringBuilder commodities = new StringBuilder();

        for (Commodity commodity : commoditiesList) {
            commodities.append(commodityTableRow);
            commodities = new StringBuilder(commodities.toString().replace("$id", commodity.getId().toString()));
            commodities = new StringBuilder(commodities.toString().replace("$name", commodity.getName()));
            commodities = new StringBuilder(commodities.toString().replace("$providerId", commodity.getProviderId().toString()));
            commodities = new StringBuilder(commodities.toString().replace("$price", commodity.getPrice().toString()));
            commodities = new StringBuilder(commodities.toString().replace("$categories", commodity.getCategories().toString()));
            commodities = new StringBuilder(commodities.toString().replace("$rating", commodity.getRating().toString()));
            commodities = new StringBuilder(commodities.toString().replace("$inStock", commodity.getInStock().toString()));
        }

        htmlString = htmlString.replace("$commodities", commodities.toString());

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

}
