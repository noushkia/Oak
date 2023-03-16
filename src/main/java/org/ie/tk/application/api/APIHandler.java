package org.ie.tk.application.api;

import io.javalin.Javalin;
import io.javalin.http.NotFoundResponse;
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

//        app.exception(NotFoundResponse.class, (e, ctx) -> {
//            ctx.redirect("/notFound");
//        });

        app.routes(() -> {
            app.get("/", ctx -> ctx.result("Hello World"));
            app.get("commodities", htmlPresentationLayer.getCommodityHtmlPresentation().getCommodities);
            app.get("commodities/{commodity_id}", htmlPresentationLayer.getCommodityHtmlPresentation().getCommodityById);
            app.get("providers/{provider_id}", htmlPresentationLayer.getProviderHtmlPresentation().getProviderById);
            app.get("users/{user_id}", htmlPresentationLayer.getUserHtmlPresentation().getUserById);
            app.get("addCredit/{user_id}/{credit}", htmlPresentationLayer.getUserHtmlPresentation().addCredit);
            app.get("/success", htmlPresentationLayer.getStatusHtmlPresentation().handleSuccess);
            app.get("/notFound", htmlPresentationLayer.getStatusHtmlPresentation().handleNotFound);
            app.get("/forbidden", htmlPresentationLayer.getStatusHtmlPresentation().handleForbidden);
            // TODO: 15.03.23 Clean Commodity and Provider HtmlPresentation
            // TODO: 15.03.23 post and get: rate, addtobuylist, removefrombuylist, vote
            // TODO: 15.03.23 Implement search by categories and price
            // TODO: 15.03.23 Implement payment (purchasedList + buy)
            // TODO: 15.03.23 Implement voteComment
            // TODO: 15.03.23 Test
        }).error(404, ctx -> {
            ctx.redirect("/notFound");
        });
    }

    public static void main(String[] args) throws IOException, InvalidUsername, CommodityNotFound, ProviderNotFound {
        APIHandler apiHandler = new APIHandler();
        apiHandler.run();
    }
}
