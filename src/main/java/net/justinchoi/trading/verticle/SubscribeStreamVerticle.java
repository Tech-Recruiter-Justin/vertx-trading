package net.justinchoi.trading.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.logging.Logger;

public class SubscribeStreamVerticle extends AbstractVerticle {

    private static final Logger logger = Logger.getLogger("SubscribeStreamVerticle");
    private final WebSocket webSocket;
    private final String stream;

    public SubscribeStreamVerticle(WebSocket webSocket, String stream) {
        this.webSocket = webSocket;
        this.stream = stream;
    }

    @Override
    public void start(Promise<Void> promise) {
        logger.info(String.format("Start getting order book for %s", stream));
        JsonObject orderBookReqJson = new JsonObject();
        orderBookReqJson.put("id", 1);
        orderBookReqJson.put("method", "SUBSCRIBE");


        JsonArray jsonArray = new JsonArray();
        jsonArray.add(stream);
        orderBookReqJson.put("params", jsonArray);
        webSocket.writeFinalTextFrame(orderBookReqJson.toString());
        webSocket.frameHandler(frame -> {
            if (frame.isText()) {
                logger.info(frame.textData());
            }
        });
    }

}
