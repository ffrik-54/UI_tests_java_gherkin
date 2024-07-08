package com.utils;

import com.data.Order;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

/**
 * Mother Class to define the the Driver thread for the iPad and the Queue.
 *
 * @author pierredesporte
 */

public abstract class DriverThread extends Thread {

    private final WebDriver webDriver;
    private final AppiumDriver iosDriver;
    protected List<Queue> queues;
    protected Order order;

    public DriverThread(String name, AppiumDriver iosDriver, WebDriver webDriver, List<Queue> queues, Order order) {
        this.iosDriver = iosDriver;
        this.webDriver = webDriver;
        this.queues = queues;
        this.order = order;
    }

    public abstract void run();

    public AppiumDriver getIosDriver() {
        return iosDriver;
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public static List<Order> getOrdersFromQueues(List<Queue> queues) throws InterruptedException {
        List<Order> orders = new ArrayList<>();

        for (int i = 0; i < queues.size(); i++)
            orders.add((Order) queues.get(i).get());

        return orders;
    }

    public static void wait(List<Thread> threads) throws InterruptedException {
        for (int i = 0; i < threads.size(); i++) {
            threads.get(i).join();
        }
    }
}