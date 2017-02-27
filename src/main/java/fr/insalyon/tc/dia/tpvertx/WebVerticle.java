package fr.insalyon.tc.dia.tpvertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;

public class WebVerticle extends AbstractVerticle {

    @Override
    public void start() {
        vertx.createHttpServer()
                .requestHandler(req -> {
                    switch (req.path()) {
                        case "/auth":
                            if (req.method() == HttpMethod.POST) {
                                req.bodyHandler(handleAuthWebRequest(req));
                                break;
                            }
                        case "/":
                        default:
                            req.response()
                                    .setStatusCode(404)
                                    .putHeader("Content-Type", "text/plain")
                                    .end();
                    }
                })
                .listen(6958, asyncResult -> {
                    if (asyncResult.succeeded()) {
                        System.out.println("Auth server is running");
                    }
                });
    }

    private Handler<Buffer> handleAuthWebRequest(HttpServerRequest req) {
        return data -> {
            try {
                final JsonObject message = new JsonObject(String.valueOf(data));
                vertx.eventBus()
                        .send("authentication.requests",
                                message,
                                response -> {
                                    final JsonObject result = (JsonObject) response.result().body();
                                    req.response()
                                            .setStatusCode(200)
                                            .putHeader("Content-Type", "text/json")
                                            .end(String.valueOf(result));
                                });
            } catch (DecodeException e) {
                req.response()
                        .setStatusCode(406)
                        .putHeader("Content-Type", "text/json")
                        .end(new JsonObject().put("success", false).put("reason", "Can not parse request").toString());
            }
        };
    }
}