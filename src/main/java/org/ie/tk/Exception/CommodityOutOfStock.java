package org.ie.tk.Exception;

public class CommodityOutOfStock extends Exception{
    public CommodityOutOfStock(String commId) {
        super("Commodity with id " + commId + " is out of stock");
    }
}
