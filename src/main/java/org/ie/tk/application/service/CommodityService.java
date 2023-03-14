package org.ie.tk.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import jdk.jshell.spi.ExecutionControl;
import org.ie.tk.data.Database;
import org.ie.tk.domain.Commodity;
import org.ie.tk.domain.User;
import org.ie.tk.exception.Commodity.CommodityNotFound;
import org.ie.tk.exception.Commodity.InvalidRating;
import org.ie.tk.exception.Provider.ProviderNotFound;
import org.ie.tk.exception.User.UserNotFound;

import java.util.List;

public class CommodityService extends Service {

    public CommodityService(Database db) {
        super(db);
    }

    public void addCommodity(Commodity commodity) throws ProviderNotFound {
        db.fetchProvider(commodity.getProviderId());
        db.addCommodity(commodity);
    }


    public List<Commodity> getCommoditiesList() {
        return db.fetchCommodities(c -> true);
    }

    public Commodity getCommodityById(Integer commodityId) throws CommodityNotFound {
        return db.fetchCommodity(commodityId);
    }

    public List<Commodity> getCommoditiesByCategory(String category) {
        return db.fetchCommodities(c -> c.containsCategory(category));

    }

    public void rateCommodity(String username, Integer commodityId, String rating) throws InvalidRating, UserNotFound, CommodityNotFound {
        User user = db.fetchUser(username);
        Commodity commodity = db.fetchCommodity(commodityId);
        commodity.addUserRating(user.getUsername(), rating);
    }

    public JsonNode voteComment(JsonNode voteNode) throws ExecutionControl.NotImplementedException {
        // TODO: 14.03.23 Implement
        throw new ExecutionControl.NotImplementedException("vote comment");
    }

}
