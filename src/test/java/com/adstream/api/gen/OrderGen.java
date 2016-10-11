package com.adstream.api.gen;

import com.adstream.api.gen.MyWrap.Meta;
import com.adstream.api.tests.TestBase;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created by nataliiamoroz on 03/10/2016.
 */

public class OrderGen extends TestBase {
    private String key;
    private String secret;
    private String token;
    private String orderId;
    private String orderItemId;


    @BeforeClass
    public void init() throws InvalidKeyException, NoSuchAlgorithmException {
        key = app.getProperty("key");
        secret = app.getProperty("secret");
    }

    @Test
    public void testAuth() throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        token = app.papi().auth(secret, key);
        Response response = app.papi().auth(token);
        response.then().log().all().statusCode(200);
    }

    @Test
    public void testOrderCreation() throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        //create draft order
        String body = "{\"meta\": {\"tv\": {\"marketId\": [\"352\"]}},\"type\": \"tv_order\"}\n";
        token = app.papi().auth(secret, key);
        Response draft = app.papi().createDraft(token, body);
        draft.then().statusCode(201);
        orderId = draft.then().extract().path("id");

        //create orderItem
        //ObjectMapper mapper = new ObjectMapper();
        MyWrap myBody = new MyWrap().withMeta(new Meta().withCommon(new Meta.CommonMeta().
                withClockNumber(String.valueOf((new Date().getTime()))).
                withfirstAirDate("2016-12-26T23:00:00.000Z").
                withDuration("12s").
                withTitle("test")));
        //mapper.writeValueAsString(myBody);
        Response orderItem = app.papi().createOrderItem(token, myBody, orderId);
        orderItem.then().log().ifError().and().statusCode(201);
        orderItemId = orderItem.then().extract().path("id");

        //add destination
        String dest = "{\"id\":[\"9064\"],\"serviceLevel\": {\"id\": [\"2\"]}}";
        Response destination = app.papi().addDestination(token, dest, orderId, orderItemId);
        destination.then().statusCode(201).and().assertThat().body("id", equalTo(orderItemId));

        //proceed with order
        String assetId = "{\"library\": [\"" + orderItemId + "\"]}";
        Response order = app.papi().orderProceed(token, assetId, orderId);
        order.then().statusCode(200).and().assertThat().body("id", equalTo(orderId));
    }

}
