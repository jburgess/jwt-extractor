package com.jburgess.jwt;

import com.jburgess.jwt.model.TokenPayload;
import io.helidon.common.http.Http;
import io.helidon.security.jwt.SignedJwt;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

public class JwtService implements Service {

  private static final JsonBuilderFactory JSON_BF =
      Json.createBuilderFactory(Collections.emptyMap());
  private static final Logger LOGGER = Logger.getLogger(JwtService.class.getName());

  /**
   * A service registers itself by updating the routing rules.
   *
   * @param rules the routing rules.
   */
  @Override
  public void update(Routing.Rules rules) {
    rules.post("/", this::getTokenHandler);
  }

  private void getTokenHandler(ServerRequest request, ServerResponse response) {
    request.content().as(JsonObject.class).thenAccept(jo -> extractToken(jo, response));
  }

  private void extractToken(JsonObject jo, ServerResponse response) {
    if (!jo.containsKey("token")) {
      JsonObject jsonErrorObject =
          JSON_BF.createObjectBuilder().add("error", "No token provided").build();
      response.status(Http.Status.BAD_REQUEST_400).send(jsonErrorObject);
      return;
    }

    String token = TokenPayload.fromRest(jo).getToken();
    LOGGER.log(Level.INFO,"Received Request to decode '{0}'", token);

    SignedJwt st = SignedJwt.parseToken(token);
    JsonObject jwtPayload = st.getJwt().payloadJson();
    response.status(Http.Status.OK_200).send(jwtPayload);
  }

}
