package org.ie.tk;

import java.util.HashMap;

public class CommodityProvisionSystem {

    private HashMap<String, Commodity> commodities;
    private HashMap<String, Provider> providers;
    private HashMap<String, User> users;

    public CommodityProvisionSystem() {
        commodities = new HashMap<>();
        providers = new HashMap<>();
        users = new HashMap<>();
    }
}
