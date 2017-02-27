package fr.insalyon.tc.dia.tpvertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Verticle d'authentification.
 * Répond à des demandes d'authentifications formulées en JSON et donne une réponse.
 */
public class AuthVerticle extends AbstractVerticle {
    private final Properties users = new Properties();

    public AuthVerticle() {
        super();
        try {
            FileInputStream in = new FileInputStream("users.properties");
            users.load(in);
            in.close();
        } catch (IOException e) {
            throw new RuntimeException("Can not load user database !", e);
        }
    }

    @Override
    public void start() {
        vertx.eventBus().consumer("authentication.requests", message -> {
            final JsonObject response = new JsonObject();
            response.put("success", false);
            response.put("reason", "Unknown reason");

            try {
                JsonObject payload = (JsonObject) message.body();
                String login = payload.getString("login");
                String password = payload.getString("password");
                final String validPassword = users.getProperty(login, null);
                if (validPassword != null && validPassword.equals(password)) {
                    response.put("success", true);
                    response.remove("reason");
                } else {
                    response.put("reason", "Bad login or password");
                }
            } catch (Exception e) {
                response.put("reason", "Bad request format");
            }

            message.reply(response);
        });
    }
}
