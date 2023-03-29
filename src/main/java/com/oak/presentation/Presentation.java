package com.oak.presentation;

import com.oak.application.service.ServiceLayer;

public abstract class Presentation {
    protected ServiceLayer serviceLayer;

    public Presentation(ServiceLayer serviceLayer){
        this.serviceLayer = serviceLayer;
    }

}
