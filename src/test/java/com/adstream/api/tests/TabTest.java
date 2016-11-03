package com.adstream.api.tests;

import com.adstream.api.model.NewTab;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.restassured.mapper.ObjectMapper;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ResponseBodyExtractionOptions;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import io.restassured.response.Response;
import org.w3c.dom.Entity;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.http.ContentType.fromContentType;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;

/**
 * Created by Faiyyaz.Shaik on 10/12/2016.
 */
public class TabTest extends TestBase {

    private String ttm_userId;
    private String btm_userId;
    private String businessUnitId;
    private String tabId;
    private String customTab;
    private String publicTab;
    private String generalTab;
    private String privateTab;
    private String generalTabId;


    @BeforeClass
    public void init() {
        ttm_userId = app.getProperty("TTM_userId");
        btm_userId = app.getProperty("BTM_userId");
        businessUnitId = app.getProperty("businessUnitId");
    }


    // GET Method Scenarios

    //GET - Unauthorised request
    @Test
    public void testRetrieveTabUnAuth() throws IOException {
        Response response = app.rest().getTabDetails(null);
        response
                .then().log().all().statusCode(400)
                .assertThat().body(equalTo("Request is missing required HTTP header 'X-User-Id'"));
    }

    //GET - Invalid authorisation
    @Test
    public void testRetrieveTabInvalidAuth() throws IOException {
        Response response = app.rest().getTabDetails(ttm_userId + "123");
        response
                .then().log().all().statusCode(403)
                .assertThat().body(equalTo("The supplied authentication is not authorized to access this resource"));
    }

    //GET - Retrieve tabs available to the current user
    @Test
    public void testRetrieveAllTabs() throws IOException {
        Response response = app.rest().getTabDetails(ttm_userId);
        response
                .then().log().all().statusCode(200)
                .and().assertThat().body(matchesJsonSchemaInClasspath("Tabs.json"));

    }


    // POST Method Scenarios


    //POST - Create a new tab with  UnAuthorised user
    @Test
    public void testCreateNewTabUnAuth() throws IOException {
        NewTab body = new NewTab().setName("API Tab UnAuth").setPublic(true).setDefault(true).setTabType("OrderItemClock")
                .setBusinessUnitId(businessUnitId);
        Response response = app.rest().createNewTab(null, body);
        response.then().log().all().statusCode(400).assertThat().body(equalTo("Request is missing required HTTP header 'X-User-Id'"));
    }

    //POST - Create a new tab with  Invalid Authorisation
    @Test
    public void testCreateNewTabInvalidAuth() throws IOException {
        NewTab body = new NewTab().setName("API Tab Invalid Auth").setPublic(true).setDefault(true).setTabType("OrderItemClock")
                .setBusinessUnitId(businessUnitId);
        Response response = app.rest().createNewTab(ttm_userId + "123", body);
        response.then().log().all().statusCode(403).assertThat().body(equalTo("The supplied authentication is not authorized to access this resource"));
    }


    //POST - Create a new tab for the current user (public -True and default- True)
    @Test
    public void testCreateNewTab() throws IOException {
        NewTab body = new NewTab().setName("API General Tab").setPublic(true)
                .setDefault(true).setTabType("OrderItemClock").setBusinessUnitId(businessUnitId);
        Response response = app.rest().createNewTab(ttm_userId, body);
        response
                .then().log().all().statusCode(200)
                .and().assertThat().body(matchesJsonSchemaInClasspath("POSTTab.json"));
        generalTab = response.then().contentType(JSON).extract().path("_id");
//        System.out.println("print general tab ID..." + tabId.toString());
    }

    //POST - Create a new tab for the current user (public - True and default - False)
    @Test
    public void testCreatePrivateTab() throws IOException {
        NewTab body = new NewTab().setName("API Private Tab")
                .setPublic(false).setDefault(true).setTabType("OrderItemClock").setBusinessUnitId(businessUnitId);
        Response response = app.rest().createNewTab(ttm_userId, body);
        response.then().log().all().statusCode(200).and().assertThat().body(matchesJsonSchemaInClasspath("POSTTab.json"));
        privateTab = response.then().contentType(JSON).extract().path("_id");

    }

