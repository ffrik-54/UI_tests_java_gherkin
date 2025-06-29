package com.utils;

import io.appium.java_client.AppiumDriver;
import lombok.Getter;
import org.openqa.selenium.WebDriver;

import java.util.List;

/**
 * Mother Class to define the the Driver thread for the iPad and the Queue.
 *
 * @author ffrik-54
 */

public abstract class DriverThread extends Thread {

    @Getter
    private final WebDriver webDriver;
    @Getter
    private final AppiumDriver iosDriver;
    protected List<Queue> queues;

    public DriverThread(String name, AppiumDriver iosDriver, WebDriver webDriver, List<Queue> queues) {
        this.iosDriver = iosDriver;
        this.webDriver = webDriver;
        this.queues = queues;
    }

    public abstract void run();


    public static void wait(List<Thread> threads) throws InterruptedException {
        for (int i = 0; i < threads.size(); i++) {
            threads.get(i).join();
        }
    }
}