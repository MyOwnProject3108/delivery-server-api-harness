package com.adstream.api.tests;

import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.equalTo;



/**
 * Created by natla on 26/07/2016.
 */
public class UsersTest extends TestBase {

  private String ttm_userId;
  private String btm_userId;

  @BeforeClass
  public void init() {
    ttm_userId = app.getProperty("TTM_userId");
    btm_userId = app.getProperty("BTM_userId");
  }

  //Retrieve user details
  @Test
  public void testUserDetailsTTM() throws IOException {
    Response response = app.rest().getUserDetails(ttm_userId, ttm_userId);
    response.
            then().log().all().statusCode(200).
            and().assertThat().body(matchesJsonSchemaInClasspath("User.json"));
  }

  @Test
  public void testUserDetailsBTM() throws IOException {
    Response response = app.rest().getUserDetails(ttm_userId, btm_userId);
    response.
            then().log().all().statusCode(200).
            and().assertThat().body(matchesJsonSchemaInClasspath("User.json"));
  }

  @Test
  //retrieve non-existing user
  public void testUserDetails404() throws IOException {
    Response response = app.rest().getUserDetails(ttm_userId, ttm_userId + 1);
    response.
            then().log().all().statusCode(404)
            .assertThat().body(equalTo("The requested resource could not be found."));
  }

  //fail
  @Test
  //get information as unauthorised
  public void testUserDetailsUnauth() throws IOException {
    Response response = app.rest().getUserDetails("", ttm_userId);
    response.
            then().log().all().statusCode(400)
            .assertThat().body(equalTo("Request is missing required HTTP header 'X-User-Id'"));
  }

  //fail
  @Test
  //invalid xUserId
  public void testUserDetailsInvalid() throws IOException {
    Response response = app.rest().getUserDetails(ttm_userId + 1, ttm_userId);
    response.
            then().log().all().statusCode(403)
            .assertThat().body(equalTo("The supplied authentication is not authorized to access this resource"));
  }
}
