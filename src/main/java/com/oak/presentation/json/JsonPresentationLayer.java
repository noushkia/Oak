package com.oak.presentation.json;

import com.oak.application.service.ServiceLayer;

public class JsonPresentationLayer {

    private final UserJsonPresentation userJsonPresentation;
    private final CommodityJsonPresentation commodityJsonPresentation;
    private final ProviderJsonPresentation providerJsonPresentation;
    public JsonPresentationLayer(ServiceLayer serviceLayer){
        userJsonPresentation = new UserJsonPresentation(serviceLayer);
        commodityJsonPresentation = new CommodityJsonPresentation(serviceLayer);
        providerJsonPresentation = new ProviderJsonPresentation(serviceLayer);
    }

    public UserJsonPresentation getUserJsonPresentation() {
        return userJsonPresentation;
    }

    public CommodityJsonPresentation getCommodityJsonPresentation() {
        return commodityJsonPresentation;
    }

    public ProviderJsonPresentation getProviderJsonPresentation() {
        return providerJsonPresentation;
    }
}
