package org.ie.tk.Exception.Commodity;

public class CommodityNotFound extends Exception{

    public CommodityNotFound(Integer commId) {
        super("Commodity with id " + commId + " not found");
    }
}