    //POST - Create a new tab for the current user (public - False and default - True)
    @Test
    public void testCreatePublicTab() throws IOException {
        NewTab body = new NewTab().setName("API Public Tab")
                .setPublic(true).setDefault(false).setTabType("OrderItemClock").setBusinessUnitId(businessUnitId);
        Response response = app.rest().createNewTab(ttm_userId, body);
        response.then().log().all().statusCode(200).and().assertThat().body(matchesJsonSchemaInClasspath("POSTTab.json"));
        publicTab = response.then().contentType(JSON).extract().path("_id");

    }

    //POST - Create a new tab for the current user (public - False and default - false)
    @Test
    public void testCreateCustomTab() throws IOException {
        NewTab body = new NewTab().setName("API Custom Tab")
                .setPublic(false).setDefault(false).setTabType("OrderItemClock").setBusinessUnitId(businessUnitId);
        Response response = app.rest().createNewTab(ttm_userId, body);
        response.then().log().all().statusCode(200).and().assertThat().body(matchesJsonSchemaInClasspath("POSTTab.json"));
        customTab = response.then().contentType(JSON).extract().path("_id");

    }

    //POST - Create a new tab for the current user as BTM(public - False and default - false)
    @Test
    public void testCreateCustomBTMTab() throws IOException {
        NewTab body = new NewTab().setName("API Custom Tab")
                .setPublic(false).setDefault(false).setTabType("OrderItemSend").setBusinessUnitId(businessUnitId);
        Response response = app.rest().createNewTab(btm_userId, body);
        response.then().log().all().statusCode(200).and().assertThat().body(matchesJsonSchemaInClasspath("POSTTab.json"));

    }


    //PUT method Scenarios


    public String createGeneralTab() throws IOException {
        NewTab body = new NewTab().setName("API General Tab")
                .setPublic(false).setDefault(false).setTabType("OrderItemClock");
        Response response = app.rest().createNewTab(ttm_userId, body);
        response.then().log().all().statusCode(200).and().assertThat().body(matchesJsonSchemaInClasspath("POSTTab.json"));
        generalTabId = response.then().contentType(JSON).extract().path("_id");
        System.out.println("tab id is..."+ generalTabId.toString());
        return generalTabId;
    }


    //PUT /api/traffic/v1/tab  - update an existing tab with Invalid Authorisation
    @Test
    public void testUpdateTabWithUnAuth() throws IOException {
        createGeneralTab();
        NewTab body = new NewTab().setName("API Edit UnAuth").setPublic(true).setDefault(true).setTabType("OrderItemClock");

        Response response = app.rest().updateTab(null, body);
        response.then().log().all().statusCode(400).assertThat().body(equalTo("Request is missing required HTTP header 'X-User-Id'"));
    }

    //PUT /api/traffic/v1/tab  - update an existing tab with Invalid Authorisation
    @Test
    public void testUpdateTabWithInvalidAuth() throws IOException {
        createGeneralTab();
        NewTab body = new NewTab().setName("API Edit Invalid Auth").setPublic(true).setDefault(true).setTabType("OrderItemClock");
        Response response = app.rest().updateTab(ttm_userId + "123", body);
        response.then().log().all().statusCode(403).assertThat().body(equalTo("The supplied authentication is not authorized to access this resource"));
    }




    //PUT /api/traffic/v1/tab  - update an existing tab
    @Test
    public void testUpdateTab() throws IOException {
        createGeneralTab();
        NewTab body = new NewTab().setTabId(generalTabId).setName("API General Tab Edit").setPublic(false).setDefault(false).setTabType("OrderItemClock");
        Response response = app.rest().updateTab(ttm_userId, body);
        response.then().log().all().statusCode(200).and().body(equalTo("true"));

    }


    //PUT User tab level Scenarios


    //PUT - /api/traffic/v1/tab/user Update the order of the tabs for an unauthorised user
    @Test
    public void testTabOrderUnAuth() throws IOException {

        testCreateCustomTab();
        testCreatePrivateTab();
        String[] myTabId = {customTab, privateTab, publicTab, generalTab};
        Response response = app.rest().arrangeTabs(null, myTabId);
        response.then().log().all().statusCode(400).assertThat().body(equalTo("Request is missing required HTTP header 'X-User-Id'"));
    }


