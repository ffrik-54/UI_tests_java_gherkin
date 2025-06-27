package com.hooks;

import com.contexts.TestContext;
import com.core.WebUI;
import com.utils.Config;
import com.utils.Json;
import com.utils.Report;
import io.appium.java_client.AppiumDriver;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cucumber Hooks
 *
 * @author ffrik-54
 */
public class Hooks {

    TestContext testContext;

    Scenario scenario;


    @Before
    public void beforeScenarios(Scenario scenario) throws XMLStreamException, IOException{
        Report.logInfo("hooks : before scenario :" + scenario.getName() + "start");
        this.scenario = scenario;
        testContext = new TestContext();
        testContext.getScenarioContext().setScenario(scenario);
        Config.loadGradleProperties();
        testContext.setWebTest(true);
        Config.setWebUrl("https://www.tf1.fr");
        testContext.getDriverManager().initializeDriver();
        Json.setTestContext(testContext);
        WebUI.setTestContext(testContext);
        Report.logInfo("hooks : before scenario :" + scenario.getName() + "end");
    }

    @After(order = 0)
    public void afterScenarios() {

        Logger.getGlobal().log(Level.INFO, "Closing drivers");
        // close drivers
        if (testContext.isWebTest()) {
            WebDriver webDriver = testContext.getDriverManager().getDriver();
            webDriver.quit();
            Logger.getGlobal().log(Level.INFO, "Closed WebDriver");
        }
        if (testContext.isMobileTest()) {
            ArrayList<AppiumDriver> appiumDrivers = testContext.getDriverManager().getIosDrivers();
            for (AppiumDriver appiumDriver : appiumDrivers) {
                appiumDriver.quit();
                Logger.getGlobal().log(Level.INFO, "Closed Appium Driver");
            }
        }
    }

    @After
    public void tearDown(Scenario scenario) throws IOException {
        if (scenario.isFailed()) {
            Logger.getGlobal().log(Level.INFO, "Taking snapshot");
            //testContext.getDriverManager().takeSnapshot();

        }
    }



}
