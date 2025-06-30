package com.utils;

import lombok.Getter;
import lombok.Setter;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Config path to data path to string environments if necessary platforms if necessary compilation flags
 *
 * @author ffrik
 */

@Getter
public class Config {

    public static final int TIMEOUT_ALERT = 2;

    public static final int TIMEOUT_SHORT = 5;

    public static final int TIMEOUT_15 = 15;

    public static final int TIMEOUT_VERY_SHORT = 2;

    public static final int TIMEOUT_MEDIUM = 25;

    public static final int TIMEOUT_LOAD = 30;

    public static final int TIMEOUT_LOADER_INIT = 10;

    public static final int TIMEOUT_LONG = 60;

    public static final int NB_RETRY = 2;

    public static final int NB_RETRY_10 = 10;

    public static final int DIMENSION_WIDTH = 1325;

    public static final int DIMENSION_HEIGHT = 968;

    public static final int DIMENSION_WIDTH_FHD = 1920;

    public static final int DIMENSION_HEIGHT_FHD = 1080;

    @Getter
    private static final String dataPath = "./src/test/resources/global/Global.json";

    private static final String stringPath = "./src/test/resources/global/Strings_XX.json";

    @Getter
    private static final String dataApiPath = "./src/test/resources/global/Data_api.json";

    private static final String dataAuthUrlTokenPath = "./src/test/resources/global/*/Data_api.json";

    private static final String dataEnvPath = "./src/test/resources/global/*/Global.json";

    @Getter
    private static final String dataAccountPath = "./src/test/resources/global/XX/*/Global.json";

    private static final String propertiesFile = "gradle.properties";

    private static final String localPropertiesFile = "local.properties";

    private static final Properties gradleProperties = new Properties();

    private static final Properties localProperties = new Properties();

    @Getter
    private static final String launchableActivity = null;

    @Getter
    private static final String androidVersion = null;

    @Getter
    private static String preview_env = null; // ex: onb-783 else null to fall back to classic staging / prod

    private static String environment = null; // dev - staging - prod

    private static String headless = null; // true - false

    private static String webBrowser = null; // firefox - chrome

    private static String mobilePlatform = null; // android ios

    @Getter
    private static String webCloudType = null; // if null, launch locally else name of the cloud testing provider :

    @Getter
    private static String appIdBrowserstack = null; // if null, launch locally else name of the cloud testing provider :

    @Getter
    private static String appIdSaucelabs = null; // if null, launch locally else name of the cloud testing provider :

    @Getter
    private static String buildType = null; // the type of the build (local test, dev train release train, non

    @Getter
    private static String driverPath = null; // the path to the web browser

    @Getter
    private static String protocol = null; // http - https

    @Getter
    private static String packageName = null; // package for Android or Bundle ID for Ios

    @Getter
    private static String phoneModel = null;

    @Getter
    private static String language = null; // language selected

    @Getter
    private static String country = null; // country selected

    private static String proxy = null; // true - false

    @Getter
    private static String appPath = null; // the path of the APP File for the simulator.

    @Getter
    private static String ipaPath = null; // the path of the IPA File for the real device.

    private static String noReset = null; // true - false, don't reset app state before this session.

    @Getter
    private static List<String> configFiles;

    @Getter
    private static String url = null;

    @Setter
    @Getter
    private static String webUrl = null;

    @Getter
    private static String appiumServerUrl = null;

    @Getter
    private static String deviceFarmAppPath = null;

    @Getter
    private static String sessionIdsFilePath = null;

    private static String buildDate = null; // Date of the build - null until date asked once

    private Config() {
        throw new IllegalStateException("Utility class");
    }
    // once

    public static String getBuildDate() {
        if (buildDate == null) {
            buildDate = DateParser.getCurrentDate();
        }
        return buildDate;
    }

    public static boolean isHeadlessActivated() {
        return headless.equals("true");
    }

    public static String getDataAuthUrlTokenPath() {
        return dataAuthUrlTokenPath.replace("*", Config.getEnv());
    }

    public static String getDataEnvPath() {
        return dataEnvPath.replace("*", Config.getEnv());
    }

    public static String getStringPath() {
        return stringPath.replace("XX", language.substring(0, 2));
    }

