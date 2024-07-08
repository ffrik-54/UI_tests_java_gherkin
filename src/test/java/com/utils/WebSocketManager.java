package com.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.java_websocket.client.WebSocketClient;

import com.data.OMSWebSocketClient;
import com.steps.BaseStep;

/**
 * Utils for interaction with Websocket
 * 
 * @author pierredesporte
 *
 */

public class WebSocketManager {

    /**
     * Websocket connection
     * 
     * @param String token, cognito token.
     * @throws URISyntaxException
     * 
     **/
    public void connection(String token) throws URISyntaxException {
	URI uri = new URI("wss:" + "//staging.ws.tiller.systems/event-sync?token=" + token);
	WebSocketClient client = OMSWebSocketClient.getInstance(uri);
	try {
	    client.connectBlocking();
	} catch (InterruptedException e) {
	    Logger.getGlobal().log(Level.SEVERE, "Error connection websocket : Interrupted! -> {0}", e.getMessage());
	    Thread.currentThread().interrupt();
	}
    }

    /**
     * Websocket close
     * 
     * @throws URISyntaxException
     * 
     **/
    public void close() throws URISyntaxException {
	OMSWebSocketClient client = OMSWebSocketClient.getInstance();
	try {
	    client.closeBlocking();
	} catch (InterruptedException e) {
	    Logger.getGlobal().log(Level.SEVERE, "Error closing websocket : Interrupted! -> {0}", e.getMessage());
	    Thread.currentThread().interrupt();
	}
    }

    /**
     * Send event on the Websocket
     * 
     * @param String subscribeEvent, subscribe event to send.
     * @param String action, action made by the evenT event to send.
     * @throws URISyntaxException
     * 
     **/
    public void send(String event, String action) throws URISyntaxException {
	OMSWebSocketClient client = OMSWebSocketClient.getInstance();

	int attempts = 0;
	while (!client.isOpen() && attempts++ <= 2) {
	    Logger.getGlobal().log(Level.INFO, "Attempt {0} : Wait 1 sec the websocket open", attempts);
	    BaseStep.sleep(1000);
	}

	if (client.isOpen()) {
	    client.setAction(action);
	    client.send(event);
	}
    }

    /**
     * Get last sync message after subscription on the Websocket
     * 
     **/
    public String getLastSyncMessage() {
	OMSWebSocketClient client = OMSWebSocketClient.getInstance();
	return client.getLastSyncMessage();
    }

    /**
     * Get List messages after subscription on the Websocket
     * 
     **/
    public List<String> getMessages() {
	OMSWebSocketClient client = OMSWebSocketClient.getInstance();
	return client.getMessages();
    }

    /**
     * Get List messages after subscription on the Websocket
     * 
     **/
    public void clearMessages() {
	OMSWebSocketClient client = OMSWebSocketClient.getInstance();
	client.clearMessages();
    }

    /**
     * Get last up notification acknowledge message after sending an up notification
     * on the Websocket
     * 
     **/
    public String getLastAcknowledge() {
	OMSWebSocketClient client = OMSWebSocketClient.getInstance();
	return client.getLastAcknowledge();
    }


    /**
     * Get last up notification acknowledge message after sending an up notification
     * on the Websocket
     * 
     **/
    public String getLastDownNotification() {
	OMSWebSocketClient client = OMSWebSocketClient.getInstance();
	return client.getLastDownNotification();
    }
}
