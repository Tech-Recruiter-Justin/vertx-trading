package net.justinchoi.trading.handler;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.WebSocket;
import io.vertx.core.http.WebSocketFrame;
import io.vertx.core.json.JsonObject;

import java.util.logging.Logger;

public class FrameHandler {

    public FrameHandler(){}

    public void handleFrame(WebSocket webSocket, WebSocketFrame frame, Logger logger) {
        if (frame.isPing()) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.put("id", "test-binance-jc");
            jsonObject.put("method", "pong");
            webSocket.writePong(Buffer.buffer("PONG"));
            logger.info("frame type is: " + frame.type().toString() + " replied with PONG");
        }
        if (frame.isText()) {
            logger.info("frame type is: " + frame.type().toString());
            logger.info(frame.textData());
        }
    }

}
