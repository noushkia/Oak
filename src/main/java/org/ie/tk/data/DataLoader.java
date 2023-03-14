package org.ie.tk.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ie.tk.domain.Comment;
import org.ie.tk.domain.Commodity;
import org.ie.tk.domain.Provider;
import org.ie.tk.domain.User;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

public class DataLoader {
    static final String USERSENDPOINT = "api/users";
    static final String COMMODITIESENDPOINT = "api/commodities";
    static final String PROVIDERSENDPOINT = "api/providers";
    static final String COMMENTSENDPOINT = "api/comments";

    private final ArrayList<User> users;
    private final ArrayList<Provider> providers;
    private final ArrayList<Commodity> commodities;
    private final ArrayList<Comment> comments;

    private static ObjectMapper mapper;

    public DataLoader(String externalServiceUrl) throws IOException {
        mapper = new ObjectMapper();
        users = loadData(externalServiceUrl + USERSENDPOINT, User.class);
        providers = loadData(externalServiceUrl + PROVIDERSENDPOINT, Provider.class);
        commodities = loadData(externalServiceUrl + COMMODITIESENDPOINT, Commodity.class);
        comments = loadData(externalServiceUrl + COMMENTSENDPOINT, Comment.class);
    }

    private <T> ArrayList<T> loadData(String url, Class<T> clazz) throws IOException {
        Document doc = Jsoup.connect(url).ignoreContentType(true).get();
        return mapper.readValue(doc.wholeText(), mapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz));
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public ArrayList<Provider> getProviders() {
        return providers;
    }

    public ArrayList<Commodity> getCommodities() {
        return commodities;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }
}