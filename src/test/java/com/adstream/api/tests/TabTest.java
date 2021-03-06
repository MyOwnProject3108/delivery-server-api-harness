package com.adstream.api.tests;

import com.adstream.api.model.NewTab;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 * Created by Faiyyaz.Shaik on 10/12/2016.
 */
public class TabTest extends TestBase {

    private String ttm_userId;
    private String ttm1_userId;
    private String btm_userId;
    private String ingestId;
    private String generalTabId;
    private String btm_hub_userId;
    private String btm1_hub_userId;
    private String non_traffic_userId;
    private String broadcast_buId;
    private String bTM1_userId;


    @BeforeClass
    public void init() {
        ttm_userId = app.getProperty("TTM_userId");
        ttm1_userId = app.getProperty("TTM1_userId");
        btm_userId = app.getProperty("BTM_userId");
        ingestId = app.getProperty("IngestId");
        btm_hub_userId = app.getProperty("BTM_Hub_userId");
        btm1_hub_userId = app.getProperty("BTM1_Hub_userId");
        non_traffic_userId = app.getProperty("Non_traffic_userId");
        broadcast_buId = app.getProperty("Broadcast_BUId");
        bTM1_userId = app.getProperty("BTM1_userId");
    }


    //// GET - Retrieve tabs available to the current user
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
                .and().assertThat().body(matchesJsonSchemaInClasspath("schema/TabsList.json"));
    }

    //GET - Retrieve tabs available to the current user BTM
    @Test
    public void testRetrieveAllTabsBTMHub() throws IOException {
        Response response = app.rest().getTabDetails(btm_hub_userId);
        response
                .then().log().all().statusCode(200)
                .and().assertThat().body(matchesJsonSchemaInClasspath("schema/TabsList.json"));
    }

    ////POST Create new tab for current user
    //POST - Create a new tab with  UnAuthorised user
    @Test
    public void testCreateNewTabUnAuth() throws IOException {
        NewTab body = new NewTab().setName("API Tab UnAuth").setPublic(true).setDefault(true).setTabType("OrderItemClock");
        Response response = app.rest().createNewTab(null, body);
        response.then().log().all().statusCode(400).assertThat().body(equalTo("Request is missing required HTTP header 'X-User-Id'"));
    }

    //POST - Create a new tab with  Invalid Authorisation
    @Test
    public void testCreateNewTabInvalidAuth() throws IOException {
        NewTab body = new NewTab().setName("API Tab Invalid Auth").setPublic(true).setDefault(true).setTabType("OrderItemClock");
        Response response = app.rest().createNewTab(ttm_userId + "123", body);
        response.then().log().all().statusCode(403).assertThat().body(equalTo("The supplied authentication is not authorized to access this resource"));
    }


    // Create a general tab method and call it in other methods
    public String createTab(String name,boolean isPublic, boolean isDefault, String tabType, String xUserId) throws IOException {
        NewTab body = new NewTab().setName(name)
                .setPublic(isPublic).setDefault(isDefault).setTabType(tabType);
        Response response = app.rest().createNewTab(xUserId, body);
        response.then().log().all().statusCode(200).and().assertThat().body(matchesJsonSchemaInClasspath("schema/Tab.json"));
       return response.then().contentType(JSON).extract().path("_id");
    }


    // Create Tab combinations method
    private List<Response> createTabCombinations(List<String> tabTypeList, String role, String xUserId) throws IOException {
        List<Boolean> booleanList = Arrays.asList(true, false);

        List<Response> responses = new ArrayList<>();

        for(String tabType : tabTypeList) {
            for(boolean access: booleanList) {
                for(boolean defaultV: booleanList) {
                    NewTab body = new NewTab();
                    body.setName("API " + role + "_" + tabType + access + defaultV);
                    body.setPublic(access);
                    body.setDefault(defaultV);
                    body.setTabType(tabType);

                    Response response = app.rest().createNewTab(xUserId, body);
                    responses.add(response);
                }
            }
        }
        return responses;
    }

    //Method for positive validation
    private void positiveValidation(List<Response> responses) {
        for (Response response : responses) {
            response.then().log().all().statusCode(200)
                    .and().assertThat().body(matchesJsonSchemaInClasspath("schema/Tab.json"));
        }
    }

    //Method for negative validation
    private void negativeValidation(List<Response> responses) {
        for (Response response : responses) {
            response.then().log().all().statusCode(403)
                    .and().assertThat().body(equalTo("The supplied authentication is not authorized to access this resource"));
        }
    }

    //POST create tab combination rules for TFM - This method creates all the possible combinations
    @Test
    public void testCreateTabCombTFM() throws IOException {
        List<String> tabTypeList = Arrays.asList("Order", "OrderItemClock", "OrderItemSend");
        List<Response> responses = createTabCombinations(tabTypeList, "TTM", ttm_userId);
        positiveValidation(responses);
    }

    //POST Create tab combinations rules for BTM - Send Tab - This method creates all the possible combinations
    @Test
    public void testCreateTabCombBTM() throws IOException{
        List<String> tabTypeList = Collections.singletonList("OrderItemSend");
        List<Response> responses = createTabCombinations(tabTypeList, "BTM", btm_userId);
        positiveValidation(responses);
    }

    //POST Create tab combinations rules for BTMHub for Clock and Send types
    @Test
    public void testCreateTabCombBTMHub() throws IOException{
        List<String> tabTypeList = Arrays.asList("OrderItemClock", "OrderItemSend");
        List<Response> responses = createTabCombinations(tabTypeList, "BTMHub", btm_hub_userId);
        positiveValidation(responses);
    }

    //Negative combinations for BTM
    //Bug - when both are false as per the checklist tabs shouldn't be created but it creates tab - Swagger sends OK response
    @Test(enabled = false)
    public void createTabNCombBTM() throws IOException{
        List<String> tabTypeList = Arrays.asList("Order", "OrderItemClock");
        List<Response> responses = createTabCombinations(tabTypeList, "BTM", btm_userId);
        negativeValidation(responses);
    }

    //Negative combinations for BTMHub
    //Bug - when both are false as per the checklist tabs shouldn't be created but it creates tab - Swagger sends OK response
    @Test(enabled = false)
    public void testCreateTabNCombBTMHub() throws IOException {
        List<String> tabTypeList = Collections.singletonList("Order");
        List<Response> responses = createTabCombinations(tabTypeList, "BTMHub", btm_hub_userId);
        negativeValidation(responses);
    }

    //Tab combinations for non-Traffic user
    //Bug - Scenario is passed but when I checked as an agency user by giving permission to Traffic it has created tabs
    @Test
    public void createTabNonTrafficUser() throws IOException{
        List<String> tabTypeList = Arrays.asList("Order", "OrderItemClock", "OrderItemSend");
        List<Response> responses = createTabCombinations(tabTypeList, "nonTraffic", non_traffic_userId);
        negativeValidation(responses);
    }

    //PUT update existing tab
    //PUT /api/traffic/v1/tab  - update an existing tab with unauthorised user
    @Test
    public void testUpdateTabWithUnAuth() throws IOException {
         generalTabId = createTab("API General Tab", false,false,"Order",ttm_userId);
        NewTab body = new NewTab().setTabId(generalTabId).setCreateByUserId(ttm_userId).setName("API Edit UnAuth").setPublic(true).setDefault(true).setTabType("Order");

        Response response = app.rest().updateTab(null, body);
        response.then().log().all().statusCode(400).assertThat().body(equalTo("Request is missing required HTTP header 'X-User-Id'"));
    }

    //PUT /api/traffic/v1/tab  - update an existing tab with Invalid Authorisation
    @Test
    public void testUpdateTabWithInvalidAuth() throws IOException {
         generalTabId = createTab("API General Tab", false,false,"Order",ttm_userId);
        NewTab body = new NewTab().setTabId(generalTabId).setCreateByUserId(ttm_userId).setName("API Edit Invalid Auth").setPublic(true).setDefault(true).setTabType("Order");
        Response response = app.rest().updateTab(ttm_userId + "123", body);
        response.then().log().all().statusCode(403).assertThat().body(equalTo("The supplied authentication is not authorized to access this resource"));
    }

    //PUT /api/traffic/v1/tab  - update an existing tab
    @Test
    public void testUpdateTab() throws IOException {
         generalTabId =  createTab("API General Tab", false,false,"Order",ttm_userId);
        NewTab body = new NewTab().setTabId(generalTabId).setCreateByUserId(ttm_userId).setName("API General Tab 123").setPublic(false).setDefault(false).setTabType("Order");
        Response response = app.rest().updateTab(ttm_userId, body);
        response.then().log().all().statusCode(200).assertThat().body(equalTo("true"));
    }

    //PUT - Update the order of the tabs for the current user not passing the body in the method.
    @Test
    public void testTabOrder() throws IOException {

       String privateTabId = createTab("API New Private Tab", false,false,"Order",ttm_userId);
       String publicTabId = createTab("API New Public Tab", true,true,"Order",ttm_userId);
        String[] myTabId = {privateTabId,publicTabId};
        Response response = app.rest().arrangeTabs(ttm_userId, myTabId);
        response.then().log().all().statusCode(200).assertThat().body(equalTo("OK"));
    }


    //PUT - /api/traffic/v1/tab/user Update the order of the tabs for an unauthorised user
    @Test
    public void testTabOrderUnAuth() throws IOException {
        String privateTabId = createTab("API New Private Tab", false,false,"Order",ttm_userId);
        String publicTabId = createTab("API New Public Tab", true,true,"Order",ttm_userId);
        String[] myTabId = {privateTabId,publicTabId};
        Response response = app.rest().arrangeTabs(null, myTabId);
        response.then().log().all().statusCode(400).assertThat().body(equalTo("Request is missing required HTTP header 'X-User-Id'"));
    }

    //PUT - /api/traffic/v1/tab/user  Update the order of the tabs for the current user with invalid authorisation
    @Test
    public void testTabOrderInvalidAuth() throws IOException {
        String privateTabId = createTab("API New Private Tab", false,false,"Order",ttm_userId);
        String publicTabId = createTab("API New Public Tab", true,true,"Order",ttm_userId);
        String[] myTabId = {privateTabId,publicTabId};
        Response response = app.rest().arrangeTabs(ttm_userId + "123", myTabId);
        response.then().log().all().statusCode(403).assertThat().body(equalTo("The supplied authentication is not authorized to access this resource"));
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
         generalTabId =  createTab("API New Private Tab", false,false,"Order",ttm_userId);
        Response response = app.rest().deleteTab(ttm_userId, generalTabId + "123");
        response
                .then().log().all().statusCode(405)
                .assertThat().body(equalTo("HTTP method not allowed, supported methods: GET, PUT, OPTIONS"));

    }

    //DELETE - Delete a tab with UnAuthorised User
    @Test
    public void testDeleteTabUnAuth() throws IOException {
         generalTabId =  createTab("API New Private Tab", false,false,"Order",ttm_userId);
        Response response = app.rest().deleteTab(null, generalTabId);
        response
                .then().log().all().statusCode(400)
                .assertThat().body(equalTo("Request is missing required HTTP header 'X-User-Id'"));
    }

    //DELETE - Delete a tab with UnAuthorised User
    @Test
    public void testDeleteTabInvalidAuth() throws IOException {
        generalTabId = createTab("API New Private Tab", false,false,"Order",ttm_userId);
        Response response = app.rest().deleteTab(ttm_userId + "123", generalTabId);
        response
                .then().log().all().statusCode(403)
                .assertThat().body(equalTo("The supplied authentication is not authorized to access this resource"));
    }

    //DELETE - Delete a tab which was already deleted
    @Test
    public void testAlreadyDeletedTab() throws IOException {
        generalTabId = createTab("API New Private Tab", false,false,"Order",ttm_userId);
        app.rest().deleteTab(ttm_userId, generalTabId)
                .then().log().ifError().statusCode(200).assertThat().body(equalTo("OK"));
        Response response = app.rest().deleteTab(ttm_userId, generalTabId);
        response
                .then().log().all().statusCode(404)
                .assertThat().body(equalTo("The requested resource could not be found but may be available again in the future."));
    }

    @Test(priority = 1)
    public void removeAllAPITabs() throws IOException {
        List<String> userList = Arrays.asList(ttm_userId, btm_hub_userId, btm_userId);

        for (String xUserId : userList) {
            Response response = app.rest().getTabDetails(xUserId);

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
                app.rest().deleteTab(xUserId, id_list.get(j).toString().replace("\"", ""))
                        .then().log().ifError().statusCode(200).assertThat().body(equalTo("OK"));
            }
        }
    }

    //PUT Tab Combination scenario
    //User = TTM; Type= Order; change public False -> True
    @Test
    public void testUpdateTTMAccess() throws IOException{
        generalTabId =  createTab("API New Private Tab", false,false,"Order",ttm_userId);
            NewTab body = new NewTab().setTabId(generalTabId).setBusinessUnitId(ingestId).setName("API TTM Access Edit")
                    .setPublic(true).setDefault(false).setTabType("Order");
            Response response = app.rest().updateTab(ttm_userId, body);
            response.then().log().all().statusCode(200).assertThat().body(equalTo("true"));
            app.rest().getTabById(ttm1_userId,generalTabId).then().log().ifError().statusCode(200);
        }

    //PUT Tab Combination scenario
    //User = BTM; Type = Send; change public false -> true
    @Test
    public void testUpdateBTMAccess() throws IOException {
        generalTabId = createTab("API New Private Tab", false,false,"OrderItemSend",btm_userId);
        NewTab body = new NewTab().setTabId(generalTabId).setBusinessUnitId(broadcast_buId).setName("API BTM Access Edit")
                .setPublic(true).setDefault(false).setTabType("OrderItemSend");
        Response response = app.rest().updateTab(btm_userId, body);
        response.then().log().all().statusCode(200).assertThat().body(equalTo("true"));
        app.rest().getTabById(bTM1_userId,generalTabId).then().log().ifError().statusCode(200);
    }

    //PUT Tab  Combination scenario
    //User = BTM Hub; Type = Clock; change public true -> false
    @Test
    public void testUpdateBTMHubAccess() throws IOException{
        generalTabId = createTab("API General Tab", true,false,"OrderItemClock",btm_hub_userId);
        NewTab  body = new NewTab().setTabId(generalTabId).setCreateByUserId(btm_hub_userId).setName("API HUB Access Edit")
                .setPublic(false).setDefault(false).setTabType("OrderItemClock");
        Response response = app.rest().updateTab(btm_hub_userId, body);
        response.then().log().all().statusCode(200).assertThat().body(equalTo("true"));
        app.rest().getTabById(btm1_hub_userId,generalTabId).then().log().ifError().statusCode(500);
    }



    //PUT Tab  Combination scenario
    //User = TTM; Type = Order; change xUserId TTM -> TTM1
    @Test
    public void testUpdateTTMOrder() throws IOException {
        generalTabId = createTab("API General Tab", true,false,"Order",ttm_userId);
        NewTab body = new NewTab().setTabId(generalTabId).setBusinessUnitId(ingestId).setName("API TTM & TTM1 Order Edit")
                .setPublic(true).setDefault(false).setTabType("Order");
        Response response = app.rest().updateTab(ttm1_userId,body);
       response.then().log().all().statusCode(200).assertThat().body(equalTo("true"));
       app.rest().getTabById(ttm_userId,generalTabId).then().log().ifError().statusCode(200);
       app.rest().getTabById(ttm1_userId,generalTabId).then().log().ifError().statusCode(200);

    }


    //PUT Tab  Combination scenario
    //User = TFM; change Type = Order -> Clock and name - Bug
    @Test(enabled = false)
    public void testUpdateTFMClock() throws IOException {
        generalTabId =  createTab("API General Tab", false,false,"Order",ttm_userId);
        NewTab body = new NewTab().setTabId(generalTabId).setCreateByUserId(ttm_userId).setName("API TTM Clock Edit")
                .setPublic(false).setDefault(false).setTabType("OrderItemClock");
        Response response = app.rest().updateTab(ttm_userId,body);
        response.then().log().all().statusCode(400).assertThat().body(equalTo(""));
    }


    //PUT Tab  Combination scenario
    //User = BTMHub; Type = Clock; change Public = true ->False and name - Bug

    @Test(enabled = false)
    public void testUpdateBTMHubSend() throws IOException {
        generalTabId =  createTab("API General Tab", false,false,"OrderItemClock",btm_hub_userId);
        NewTab body = new NewTab().setTabId(generalTabId).setCreateByUserId(btm_hub_userId).setName("API BTM HUB Send Edit")
                .setPublic(false).setDefault(false).setTabType("OrderItemSend");
        Response response = app.rest().updateTab(btm_hub_userId,body);
        response.then().log().all().statusCode(400).assertThat().body(equalTo(""));
    }

    //PUT Tab  Combination scenario
    //User = TTM; Type = Send; change Public = true ->False and name
    @Test
    public void testUpdateTTMSend() throws IOException {
        generalTabId = createTab("API General Tab", false,true,"OrderItemSend",ttm_userId);
        NewTab body = new NewTab().setTabId(generalTabId).setCreateByUserId(ttm_userId).setName("API TTM Send Edit")
                .setPublic(false).setDefault(false).setTabType("OrderItemSend");
        Response response = app.rest().updateTab(ttm_userId,body);
        response.then().log().all().statusCode(400).assertThat().body(containsString("Update tab error: Default tabs cannot be updated"));
    }

    //PUT Tab  Combination scenario
    //Change User TTM -> Non Traffic User; Type = Order
    @Test
    public void testUpdateUser() throws IOException {
        generalTabId = createTab("API General Tab", false,false,"Order",ttm_userId);
        NewTab body = new NewTab().setTabId(generalTabId).setCreateByUserId(ttm_userId).setName("API Update User Edit")
                .setPublic(false).setDefault(false).setTabType("Order");
        Response response = app.rest().updateTab(non_traffic_userId,body);
        response.then().log().all().statusCode(400).assertThat().body(containsString(("Tab [BSONObjectID(\\\"" +generalTabId+ "\\\")] not found")));
      }
    }



