package com.jburgess.jwt;

import io.helidon.config.Config;
import io.helidon.media.jsonp.JsonpSupport;
import io.helidon.security.jwt.SignedJwt;
import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

/**
 * The application main class.
 */
public final class Main {

  /**
   * Cannot be instantiated.
   */
  private Main() {
  }

  /**
   * Application main entry point.
   *
   * @param args command line arguments.
   * @throws IOException if there are problems reading logging properties
   */
  public static void main(final String[] args) throws IOException {
    if (args.length == 1) {
      String st = SignedJwt.parseToken(args[0]).getJwt().payloadJson().toString();
      System.out.println(st);
      System.exit(0);
    } else {
      startServer();
    }
  }

  /**
   * Start the server.
   *
   * @return the created {@link WebServer} instance
   * @throws IOException if there are problems reading logging properties
   */
  static WebServer startServer() throws IOException {

    // load logging configuration
    setupLogging();

    // By default this will pick up application.yaml from the classpath
    Config config = Config.create();

    WebServer server = WebServer.builder(createRouting())
        .config(config.get("server"))
        .addMediaSupport(JsonpSupport.create())
        .build();

    // Try to start the server. If successful, print some info and arrange to
    // print a message at shutdown. If unsuccessful, print the exception.
    server.start().thenAccept(ws -> {
      System.out.println("WEB server is up! http://localhost:" + ws.port() + "/extractJwt");
      ws.whenShutdown().thenRun(() -> System.out.println("WEB server is DOWN. Good bye!"));
    }).exceptionally(t -> {
      System.err.println("Startup failed: " + t.getMessage());
      t.printStackTrace(System.err);
      return null;
    });

    // Server threads are not daemon. No need to block. Just react.

    return server;
  }

  /**
   * Creates new {@link Routing}.
   *
   * @return routing configured with JSON support, a health check, and a service
   */
  private static Routing createRouting() {

    JwtService jwtService = new JwtService();

    return Routing.builder().register("/extractJwt", jwtService).build();
  }

  /**
   * Configure logging from logging.properties file.
   */
  private static void setupLogging() throws IOException {
    try (InputStream is = Main.class.getResourceAsStream("/logging.properties")) {
      LogManager.getLogManager().readConfiguration(is);
    }
  }
}