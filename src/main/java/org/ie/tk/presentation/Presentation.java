package org.ie.tk.presentation;

import org.ie.tk.application.service.ServiceLayer;

public abstract class Presentation {
    protected ServiceLayer serviceLayer;

    public Presentation(ServiceLayer serviceLayer){
        this.serviceLayer = serviceLayer;
    }

}
