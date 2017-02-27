package fr.insalyon.tc.dia.tpvertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;

public class SocketVerticle extends AbstractVerticle {

    @Override
    public void start() {
        vertx.createNetServer()
                .connectHandler(socket -> {
                    System.out.println("Hello " + socket.remoteAddress());
                    socket.handler(handleAuthSocketRequest(socket));
                    socket.exceptionHandler(e -> System.err.println(e.getMessage()));
                    socket.endHandler(v -> System.out.println("Bye" + socket.remoteAddress()));
                }).listen(6963);
    }

    private Handler<Buffer> handleAuthSocketRequest(NetSocket req) {
        return data -> {
            try {
                final JsonObject message = new JsonObject(String.valueOf(data));
                vertx.eventBus()
                        .send("authentication.requests",
                                message,
                                response -> {
                                    final JsonObject result = (JsonObject) response.result().body();
                                    req.write(String.valueOf(result));
                                    req.write("\n");
                                });
            } catch (DecodeException e) {
                req.write(new JsonObject().put("success", false).put("reason", "Can not parse request").toString());
                req.write("\n");
            }
        };
    }
}