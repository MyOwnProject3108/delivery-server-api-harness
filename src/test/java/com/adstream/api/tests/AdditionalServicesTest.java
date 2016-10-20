package com.adstream.api.tests;

import com.adstream.api.model.StatusAS;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.io.IOException;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 * Created by natla on 26/07/2016.
 */
public class AdditionalServicesTest extends TestBase {

  private String ttm_userId;
  private String btm_userId;
  private String dubbingServiceId;

  @BeforeClass
  public void init() throws IOException, InterruptedException {
    ttm_userId = app.getProperty("TTM_userId");
    btm_userId = app.getProperty("BTM_userId");
    dubbingServiceId = getDubbingServiceId();
  }

  private String getDubbingServiceId() throws IOException, InterruptedException {
    setNew();
    return app.rest().getOrderDetails(ttm_userId, "5800fa9c7af9be2d19d8e3f4")
              .then().log().ifError().statusCode(200)
              .extract()
              .path("orderItems[0].additionalServices.dubbingServices[0]._id");
  }

  private void setNew() throws IOException, InterruptedException {
    Response activity = app.mock().createActivity(ttm_userId, "5800fa9c7af9be2d19d8e3f4");
    activity.then().log().ifError().statusCode(202);
  }

  private void setInProgress() throws IOException, InterruptedException {
    setNew();
    app.rest().updateDubbingStatus(ttm_userId, dubbingServiceId,
            new StatusAS().withOldStatus("New").withNewStatus("Transfer In Progress")).then().log().ifError();
  }

  private void setComplete() throws IOException, InterruptedException {
    setNew();
    app.rest().updateDubbingStatus(ttm_userId, dubbingServiceId,
            new StatusAS().withOldStatus("New").withNewStatus("Transfer Complete")).then().log().ifError();
  }

  //TRANSITIONS
  //get Transitions as different Users
  @Test
  public void testGetTransitionsTTM() throws IOException {
    Response response = app.rest().getASTransitions(ttm_userId);
    response
            .then().log().all().statusCode(200)
            .and().assertThat().body(matchesJsonSchemaInClasspath("ASTransitions.json"));
  }

  @Test
  //should BTM see transitions????
  public void testGetTransitionsBTM() throws IOException {
    Response response = app.rest().getASTransitions(btm_userId);
    response
            .then().log().all().statusCode(200)
            .assertThat().body(matchesJsonSchemaInClasspath("ASTransitions.json"));
  }

  @Test
  //unauthorised request
  public void testGetTransitionsUnauth() throws IOException {
    Response response = app.rest().getASTransitions("");
    response
            .then().log().all().statusCode(400)
            .assertThat().body(equalTo("Request is missing required HTTP header 'X-User-Id'"));

  }

  @Test
  //invalid xUserId
  public void testGetTransitionsInvalid() throws IOException {
    Response response = app.rest().getASTransitions(ttm_userId + "1");
    response
            .then().log().all().statusCode(403)
            .assertThat().body(equalTo("The supplied authentication is not authorized to access this resource"));
  }

  //DUBBING SERVICE
  //update DubbingService status
  @Test
  public void testUpdDubbing_NewToProgress() throws IOException, InterruptedException {
    setNew();
    StatusAS body = new StatusAS().withOldStatus("New").withNewStatus("Transfer In Progress");
    Response response = app.rest().updateDubbingStatus(ttm_userId, dubbingServiceId, body);
    response
            .then().log().all().statusCode(200)
            .assertThat().body(equalTo("OK"));
  }

  //TOFIX: server shouldn't set status to Complete without completeDate
  @Test
  public void testUpdDubbing_ProgressToComplete() throws IOException, InterruptedException {
    setInProgress();
    StatusAS body = new StatusAS().withOldStatus("Transfer In Progress").withNewStatus("Transfer Complete");
    Response response = app.rest().updateDubbingStatus(ttm_userId, dubbingServiceId, body);
    response
            .then().log().all().statusCode(200)
            .assertThat().body(equalTo("OK"));
  }

  //TOFIX: server shouldn't set status to Complete without completeDate
  @Test
  public void testUpdDubbing_NewToComplete() throws IOException, InterruptedException {
    setNew();
    StatusAS body = new StatusAS().withOldStatus("New").withNewStatus("Transfer Complete");
    Response response = app.rest().updateDubbingStatus(ttm_userId, dubbingServiceId, body);
    response
            .then().log().all().statusCode(200)
            .assertThat().body(equalTo("OK"));
  }

  @Test
  //unauthorised request
  public void testUpdDubbingUnauth() throws IOException {
    StatusAS body = new StatusAS().withOldStatus("").withNewStatus("");
    Response response = app.rest().updateDubbingStatus("", dubbingServiceId, body);
    response
            .then().log().all().statusCode(400)
            .assertThat().body(equalTo("Request is missing required HTTP header 'X-User-Id'"));
  }

  @Test
  //invalid xUserId
  public void testUpdDubbingInvalid() throws IOException {
    StatusAS body = new StatusAS().withOldStatus("").withNewStatus("");
    Response response = app.rest().updateDubbingStatus(ttm_userId + "1", dubbingServiceId, body);
    response
            .then().log().all().statusCode(403)
            .assertThat().body(equalTo("The supplied authentication is not authorized to access this resource"));
  }

  //TOFIX: response body has spare ""
  @Test
  public void testUpdDubbing_NewToNew() throws IOException, InterruptedException {
    setNew();
    StatusAS body = new StatusAS().withOldStatus("New").withNewStatus("New");
    Response response = app.rest().updateDubbingStatus(ttm_userId, dubbingServiceId, body);
    response
            .then().log().all().statusCode(400)
            .assertThat().body(equalTo("\"Statuses transition not allowed from " +
            "[" + body.getOldStatus() + "] to [" + body.getNewStatus() + "]\""));
  }

  //TOFIX: response body has spare ""
  @Test
  public void testUpdDubbing_ProgressToProgress() throws IOException, InterruptedException {
    setInProgress();
    StatusAS body = new StatusAS().withOldStatus("Transfer In Progress").withNewStatus("Transfer In Progress");
    Response response = app.rest().updateDubbingStatus(ttm_userId, dubbingServiceId, body);
    response
            .then().log().all().statusCode(400)
            .assertThat().body(equalTo("\"Statuses transition not allowed from " +
            "[" + body.getOldStatus() + "] to [" + body.getNewStatus() + "]\""));
  }

  //TOFIX: response body has spare ""
  @Test
  public void testUpdDubbing_CompleteToComplete() throws IOException, InterruptedException {
    setComplete();
    StatusAS body = new StatusAS().withOldStatus("Transfer Complete").withNewStatus("Transfer Complete");
    Response response = app.rest().updateDubbingStatus(ttm_userId, dubbingServiceId, body);
    response
            .then().log().all().statusCode(400)
            .assertThat().body(equalTo("\"Statuses transition not allowed from " +
                    "[" + body.getOldStatus() + "] to [" + body.getNewStatus() + "]\""));
  }
}