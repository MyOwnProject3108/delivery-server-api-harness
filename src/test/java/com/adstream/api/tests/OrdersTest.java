package com.adstream.api.tests;

import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;


/**
 * Created by nataliiamoroz on 14/10/2016.
 */
public class OrdersTest extends TestBase {

    private String ttm_userId;
    private String btm_userId;

    @BeforeClass
    public void init() throws IOException {
        ttm_userId = app.getProperty("TTM_userId");
        btm_userId = app.getProperty("BTM_userId");
    }

    @Test
    public void testOrderDetailsTTM() throws IOException, InterruptedException {
        //mock QCed only, non-ingested order
        Response activity = app.mock().createActivity(ttm_userId, "5800f6207af9be2d19d8e12c");
        activity.then().log().all().statusCode(202);
        Response orderDetails = app.rest().getOrderDetails(ttm_userId, "5800f6207af9be2d19d8e12c");
        String orderItemId = orderDetails.then().log().all().statusCode(200).and().extract().path("orderItems[0].orderItemId");
        Response orderItemDetails = app.rest().getOrderItemDetails(ttm_userId, orderItemId);
        orderItemDetails.then().log().all().statusCode(200);
    }

    @Test
    public void testOrderDetails() throws IOException, InterruptedException {
        //mock order with dubbing service
        Response activity = app.mock().createActivity(ttm_userId, "5800fa9c7af9be2d19d8e3f4");
        activity.then().log().all().statusCode(202);
        Response orderDetails = app.rest().getOrderDetails(ttm_userId, "5800fa9c7af9be2d19d8e3f4");
        orderDetails.then().log().all().statusCode(200);
        app.mock().shutdown();
    }

    @Test
    public void parseOrderDetails() throws IOException, InterruptedException {
        //mock order with dubbing service
        Response activity = app.mock().createActivity(ttm_userId, "5800fa9c7af9be2d19d8e3f4");
        activity.then().log().all().statusCode(202);
        Response orderDetails = app.rest().getOrderDetails(ttm_userId, "5800fa9c7af9be2d19d8e3f4");
        String dsId = orderDetails.then().log().all().statusCode(200).and().extract()
                .path("orderItems[0].additionalServices.dubbingServices[0]._id");
        app.mock().shutdown();
    }
}
