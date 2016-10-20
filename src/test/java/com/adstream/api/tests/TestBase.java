package com.adstream.api.tests;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import com.adstream.api.appmanager.ApplicationManager;


/**
 * Created by natla on 02/06/2016.
 */
public class TestBase {

  protected static final ApplicationManager app = new ApplicationManager();

//  @BeforeSuite
//  public void setUp() throws Exception {
//    app.init();
//  }

  @AfterSuite
  public void tearDown() throws Exception {
    app.mock().shutdown();
  }
}
