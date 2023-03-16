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
