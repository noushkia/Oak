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

public class UserHtmlPresentation extends HtmlPresentation {
    public UserHtmlPresentation(ServiceLayer serviceLayer) {
        super(serviceLayer);
    }

    public static String marshalUserObject(User user) throws IOException {
        File input = new File("src/main/resources/templates/User.html");
        Document doc = Jsoup.parse(input, "UTF-8");
        String htmlString = doc.html();

        htmlString = htmlString.replace("$username", user.getUsername());
        htmlString = htmlString.replace("$email", user.getEmail());
        htmlString = htmlString.replace("$birthdate", user.getBirthDate().toString());
        htmlString = htmlString.replace("$address", user.getAddress());
        htmlString = htmlString.replace("$credit", user.getCredit().toString());

        String buyListTableRow = """
                <tr>
                            <td>$id</td>
                            <td>$name</td>
                            <td>$providerId</td>
                            <td>$price</td>
                            <td>$categories</td>
                            <td>$rating</td>
                            <td>$inStock</td>
                            <td><a href="/commodities/$id">Link</a></td>
                            <td>
                                <form action="/removeFromBuyList/$username/$id" method="GET">
                                    <button type="submit">Remove</button>
                                </form>
                            </td>
                </tr>
                """;

        StringBuilder buyList = new StringBuilder();

        for (Commodity commodity : user.getBuyList()) {
            buyList.append(buyListTableRow);
            buyList = new StringBuilder(buyList.toString().replace("$id", commodity.getId().toString()));
            buyList = new StringBuilder(buyList.toString().replace("$name", commodity.getName()));
            buyList = new StringBuilder(buyList.toString().replace("$providerId", commodity.getProviderId().toString()));
            buyList = new StringBuilder(buyList.toString().replace("$price", commodity.getPrice().toString()));
            buyList = new StringBuilder(buyList.toString().replace("$categories", commodity.getCategories().toString()));
            buyList = new StringBuilder(buyList.toString().replace("$rating", commodity.getRating().toString()));
            buyList = new StringBuilder(buyList.toString().replace("$inStock", commodity.getInStock().toString()));
        }

        htmlString = htmlString.replace("$buyList", buyList.toString());

        String purchasedListTableRow = """
                <tr>
                            <td>$id</td>
                            <td>$name</td>
                            <td>$providerId</td>
                            <td>$price</td>
                            <td>$categories</td>
                            <td>$rating</td>
                            <td>$inStock</td>
                            <td><a href="/commodities/$id">Link</a></td>
                </tr>
                """;


        StringBuilder purchasedList = new StringBuilder();

        for (Commodity commodity : user.getBuyList()) {
            purchasedList.append(purchasedListTableRow);
            purchasedList = new StringBuilder(purchasedList.toString().replace("$id", commodity.getId().toString()));
            purchasedList = new StringBuilder(purchasedList.toString().replace("$name", commodity.getName()));
            purchasedList = new StringBuilder(purchasedList.toString().replace("$providerId", commodity.getProviderId().toString()));
            purchasedList = new StringBuilder(purchasedList.toString().replace("$price", commodity.getPrice().toString()));
            purchasedList = new StringBuilder(purchasedList.toString().replace("$categories", commodity.getCategories().toString()));
            purchasedList = new StringBuilder(purchasedList.toString().replace("$rating", commodity.getRating().toString()));
            purchasedList = new StringBuilder(purchasedList.toString().replace("$inStock", commodity.getInStock().toString()));
        }

        htmlString = htmlString.replace("$purchasedList", purchasedList.toString());

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
