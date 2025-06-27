package com.steps.web;

import com.contexts.TestContext;
import com.core.WebUI;
import com.pages.web.HomePage;
import com.utils.Report;
import io.cucumber.java.fr.Etantdonné;

/**
 * CMP step
 *
 * @author ffrik
 */

public class HomeSteps {

    private TestContext testContext;

    public HomeSteps(TestContext context) {
        testContext = context;
    }

    @Etantdonné("un utilisateur accède pour la première fois au site TF1+")
    public void un_utilisateur_accede_pour_la_première_fois_au_site_TF1(){

        testContext.setWebTest(true);
        Report.logInfo("step : un utilisateur accède pour la première fois au site TF1+ : start");
        Report.logInfo("step : un utilisateur accède pour la première fois au site TF1+ : 2");
        HomePage.navigateToUrl("");
        Report.logInfo("step : un utilisateur accède pour la première fois au site TF1+ : 3");
        WebUI.deleteAllCookies();
        Report.logInfo("step : un utilisateur accède pour la première fois au site TF1+ : 4");
    }


}
