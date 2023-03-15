package org.ie.tk.application.api;

import io.javalin.Javalin;
import org.ie.tk.application.Handler;
import org.ie.tk.exception.Commodity.CommodityNotFound;
import org.ie.tk.exception.Provider.ProviderNotFound;
import org.ie.tk.exception.User.InvalidUsername;
import org.ie.tk.presentation.html.HtmlPresentationLayer;

import java.io.IOException;


public class APIHandler extends Handler {
    private Javalin app;
    private final HtmlPresentationLayer htmlPresentationLayer;
    public APIHandler() throws IOException, InvalidUsername, CommodityNotFound, ProviderNotFound {
        super();
        htmlPresentationLayer = new HtmlPresentationLayer(serviceLayer);
    }

    public void run() {
        app = Javalin.create().start(5000);

        app.routes(() -> {
            app.get("/", ctx -> ctx.result("Hello World"));
            app.get("commodities", htmlPresentationLayer.getCommodityHtmlPresentation().getCommodities);
            app.get("commodities/{commodity_id}", htmlPresentationLayer.getCommodityHtmlPresentation().getCommodityById);
        });
    }

    public static void main(String[] args) throws IOException, InvalidUsername, CommodityNotFound, ProviderNotFound {
        APIHandler apiHandler = new APIHandler();
        apiHandler.run();
    }
}
