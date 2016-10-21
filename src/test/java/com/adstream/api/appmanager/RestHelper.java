package com.adstream.api.appmanager;

import com.adstream.api.model.BodyBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    ObjectMapper mapper = new ObjectMapper();

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

    private Response sendPutRequest(String xUserId, String path, Object body) throws JsonProcessingException {
        RequestSpecification partialReq = given().
                baseUri(baseURI).port(port).
                contentType("application/json");
        String bodyStr = mapper.writeValueAsString(body);
        return setUserHeader(partialReq, xUserId)
                .body(bodyStr)
                .when()
                .put(path);
    }

    private Response sendPostRequest(String xUserId, String path, BodyBuilder body) {
        RequestSpecification partialReq = given().
                baseUri(baseURI).port(port).
                contentType("application/json");
        return setUserHeader(partialReq, xUserId)
                .body(body)
                .when()
                .post(path);
    }

    private Response sendDeleteRequest(String xUserId, String path) throws IOException{
        RequestSpecification partialReq = given().
                baseUri(baseURI).port(port);
        return setUserHeader(partialReq, xUserId)
                .when()
                .delete(path);

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


    //Tabs
    //get /api/traffic/v1/tab -- Retrieve tabs available to the current user
    public Response getTabDetails(String xUserId) throws IOException{
        return sendGetRequest(xUserId, "/api/traffic/v1/tab");
    }

    //DELETE /api/traffic/v1/tab/{tabId}
    public Response deleteTab(String xUserId, String tabId) throws IOException{
        return sendDeleteRequest(xUserId, "/api/traffic/v1/tab/" + tabId);
    }

    //POST /api/traffic/v1/tab
    public Response postCreateNewTab(String xUserId, BodyBuilder body){
        return  sendPostRequest(xUserId,"/api/traffic/v1/tab", body);
    }

    //PUT /api/traffic/v1/tab/user - Arrange tabs for the user
    public Response putArrangeTabs(String xUserId, Object body) throws JsonProcessingException {
        return sendPutRequest(xUserId, "/api/traffic/v1/tab/user",body);
    }

}