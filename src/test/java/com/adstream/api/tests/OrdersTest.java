package com.adstream.api.tests;
import org.testng.annotations.BeforeClass;

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

}
