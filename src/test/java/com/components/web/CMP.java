package com.components.web;

import com.core.WebUI;
import com.utils.Report;
import com.utils.Timeout;
import org.openqa.selenium.By;
/**
 * Component for consents
 *
 * @author ffrik
 */
public class CMP extends com.pages.PageExample{

    public final static String acceptAll = "TOUT ACCEPTER";
    public final static String rejectAll = "TOUT REFUSER";
    public final static String customize = "PARAMETRER VOS CHOIX";


    public static void acceptAllConsents(){
        try {
            Report.logInfo("La première tentative validation CMP ");
            settingCMP(getBtnDidomiNoticeAgreeObject());
        } catch (Exception ex1) {
            try {
                Report.logInfo("La deuxième tentative validation CMP après un refresh de la page");
                WebUI.refresh();
                settingCMP(getBtnDidomiNoticeAgreeObject());
            } catch (Exception ex2) {
                Report.logInfo("La troisième tentative validation CMP après un refresh de la page");
                WebUI.refresh();
                settingCMP(getBtnDidomiNoticeAgreeObject());
            }
        }
    }

    /**
     * @description refuse all Consents
     */
    public static void refuseAllConsents(){
        try {
            Report.logInfo("La première tentative Refus CMP ");
            settingCMP(getBtnDidomiNoticeDisagreeObject());
        } catch (Exception ex1) {
            try {
                Report.logInfo("La deuxième tentative refus CMP après un refresh de la page");
                WebUI.refresh();
                settingCMP(getBtnDidomiNoticeDisagreeObject());
            } catch (Exception ex2) {
                Report.logInfo("La troisième tentative refus CMP après un refresh de la page");
                WebUI.refresh();
                settingCMP(getBtnDidomiNoticeDisagreeObject());
            }
        }
    }

    /**
     * @description paramétrer des popups légales (Cookies, CMP...)
     */
    public static void settingCMP(By cmpObject) {
        if (WebUI.waitForElementVisible(cmpObject, Timeout.MEDIUM_TIME)) {
            WebUI.enhancedClick(cmpObject);
            Report.logInfo("CMP détectée ");
        }else {
            Report.logFailed("CMP non détectée ");
        }
    }

    public static void customizeConsents(){
        WebUI.enhancedClick(getBtnDidomiNoticeLearnMoreObject());
    }

    public static By getBtnDidomiNoticeAgreeObject() {
        return By.id("didomi-notice-agree-button");
    }

    public static By getBtnDidomiNoticeDisagreeObject() {
        return By.id("didomi-notice-disagree-button");
    }

    public static By getBtnDidomiNoticeLearnMoreObject() {
        return By.id("didomi-notice-learn-more-button");
    }

    public static By getLinkDidomiNoticeViewPartnersObject() {
        return By.className("didomi-notice-view-partners-link");
    }

    public static By getPopupDidomiObject() {
        return By.id("didomi-popup");
    }

}
