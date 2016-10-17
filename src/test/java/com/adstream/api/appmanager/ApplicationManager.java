package com.adstream.api.appmanager;

import com.github.tomakehurst.wiremock.WireMockServer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by natla on 02/06/2016.
 */
public class ApplicationManager {
    private final Properties properties;
    private RestHelper restHelper;
    private PapiHelper papiHelper;
    private MockHelper mockHelper;
    //private WireMockServer wms;

    public ApplicationManager() {
        properties = new Properties();
    }

    public void init() throws IOException {
        String target = System.getProperty("target", "qa19");
        properties.load(new FileReader(new File(String.format("src/test/resources/%s.properties", target))));
    }

    public String getProperty(String key) {
          return properties.getProperty(key);
      }

//    public void stop() {
//        if (wms != null) {
//            wms.stop();
//        }
//    }

    public RestHelper rest() {
        if (restHelper == null) {
          restHelper = new RestHelper(this);
        }
        return restHelper;
    }

    public MockHelper mock() {
        if (mockHelper == null) {
            mockHelper = new MockHelper(this);
        }
        return mockHelper;
    }

//    public WireMockServer getWms() {
//        if (wms == null) {
//            wms = new WireMockServer();
//            wms.start();
//        }
//        return wms;
//    }

    public PapiHelper papi() {
        if (papiHelper == null) {
            papiHelper = new PapiHelper(this);
        }
        return papiHelper;
    }

}
