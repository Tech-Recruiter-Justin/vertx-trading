package net.justinchoi.trading.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.JsonObject;
import net.justinchoi.trading.handler.FrameHandler;

import java.util.logging.Logger;

public class DepthReqVerticle extends AbstractVerticle {

    private static final Logger logger = Logger.getLogger("DepthReqVerticle");
    private final FrameHandler frameHandler = new FrameHandler();
    private final WebSocket webSocket;
    private final String symbol;
    private final int limit;

    public DepthReqVerticle(WebSocket webSocket, String symbol, int limit) {
        this.webSocket = webSocket;
        this.symbol = symbol;
        this.limit = limit;
    }

    @Override
    public void start(Promise<Void> promise) {
        logger.info(String.format("Start getting order book for %s, limit %s", symbol, limit));
        JsonObject orderBookReqJson = new JsonObject();
        orderBookReqJson.put("id", "jc-order-book-req-depth");
        orderBookReqJson.put("method", "depth");

        JsonObject params = new JsonObject();
        params.put("symbol", symbol.toUpperCase());
        params.put("limit", limit);

        orderBookReqJson.put("params", params);
        webSocket.writeFinalTextFrame(orderBookReqJson.toString());
        webSocket.frameHandler(frame -> {
            frameHandler.handleFrame(webSocket, frame, logger);
        });
    }

}
