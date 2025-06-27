package com.steps.web;

import com.components.web.CMP;
import com.contexts.TestContext;
import com.core.WebUI;
import com.utils.Report;
import com.utils.Timeout;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Lorsque;

/**
 * CMP step
 *
 * @author ffrik
 */

public class CMPSteps {

    private TestContext testContext;

    public CMPSteps(TestContext context) {
        testContext = context;
    }

    @Lorsque("le bouton {string} de la notice est cliquable")
    public void le_bouton_de_la_notice_est_cliquable(String btnConsents) {
        switch (btnConsents){
            case CMP.acceptAll:
                WebUI.verifyElementClickable(CMP.getBtnDidomiNoticeAgreeObject());
                break;
            case CMP.rejectAll:
                WebUI.verifyElementClickable(CMP.getBtnDidomiNoticeDisagreeObject());
                break;
            case CMP.customize:
                WebUI.verifyElementClickable(CMP.getBtnDidomiNoticeLearnMoreObject());
                break;
            default:
                Report.logError("le bouton de la notice Didomi " + btnConsents + " n'est pas pris en compte");
        }
    }

    @Lorsque("l'utilisateur clique sur le bouton {string} sur la notice consentement")
    public void clique_CTA_X_bandeau_cookies(String btnConsents) {
        switch (btnConsents) {
            case CMP.acceptAll:
                CMP.acceptAllConsents();
                break;
            case CMP.rejectAll:
                CMP.refuseAllConsents();
                break;
            case CMP.customize:
                CMP.customizeConsents();
                break;
            default:
                Report.logError("le bouton de la notice Didomi " + btnConsents + " n'est pas pris en compte");
        }
    }

    @Lorsque("le lien {string} de la notice est cliquable")
    public void le_lien_de_la_notice_est_cliquable(String linkConsents) {
        WebUI.verifyElementClickable(CMP.getLinkDidomiNoticeViewPartnersObject());
        Report.logInfo("Le lien " + linkConsents + " est cliquable");
    }

    @Lorsque("la notice Didomi est affichée")
    public void la_notice_Didomi_est_affichee() {
        WebUI.switchToDefaultContent();
        WebUI.verifyElementPresent(CMP.getPopupDidomiObject(), Timeout.SHORT_TIME);
        Report.logInfo("la notice Didomi est affichée");
    }

    @Alors("la notice Didomi disparaît")
    public void la_notice_Didomi_disparaît() {
        WebUI.verifyElementNotPresent(CMP.getPopupDidomiObject(), Timeout.SHORT_TIME);
        Report.logInfo("la notice Didomi disparaît");
    }



}
