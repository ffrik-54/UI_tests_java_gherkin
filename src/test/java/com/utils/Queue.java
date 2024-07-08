package com.utils;

public class Queue {

    private Object object;
    private boolean available = false;
    int attempts = 0;

    public synchronized Object get(int timeout) throws InterruptedException {

        // Wait for the queue is available to get the object with max attempt to avoid
        // infinite loop.
        while (!available && attempts++ <= 30)
            wait(timeout * 1000L);

        available = false;
        attempts = 0;
        notifyAll();
        return object;
    }

    public synchronized void put(Object object, int timeout) throws InterruptedException {

        // Wait for the queue is available to put the object with max attempt to avoid
        // infinite loop.
        while (available && attempts++ <= 30)
            wait(timeout * 1000L);

        this.object = object;
        available = true;
        attempts = 0;
        notifyAll();
    }

    public synchronized Object get() throws InterruptedException {

        return get(Config.TIMEOUT_SHORT);
    }

    public synchronized void put(Object object) throws InterruptedException {
        put(object, Config.TIMEOUT_SHORT);
    }
}