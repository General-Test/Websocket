package org.christu.secure.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.java_websocket.server.WebSocketServer;

import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

public class SecureWebSocketServer extends WebSocketServer {

    static CountDownLatch latch = new CountDownLatch(1);

    public SecureWebSocketServer(int port, SSLContext sslContext) {
        super(new InetSocketAddress(port));
        this.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(sslContext));
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        System.out.println("New WebSocket connection opened: " + webSocket.getRemoteSocketAddress());
        latch.countDown();
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        System.out.println("WebSocket connection closed: " + s);
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        System.out.println("Received message: " + s);
        webSocket.send("Hello from the server!");
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onStart() {

    }

    public static void main(String[] args) throws Exception {
        int port = 8443; // Replace with your desired port
        String keystorePath = "cert/server/server.p12";
        String keystorePassword = "qwerty";
        String truststorePath = "cert/ca/ca-cert.pem";


        SSLContext sslContext = SSLContextHelper.createSSLContextServer(keystorePath, keystorePassword, truststorePath);
        WebSocketServer server = new SecureWebSocketServer(port, sslContext);

//        server.setWebSocketFactory((WebSocketServerFactory) sslContext.getSocketFactory());

        try {
            server.start();
            latch.await();
            System.out.println("WebSocket server started on port: " + port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}