    public static void loadGradleProperties() throws IOException, XMLStreamException {
        environment = loadProperty("env");
        Logger.getGlobal().log(Level.INFO, "environment: {0}", environment);
        buildType = loadProperty("buildtype");
        Logger.getGlobal().log(Level.INFO, "buildType: {0}", buildType);
        headless = loadProperty("headless");
        Logger.getGlobal().log(Level.INFO, "headless: {0}", headless);
        protocol = loadProperty("protocol");
        Logger.getGlobal().log(Level.INFO, "protocol: {0}", protocol);
        preview_env = loadProperty("preview_env");
        Logger.getGlobal().log(Level.INFO, "preview_env: {0}", preview_env);
        language = loadProperty("language");
        Logger.getGlobal().log(Level.INFO, "language: {0}", language);
        country = loadProperty("country");
        Logger.getGlobal().log(Level.INFO, "country: {0}", country);
        proxy = loadProperty("proxy");
        Logger.getGlobal().log(Level.INFO, "proxy: {0}", proxy);
        mobilePlatform = loadProperty("mobile_type");
        Logger.getGlobal().log(Level.INFO, "mobile_type: {0}", mobilePlatform);
        appIdBrowserstack = loadProperty("browserstack_app_id");
        Logger.getGlobal().log(Level.INFO, "browserstack_app_id: {0}", appIdBrowserstack);
        appIdSaucelabs = loadProperty("saucelabs_app");
        Logger.getGlobal().log(Level.INFO, "saucelabs_app: {0}", appIdSaucelabs);
        webBrowser = loadProperty("web_browser_type");
        Logger.getGlobal().log(Level.INFO, "web_browser_type: {0}", webBrowser);
        webCloudType = loadProperty("web_browser_cloudprovider");
        Logger.getGlobal().log(Level.INFO, "web_browser_cloudprovider: {0}", webCloudType);
        driverPath = loadProperty(
            "web_" + webBrowser.substring(0, 1).toUpperCase() + webBrowser.substring(1).toLowerCase() + "_driver_path");
        Logger.getGlobal().log(Level.INFO, "driverPath: {0}", driverPath);
        packageName = loadProperty("ipa_package_name");
        Logger.getGlobal().log(Level.INFO, "packageName: {0}", packageName);
        phoneModel = loadProperty("phone_model");
        Logger.getGlobal().log(Level.INFO, "phoneModel: {0}", phoneModel);
        appPath = loadProperty("local_ipad_app_path");
        Logger.getGlobal().log(Level.INFO, "App Path: {0}", appPath);
        ipaPath = loadProperty("local_ipad_ipa_path");
        Logger.getGlobal().log(Level.INFO, "Ipa Path: {0}", ipaPath);
        noReset = loadProperty("no_reset");
        Logger.getGlobal().log(Level.INFO, "no_reset: {0}", noReset);
        appiumServerUrl = loadProperty("appium_server_url");
        Logger.getGlobal().log(Level.INFO, "appium_server_url: {0}", appiumServerUrl);
        deviceFarmAppPath = loadProperty("mobiles_farm_app_path");
        Logger.getGlobal().log(Level.INFO, "mobiles_farm_app_path: {0}", deviceFarmAppPath);
        sessionIdsFilePath = loadProperty("session_ids_file_path");
        Logger.getGlobal().log(Level.INFO, "session_ids_file_path: {0}", sessionIdsFilePath);

        String configFile = loadProperty("local_device_file");

        configFiles = new ArrayList<>();
        if (configFile != null && !configFile.isEmpty()) {
            if (configFile.replace(" ", "").contains(",")) {
                configFiles = Arrays.asList(configFile.split(","));
            } else {
                configFiles.add(configFile);
            }
            Logger.getGlobal().log(Level.INFO, "config file: {0}", configFiles);
        }

        url = loadProperty("local_ipad_url");
        Logger.getGlobal().log(Level.INFO, "url: {0}", url);
    }

    /**
     * load property from gradle use command line value if key not defined in command line then use value in
     * gradle.properties
     **/
    public static String loadProperty(String key) {

        if (System.getProperty(key, "").length() > 1)
        // property defined in command line
        {
            return System.getProperty(key, "");
        }

        // not defined in command line : read local properties value
        if (new File(localPropertiesFile).exists()) {
            try (FileInputStream input = new FileInputStream(localPropertiesFile)) {
                localProperties.load(input);
                if ((localProperties.getProperty(key) != null) && (localProperties.getProperty(key).length() > 1)) {
                    return localProperties.getProperty(key);
                }
            } catch (IOException e) {
                Report.logError(e.getMessage());
            }
        }

        // not present in local properties : read gradle properties value
        try (FileInputStream input = new FileInputStream(propertiesFile)) {
            gradleProperties.load(input);
            if ((gradleProperties.getProperty(key) != null) && (gradleProperties.getProperty(key).length() > 1)) {
                return gradleProperties.getProperty(key);
            }
        } catch (IOException e) {
            Report.logError(e.getMessage());
        }

        return null;

    }

    public static boolean isWebBrowserChrome() {
        return webBrowser.equalsIgnoreCase("chrome");
    }

    public static boolean isWebBrowserFirefox() {
        return webBrowser.equalsIgnoreCase("firefox");
    }

    public static boolean isWebBrowserSafari() {
        return webBrowser.equalsIgnoreCase("safari");
    }

    public static String getEnv() {
        return environment;
    }

    public static boolean isProxyActivated() {
        return proxy.equals("true");
    }

    public static boolean isNoReset() {
        return noReset.equals("true");
    }

    public static boolean isPlatformAndroid() {
        return mobilePlatform.equals("Android");
    }

    public static boolean isPlatformIos() {
        return mobilePlatform.equals("iOS");
    }

}
