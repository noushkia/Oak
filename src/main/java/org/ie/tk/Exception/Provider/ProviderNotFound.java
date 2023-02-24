package org.ie.tk.Exception.Provider;

public class ProviderNotFound extends Exception{
    public ProviderNotFound(String providerId) {
        super("No provider with id " + providerId + " found");
    }
}
