package com.data;

import com.pages.mobile.IpadMainPage;
import com.steps.mobile.IpadBaseStep;
import io.appium.java_client.AppiumDriver;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;

import static org.testng.Assert.assertTrue;

/**
 * Example to represent objects linked to the SUT
 *
 * @author ffrik
 */
@Getter
@AllArgsConstructor
public class ObjectExample {

    @Setter
    private String param1;

    @Setter
    private int param2;

}