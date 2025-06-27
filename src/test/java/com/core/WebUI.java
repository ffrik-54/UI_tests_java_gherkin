package com.core;

import com.contexts.TestContext;
import com.utils.Config;
import com.utils.Timeout;
import lombok.Setter;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebUI {

    @Setter
    private static TestContext testContext;

    private static final String NOT_FOUND = "cannot find element : ";

    private static final String NOT_FOUND_CLICKABLE = "cannot find clickable element : ";

    private static final String RETRIES = " retries of ";

    private static final String UNIT_TIME = " seconds";

    public static void refresh() {
        testContext.getDriverManager().getDriver().navigate().refresh();
    }

    public static void enhancedClick(By element) {
        testContext.getDriverManager().getDriver().findElement(element).click();
    }

    public static void verifyElementClickable(By element) {
        WebDriverWait wait =
                new WebDriverWait(testContext.getDriverManager().getDriver(),Duration.ofSeconds(Timeout.SHORT_TIME));
        int retry = 0;
        while (retry < Config.NB_RETRY) {
            try {
                wait.until(ExpectedConditions.elementToBeClickable(element));
                return;
            } catch (NoSuchElementException | TimeoutException e) {
                Logger.getGlobal().log(Level.INFO, NOT_FOUND_CLICKABLE + element.toString());
            } catch (Exception e) {
                if (retry == Config.NB_RETRY - 1) {
                    Logger.getGlobal()
                            .log(Level.INFO, "on try {0} error {1} seconds", new Object[]{retry, e.toString()});
                }
            }
            retry++;

        }
        throw new NoSuchElementException(
                "NoSuchElementException #" + ", Element: " + element + " still not clickable after " + Config.NB_RETRY + RETRIES
                        + 2 + UNIT_TIME);
    }

    public static boolean waitForElementVisible(By id, int time){
        WebDriverWait wait =
                new WebDriverWait(testContext.getDriverManager().getDriver(),Duration.ofSeconds(time));
        /*
         * retry is used to catch a specific Saucelabs random error
         * UnsupportedCommandException try to do the command, if special exception,
         * retry N times, else the return exits the method
         */
        int retry = 0;
        while (retry < Config.NB_RETRY) {
            try {
                WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(id));
                if (element != null) {
                    return true;
                } else {
                    Logger.getGlobal().log(Level.INFO, "Element Not Present");
                    return false;
                }
            } catch (NoSuchElementException | TimeoutException e) {
                Logger.getGlobal().log(Level.INFO, NOT_FOUND + id.toString());
                return false;
            } catch (Exception e) {
                Logger.getGlobal().log(Level.INFO, "on try {0} error {1}", new Object[]{retry, e.toString()});
                retry++;
            }
        }
        if (retry == Config.NB_RETRY) {
            throw new RuntimeException("Error on isElementPresent after " + Config.NB_RETRY + " retry");
        }
        return false;
    }

    public static void switchToDefaultContent(){
        testContext.getDriverManager().getDriver().switchTo().defaultContent();
    }

    public static void verifyElementNotPresent(By id, int time){
        WebDriverWait wait =
                new WebDriverWait(testContext.getDriverManager().getDriver(),Duration.ofSeconds(time));
        int retry = 0;
        while (retry < Config.NB_RETRY) {
            try {
                wait.until(ExpectedConditions.invisibilityOfElementLocated(id));
                return;
            } catch (Exception e) {
                if (retry == Config.NB_RETRY - 1) {
                    Logger.getGlobal()
                            .log(Level.INFO, "on try {0} error wait timeout {1}", new Object[]{retry, e.toString()});
                }
            }
            retry++;
        }
        Logger.getGlobal().log(Level.INFO, "Element: {0} still present after {1} seconds",
                new Object[]{id, Config.NB_RETRY + RETRIES + time});
        throw new UnsupportedCommandException(
                "NoSuchElementExceptionElement, Element: " + id + " still present after " + Config.NB_RETRY + RETRIES + time
                        + UNIT_TIME);
    }

    public static void verifyElementPresent(By id, int time){

        WebDriverWait wait =
                new WebDriverWait(testContext.getDriverManager().getDriver(),Duration.ofSeconds(time));
            int retry = 0;
            while (retry < Config.NB_RETRY) {
                try {
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("didomi-notice-agree-button")));
                    return;
                } catch (Exception e) {
                    if (retry == Config.NB_RETRY - 1) {
                        Logger.getGlobal().log(Level.INFO, "on try {0} error {1}", new Object[]{retry, e.toString()});
                    }
                }
                retry++;
            }
            throw new NoSuchElementException(
                    "NoSuchElementException #" + ", Element: " + id + " still not visible after " + Config.NB_RETRY + RETRIES
                            + time + " seconds");
    }

    public static void deleteAllCookies() {
        testContext.getDriverManager().getDriver().manage().deleteAllCookies();
    }

    public static void navigateToUrl(String url) {
        testContext.getDriverManager().getDriver().get(url);
    }

}
