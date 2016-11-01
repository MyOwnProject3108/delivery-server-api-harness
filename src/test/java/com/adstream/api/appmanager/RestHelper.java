package com.adstream.api.appmanager;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import javax.annotation.Nullable;
import java.io.IOException;

import static io.restassured.RestAssured.*;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.port;


/**
 * Created by natla on 26/07/2016.
 */
public class RestHelper {

    public RestHelper(ApplicationManager app) {
        baseURI = app.getProperty("baseURI");
        port = new Integer(app.getProperty("ds_port"));
    }

    private RequestSpecification setUserHeader(RequestSpecification request, @Nullable String xUserId) {
        if (xUserId != null)
            return request.header("X-User-Id", xUserId);
        return request;
    }

    private Response sendGetRequest(@Nullable String xUserId, String path) {
        RequestSpecification partialReq = given().
                baseUri(baseURI).port(port);
        return setUserHeader(partialReq, xUserId)
                .when()
                .get(path);
    }

    private Response sendPutRequest(@Nullable String xUserId, String path, Object body) {
        RequestSpecification partialReq = given().
                baseUri(baseURI).port(port).
                contentType("application/json");
        return setUserHeader(partialReq, xUserId)
                .body(body)
                .when()
                .put(path);
    }

    private Response sendPostRequest(@Nullable String xUserId, String path, Object body) {
        RequestSpecification partialReq = given().
                baseUri(baseURI).port(port).
                contentType("application/json");
        return setUserHeader(partialReq, xUserId)
                .body(body)
                .when()
                .post(path);
    }

    //AdditionalServices
    //GET /api/traffic/v1/additionalService/transitions -- Get map of transitions to display in UI
    public Response getASTransitions(@Nullable String xUserId) throws IOException {
        return sendGetRequest(xUserId, "/api/traffic/v1/additionalService/transitions");
    }

    //PUT /api/traffic/v1/dubbingService/{dubbingServiceId} -- Updates the status of dubbing Service
    public Response updateDubbingStatus(@Nullable String xUserId, String dubbingServiceId, Object body) throws IOException {
        return sendPutRequest(xUserId, "/api/traffic/v1/dubbingService/" + dubbingServiceId, body);
    }

    //Users
    //GET /api/core/v1/user/{userId} -- Retrieve user details
    public Response getUserDetails(@Nullable String xUserId, String userId) throws IOException {
        return sendGetRequest(xUserId, "/api/core/v1/user/" + userId);
    }

    public Response getOrderDetails(@Nullable String xUserId, String orderId) throws IOException {
        return sendGetRequest(xUserId, "/api/traffic/v1/order/" + orderId);
    }

    public Response getOrderItemDetails(@Nullable String xUserId, String orderItemId) throws IOException {
        return sendGetRequest(xUserId, "/api/traffic/v1/orderitem/" + orderItemId);
    }

}
