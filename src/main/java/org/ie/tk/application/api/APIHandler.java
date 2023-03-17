package org.ie.tk.application.api;

import io.javalin.Javalin;
import org.ie.tk.application.Handler;
import org.ie.tk.exception.Commodity.CommodityNotFound;
import org.ie.tk.exception.Provider.ProviderNotFound;
import org.ie.tk.exception.User.InvalidUsername;
import org.ie.tk.presentation.html.HtmlPresentationLayer;

import java.io.IOException;

// TODO: 15.03.23 Implement voteComment
// TODO: 15.03.23 Test

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
            app.get("providers/{provider_id}", htmlPresentationLayer.getProviderHtmlPresentation().getProviderById);
            app.get("users/{username}", htmlPresentationLayer.getUserHtmlPresentation().getUserById);

            app.get("addCredit/{username}/{credit}", htmlPresentationLayer.getUserHtmlPresentation().addCredit);
            app.post("addCredit/{username}", htmlPresentationLayer.getUserHtmlPresentation().addCredit);

            app.get("addToBuyList/{username}/{commodity_id}", htmlPresentationLayer.getUserHtmlPresentation().addToBuyList);
            app.post("addToBuyList/{commodity_id}", htmlPresentationLayer.getUserHtmlPresentation().addToBuyList);

            app.get("removeFromBuyList/{username}/{commodity_id}", htmlPresentationLayer.getUserHtmlPresentation().removeFromBuyList);

            app.get("rateCommodity/{username}/{commodity_id}/{rate}", htmlPresentationLayer.getCommodityHtmlPresentation().rateCommodity);
            app.post("rateCommodity/{commodity_id}", htmlPresentationLayer.getCommodityHtmlPresentation().rateCommodity);

            app.get("commodities/search/{start_price}/{end_price}", htmlPresentationLayer.getCommodityHtmlPresentation().getCommoditiesByPriceRange);
            app.get("commodities/search/{categories}", htmlPresentationLayer.getCommodityHtmlPresentation().getCommoditiesByCategory);

            app.get("/success", htmlPresentationLayer.getStatusHtmlPresentation().handleSuccess);
            app.get("/notFound", htmlPresentationLayer.getStatusHtmlPresentation().handleNotFound);
            app.get("/forbidden", htmlPresentationLayer.getStatusHtmlPresentation().handleForbidden);

            app.post("/finalizeBuyList/{username}", htmlPresentationLayer.getUserHtmlPresentation().finalizeBuyList);
        }).error(404, ctx -> ctx.redirect("/notFound"));
    }

    public static void main(String[] args) throws IOException, InvalidUsername, CommodityNotFound, ProviderNotFound {
        APIHandler apiHandler = new APIHandler();
        apiHandler.run();
    }
}
