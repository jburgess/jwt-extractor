package com.jburgess.jwt;

import io.helidon.media.jsonp.JsonpSupport;
import io.helidon.webclient.WebClient;
import io.helidon.webserver.WebServer;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

  private static WebServer webServer;
  private static WebClient webClient;
  private static final JsonBuilderFactory JSON_BUILDER = Json
      .createBuilderFactory(Collections.emptyMap());
  private static final JsonObject TEST_JSON_OBJECT;

  static {
    TEST_JSON_OBJECT = JSON_BUILDER.createObjectBuilder()
        .add("token",
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
        .build();
  }

  @BeforeAll
  public static void startTheServer() throws Exception {
    webServer = Main.startServer();

    long timeout = 2000; // 2 seconds should be enough to start the server
    long now = System.currentTimeMillis();

    while (!webServer.isRunning()) {
      Thread.sleep(100);
      if ((System.currentTimeMillis() - now) > timeout) {
        Assertions.fail("Failed to start webserver");
      }
    }

    webClient = WebClient.builder()
        .baseUri("http://localhost:" + webServer.port())
        .addMediaSupport(JsonpSupport.create())
        .build();
  }

  @AfterAll
  public static void stopServer() throws Exception {
    if (webServer != null) {
      webServer.shutdown()
          .toCompletableFuture()
          .get(10, TimeUnit.SECONDS);
    }
  }

  @Test
  public void testExtractJwt() throws Exception {
    webClient.post()
        .path("/extractJwt")
        .submit(TEST_JSON_OBJECT)
        .thenAccept(response -> {
          Assertions.assertEquals(200, response.status().code());
          response.content().as(JsonObject.class).thenAccept(r -> {
            //TODO: This is not asserted
            Assertions.assertEquals("1234567890", r.getString("sub"));
            Assertions.assertEquals("John Doe", r.getString("name"));
            Assertions.assertEquals(1516239022, r.getInt("iat"));
            Assertions.assertEquals(1234567890, r.getInt("upn"));
          });
        })
        .toCompletableFuture()
        .get();
  }
}