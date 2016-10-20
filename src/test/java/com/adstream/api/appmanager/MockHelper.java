package com.adstream.api.appmanager;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.response.Response;

import java.io.IOException;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.port;

/**
 * Created by nataliiamoroz on 17/10/2016.
 */
public class MockHelper {
    private WireMockServer wms;

    public MockHelper(ApplicationManager app) {
        baseURI = app.getProperty("baseURI");
        port = new Integer(app.getProperty("ds_port"));

        wms = new WireMockServer();
        wms.start();
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

    public Response createActivity(String xUserId, String orderId) throws IOException, InterruptedException {
        Response resp = sendPostRequest(xUserId, "/admin/activities/order", orderId);
        Thread.sleep(500);
        return resp;
    }

    public void shutdown() {
        wms.shutdown();
    }
}
