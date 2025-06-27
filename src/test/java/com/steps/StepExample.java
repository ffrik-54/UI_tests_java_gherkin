package com.steps;

import com.contexts.TestContext;
import com.utils.Config;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base step
 *
 * @author ffrik
 */
public abstract class StepExample {

    TestContext testContext;

    /**
     * switchTab() switch from current browser to chosen one (default: index n 1).
     *
     * @param driver (WebDriver).
     * @param index  (Integer) corresponds to tab index that you want to switch to.
     **/
    public static void switchTab(WebDriver driver, int index) {
        List<String> browserTabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(browserTabs.get(index));
    }

    /**
     * mouseClick() move to chosen element and click on it.
     *
     * @param driver (WebDriver).
     * @param by     corresponds to the element on which you want to click.
     */
    public static void mouseClick(WebDriver driver, By by) {
        WebElement element = driver.findElement(by);
        mouseClick(driver, element);
    }

    /**
     * mouseClick() move to chosen element and click on it.
     *
     * @param element (WebElement) corresponds to the element on which you want to click.
     */
    public static void mouseClick(WebDriver driver, WebElement element) {
        if (element != null) {
            new Actions(driver).moveToElement(element).perform();
        }
        new Actions(driver).click().perform();
    }

    public static void openNewTab(WebDriver driver) {
        Logger.getGlobal().log(Level.INFO, "url new tab");

        ((JavascriptExecutor) driver).executeScript("window.open()");
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(1));
    }

    /**
     * Get the index of the element in the table
     *
     * @param name,  the name of the element that we want to check in the Table
     * @param table, the html format table that we want to check in the Table
     * @return int index, the index found in the table
     **/
    public static int getTableIndex(WebDriver driver, String name, By table) {

        int index = 0;
        WebElement simpleTable = driver.findElement(table);
        List<WebElement> rows = simpleTable.findElements(By.tagName("tr"));

        for (WebElement row : rows) {
            index++;
            WebElement field = row.findElement(By.tagName("td"));
            if (field.getText().equals(name)) {
                return index;
            }
        }
        return 0;
    }

    /**
     * Get the size of the table
     *
     * @param table, the html format table that we want to check in the Table
     * @return int index, the size found of the table
     **/
    public static int getSizeTable(WebDriver driver, By table) {

        WebElement simpleTable = driver.findElement(table);
        List<WebElement> rows = simpleTable.findElements(By.tagName("tr"));
        return rows.size();
    }



    /**
     * Switch Button to Enable or Disable it
     *
     * @param elementClick    the button to switch
     * @param elementCheckBox the hide checkbox with the real state.
     * @param state           true to enable and false to disable.
     **/
    public static void switchButton(WebElement elementClick, WebElement elementCheckBox, boolean state) {

        if (state != elementCheckBox.isSelected()) {
            elementClick.click();
        }
    }

    /**
     * To use when normal scroll and click isn't working properly
     *
     * @param element
     */
    public static void scrollAndClickByJS(WebDriver driver, By element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(element));
    }

    /**
     * filledInField() fill a chosen field with input in 'content' parameter.
     *
     * @param driver  (WebDriver).
     * @param id      (By) corresponds to element in the page that you want to fill with text.
     * @param content (String) corresponds to text you want to send as input.
     **/
    public static void fillInField(WebDriver driver, By id, String content) {
        Wait<WebDriver> wait = new FluentWait<>(driver).withTimeout(Duration.ofSeconds(Config.TIMEOUT_MEDIUM))
            .pollingEvery(Duration.ofMillis(500)).ignoring(StaleElementReferenceException.class)
            .ignoring(NoSuchElementException.class);
        WebElement field = wait.until(ExpectedConditions.elementToBeClickable(id));
        field.clear();
        field.sendKeys(content);
    }

}
