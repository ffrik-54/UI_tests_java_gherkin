package com.pages.web;

import com.components.web.CMP;
import com.core.WebUI;
import com.utils.Config;
import org.apache.commons.lang3.StringUtils;


public class BasePage {



    public static void initVisitOnPage(String path) {
        //ApiUtils.writeHAR();
        navigateToUrl(path);
        CMP.acceptAllConsents();
        //Partner.clickOnAcceptPartnerOffersBtn()
    }

    public static void navigateToUrl(String path) {
        //Partner.clickOnAcceptPartnerOffersBtn()
        WebUI.navigateToUrl(StringUtils.substringBeforeLast(Config.getWebUrl(), "?") + path);
    }

    public static void navigateToPage(String path) {
        //Partner.clickOnAcceptPartnerOffersBtn()
        WebUI.navigateToUrl(StringUtils.substringBeforeLast(Config.getWebUrl(), "?") + path);
    }
}