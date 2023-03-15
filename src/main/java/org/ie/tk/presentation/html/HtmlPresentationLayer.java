package org.ie.tk.presentation.html;

import org.ie.tk.application.service.ServiceLayer;

public class HtmlPresentationLayer {
    private final UserHtmlPresentation userHtmlPresentation;
    private final CommodityHtmlPresentation commodityHtmlPresentation;
    private final ProviderHtmlPresentation providerHtmlPresentation;
    public HtmlPresentationLayer(ServiceLayer serviceLayer){
        userHtmlPresentation = new UserHtmlPresentation(serviceLayer);
        commodityHtmlPresentation = new CommodityHtmlPresentation(serviceLayer);
        providerHtmlPresentation = new ProviderHtmlPresentation(serviceLayer);
    }

    public UserHtmlPresentation getUserHtmlPresentation() {
        return userHtmlPresentation;
    }

    public CommodityHtmlPresentation getCommodityHtmlPresentation() {
        return commodityHtmlPresentation;
    }

    public ProviderHtmlPresentation getProviderHtmlPresentation() {
        return providerHtmlPresentation;
    }
}
