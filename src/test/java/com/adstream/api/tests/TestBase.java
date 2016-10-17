package com.adstream.api.tests;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import com.adstream.api.appmanager.ApplicationManager;

import java.io.IOException;

/**
 * Created by natla on 02/06/2016.
 */
public class TestBase {

  protected static final ApplicationManager app = new ApplicationManager();
  //public WireMockServer wms = new WireMockServer();

  @BeforeSuite
  public void setUp() throws Exception {
    app.init();

   // wms.start();


  }

  @AfterSuite(alwaysRun = true)
  public void tearDown() throws IOException {
    //wms.stop();
    //app.stop();
  }

}
