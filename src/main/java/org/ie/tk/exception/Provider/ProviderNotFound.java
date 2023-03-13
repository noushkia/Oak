package org.ie.tk.exception.Provider;

public class ProviderNotFound extends Exception{

    public ProviderNotFound(Integer providerId) {
        super("No provider with id " + providerId + " found");
    }
}
