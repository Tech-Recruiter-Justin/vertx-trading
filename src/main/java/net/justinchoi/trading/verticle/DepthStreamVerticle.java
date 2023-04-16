package net.justinchoi.trading.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.*;
import net.justinchoi.trading.handler.FrameHandler;

import java.util.logging.Logger;

public class DepthStreamVerticle extends AbstractVerticle {

    private static final Logger logger = Logger.getLogger("DepthStreamVerticle");
    public static final String DEPTH = "@depth";
    private final FrameHandler frameHandler = new FrameHandler();
    private final String stream;

    public DepthStreamVerticle(String stream) {
        this.stream = stream + DEPTH;
    }

    @Override
    public void start(Promise<Void> future) {
        logger.info("Connecting to stream " + stream);
        HttpClientOptions httpClientOptions = new HttpClientOptions()
            .setKeepAlive(true)
            .setSsl(true)
            .setTrustAll(true);

        HttpClient client = vertx.createHttpClient(httpClientOptions);
        WebSocketConnectOptions options = new WebSocketConnectOptions()
            .setHost("stream.binance.com")
            .setPort(9443)
            .setURI("/ws/" + stream)
            .setAllowOriginHeader(false);
        client
            .webSocket(options)
            .onComplete(res -> {
                if (res.succeeded()) {
                    WebSocket webSocket = res.result();
                    logger.info(String.format("Connected to Binance %s Websocket!", stream));
                    webSocket.frameHandler(frame ->
                        frameHandler.handleFrame(webSocket, frame, logger)
                    );
                    vertx.deployVerticle(new SubscribeStreamVerticle(webSocket, stream));
                } else {
                    logger.warning("Cannot connect to Binance...");
                }
            })
            .onFailure(Throwable::printStackTrace);
    }

}
