package com.contexts;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.data.Store;
import com.utils.Config;
import com.utils.DriverManager;
import lombok.Getter;
import lombok.Setter;

/**
 * Test Context
 * Common context variables for all tests are here
 * @author ffrik
 */
public class TestContext {
    @Getter
    private final DriverManager driverManager;

    @Getter
    private final ScenarioContext scenarioContext;

    @Setter
    @Getter
    private String testName = null;

    @Setter
    @Getter
    private String appName = null;

    @Getter
    @Setter
    private String baseUrl = null;

    @Getter
    @Setter
    private boolean webTest = false;

    @Getter
    @Setter
    private boolean mobileTest = false;

    @Getter
    private final long timestamp = new Date().getTime();

    public TestContext() {
	driverManager = new DriverManager(this);
	scenarioContext = new ScenarioContext();
    }

    public long getTestTimestamp() {
        return timestamp;
    }
}