    //PUT - /api/traffic/v1/tab/user  Update the order of the tabs for the current user with invalid authorisation
    @Test
    public void testTabOrderInvalidAuth() throws IOException {
        testCreateCustomTab();
        testCreatePrivateTab();

        String[] myTabId = {customTab, privateTab};
        Response response = app.rest().arrangeTabs(ttm_userId + "123", myTabId);
        response.then().log().all().statusCode(403).assertThat().body(equalTo("The supplied authentication is not authorized to access this resource"));
    }


    //PUT - Update the order of the tabs for the current user not passing the body in the method.
    @Test
    public void testTabOrder() throws IOException {
        testCreateCustomTab();
        testCreatePrivateTab();
        testCreatePublicTab();
        testCreateNewTab();
        String[] myTabId = {customTab, privateTab, publicTab, generalTab};
        Response response = app.rest().arrangeTabs(ttm_userId, myTabId);
        response.then().log().all().statusCode(200).assertThat().body(equalTo("OK"));
    }


    //DELETE method Scenarios


    //DELETE - Delete a tab with Empty TabId...Response message needs to be fixed
    @Test
    public void testDeleteTabEmptyTabId() throws IOException {
        Response response = app.rest().deleteTab(ttm_userId, null);
        response
                .then().log().all().statusCode(405)
                .assertThat().body(equalTo("HTTP method not allowed, supported methods: GET, PUT, OPTIONS"));
    }

    //DELETE - Delete a tab with by providing invalid TabId
    @Test
    public void testDeleteTabInvalidTabId() throws IOException {
        Response response = app.rest().deleteTab(ttm_userId, tabId + "123");
        response
                .then().log().all().statusCode(405)
                .assertThat().body(equalTo("HTTP method not allowed, supported methods: GET, PUT, OPTIONS"));

    }

    //DELETE - Delete a tab with UnAuthorised User
    @Test
    public void testDeleteTabUnAuth() throws IOException {
        Response response = app.rest().deleteTab(null, tabId);
        response
                .then().log().all().statusCode(400)
                .assertThat().body(equalTo("Request is missing required HTTP header 'X-User-Id'"));
    }

    //DELETE - Delete a tab with UnAuthorised User
    @Test
    public void testDeleteTabInvalidAuth() throws IOException {
        Response response = app.rest().deleteTab(ttm_userId + "123", tabId);
        response
                .then().log().all().statusCode(403)
                .assertThat().body(equalTo("The supplied authentication is not authorized to access this resource"));
    }

    //DELETE - Delete a tab - This is a method which is called in other tests...
    public void testDeleteTab(String tabId) throws IOException {
        Response response = app.rest().deleteTab(ttm_userId, tabId);
        response.then().log().all().statusCode(200).assertThat().body(equalTo("OK"));

    }


    //DELETE - Delete a tab which was already deleted
    @Test
    public void testAlreadyDeletedTab() throws IOException {
        testCreateCustomTab();
        testDeleteTab(customTab);
        Response response = app.rest().deleteTab(ttm_userId, customTab);
        response
                .then().log().all().statusCode(404)
                .assertThat().body(equalTo("The requested resource could not be found but may be available again in the future."));
    }

    @Test(priority = 1)
    public void removeAllAPITabs() throws IOException {
        Response response = app.rest().getTabDetails(ttm_userId);

        JsonObject jsonObject = new JsonObject();
        List id_list = new ArrayList();
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = (JsonArray) parser.parse(response.asString());
        for (int i = 0; i < jsonArray.size(); i++) {
            jsonObject = jsonArray.get(i).getAsJsonObject();
            if (jsonObject.get("name").toString().contains("API"))
                id_list.add(jsonObject.get("_id"));
        }

        for (int j = 0; j < id_list.size(); j++) {

            testDeleteTab(id_list.get(j).toString().replace("\"", ""));
        }

    }
}


