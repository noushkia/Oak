package org.ie.tk.presentation.html;

import io.javalin.http.Handler;
import org.ie.tk.application.service.ServiceLayer;
import org.ie.tk.domain.Commodity;
import org.ie.tk.domain.User;
import org.ie.tk.exception.User.UserNotFound;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class UserHtmlPresentation extends HtmlPresentation {
    public UserHtmlPresentation(ServiceLayer serviceLayer) {
        super(serviceLayer);
    }

    private static String createTableRowsForCommodities(List<Commodity> commodities, String htmlString, String tablePlaceholder) {
        String tableRow = """
                <tr>
                    <td>$id</td>
                    <td>$name</td>
                    <td>$providerId</td>
                    <td>$price</td>
                    <td>$categories</td>
                    <td>$rating</td>
                    <td>$inStock</td>
                    <td><a href="/commodities/$id">Link</a></td>
                    $button
                </tr>
                """;


        StringBuilder tableRows = new StringBuilder();

        for (Commodity commodity : commodities) {
            StringBuilder row = new StringBuilder(tableRow);
            row.replace(row.indexOf("$id"), row.indexOf("$id") + 3, commodity.getId().toString());
            row.replace(row.indexOf("$name"), row.indexOf("$name") + 5, commodity.getName());
            row.replace(row.indexOf("$providerId"), row.indexOf("$providerId") + 11, commodity.getProviderId().toString());
            row.replace(row.indexOf("$price"), row.indexOf("$price") + 6, commodity.getPrice().toString());
            row.replace(row.indexOf("$categories"), row.indexOf("$categories") + 11, commodity.getCategories().toString());
            row.replace(row.indexOf("$rating"), row.indexOf("$rating") + 7, commodity.getRating().toString());
            row.replace(row.indexOf("$inStock"), row.indexOf("$inStock") + 8, commodity.getInStock().toString());
            if (Objects.equals(tablePlaceholder, "$buyList")) {
                String button = """
                    <td>
                        <form action="/removeFromBuyList/$username/$id" method="GET">
                            <button type="submit">Remove</button>
                        </form>
                    </td>
                    """;
                row.replace(row.indexOf("$button"), row.indexOf("$button") + 7, button);
            } else {
                row.replace(row.indexOf("$button"), row.indexOf("$button") + 7, "");
            }
            tableRows.append(row);
        }

        htmlString = htmlString.replace(tablePlaceholder, tableRows.toString());

        return htmlString;
    }

    public static String marshalUserObject(User user) throws IOException {
        File input = new File(USER_TEMPLATE_PATH);
        Document doc = Jsoup.parse(input, "UTF-8");
        String htmlString = doc.html();

        htmlString = htmlString.replace("$username", user.getUsername());
        htmlString = htmlString.replace("$email", user.getEmail());
        htmlString = htmlString.replace("$birthdate", user.getBirthDate().toString());
        htmlString = htmlString.replace("$address", user.getAddress());
        htmlString = htmlString.replace("$credit", user.getCredit().toString());


        htmlString = createTableRowsForCommodities(user.getBuyList(), htmlString, "$buyList");
        htmlString = createTableRowsForCommodities(user.getPurchasedList(), htmlString, "$purchasedList");

        return htmlString;
    }

    public Handler getUserById = ctx -> {
        try {
            String username = ctx.pathParamAsClass("user_id", String.class).get();
            User user = serviceLayer.getUserService().getUserById(username);

            String response = marshalUserObject(user);

            ctx.html(response);
        } catch (UserNotFound userNotFound) {
            ctx.redirect("/notFound");
        }
    };

    public Handler addCredit = ctx -> {
        try {
            String username = ctx.pathParamAsClass("user_id", String.class).get();
            Integer credit = ctx.pathParamAsClass("credit", Integer.class).get();

            serviceLayer.getUserService().addCredit(username, credit);

            ctx.redirect("/success"); // TODO: 15.03.23 Where to redirect?
        } catch (UserNotFound userNotFound) {
            ctx.redirect("/notFound");
        }
    };
}
