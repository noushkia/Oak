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
import java.text.SimpleDateFormat;
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
            String row = tableRow;
            if (Objects.equals(tablePlaceholder, "$buyList")) {
                String button = """
                    <td>
                        <form action="/removeFromBuyList/$username/$id" method="GET">
                            <button type="submit">Remove</button>
                        </form>
                    </td>
                    """;
                row = row.replace("$button", button);
            } else {
                row = row.replace("$button", "");
            }
            row = marshalCommodityEntry(row, commodity);

            tableRows.append(row);
        }

        htmlString = htmlString.replace(tablePlaceholder, tableRows.toString());

        return htmlString;
    }

    public static String marshalUserObject(User user) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        File input = new File(USER_TEMPLATE_PATH);
        Document doc = Jsoup.parse(input, "UTF-8");
        String htmlString = doc.html();
        htmlString = createTableRowsForCommodities(user.getBuyList(), htmlString, "$buyList");
        htmlString = createTableRowsForCommodities(user.getPurchasedList(), htmlString, "$purchasedList");

        htmlString = htmlString.replaceAll("\\$username", user.getUsername())
                .replaceAll("\\$email", user.getEmail())
                .replaceAll("\\$birthdate", dateFormat.format(user.getBirthDate()))
                .replaceAll("\\$address", user.getAddress())
                .replaceAll("\\$credit", String.valueOf(user.getCredit()));

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

            ctx.redirect("/success");
        } catch (UserNotFound userNotFound) {
            ctx.redirect("/notFound");
        }
    };
}
