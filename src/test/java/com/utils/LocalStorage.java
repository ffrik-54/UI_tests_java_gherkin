package com.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;


/**
 * Cross browser 
 * local storage 
 * handling methods
 * 
 * @author bhamdi
 */ 

public class LocalStorage {

	private LocalStorage() {
	}

	/**
	 * clear local storage to avoid tutorials
	 */
	public static void clearLocalStorage(WebDriver webDriver)
	{
	    String keyPersist = "persist:onboarding";
	    String value = "{\"accounting_report\":\"true\","
	    		+ "\"accounting_report_hint\":\"true\","
	    		+ "\"current_tills_report\":\"true\","	    		
	    		+ "\"current_tills_report_hint\":\"true\","
	    		+ "\"current_tills_report_modal\":\"true\","
	    		+ "\"dashboard\":\"true\","
	    		+ "\"dashboard_hint\":\"true\","
	    		+ "\"dashboard_modal\":\"true\","
	    		+ "\"orders_report\":\"true\","	 
	    		+ "\"orders_report_hint\":\"true\","
	    		+ "\"product_sales_option_report_hint\":\"true\","
	    		+ "\"product_sales_sellers_content\":\"true\","
	    		+ "\"product_sales_report\":\"true\","
	    		+ "\"product_sales_report_hint\":\"true\","
	    		+ "\"reports__new\":\"true\","
	    		+ "\"reports__settings__hint\":\"true\","	    		
	    		+ "\"service_report_hint\":\"true\","
	    		+ "\"tills_report\":\"true\","
	    		+ "\"tills_report_hint\":\"true\"}";
	    
		JavascriptExecutor jsExecutor = (JavascriptExecutor) webDriver;
		jsExecutor.executeScript(String.format("window.localStorage.removeItem('%s');",keyPersist));
		jsExecutor.executeScript(String.format("window.localStorage.setItem('%s','%s');",keyPersist,value));
		jsExecutor.executeScript("window.setTimeout(function(){location.reload()},3000)");

	}
}
