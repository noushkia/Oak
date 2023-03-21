package com.oak.presentation.html;

import com.oak.application.service.ServiceLayer;
import com.oak.domain.Commodity;
import com.oak.presentation.Presentation;

public abstract class HtmlPresentation extends Presentation {
    protected static final String USER_TEMPLATE_PATH = "src/main/resources/templates/user.html";
    protected static final String COMMODITY_TEMPLATE_PATH = "src/main/resources/templates/commodity.html";
    protected static final String COMMODITIES_TEMPLATE_PATH = "src/main/resources/templates/commodities.html";
    protected static final String PROVIDER_TEMPLATE_PATH = "src/main/resources/templates/provider.html";
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
