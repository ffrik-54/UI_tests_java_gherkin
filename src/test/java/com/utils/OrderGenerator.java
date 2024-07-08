package com.utils;

import com.data.Order;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.testng.annotations.DataProvider;

public class OrderGenerator {

    protected static final Type DATASET_TYPE = new TypeToken<List<Order>>() {
    }.getType();

    /**
     * Get orders from order file.
     *
     * @return List of orders.
     * @throws JsonIOException
     * @throws JsonSyntaxException
     * @throws FileNotFoundException
     */
    public static List<Order> getOrdersFromFile() throws JsonIOException, JsonSyntaxException, FileNotFoundException {
        return getOrdersFromFilePath(Config.getOrderProvider());
    }

    /**
     * Get orders from order file with Path.
     *
     * @return List of orders.
     */
    public static List<Order> getOrdersFromFilePath(String path)
        throws JsonIOException, JsonSyntaxException, FileNotFoundException {

        Gson gson = new Gson();
        Logger.getGlobal().log(Level.INFO, "getOrderProvider in getOrders : {0}", path);
        JsonElement jsonData = new JsonParser().parse(new FileReader(path));
        Logger.getGlobal().log(Level.INFO, "Get the Json order file {0}", path);
        JsonElement dataSet = jsonData.getAsJsonObject().get("ordersList");

        return gson.fromJson(dataSet, DATASET_TYPE);

    }

    /**
     * Get orders from the file passed as parameter.
     *
     * @param orderFileName, the name of the JSON file with the order description
     * @return List of orders retrieved from the JSON file in input.
     */
    public static List<Order> getOrdersFromFile(String orderFileName)
        throws JsonIOException, JsonSyntaxException, FileNotFoundException {

        Gson gson = new Gson();
        Logger.getGlobal()
            .log(Level.INFO, "getOrderProvider in getOrders:" + Json.get("data_order_path") + orderFileName);
        JsonElement jsonData = new JsonParser().parse(new FileReader(Json.get("data_order_path") + orderFileName));
        Logger.getGlobal().log(Level.INFO, "Get the Json order file {0}", Json.get("data_order_path") + orderFileName);
        JsonElement dataSet = jsonData.getAsJsonObject().get("ordersList");

        return gson.fromJson(dataSet, DATASET_TYPE);

    }

    /**
     * Get Data From Json File for the Orders Data.
     *
     * @return Object[][] Order.
     * @throws FileNotFoundException
     * @throws JsonIOException
     * @throws JsonSyntaxException
     */
    @DataProvider(name = "OrdersProvider")
    public static Object[][] getOrders() throws JsonIOException, JsonSyntaxException, FileNotFoundException {
        List<Order> orders = getOrdersFromFile();

        Object[][] returnValue = new Object[orders.size()][1];
        int index = 0;
        for (Object[] each : returnValue) {
            each[0] = orders.get(index++);
        }
        return returnValue;
    }
}
