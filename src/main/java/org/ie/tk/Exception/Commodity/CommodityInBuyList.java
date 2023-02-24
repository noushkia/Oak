package org.ie.tk.Exception.Commodity;

public class CommodityInBuyList extends Exception {
    public CommodityInBuyList(String username, String commId) {
        super("Commodity with id " + commId + " already in user with username " + username + " buylist");
    }
}
