package com.oak.presentation.html;

import com.oak.exception.Provider.ProviderNotFound;
import io.javalin.http.Handler;
import com.oak.application.service.ServiceLayer;
import com.oak.domain.Commodity;
import com.oak.domain.Provider;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class ProviderHtmlPresentation extends HtmlPresentation {
    public ProviderHtmlPresentation(ServiceLayer serviceLayer) {
        super(serviceLayer);
    }

    public static String marshalProviderObject(Provider provider) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        File input = new File(PROVIDER_TEMPLATE_PATH);
        Document doc = Jsoup.parse(input, "UTF-8");
        String htmlString = doc.html();
        htmlString = htmlString.replaceAll("\\$id", String.valueOf(provider.getId()))
                .replaceAll("\\$name", provider.getName())
                .replaceAll("\\$registryDate", dateFormat.format(provider.getRegistryDate()));

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
            String row = commodityTableRow.replaceAll("\\$id", String.valueOf(commodity.getId()))
                    .replaceAll("\\$name", commodity.getName())
                    .replaceAll("\\$price", String.valueOf(commodity.getPrice()))
                    .replaceAll("\\$categories", String.valueOf(commodity.getCategories()))
                    .replaceAll("\\$rating", String.valueOf(commodity.getRating()))
                    .replaceAll("\\$inStock", String.valueOf(commodity.getInStock()));
            commodities.append(row);
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
