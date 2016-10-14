package com.adstream.api.tests;

import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

/**
 * Created by nataliiamoroz on 14/10/2016.
 */
public class OrdersTest extends TestBase {

    private String ttm_userId;
    private String btm_userId;

    @BeforeClass
    public void init() {
        ttm_userId = app.getProperty("TTM_userId");
        btm_userId = app.getProperty("BTM_userId");
    }

    @Test
    public void testOrderDetailsTTM() throws IOException {
        Response activity = app.rest().createActivity("4ef31ce1766ec96769b399c0", "5800fa9c7af9be2d19d8e3f4");
        activity.then().log().all().statusCode(202);
        Response response = app.rest().getOrderDetails("4ef31ce1766ec96769b399c0", "5800fa9c7af9be2d19d8e3f4");
        response.
                then().log().all();
    }
}
