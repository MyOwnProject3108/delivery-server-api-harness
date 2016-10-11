package com.adstream.api.appmanager;

import com.adstream.api.gen.MyWrap;
import io.restassured.response.Response;
import org.apache.commons.net.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import static io.restassured.RestAssured.*;
import static io.restassured.RestAssured.baseURI;


/**
 * Created by natla on 26/07/2016.
 */
public class PapiHelper {
  private ApplicationManager app;

  public PapiHelper(ApplicationManager app) {
    this.app = app;
    baseURI = app.getProperty("web_baseURI");
  }

  public String auth(String secret, String key) throws NoSuchAlgorithmException, InvalidKeyException {

    long epoch = (long) Math.floor(new Date().getTime() / 1000);

    Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
    SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
    sha256_HMAC.init(secret_key);

    String hash = Base64.encodeBase64String(sha256_HMAC.doFinal((key + epoch).getBytes()));
    hash = hash.replace("\r\n", "");
    String token = "A5-API " + key + ":" + hash + ":" + epoch;

    return token;
  }

  public Response auth(String token) throws IOException {
    return given().
              baseUri(baseURI).header("Authorization", token).
            when().
              get("/api/v2/auth/user");
  }

  //create draft order
  public Response createDraft(String token, String body) throws IOException {
      return given().
                baseUri(baseURI).
                contentType("application/json").
                header("Authorization", token).
                body(body).
              when().
                post("/api/v2/orders");
  }

  //create order item
  public Response createOrderItem(String token, MyWrap body, String orderId) throws IOException {
    return given().
              baseUri(baseURI).
              contentType("application/json").
              header("Authorization", token).
              body(body).
            when().
            post("/api/v2/orders/" + orderId + "/items");
  }

  //add destination
  public Response addDestination(String token, String body, String orderId, String orderItemId) throws IOException {
    return given().
            baseUri(baseURI).
            contentType("application/json").
            header("Authorization", token).
            body(body).
            when().
            post("/api/v2/orders/" + orderId + "/items/" + orderItemId + "/destinations");
  }

  //add destination
  public Response orderProceed(String token, String body, String orderId) throws IOException {
    return given().
            baseUri(baseURI).
            contentType("application/json").
            header("Authorization", token).
            body(body).
            when().
            put("/api/v2/orders/" + orderId + "/process");
  }

}
