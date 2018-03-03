package com.htaviet.websocket.main;

import com.htaviet.websocket.common.Properties;
import com.htaviet.websocket.events.AlarmSocket;
import com.htaviet.websocket.events.DeviceSocket;
import javax.websocket.server.ServerContainer;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

/**
 *
 * @author ThuyetLV
 */
public class WebsocketStart {

    private final static Logger logger = Logger.getLogger(WebsocketStart.class.getSimpleName());
    private static Server server = null;
    private static ServerConnector connector = null;

    public static void run() {
        server = new Server();
        connector = new ServerConnector(server);
        int portNumber = Properties.getWebSocketPort();
        connector.setPort(portNumber);
        server.addConnector(connector);

        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        try {
            // Initialize javax.websocket layer
            ServerContainer wscontainer = WebSocketServerContainerInitializer.configureContext(context);

            // Add WebSocket endpoint to javax.websocket layer
            wscontainer.addEndpoint(AlarmSocket.class);
            wscontainer.addEndpoint(DeviceSocket.class);

            server.start();
            logger.info("Websocket started and now waiting for new connnection !");
            // Display output of the stream
            // Block until server is ready
            server.join();
            logger.info("Websocket started !");
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            logger.error(ex.getMessage(), ex);
        }
    }

    public static void closeWebSocket() {
        try {
            connector.close();
            server.stop();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public static void main(String[] args) {
        run();
    }
}
