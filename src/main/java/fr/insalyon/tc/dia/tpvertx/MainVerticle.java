package fr.insalyon.tc.dia.tpvertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() {

        vertx.deployVerticle("fr.insalyon.tc.dia.tpvertx.AuthVerticle");
        vertx.deployVerticle("fr.insalyon.tc.dia.tpvertx.WebVerticle", new DeploymentOptions().setInstances(4));
        vertx.deployVerticle("fr.insalyon.tc.dia.tpvertx.SocketVerticle", new DeploymentOptions().setInstances(2));

    }
}