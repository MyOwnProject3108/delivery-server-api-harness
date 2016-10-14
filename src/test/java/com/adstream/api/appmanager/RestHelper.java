package com.adstream.api.appmanager;

import com.adstream.api.model.BodyBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;

import static io.restassured.RestAssured.*;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.port;


/**
 * Created by natla on 26/07/2016.
 */
public class RestHelper {
    private ApplicationManager app;

    public RestHelper(ApplicationManager app) {
        this.app = app;
        baseURI = app.getProperty("baseURI");
        port = new Integer(app.getProperty("ds_port"));
    }

    private RequestSpecification setUserHeader(RequestSpecification request, String xUserId) {
        if (xUserId.length() > 0)
            return request.header("X-User-Id", xUserId);
        return request;
    }

    private Response sendGetRequest(String xUserId, String path) {
        RequestSpecification partialReq = given().
                baseUri(baseURI).port(port);
        return setUserHeader(partialReq, xUserId)
                .when()
                .get(path);
    }

    private Response sendPutRequest(String xUserId, String path, BodyBuilder body) {
        RequestSpecification partialReq = given().
                baseUri(baseURI).port(port).
                contentType("application/json");
        return setUserHeader(partialReq, xUserId)
                .body(body)
                .when()
                .put(path);
    }

    private Response sendPostRequest(String xUserId, String path, String orderId) {
        return given()
                .baseUri(baseURI).port(port)
                .header("subjectId", xUserId)
                .header("orderId", orderId)
                .header("ActivityType", "orderPlaced")
                .when()
                .post(path);
    }

    //AdditionalServices
    //GET /api/traffic/v1/additionalService/transitions -- Get map of transitions to display in UI
    public Response getASTransitions(String xUserId) throws IOException {
        return sendGetRequest(xUserId, "/api/traffic/v1/additionalService/transitions");
    }

    //PUT /api/traffic/v1/dubbingService/{dubbingServiceId} -- Updates the status of dubbing Service
    public Response updateDubbingStatus(String xUserId, String dubbingServiceId, BodyBuilder body) throws IOException {
        return sendPutRequest(xUserId, "/api/traffic/v1/dubbingService/" + dubbingServiceId, body);
    }

    //Users
    //GET /api/core/v1/user/{userId} -- Retrieve user details
    public Response getUserDetails(String xUserId, String userId) throws IOException {
        return sendGetRequest(xUserId, "/api/core/v1/user/" + userId);
    }

    public Response getOrderDetails(String xUserId, String orderId) throws IOException {
        return sendGetRequest(xUserId, "/api/traffic/v1/order/" + orderId);
    }

    public Response createActivity(String xUserId, String orderId) throws IOException {
        return sendPostRequest(xUserId, "/admin/activities/order", orderId);
    }
}
