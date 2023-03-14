package org.ie.tk.presentation.json;

import org.ie.tk.application.service.ServiceLayer;

public class JsonPresentationLayer {

    private UserJsonPresentation userJsonPresentation;
    public JsonPresentationLayer(ServiceLayer serviceLayer){
        userJsonPresentation = new UserJsonPresentation(serviceLayer);
    }
}
