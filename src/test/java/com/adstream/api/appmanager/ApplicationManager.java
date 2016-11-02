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
    private MockHelper mockHelper;

    public ApplicationManager() {
        properties = new Properties();
        String target = System.getProperty("target");
        try {
            properties.load(new FileReader(new File(String.format("src/test/resources/%s.properties", target))));
        } catch (IOException e) {
            throw new RuntimeException("Missing target property file into VM options: -Dtarget=<target.properties>");
        }
    }

    public String getProperty(String key) {
          return properties.getProperty(key);
      }

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
}
