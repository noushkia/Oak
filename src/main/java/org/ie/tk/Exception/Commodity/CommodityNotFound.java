package org.ie.tk.Exception.Commodity;

public class CommodityNotFound extends Exception{
    public CommodityNotFound(String commId) {
        super("Commodity with id " + commId + " not found");
    }
}
