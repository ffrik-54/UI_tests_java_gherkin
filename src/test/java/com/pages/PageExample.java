package com.pages;

import com.utils.Config;
import org.openqa.selenium.By;

/**
 * Base page
 *
 * @author ffrik
 */
public class PageExample {

    /**
     * Get the ID for android and ios if available
     *
     * @param id,    to pass the id of the element.
     * @param index, to manage the index for tables/cells (if 0 : no index)
     */
    public static By getId(String id, int index) {
        By result = null;
        if (Config.isPlatformAndroid()) {
            result = By.xpath("//*[@id='" + id + "'][" + index + "]");
        } else if (Config.isPlatformIos()) {
            if (index != 0) {
                result = By.xpath("(//*[@name='" + id + "'])[" + index + "]");
            } else {
                result = By.xpath("//*[@id='" + id + "']");
            }
        }
        return result;
    }

    /**
     * Get the ID for android and ios if available
     *
     * @param id, to pass the id of the element.
     */
    public static By getId(String id) {
        return getId(id, 0);
    }

}
