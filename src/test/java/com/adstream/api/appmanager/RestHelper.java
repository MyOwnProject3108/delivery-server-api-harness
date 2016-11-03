package com.adstream.api.appmanager;

import com.adstream.api.model.BodyBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    ObjectMapper mapper = new ObjectMapper();

    public RestHelper(ApplicationManager app) {
        baseURI = app.getProperty("baseURI");
        try {
            port = new Integer(app.getProperty("ds_port"));
        } catch (Exception e) {
            throw new RuntimeException("Cannot read service port", e);
        }
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

    private Response sendPutRequest(@Nullable String xUserId, String path, Object body) throws JsonProcessingException{
        RequestSpecification partialReq = given().
                baseUri(baseURI).port(port).
                contentType("application/json");
        String bodyStr = mapper.writeValueAsString(body);
        return setUserHeader(partialReq, xUserId)
                .body(bodyStr)
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

    private Response sendDeleteRequest(@Nullable String xUserId, String path) throws IOException{
        RequestSpecification partialReq = given().
                baseUri(baseURI).port(port);
        return setUserHeader(partialReq, xUserId)
                .when()
                .delete(path);

    }



    //AdditionalServices
    //GET /api/traffic/v1/additionalService/transitions -- Get map of transitions to display in UI
    public Response getASTransitions(@Nullable String xUserId) throws IOException {
        return sendGetRequest(xUserId, "/api/traffic/v1/additionalService/transitions");
    }

    //PUT /api/traffic/v1/dubbingService/{dubbingServiceId} -- Updates the status of dubbing Service
    public Response updateDubbingStatus(@Nullable String xUserId, String dubbingServiceId, BodyBuilder body) throws IOException {
        return sendPutRequest(xUserId, "/api/traffic/v1/dubbingService/" + dubbingServiceId, body);
    }

    //Users
    //GET /api/core/v1/user/{userId} -- Retrieve user details
    public Response getUserDetails(@Nullable String xUserId, String userId) throws IOException {
        return sendGetRequest(xUserId, "/api/core/v1/user/" + userId);
    }


    //Tabs
    //get /api/traffic/v1/tab -- Retrieve tabs available to the current user
    public Response getTabDetails(@Nullable String xUserId) throws IOException{
        return sendGetRequest(xUserId, "/api/traffic/v1/tab");
    }

    //DELETE /api/traffic/v1/tab/{tabId}
    public Response deleteTab(@Nullable String xUserId, @Nullable String tabId) throws IOException{
        return sendDeleteRequest(xUserId, "/api/traffic/v1/tab/" + tabId);
    }

    //POST /api/traffic/v1/tab
    public Response createNewTab(@Nullable String xUserId, Object body){
        return  sendPostRequest(xUserId,"/api/traffic/v1/tab", body);
    }

    //PUT /api/traffic/v1/tab/user - Update a tab
    //Added now.......
    public Response updateTab(@Nullable String xUserId, Object body) throws IOException{
        return sendPutRequest(xUserId, "/api/traffic/v1/tab", body);
    }

    //PUT /api/traffic/v1/tab/user - Arrange tabs for the user
    public Response arrangeTabs(@Nullable String xUserId, Object body) throws JsonProcessingException {
        return sendPutRequest(xUserId, "/api/traffic/v1/tab/user",body);
    }


    public Response getOrderDetails(@Nullable String xUserId, String orderId) throws IOException {
        return sendGetRequest(xUserId, "/api/traffic/v1/order/" + orderId);
    }

    public Response getOrderItemDetails(@Nullable String xUserId, String orderItemId) throws IOException {
        return sendGetRequest(xUserId, "/api/traffic/v1/orderitem/" + orderItemId);
    }

}
