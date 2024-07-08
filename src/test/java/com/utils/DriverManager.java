package com.utils;

import com.contexts.TestContext;
import com.steps.BaseStep;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import lombok.Getter;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.grid.config.ConfigException;
import org.openqa.selenium.html5.LocalStorage;
import org.openqa.selenium.html5.WebStorage;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteExecuteMethod;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.html5.RemoteWebStorage;
import org.openqa.selenium.safari.SafariDriver;

import javax.xml.stream.XMLStreamException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DriverManager {

    private final TestContext testContext;

    @Getter
    ArrayList<AppiumDriver> iosDrivers = new ArrayList<>();

    private WebDriver webDriver;

    public DriverManager(TestContext context) {
        this.testContext = context;
    }

    public static void killSafariDriver() {
        String s;
        Process p;
        try {
            p = Runtime.getRuntime().exec("if pgrep -x \"safaridriver\" > /dev/null then killall safaridriver");
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null) {
                Logger.getGlobal().log(Level.INFO, "line: " + s);
            }
            p.waitFor();
            Logger.getGlobal().log(Level.INFO, "exit: " + p.exitValue());
            p.destroy();
        } catch (InterruptedException e) {
            Logger.getGlobal().log(Level.SEVERE, "Error killing Safari driver : Interrupted!", e.getMessage());
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            Logger.getGlobal().log(Level.SEVERE, "Error killing Safari driver: ", e.getMessage());
        }
    }

    public WebDriver getDriver() {
        return webDriver;
    }

    public void initializeDriver() throws XMLStreamException, IOException {
        setUpDrivers(1, Json.get("data_web_url"));
    }

    public void initializeDriver(String url) throws XMLStreamException, IOException {
        setUpDrivers(1, url);
    }

    public void initializeDrivers(int nbDrivers) throws XMLStreamException, IOException {
        setUpDrivers(nbDrivers, Json.get("data_web_url"));
    }

    public void initializeDrivers(int nbDrivers, String url) throws XMLStreamException, IOException {
        setUpDrivers(nbDrivers, url);
    }

    public void setUpDrivers(int nbMobileDriver, String url) throws XMLStreamException, IOException {

        setUrl(url);
        if (testContext.isWebTest()) {
            if (testContext.getDriverManager().getDriver() == null) {
                setUpWebDriver();
            } else {
                testContext.getDriverManager().getDriver().get(testContext.getBaseUrl());
            }
            setDefaultCookies(webDriver);
            webDriver.navigate().refresh();
        }
        if (testContext.isMobileTest()) {
            setUpMobileDrivers(nbMobileDriver);
        }
    }

    public void setUpWebDriver() throws XMLStreamException, MalformedURLException {
        String url = testContext.getBaseUrl();
        if (!url.contains("client_id")) {
            url += Json.get("data_resource_login");
        }
        setUpWebDriver(url, null);
    }

    public void setUpWebDriver(String url, Dimension dimension) throws XMLStreamException, MalformedURLException {
        if ((Config.getWebCloudType() != null) && (Config.getWebCloudType().equals("Browserstack"))) {
            setUpWebDriverBrowserStack(url, dimension);
        } else {
            setUpWebDriverLocal(url, dimension);
        }
    }

    public void setUpWebDriverBrowserStack(String url, Dimension dimension)
            throws MalformedURLException {

        if (Config.isWebBrowserChrome()) {
            loadChromeCapabilitiesBrowserStack();
        }

        configWebDriver(webDriver, url, dimension);

        clearLocalStorageBrowserStack();
    }

    public void setUpWebDriverLocal(String url, Dimension dimension) {
        if (Config.isWebBrowserChrome()) {
            loadChromeCapabilities();
        }

        if (Config.isWebBrowserFirefox()) {
            loadFirefoxCapabilities();
        }

        if (Config.isWebBrowserSafari()) {
            loadSafariCapabilities();
        }

        configWebDriver(webDriver, url, dimension);

        // TODO Find a way to clear local Storage Safari
        if (!Config.isWebBrowserSafari()) {
            clearLocalStorage();
        }
    }

    public void setUpMobileDrivers(int nbDriver) throws XMLStreamException, IOException {
        String configFile = null;

        testContext.setAppName(Json.get("env_app_name"));
        Logger.getGlobal().log(Level.INFO, "number of mobile drivers: {0}", nbDriver);

        if (Config.getConfigFiles().size() != nbDriver) {
            throw new ConfigException("Bad configuration file : the list of config file is not correct");
        }

        nbDriver -= iosDrivers.size();
        for (int i = 0; i < nbDriver; i++) {
            if (!BaseStep.isNullOrEmpty(Config.getConfigFiles().get(i))) {
                configFile = Config.getConfigFiles().get(i); // get the config file to determine the driver properties
            } else {
                configFile = "";
            }

            if (configFile.contains("local")) {
                if (BaseStep.isNullOrEmpty(Config.getIpaPath())) {
                    throw new ConfigException("Missing property IPA path");
                } else {
                    Logger.getGlobal().log(Level.INFO, "Mobile driver: {0} : Local", i);
                    iosDrivers.add(i, setUpMobileDriver(i, false));
                    continue;
                }
            } else if (configFile.contains("simulator")) {
                if (BaseStep.isNullOrEmpty(Config.getAppPath())) {
                    throw new ConfigException("Missing property APP path");
                } else {
                    // we have a path app for the simulator available -> use simulator for the
                    // current driver
                    Logger.getGlobal().log(Level.INFO, "Mobile driver: {0} : Simulator", i);
                    iosDrivers.add(i, setUpMobileDriver(i, true));
                    continue;
                }
            } else if (configFile.contains("browserstack")) {
                if (BaseStep.isNullOrEmpty(Config.getAppIdBrowserstack())) {
                    throw new ConfigException("Missing property Browserstack APP ID");
                } else {
                    // we have browserstack available -> use browserstack for the current driver
                    Logger.getGlobal().log(Level.INFO, "Mobile driver: {0} : Browserstack", i);
                    iosDrivers.add(i, setUpMobileDriverBrowserStack(i));
                    continue;
                }
            } else if (configFile.contains("saucelabs")) {
                if (BaseStep.isNullOrEmpty(Config.getAppIdSaucelabs())) {
                    throw new ConfigException("Missing property SauceLabs APP ID");
                } else {
                    // we have saucelabs available -> use saucelabs for the current driver
                    Logger.getGlobal().log(Level.INFO, "Mobile driver: {0} : Saucelabs", i);
                    iosDrivers.add(i, setUpMobileDriverSaucelabs(i));
                    continue;
                }
            } else if (configFile.contains("mobiles_farm")) {
                if (BaseStep.isNullOrEmpty(Config.getDeviceFarmAppPath())) {
                    throw new ConfigException("Missing property mobiles farm IPA path");
                } else {
                    Logger.getGlobal().log(Level.INFO, "Mobile driver: {0} : Mobiles Farm", i);
                    iosDrivers.add(i, setUpMobileDriverFarm(i));
                    continue;
                }
            } else {
                throw new ConfigException("Device configuration is missing or incorrect. See README.md");
            }
        }
    }

    public void configWebDriver(WebDriver driver, String url, Dimension dimension) {

        if (dimension != null) {
            driver.manage().window().setSize(dimension);
        } else {
            driver.manage().window().setSize(new Dimension(Config.DIMENSION_WIDTH_FHD, Config.DIMENSION_HEIGHT_FHD));
        }

        if (url != null) {
            if (url.contains(Json.get("data_resource_login"))) {
                url += "?lng=" + Config.getLanguage();
            }
            Logger.getGlobal().log(Level.INFO, "web url : {0}", url);
            driver.get(url);
            setDefaultCookies(driver);
        }
    }

    public void setDefaultCookies(WebDriver driver) {
        driver.manage().addCookie(new Cookie("OptanonConsent",
                "isGpcEnabled=0&datestamp=Tue+Aug+29+2023+14%3A00%3A30+GMT%2B0200+(Central+European+Summer+Time)&version=202307.1.0&browserGpcFlag=0&isIABGlobal=false&hosts=&consentId=98509179-776c-402e-b927-4b8c3adbeac6&interactionCount=2&landingPath=NotLandingPage&groups=C0001%3A1%2CC0002%3A1%2CC0003%3A1&AwaitingReconsent=false"));
        driver.manage().addCookie(new Cookie("OptanonAlertBoxClosed", "2023-08-29T12:00:30.029Z"));
    }

    public AppiumDriver setUpMobileDriver(int index, boolean simulator) throws XMLStreamException, IOException {
        AppiumDriver driver = null;

        if (Config.isPlatformAndroid()) {
            driver = loadAndroidCapabilities();
        }

        if (Config.isPlatformIos()) {
            driver = loadIosCapabilities(index, simulator);
        }

        return driver;
    }

    public void loadChromeCapabilities() {
        System.setProperty("webdriver.chrome.driver", Config.getDriverPath());
        ChromeOptions options = new ChromeOptions();

        Map<String, Object> prefs = new HashMap<>();

        if (Config.isHeadlessActivated()) {
            options.addArguments("--headless=new");
            // THIS DEACTIVATES LANG CONFIG chrome driver bug :
            // https://bugs.chromium.org/p/chromedriver/issues/detail?id=1925
        }

        if (Config.isProxyActivated()) {

            String strProxy = Json.get("data_proxy_url");

            Proxy proxy = new Proxy();
            proxy.setAutodetect(false);
            proxy.setHttpProxy(strProxy);
            proxy.setSslProxy(strProxy);
            proxy.setNoProxy("no_proxy-var");
            options.setCapability("proxy", proxy);
        }

        options.addArguments("--lang=" + Config.getLanguage() + "-" + Config.getCountry());
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        prefs.put("intl.accept_languages", Config.getLanguage());

        options.setExperimentalOption("prefs", prefs);
        webDriver = new ChromeDriver(options);

    }

    public void loadFirefoxCapabilities() {
        System.setProperty("webdriver.gecko.driver", Config.getDriverPath());
        FirefoxOptions options = new FirefoxOptions();

        if (Config.isHeadlessActivated()) {
            options.addArguments("--headless");
        }

        if (Config.isProxyActivated()) {

            String strProxy = Json.get("data_proxy_url");

            Proxy proxy = new Proxy();
            proxy.setAutodetect(false);
            proxy.setHttpProxy(strProxy);
            proxy.setSslProxy(strProxy);
            proxy.setNoProxy("no_proxy-var");
            options.setCapability("proxy", proxy);
        }

        FirefoxProfile firefoxProfile = new FirefoxProfile();
        firefoxProfile.setPreference("intl.accept_languages", Config.getLanguage() + "-" + Config.getCountry());
        options.setProfile(firefoxProfile);
        webDriver = new FirefoxDriver(options);
    }

    public void loadSafariCapabilities() {
        System.setProperty("webdriver.safari.driver", Config.getDriverPath());
        webDriver = new SafariDriver();
        webDriver.manage().window().maximize();
    }

    public AppiumDriver loadAndroidCapabilities() throws MalformedURLException {
        UiAutomator2Options options =
                new UiAutomator2Options().setPlatformVersion(Config.getAndroidVersion()).setPlatformName("Android")
                        .setDeviceName(Config.getPhoneModel()).setAutoGrantPermissions(true)
                        .setAppPackage(Config.getPackageName()) // This package name of your app (you can get it from apk info
                        // app)
                        .setAppActivity(Config.getLaunchableActivity()); // This is Launcher activity of your app (you can get
        // it from apk info app)

        // Set other capabilities, those who do not have defined methods
        options.setCapability("BROWSER_NAME", "Android");
        options.setCapability("autoGrantPermissions", "true");
        options.setCapability("autoAcceptAlerts", "true");
        options.setCapability("locationServicesAuthorized", "true");

        // Create RemoteWebDriver instance and connect to the Appium server
        // It will launch the Calculator App in Android Device using the configurations
        // specified in Desired Capabilities

        return new AndroidDriver(new URL(Config.getAppiumServerUrl()), options);
    }

    public AppiumDriver loadIosCapabilities(int index, boolean simulator) throws IOException {
        // Get the configuration driver from the config file given in the
        // local/gradle.properties
        String config = Json.getArrayFromJsonFile(Config.getConfigFiles().get(index), "capabilities");
        Logger.getGlobal().log(Level.INFO, "automationName: {0}", Json.getFromString(config, "automationName"));
        Logger.getGlobal().log(Level.INFO, "platformName: {0}", Json.getFromString(config, "platformName"));
        Logger.getGlobal().log(Level.INFO, "platformVersion: {0}", Json.getFromString(config, "platformVersion"));
        Logger.getGlobal().log(Level.INFO, "deviceName: {0}", Json.getFromString(config, "deviceName"));

        XCUITestOptions options = new XCUITestOptions().setAutomationName(Json.getFromString(config, "automationName"))
                .setPlatformName(Json.getFromString(config, "platformName"))
                .setPlatformVersion(Json.getFromString(config, "platformVersion"))
                .setDeviceName(Json.getFromString(config, "deviceName"));

        if (!simulator) {
            // Set the udid and bundleId only for the real device and the path of the IPA
            // file .
            Logger.getGlobal().log(Level.INFO, "udid: {0}", Json.getFromString(config, "udid"));
            options.setUdid(Json.getFromString(config, "udid")).setBundleId(Config.getPackageName())
                    .setApp(Config.loadProperty("local_ipad_ipa_path").replace("*", Config.loadProperty("env")));
        } else
        // Set the path of the APP file.
        {
            options.setApp(Config.loadProperty("local_ipad_app_path").replace("*", Config.loadProperty("env")));
        }

        options.setLanguage(Config.getLanguage()).setFullReset(false).setNoReset(Config.isNoReset())
                .setAutoAcceptAlerts(true).setCommandTimeouts(Duration.ofSeconds(800));

        String url;
        if (BaseStep.isNullOrEmpty(Config.getUrl())) {
            url = Config.getAppiumServerUrl();
        } else {
            Logger.getGlobal().log(Level.INFO, "systemPort: {0}", Json.getFromString(config, "systemPort"));
            options.setWdaLocalPort(Integer.valueOf(Json.getFromString(config, "systemPort")));
            options.setCapability("systemPort", Json.getFromString(config, "systemPort"));
            url = Config.getUrl();
        }
        Logger.getGlobal().log(Level.INFO, "Appium url: {0}", url);
        // Set timeout to 900secs (60secs by default)
        options.setCapability("appium:newCommandTimeout", 900);
        return new IOSDriver(new URL(url), options);
    }

    public void loadChromeCapabilitiesBrowserStack() throws MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();

        caps.setCapability("os", "Windows");
        caps.setCapability("os_version", "10");
        caps.setCapability("browser", "Chrome");
        caps.setCapability("browser_version", "86");
        caps.setCapability("name", Json.get("browserstack_username") + "'s First Test");

        webDriver = new RemoteWebDriver(new URL(
                "https://" + Config.loadProperty("browserstack_username") + ":" + Config.loadProperty(
                        "browserstack_automate_key") + Config.loadProperty("browserstack_url_web")), caps);
    }

    public void clearLocalStorageBrowserStack() {
        RemoteExecuteMethod executeMethod = new RemoteExecuteMethod((RemoteWebDriver) webDriver);
        RemoteWebStorage webStorage = new RemoteWebStorage(executeMethod);
        LocalStorage storage = webStorage.getLocalStorage();

        String keyPersist = "persist:onboarding";
        String value = "{\"accounting_report\":\"true\"," + "\"accounting_report_hint\":\"true\","
                + "\"current_tills_report\":\"true\"," + "\"current_tills_report_hint\":\"true\","
                + "\"current_tills_report_modal\":\"true\"," + "\"dashboard\":\"true\"," + "\"dashboard_hint\":\"true\","
                + "\"dashboard_modal\":\"true\"," + "\"orders_report\":\"true\"," + "\"orders_report_hint\":\"true\","
                + "\"product_sales_report\":\"true\"," + "\"product_sales_report_hint\":\"true\","
                + "\"reports__new\":\"true\"," + "\"reports__settings__hint\":\"true\","
                + "\"service_report_hint\":\"true\"," + "\"tills_report\":\"true\"," + "\"tills_report_hint\":\"true\","
                + "\"_persist\":\"{\"version\":-1,\"rehydrated\":true}\"}";

        storage.setItem(keyPersist, value);

        webDriver.navigate().refresh();
    }

    public void clearLocalStorage() {
        LocalStorage local = ((WebStorage) webDriver).getLocalStorage();

        String keyPersist = "persist:onboarding";
        String value = "{\"accounting_report\":\"true\"," + "\"accounting_report_hint\":\"true\","
                + "\"current_tills_report\":\"true\"," + "\"current_tills_report_hint\":\"true\","
                + "\"current_tills_report_modal\":\"true\"," + "\"dashboard\":\"true\"," + "\"dashboard_hint\":\"true\","
                + "\"dashboard_modal\":\"true\"," + "\"orders_report\":\"true\"," + "\"orders_report_hint\":\"true\","
                + "\"product_sales_report\":\"true\"," + "\"product_sales_report_hint\":\"true\","
                + "\"reports__new\":\"true\"," + "\"reports__settings__hint\":\"true\","
                + "\"service_report_hint\":\"true\"," + "\"tills_report\":\"true\"," + "\"tills_report_hint\":\"true\","
                + "\"_persist\":\"{\\\"version\\\":-1,\\\"rehydrated\\\":true}\"}";

        Logger.getGlobal().log(Level.INFO, "Persist values:", value);
        local.setItem(keyPersist, value);
        webDriver.navigate().refresh();

    }

    public void setUrl(String url) {
        String env_prefix = null;
        String url_suffix = null;
        if (Config.getPreview_env() == null || Config.getPreview_env().isEmpty()) {
            env_prefix = Json.get("env_url_prefix");
            url_suffix = "tillersystems.com";
        } else {
            env_prefix = Config.getPreview_env();
            url_suffix = "tiller.systems";
        }
        testContext.setBaseUrl(
                url.replace("ENV", env_prefix).replace("PROTOCOL", Config.getProtocol()).replace("URL_SUFFIX", url_suffix));
        Logger.getGlobal().log(Level.INFO, "Set Base url : {0}", testContext.getBaseUrl());
    }

    public AppiumDriver setUpMobileDriverSaucelabs(int index) throws XMLStreamException, IOException {
        // Get the configuration driver from the config file given in the
        // local/gradle.properties
        String config = Json.getArrayFromJsonFile(Config.getConfigFiles().get(index), "capabilities");

        MutableCapabilities caps = new MutableCapabilities();
        caps.setCapability("platformName", Json.getFromString(config, "platformName"));
        caps.setCapability("appium:app", "storage:" + Config.loadProperty("saucelabs_app"));

        // Specify device and os_version for testing
        Logger.getGlobal().log(Level.INFO, "device: {0}", Json.getFromString(config, "deviceName"));
        caps.setCapability("appium:deviceName", Json.getFromString(config, "deviceName"));
        Logger.getGlobal().log(Level.INFO, "version: {0}", Json.getFromString(config, "platformVersion"));
        caps.setCapability("appium:platformVersion", Json.getFromString(config, "platformVersion"));
        caps.setCapability("appium:autoAcceptAlerts", "true");

        caps.setCapability("appium:automationName", "xcuitest");
        MutableCapabilities sauceOptions = new MutableCapabilities();
        // Set other Saucelabs capabilities

        sauceOptions.setCapability("appium:language", Config.loadProperty("language"));
        sauceOptions.setCapability("appium:locale", Config.loadProperty("country"));
        sauceOptions.setCapability("appium:orientation", Json.getFromString(config, "orientation"));
        sauceOptions.setCapability("appium:project", "Tiller POS");
        sauceOptions.setCapability("build", Config.getBuildType() + " - " + Config.getBuildDate());
        sauceOptions.setCapability("name", testContext.getTestName());
        sauceOptions.setCapability("cacheId", Config.getCacheId());
        sauceOptions.setCapability("appiumVersion", "2.0.0");
        Logger.getGlobal()
                .log(Level.INFO, "build name used: {0}", Config.getBuildType() + " - " + Config.getBuildDate());
        Logger.getGlobal().log(Level.INFO, "cacheId used: {0}", Config.getCacheId());

        // Set timeout to 900secs (60secs by default)
        sauceOptions.setCapability("appium:newCommandTimeout", 900);
        caps.setCapability("sauce:options", sauceOptions);
        String url = Config.loadProperty("saucelabs_url_app");
        Logger.getGlobal().log(Level.INFO, "url: {0}", url);

        // Initialize the remote Webdriver using BrowserStack remote URL
        // and desired capabilities defined above
        IOSDriver driver = new IOSDriver(new URL(url), caps);
        Logger.getGlobal()
                .log(Level.INFO, "driver successfully created in Saucelabs: {0}", driver.getCapabilities().toString());
        return driver;
    }

    public AppiumDriver setUpMobileDriverFarm(int index) throws IOException {

        String config = Json.getArrayFromJsonFile(Config.getConfigFiles().get(index), "capabilities");
        String udids = Objects.requireNonNull(Json.getFromString(config, "udids"))
                .replaceAll("[\\[\\]]", "").replaceAll("\"", "");
        Logger.getGlobal().log(Level.INFO, "automationName: {0}", Json.getFromString(config, "automationName"));
        Logger.getGlobal().log(Level.INFO, "platformName: {0}", Json.getFromString(config, "platformName"));
        Logger.getGlobal().log(Level.INFO, "udids: {0}", udids);


        XCUITestOptions options = new XCUITestOptions().setAutomationName(Json.getFromString(config, "automationName"))
                .setPlatformName(Json.getFromString(config, "platformName"))
                .setApp(Config.loadProperty("mobiles_farm_app_path"));

        options.setLanguage(Config.getLanguage()).setFullReset(false).setNoReset(Config.isNoReset())
                .setAutoAcceptAlerts(true).setCommandTimeouts(Duration.ofSeconds(800));

        String url = null;
        if (BaseStep.isNullOrEmpty(Config.getUrl())) {
            url = Config.getAppiumServerUrl();
        }
        Logger.getGlobal().log(Level.INFO, "Appium url: {0}", url);
        // Set timeout to 900secs (60secs by default)
        options.setCapability("appium:showXcodeLog", true);
        options.setCapability("appium:newCommandTimeout", 900);
        options.setCapability("appium:udids", udids);
        IOSDriver driver = new IOSDriver(new URL(url), options);
        Files.writeFile(Config.getSessionIdsFilePath(), testContext
                .getScenarioContext()
                .getScenario()
                .getName()
                .replaceAll("\"", "") + " -> " + driver.getSessionId(), true);
        return driver;
    }

    public AppiumDriver setUpMobileDriverBrowserStack(int index) throws XMLStreamException, IOException {
        // Get the configuration driver from the config file given in the
        // local/gradle.properties
        String config = Json.getArrayFromJsonFile(Config.getConfigFiles().get(index), "capabilities");

        DesiredCapabilities caps = new DesiredCapabilities();

        // Set your access credentials
        caps.setCapability("browserstack.user", Config.loadProperty("browserstack_username"));
        caps.setCapability("browserstack.key", Config.loadProperty("browserstack_automate_key"));

        // Set URL of the application under test
        caps.setCapability("app", Config.loadProperty("browserstack_app_id"));

        // Specify device and os_version for testing
        Logger.getGlobal().log(Level.INFO, "device: {0}", Json.getFromString(config, "deviceName"));
        caps.setCapability("device", Json.getFromString(config, "deviceName"));

        Logger.getGlobal().log(Level.INFO, "version: {0}", Json.getFromString(config, "version"));
        caps.setCapability("os_version", Json.getFromString(config, "version"));
        caps.setCapability("language", Config.getLanguage());

        // geoLocation feature to test the different country :
        // caps.setCapability("browserstack.geoLocation", Config.getCountry());

        // Set other BrowserStack capabilities
        caps.setCapability("deviceOrientation", "landscape");
        caps.setCapability("project", "Tiller POS");
        caps.setCapability("build", Config.getBuildType());
        caps.setCapability("name", testContext.getTestName());

        // Set the timeout duration to 5min to avoid the fail during precondition and
        // post conditions.
        // Only for MultiDriver.
        if (index > 1) {
            caps.setCapability("browserstack.idleTimeout", "300");
        }

        if (Config.isProxyActivated()) {
            caps.setCapability("browserstack.local", "true");
            /*
             * TODO Local bsLocal = new Local(); HashMap<String, String> bsLocalArgs = new
             * HashMap<String, String>(); bsLocalArgs.put("key",
             * "<browserstack-accesskey>"); bsLocal.start(bsLocalArgs);
             * bsLocalArgs.put("proxyHost", "127.0.0.1"); bsLocalArgs.put("proxyPort",
             * "8000");
             */
        }

        String url = null;
        url = Config.loadProperty("browserstack_url_app");

        // Initialize the remote Webdriver using BrowserStack remote URL
        // and desired capabilities defined above
        return new IOSDriver(new URL(url), caps);
    }

    public void takeSnapshot() throws IOException {
        // take final snapshot
        String name;
        if (testContext.getTestName() != null && !getClass().getName().equals(testContext.getTestName())) {
            name = getClass().getName() + "_" + testContext.getTestName();
        } else {
            name = getClass().getName();
        }

        name = name.replace("\"", "");
        try {
            if (testContext.isWebTest()) {
                BaseStep.takeSnapShot(webDriver, "./snapshots/" + name + ".png");
            }
            if (testContext.isMobileTest()) {
                for (AppiumDriver appiumDriver : iosDrivers) {
                    BaseStep.takeSnapShot(appiumDriver, "./snapshots/" + name + "_IPAD.png");
                }
            }
        } catch (Exception e) {
            Logger.getGlobal().log(Level.SEVERE, e.toString());
        }
    }
}
