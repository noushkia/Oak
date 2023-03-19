package com.oak.presentation.html;

import io.javalin.http.Handler;
import com.oak.application.service.ServiceLayer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;

public class StatusHtmlPresentation extends HtmlPresentation{
    public StatusHtmlPresentation(ServiceLayer serviceLayer) {
        super(serviceLayer);
    }

    public Handler handleSuccess = ctx -> {
        File input = new File("src/main/resources/templates/200.html");
        Document doc = Jsoup.parse(input, "UTF-8");
        ctx.html(doc.html());
    };

    public Handler handleNotFound = ctx -> {
        File input = new File("src/main/resources/templates/404.html");
        Document doc = Jsoup.parse(input, "UTF-8");
        ctx.html(doc.html());
    };

    public Handler handleForbidden = ctx -> {
        File input = new File("src/main/resources/templates/403.html");
        Document doc = Jsoup.parse(input, "UTF-8");
        ctx.html(doc.html());
    };


}
