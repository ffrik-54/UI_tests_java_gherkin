package com.contexts;

import com.utils.DriverManager;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Test Context
 * Common context variables for all tests are here
 * @author ffrik
 */
@Getter
public class TestContext {
    private final DriverManager driverManager;

    private final ScenarioContext scenarioContext;

    @Setter
    private String testName = null;

    @Setter
    private String appName = null;

    @Setter
    private boolean webTest = false;

    @Setter
    private boolean mobileTest = false;

    private final long timestamp = new Date().getTime();

    public TestContext() {
	driverManager = new DriverManager(this);
	scenarioContext = new ScenarioContext();
    }

    public long getTestTimestamp() {
        return timestamp;
    }
}
