package org.ie.tk.presentation.html;

import org.ie.tk.application.service.ServiceLayer;
import org.ie.tk.domain.Commodity;
import org.ie.tk.presentation.Presentation;

public abstract class HtmlPresentation extends Presentation {
    protected static final String USER_TEMPLATE_PATH = "src/main/resources/templates/User.html";
    protected static final String COMMODITY_TEMPLATE_PATH = "src/main/resources/templates/Commodity.html";
    protected static final String COMMODITIES_TEMPLATE_PATH = "src/main/resources/templates/Commodities.html";
    protected static final String PROVIDER_TEMPLATE_PATH = "src/main/resources/templates/Provider.html";
    protected static String marshalCommodityEntry(String tableRow, Commodity commodity) {
        return tableRow.replaceAll("\\$id", String.valueOf(commodity.getId()))
                .replaceAll("\\$name", commodity.getName())
                .replaceAll("\\$providerId", String.valueOf(commodity.getProviderId()))
                .replaceAll("\\$price", String.valueOf(commodity.getPrice()))
                .replaceAll("\\$categories", String.valueOf(commodity.getCategories()))
                .replaceAll("\\$rating", String.valueOf(commodity.getRating()))
                .replaceAll("\\$inStock", String.valueOf(commodity.getInStock()));
    }
    public HtmlPresentation(ServiceLayer serviceLayer) {
        super(serviceLayer);
    }
}
