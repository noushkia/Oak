package org.ie.tk.presentation.html;

import io.javalin.http.Handler;
import org.ie.tk.application.service.ServiceLayer;
import org.ie.tk.domain.Commodity;
import org.ie.tk.domain.Provider;
import org.ie.tk.exception.Provider.ProviderNotFound;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

public class ProviderHtmlPresentation extends HtmlPresentation {
    public ProviderHtmlPresentation(ServiceLayer serviceLayer) {
        super(serviceLayer);
    }

    public static String marshalProviderObject(Provider provider) throws IOException {
        File input = new File("src/main/resources/templates/Provider.html");
        Document doc = Jsoup.parse(input, "UTF-8");
        String htmlString = doc.html();
        htmlString = htmlString.replace("$id", provider.getId().toString());
        htmlString = htmlString.replace("$name", provider.getName());
        htmlString = htmlString.replace("$registryDate", provider.getRegistryDate().toString());

        String commodityTableRow = """
                <tr>
                    <td>$id</td>
                    <td>$name</td>
                    <td>$price</td>
                    <td>$categories</td>
                    <td>$rating</td>
                    <td>$inStock</td>
                    <td><a href="/commodities/$id">Link</a></td>
                </tr>
                """;


        StringBuilder commodities = new StringBuilder();

        for (Commodity commodity : provider.getProvidedCommodities()) {
            commodities.append(commodityTableRow);
            commodities = new StringBuilder(commodities.toString().replace("$id", commodity.getId().toString()));
            commodities = new StringBuilder(commodities.toString().replace("$name", commodity.getName()));
            commodities = new StringBuilder(commodities.toString().replace("$price", commodity.getPrice().toString()));
            commodities = new StringBuilder(commodities.toString().replace("$categories", commodity.getCategories().toString()));
            commodities = new StringBuilder(commodities.toString().replace("$rating", commodity.getRating().toString()));
            commodities = new StringBuilder(commodities.toString().replace("$inStock", commodity.getInStock().toString()));
        }

        htmlString = htmlString.replace("$commodities", commodities.toString());

        return htmlString;
    }

    public Handler getProviderById = ctx -> {
        try {
            Integer providerId = ctx.pathParamAsClass("provider_id", Integer.class).get();
            Provider provider = serviceLayer.getProviderService().getProviderById(providerId);

            String response = marshalProviderObject(provider);

            ctx.html(response);
        } catch (ProviderNotFound providerNotFound) {
            ctx.redirect("/notFound");
        }
    };
}
