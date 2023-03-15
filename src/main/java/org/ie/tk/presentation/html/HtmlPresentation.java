package org.ie.tk.presentation.html;

import org.ie.tk.application.service.ServiceLayer;
import org.ie.tk.presentation.Presentation;

public abstract class HtmlPresentation extends Presentation {
    protected static final String USER_TEMPLATE_PATH = "src/main/resources/templates/User.html";
    protected static final String COMMODITY_TEMPLATE_PATH = "src/main/resources/templates/Commodity.html";
    protected static final String COMMODITIES_TEMPLATE_PATH = "src/main/resources/templates/Commodities.html";

    public HtmlPresentation(ServiceLayer serviceLayer) {
        super(serviceLayer);
    }
}
