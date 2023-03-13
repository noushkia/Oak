package org.ie.tk.exception.Commodity;

public class CommodityOutOfStock extends Exception{

    public CommodityOutOfStock(Integer commId) {
        super("Commodity with id " + commId + " is out of stock");
    }
}
