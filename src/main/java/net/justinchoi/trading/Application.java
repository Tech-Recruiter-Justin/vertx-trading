package net.justinchoi.trading;

import io.vertx.core.Vertx;
import net.justinchoi.trading.verticle.DepthStreamVerticle;

public class Application {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new DepthStreamVerticle("bnbbtc"));
    }
}
