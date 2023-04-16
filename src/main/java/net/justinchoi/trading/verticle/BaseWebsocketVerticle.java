package net.justinchoi.trading.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.WebSocket;
import io.vertx.core.http.WebSocketConnectOptions;
import net.justinchoi.trading.handler.FrameHandler;

import java.util.logging.Logger;

public class BaseWebsocketVerticle extends AbstractVerticle {

    private static final Logger logger = Logger.getLogger("BaseWebsocketVerticle");
    private static final String HOST = "ws-api.binance.com";
    private static final int PORT = 443;
    private static final String URI = "/ws-api/v3";
    private final FrameHandler frameHandler = new FrameHandler();

    @Override
    public void start(Promise<Void> promise) {
        logger.info(String.format("Started connection to host = %s, port = %s, uri = %s", HOST, PORT, URI));
        HttpClientOptions httpClientOptions = new HttpClientOptions()
            .setKeepAlive(true)
            .setSsl(true)
            .setTrustAll(true);

        HttpClient client = vertx.createHttpClient(httpClientOptions);
        WebSocketConnectOptions options = new WebSocketConnectOptions()
            .setHost(HOST)
            .setPort(PORT)
            .setURI(URI)
            .setAllowOriginHeader(false);
        client
            .webSocket(options)
            .onComplete(res -> {
                if (res.succeeded()) {
                    WebSocket webSocket = res.result();
                    logger.info("Connected to Binance Base Websocket!");
                    webSocket.frameHandler(frame ->
                        frameHandler.handleFrame(webSocket, frame, logger)
                    );
                    vertx.deployVerticle(new DepthReqVerticle(webSocket, "BNBBTC", 5));
                } else {
                    logger.warning("Cannot connect to Binance...");
                }
            })
            .onFailure(Throwable::printStackTrace);
    }

}
