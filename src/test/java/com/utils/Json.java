package com.utils;

import com.contexts.TestContext;
import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Json {

    private static TestContext testContext;

    public static void setTestContext(TestContext testContext) {
        Json.testContext = testContext;
    }

    /**
     * Get Json Data from a Json file.
     *
     * @param path, the path of the file.
     * @return JsonObject Data.s
     * @throws IOException
     * @throws JsonSyntaxException
     */
    public static JsonObject getJsonData(String path) throws JsonSyntaxException, IOException {
        JsonElement jsonData = new JsonParser().parse(new FileReader(path));
        if (jsonData instanceof JsonArray) {
            return (JsonObject) (jsonData.getAsJsonArray()).get(0);
        }
        return jsonData.getAsJsonObject();
    }

    /**
     * Returns the String located in JSON file
     *
     * @param index: the index of the string
     */
    public static String get(String index) {
        JSONParser parser = new JSONParser();
        String path; // the file where to find the key for the index

        if (index.isEmpty())
        // to allow empty strings
        {
            return "";
        }

        String storeName = testContext.getScenarioContext().getStores().get(0).getStoreName();

        if (index.startsWith("string_")) {
            path = Config.getStringPath();
        } else if (index.startsWith("store_")) {
            if (storeName != null) {
                path = Config.getDataStorePath().replace("XX", Config.getEnv()).replace("*", storeName);
            } else {
                throw new NullPointerException("current store not set");
            }
        } else if (index.startsWith("api_")) {
            path = Config.getDataApiPath();
        } else if (index.startsWith("url_") || index.startsWith("auth_") || index.startsWith("token_")) {
            path = Config.getDataAuthUrlTokenPath();
        } else if (index.startsWith("env_")) {
            path = Config.getDataEnvPath();
        } else if (index.startsWith("raw_")) {
            path = Config.getDataRawTicketPath();
        } else if (index.startsWith("event_")) {
            path = Config.getDataEventPath();
        } else if (index.startsWith("oms_event_")) {
            path = Config.getDataOMSEventPath();
        } else if (index.startsWith("flag_")) {
            path = Config.getFlagPath();
        } else if (index.startsWith("unified_catalog")) {
            path = Config.getUnifiedCatalogPath();
        } else {
            path = Config.getDataPath();
        }

        try {
            Object obj = parser.parse(new FileReader(path));

            JSONObject jsonObject = (JSONObject) obj;

            String result = (String) jsonObject.get(index);

            if (result == null && index.startsWith("store_")) {
                if (storeName != null) {
                    path = storeName.startsWith("storeqadata") ? path.replace(storeName, "storeqadata")
                        : path.replace(storeName, "storeqa");
                } else {
                    throw new NullPointerException("current store not set");
                }

                obj = parser.parse(new FileReader(path));
                jsonObject = (JSONObject) obj;
                result = (String) jsonObject.get(index);
            }
            if ((result != null) && (index.startsWith("path_"))) {
                assert storeName != null;
                result = result.replace("*", storeName);
            }

            return result;
        } catch (Exception e) {
            Logger.getGlobal().log(Level.SEVERE, e.toString());
            return null;
        }
    }

    /**
     * gets the value located in json format string
     *
     * @param json, the json formatted string source
     * @param key,  the key of the value
     * @return the value located in json format string
     */
    public static String getFromString(String json, String key) {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new StringReader(json));
            JSONObject jsonObject = (JSONObject) obj;
            return String.valueOf(jsonObject.get(key));
        } catch (Exception e) {
            Logger.getGlobal().log(Level.SEVERE, e.toString());
            return null;
        }
    }

    /**
     * gets the data in json object format
     *
     * @param json : the json formatted string source
     * @return the data in json object format
     */
    public static JsonArray getArrayFromString(String json) {
        JsonParser parser = new JsonParser();
        try {
            JsonElement obj = parser.parse(new StringReader(json));
            return obj.getAsJsonArray();
        } catch (JsonIOException | JsonSyntaxException e) {
            Logger.getGlobal().log(Level.SEVERE, e.toString());
            return null;
        }
    }

    /**
     * gets the json object data from string
     *
     * @param json : the json formatted string source
     * @param key  : the key of the object
     * @return the json object format
     * @throws ParseException
     */
    public static List<JSONObject> getObjectFromString(String json, String key) throws ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(json);
        JSONObject jsonObject = (JSONObject) obj;
        JSONArray array = (JSONArray) jsonObject.get(key);
        List<JSONObject> arrayList = new ArrayList<>();
        Iterator<JSONObject> iterator = array.iterator();
        while (iterator.hasNext()) {
            arrayList.add(iterator.next());
        }
        return arrayList;
    }

    /**
     * addPropertyToJson() to Add a Property on Json File. parameters.
     *
     * @param path          (String) path of the json file.
     * @param propertyKey   (String) corresponds to key to add request body.
     * @param propertyValue (String) corresponds to value to add for the key.
     * @return content of the file.
     * @throws IOException
     */
    public static String addPropertyToJson(String path, String propertyKey, String propertyValue) throws IOException {
        return addPropertyToJson(path, null, null, null, propertyKey, propertyValue, null);
    }

    /**
     * addPropertyToJson() to Add a Property on Json File. parameters.
     *
     * @param path          (String) path of the json file.
     * @param childKey      (String) corresponds to the key of the child object.
     * @param propertyKey   (String) corresponds to key to add request body.
     * @param propertyValue (String) corresponds to value to add for the key.
     * @return Content content of the file.
     * @throws IOException
     */
    public static String addPropertyToJson(String path, String childKey, String propertyKey, String propertyValue)
        throws IOException {
        return addPropertyToJson(path, childKey, null, null, propertyKey, propertyValue, null);
    }

    /**
     * writeJsonFile() write a json in a dedicated file.
     *
     * @param jsonFile    (File) File of the json to write.
     * @param jsonElement (jsonElement) content of the json file.
     * @throws IOException
     */
    public static void writeJsonFile(File jsonFile, JsonElement jsonElement) throws IOException {

        String resultingJson = new Gson().toJson(jsonElement);
        FileUtils.writeStringToFile(jsonFile, resultingJson, "ISO-8859-1");
    }

    /**
     * addPropertyToJson() to Add a Property on Json File. parameters.
     *
     * @param path          (String) path of the json file.
     * @param childKey      (String) corresponds to the key of the child object.
     * @param subChildKey   (String) corresponds to the key of the sub child object.
     * @param arrayName     (String) corresponds to the name of the array.
     * @param propertyKey   (String) corresponds to key to add request body.
     * @param propertyValue (String) corresponds to value to add for the key.
     * @return content of the file.
     * @throws IOException
     */
    public static String addPropertyToJson(String path, String childKey, String subChildKey, String arrayName,
        String propertyKey, String propertyValue, String type, boolean debug) throws IOException {
        String content = "";

        if (path != null) {
            File jsonFile = new File(path);
            JsonElement jsonElement = getJsonElementFromFile(path, jsonFile);
            if (debug) {
                Logger.getGlobal().log(Level.INFO, "jsonElement: {0} ", jsonElement);
            }
            addPropertyToJson(jsonElement, childKey, subChildKey, arrayName, propertyKey, propertyValue, type, debug);
            writeJsonFile(jsonFile, jsonElement);
            content = new String(Files.readAllBytes(Paths.get(path)));
        }
        return content;
    }

    public static String addPropertyToJson(String path, String childKey, String subChildKey, String arrayName,
        String propertyKey, String propertyValue, String type) throws IOException {
        return addPropertyToJson(path, childKey, subChildKey, arrayName, propertyKey, propertyValue, type, false);
    }

    /**
     * addPropertyToJson() to Add a Property on Json File.
     *
     * @param jsonElement   (JsonElement) the json element.
     * @param childKey      (String) corresponds to the key of the child object.
     * @param subChildKey   (String) corresponds to the key of the sub child object.
     * @param arrayName     (String) corresponds to the name of the array.
     * @param propertyKey   (String) corresponds to key to add request body.
     * @param propertyValue (String) corresponds to value to add for the key.
     */
    public static void addPropertyToJson(JsonElement jsonElement, String childKey, String subChildKey, String arrayName,
        String propertyKey, String propertyValue, String type, boolean debug) {

        if (jsonElement instanceof JsonObject) {
            addPropertyToObject(jsonElement, childKey, subChildKey, arrayName, propertyKey, propertyValue, type, debug);
        } else if (jsonElement instanceof JsonArray) {
            addPropertyToArray(jsonElement, null, propertyKey, propertyValue, type);
        }
    }

    public static void addPropertyToJson(JsonElement jsonElement, String childKey, String subChildKey, String arrayName,
        String propertyKey, String propertyValue, String type) {
        addPropertyToJson(jsonElement, childKey, subChildKey, arrayName, propertyKey, propertyValue, type, false);
    }

    /**
     * getJsonElement() From Json File.
     *
     * @param path     (String) path of the json file.
     * @param jsonFile (File) File of the json.
     * @return jsonElement (jsonElement) content of the json file.
     * @throws IOException
     */
    public static JsonElement getJsonElementFromFile(String path, File jsonFile) throws IOException {
        if (path != null) {
            String jsonString = FileUtils.readFileToString(jsonFile, Charsets.UTF_8);
            return new Gson().fromJson(jsonString, JsonElement.class);
        }
        return null;
    }

    /**
     * addPropertyToArray() to Add a Property on Json Array.
     *
     * @param jElement      (JsonElement) corresponds to the json Element of the Json.
     * @param arrayName     (String) corresponds to the name of the array.
     * @param propertyKey   (String) corresponds to key to add request body.
     * @param propertyValue (String) corresponds to value to add for the key.
     */
    public static void addPropertyToArray(JsonElement jElement, String arrayName, String propertyKey,
        String propertyValue, String type) {
        JsonArray jArray;
        if (arrayName != null) {
            jArray = jElement.getAsJsonObject().getAsJsonArray(arrayName);
        } else {
            jArray = jElement.getAsJsonArray();
        }
        for (int i = 0; i < jArray.size(); i++) {
            JsonObject jObject = jArray.get(i).getAsJsonObject();
            addProperty(jObject, propertyKey, propertyValue, type);
        }
    }

    /**
     * addProperty() to Add a Property with type.
     *
     * @param jObject       (JsonObject) corresponds to the json Object.
     * @param propertyKey   (String) corresponds to key to add request body.
     * @param propertyValue (String) corresponds to value to add for the key.
     * @param type          (String) corresponds to type for the key.
     */
    public static void addProperty(JsonObject jObject, String propertyKey, String propertyValue, String type) {

        if (type != null) {
            if (type.equals("number")) {
                jObject.addProperty(propertyKey, Integer.parseInt(propertyValue));
            } else if (type.equals("double")) {
                jObject.addProperty(propertyKey, Double.parseDouble(propertyValue));
            }
        } else {
            if (propertyValue != null && (propertyValue.equals("true") || propertyValue.equals("false"))) {
                jObject.addProperty(propertyKey, Boolean.parseBoolean(propertyValue));
            } else {
                jObject.addProperty(propertyKey, propertyValue);
            }
        }
    }

    /**
     * addPropertyToObject() to Add a Property on Json Object.
     *
     * @param jElement      (JsonElement) corresponds to the json Element of the Json.
     * @param childKey      (String) corresponds to the child Key of the json.
     * @param subChildKey   (String) corresponds to the sub child Key of the json.
     * @param arrayName     (String) corresponds to the name of the array.
     * @param propertyKey   (String) corresponds to key to add request body.
     * @param propertyValue (String) corresponds to value to add for the key.
     */
    public static void addPropertyToObject(JsonElement jElement, String childKey, String subChildKey, String arrayName,
        String propertyKey, String propertyValue, String type, boolean debug) {
        JsonElement parentJsonElement = jElement.getAsJsonObject().get(childKey);
        if (debug) {
            Logger.getGlobal().log(Level.INFO, "parentJsonElement: {0} ", parentJsonElement);
        }
        JsonObject jObject = jElement.getAsJsonObject();
        if (parentJsonElement != null) {
            if (arrayName != null) {
                if (subChildKey == null) {
                    addPropertyToArray(parentJsonElement, arrayName, propertyKey, propertyValue, type);
                } else {
                    addPropertyToObject(parentJsonElement, subChildKey, null, arrayName, propertyKey, propertyValue,
                        type, debug);
                }
            } else {
                if (subChildKey == null) {
                    addPropertyToObject(parentJsonElement, childKey, null, null, propertyKey, propertyValue, type,
                        debug);
                } else {
                    addPropertyToObject(parentJsonElement, subChildKey, null, null, propertyKey, propertyValue, type,
                        debug);
                }
            }
        } else {
            addProperty(jObject, propertyKey, propertyValue, type);
        }
    }

    /**
     * getArrayFromJsonFile() to Get the content of an array from a Json File. parameters.
     *
     * @param json,  path of the json file.
     * @param array, corresponds to the array to get.
     * @return Content Array value.
     */
    public static String getArrayFromJsonFile(String json, String array) {
        return getArrayFromJsonFile(json, array, 0);
    }

    /**
     * getArrayFromJsonFile() to Get the content of an array from a Json File. parameters.
     *
     * @param json,  the json file.
     * @param array, corresponds to the array to get.
     * @param index, the index of the array.
     * @return Content Array value.
     */
    public static String getArrayFromJsonFile(String json, String array, int index) {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(json));

            JSONObject jsonObject = (JSONObject) obj;

            JSONArray arrayList = (JSONArray) jsonObject.get(array);

            JSONObject jobj = (JSONObject) arrayList.get(index);
            return jobj.toString();
        } catch (Exception e) {
            Logger.getGlobal().log(Level.SEVERE, e.toString());
            return null;
        }
    }

    /**
     * deleteProperty() to remove a Property from json.
     *
     * @param jElement    (JsonElement) corresponds to the json Element of the Json.
     * @param propertyKey (String) corresponds to key to add request body.
     */
    public static void deleteProperty(JsonElement jElement, String propertyKey) {
        JsonObject jObject = jElement.getAsJsonObject();
        jObject.remove(propertyKey);
    }

    /**
     * addObjectToJsonObject() to add/replace any JSON Element (JSONArray, JSONObject, JSONPrimitive) to an existing
     * JsonObject.
     *
     * @param parentObject (Object) corresponds to the main JSON Object.
     * @param objectToAdd  (JSONObject) corresponds to the JSON Element to add/replace.
     * @param key          (String) corresponds to key of the JSON Element to add.
     */

    public static void addObjectToJsonObject(Object parentObject, Object objectToAdd, String key) {
        if (parentObject instanceof JsonObject) {
            JsonObject parentJson = (JsonObject) parentObject;
            if (parentJson.get(key) != null) {
                // When the key is found, replace the existing value with the new object
                if (objectToAdd instanceof JsonElement) {
                    parentJson.add(key, (JsonElement) objectToAdd);
                } else if (objectToAdd instanceof String) {
                    parentJson.addProperty(key, (String) objectToAdd);
                }
            }
            // keep searching for the same key on different levels
            for (String existingKey : ((JsonObject) parentObject).keySet()) {
                Object value = parentJson.get(existingKey);
                addObjectToJsonObject(value, objectToAdd, key);
            }
        } else if (parentObject instanceof JsonArray) {
            JsonArray parentArray = (JsonArray) parentObject;
            for (int i = 0; i < parentArray.size(); i++) {
                Object value = parentArray.get(i);
                addObjectToJsonObject(value, objectToAdd, key);
            }
        }
    }

    public static JSONObject getJsonFromUrl(String path) throws IOException, ParseException {
        URL url = new URL(path);
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(new InputStreamReader(url.openStream()));
    }

}