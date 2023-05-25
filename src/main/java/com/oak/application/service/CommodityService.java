package com.oak.application.service;

import com.oak.data.dao.CommentDAO;
import com.oak.data.dao.CommodityDAO;
import com.oak.data.dao.DAOLayer;
import com.oak.domain.Comment;
import com.oak.domain.Commodity;
import com.oak.domain.User;
import com.oak.exception.Commodity.InvalidRating;
import com.oak.exception.Provider.ProviderNotFound;
import com.oak.exception.User.UserNotFound;
import com.oak.data.Database;
import com.oak.exception.Commodity.CommodityNotFound;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CommodityService extends Service {
    private Predicate<Commodity> query = c -> true;
    private Comparator<Commodity> comparator = null;

    public CommodityService(Database db, DAOLayer daoLayer) {
        super(db, daoLayer);
    }

    public void setQuery(String method, String input) {
        CommodityDAO commodityDAO = daoLayer.getCommodityDAO();
        if (method.contains("category")) {
            commodityDAO.setCategoryCondition(input);
        } else if (method.contains("name")) {
            final String lowercaseInput = input.toLowerCase();
            commodityDAO.setNameCondition(lowercaseInput);
        } else if (method.contains("provider")) {
            commodityDAO.setProviderNameCondition(input);
        }
    }

    public void setQuery(String method) {
        if (method.contains("onlyAvailableCommodities")) {
            daoLayer.getCommodityDAO().setAvailableCondition();
        }
    }

    public void setComparator(String method) {
        daoLayer.getCommodityDAO().setSort(method);
    }

    public void reset() {
        daoLayer.getCommodityDAO().reset();
    }

    public void addCommodity(Commodity commodity) throws ProviderNotFound {
        CommodityDAO commodityDAO = daoLayer.getCommodityDAO();
        commodityDAO.addCommodity(commodity);
    }


    public List<Commodity> getCommoditiesList() {
        return daoLayer.getCommodityDAO().fetchCommodities();
    }

    public void setPagination(Integer limit, Integer pageNumber) {
        daoLayer.getCommodityDAO().setPagination(limit, pageNumber);
    }

    public Integer getNumberOfPages() {
        return daoLayer.getCommodityDAO().getNumberOfPages();
    }

    private void prepareCommodity(Commodity commodity) {
        CommentDAO commentDAO =  daoLayer.getCommentDAO();
        List<Comment> comments = commentDAO.fetchComments(commodity.getId());
        for (Comment comment : comments) {
            HashMap<String, Integer> votes = commentDAO.fetchVotes(comment.getId());
            comment.setUserVotes(votes);
            commodity.addComment(comment);
        }
        HashMap<String, Integer> ratings = daoLayer.getCommodityDAO().fetchRatings(commodity.getId());
        commodity.setUserRatings(ratings);
    }

    public List<Commodity> getSuggestedCommodities(Integer commodityId) throws CommodityNotFound {
        Commodity inputCommodity = db.fetchCommodity(commodityId);
        List<Commodity> allCommodities = db.fetchCommodities(c -> true);
        Comparator<Commodity> scoringFunc = Comparator.comparingDouble(c -> (c.isInSimilarCategory(inputCommodity) ? 11.0 : 0.0) + c.getRating());
        return allCommodities.stream()
                .filter(c -> !c.getId().equals(commodityId)) // Skip the input Commodity object
                .sorted(scoringFunc.reversed().thenComparingInt(c -> (int) (Math.random() * 1000)))
                .limit(5)
                .collect(Collectors.toList());
    }

    public Commodity getCommodityById(Integer commodityId) throws CommodityNotFound {
        Commodity commodity = daoLayer.getCommodityDAO().fetchCommodity(commodityId);
        prepareCommodity(commodity);
        return commodity;
    }

    public List<Commodity> getCommoditiesByCategory(String category) {
        return db.fetchCommodities(c -> c.containsCategory(category));
    }

    public List<Commodity> getCommoditiesByPrice(Integer startPrice, Integer endPrice) {
        return db.fetchCommodities(c -> c.isInPriceRange(startPrice, endPrice));
    }

    public void rateCommodity(String username, Integer commodityId, String rating) throws InvalidRating, UserNotFound, CommodityNotFound {
        User user = db.fetchUser(username);
        Commodity commodity = db.fetchCommodity(commodityId);
        commodity.addUserRating(user.getUsername(), rating);
    }

}
