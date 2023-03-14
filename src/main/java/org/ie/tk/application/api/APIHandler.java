package org.ie.tk.application.api;

import io.javalin.Javalin;
import org.ie.tk.application.Handler;

import java.io.IOException;


public class APIHandler extends Handler {
    private Javalin app;
    public APIHandler() throws IOException {
        super();
    }

    public void run() {
        app = Javalin.create().start(5000);

        app.routes(() -> {
            app.get("/", ctx -> ctx.result("Hello World"));
        });
    }

    public static void main(String[] args) throws IOException {
        APIHandler apiHandler = new APIHandler();
        apiHandler.run();
    }
}
