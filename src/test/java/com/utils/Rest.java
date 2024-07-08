package com.utils;

import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Methods for Rest request
 *
 * @author ffrik
 */

public class Rest {
    public static final String CONTENT_TYPE = "application/x-www-form-urlencoded";

    private final static Charset charset = StandardCharsets.UTF_8;

    /**
     * doRequest() make a simple HTTP request to the chosen URL with optional
     * parameters.
     *
     * @param method (String) correspond to HTTP request method
     *               (GET,POST,PUT,OPTION,DELETE).
     * @param url    (String) correspond to the URL you want to request
     * @param params (Map<String, String>) correspond to parameters passed in
     *               request body.
     * @return Content response content
     * @throws IOException
     */
    public static Response doRequest(String method, String url, Map<String, Object> params,
                                     Map<String, String> queryParams) throws IOException {
        return doRequest(method, url, queryParams, params, null, false);
    }

    /**
     * doRequest() make a simple HTTP request to the chosen URL with optional
     * parameters.
     *
     * @param method        (String) correspond to HTTP request method
     *                      (GET,POST,PUT,OPTION,DELETE).
     * @param url           (String) correspond to the URL you want to request
     * @param params        (Map<String, String>) correspond to parameters passed in
     *                      request body.
     * @param headers       (Map<String, String>) correspond to headers for the
     *                      request.
     * @param debugMessage, to Display all the message for the connection.
     * @return Content response content
     * @throws IOException
     */
    public static Response doRequest(String method, String url, Map<String, String> queryParams,
                                     Map<String, Object> params, Map<String, String> headers, boolean debugMessage) throws IOException {

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder = setParams(params);
        byte[] dataBytes = queryBuilder.toString().getBytes(charset);

        if (debugMessage) {
            Logger.getGlobal().log(Level.WARNING, "Params -> {0}", queryBuilder);
        }

        if (queryParams != null) {
            url = addQueryParam(url, queryParams);
        }

        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("accept", "application/json");
        connection.setRequestProperty("Content-Length", String.valueOf(dataBytes.length));
        connection.setRequestProperty("Content-Type", CONTENT_TYPE + ";charset=" + charset);

        for (Map.Entry<String, String> list : setHeaders(headers, debugMessage).entrySet()) {
            connection.setRequestProperty(list.getKey(), list.getValue());
        }

        if ((method.equals("POST")) && !connection.getDoOutput()) {
            connection.setDoOutput(true);
            try (OutputStream output = connection.getOutputStream()) {
                connection.getOutputStream().write(dataBytes);
            }
        }
        displayHeadersResponse(debugMessage, connection);

        BufferedReader br = null;

        if (debugMessage) {
            Logger.getGlobal().log(Level.WARNING, "Status code -> {0}", connection.getResponseCode());
        }

        if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 400) {
            br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } else if (connection.getResponseCode() >= 400 && connection.getResponseCode() < 500) {
            br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }

        if (br == null) {
            throw new NullPointerException("cannot get response");
        }

        Logger.getGlobal().log(Level.INFO, "{0} -> {1} -> {2}", new Object[]{method, url, connection.getResponseCode()});
        String strResponse = br.readLine();

        Response response = new Response(strResponse, connection.getResponseCode());

        if (debugMessage) {
            Logger.getGlobal().log(Level.WARNING, "Response -> {0}", strResponse);
        }
        return response;
    }

    /**
     * SetParam() to set the param for the HTTP Request.
     *
     * @param params (Map<String, String>) correspond to parameters passed in
     *               request body.
     * @return StringBuilder, containing the params.
     * @throws UnsupportedEncodingException
     */
    public static StringBuilder setParams(Map<String, Object> params) throws UnsupportedEncodingException {

        StringBuilder queryBuilder = new StringBuilder();

        if (params != null) {
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (queryBuilder.length() != 0)
                    queryBuilder.append('&');
                queryBuilder.append(URLEncoder.encode(param.getKey(), charset));
                queryBuilder.append('=');
                queryBuilder.append(URLEncoder.encode(String.valueOf(param.getValue()), charset));
            }
        }
        return queryBuilder;
    }

    /**
     * addQueryParam() to set the QueryParam in the url for the HTTP Request.
     *
     * @param url    (String) correspond to the URL you want to request
     * @param params (Map<String, String>) correspond to parameters passed in
     *               request body.
     * @return String, url containing the params.
     * @throws URISyntaxException
     */
    public static String addQueryParam(String url, Map<String, String> params) {

        URIBuilder ub;
        try {
            ub = new URIBuilder(url);
            for (Map.Entry<String, String> entry : params.entrySet()) {
                ub.addParameter(entry.getKey(), entry.getValue());
            }
            return ub.toString();
        } catch (URISyntaxException e) {
            Logger.getGlobal().log(Level.SEVERE, "Eror Syntax : {0}", e.toString());
        }
        return url;
    }

    /**
     * SetHeaders() to set the List of Headers for the HTTP Request.
     *
     * @param headers       (Map<String, String>) correspond to headers for the
     *                      request.
     * @param debugMessage, to Display all the message for the connection.
     * @return Map of headers.
     */
    public static Map<String, String> setHeaders(Map<String, String> headers, boolean debugMessage) {
        Map<String, String> map = new LinkedHashMap<>();

        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                if (debugMessage) {
                    String message = header.getKey() + " : " + headers.get(header.getKey());
                    Logger.getGlobal().log(Level.WARNING, "Hearders -> {0}", message);
                }
                map.put(header.getKey(), headers.get(header.getKey()));
            }
        }
        return map;
    }

    /**
     * DisplayHeadersResponse() to see the Headers in the console.
     *
     * @param debugMessage (Boolean), to Display all the message for the connection.
     * @param connection   (HttpURLConnection), the url connection to get the
     *                     hearder fields.
     */
    public static void displayHeadersResponse(boolean debugMessage, HttpURLConnection connection) {
        if (debugMessage) {
            Map<String, List<String>> map = connection.getHeaderFields();
            Logger.getGlobal().log(Level.WARNING, "Printing All Response Header");
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                String message = entry.getKey() + " : " + entry.getValue();
                Logger.getGlobal().log(Level.WARNING, "Responses Headers -> {0}", message);
            }
        }
    }

    /**
     * doRequest() make a simple HTTP request to the chosen URL with optional
     * parameters.
     *
     * @param headers (Map<String, String>) correspond to headers passed in request.
     * @param url     (String) correspond to the URL you want to request
     * @param body    (String) correspond to request body
     * @return Content response content
     * @throws UnsupportedEncodingException
     */
    public static Response doRequest(Map<String, String> headers, String url, String body)
            throws UnsupportedEncodingException {

        return doRequest(headers, url, body, CONTENT_TYPE);
    }


    /**
     * doRequest() make a simple HTTP request to the chosen URL with optional
     * parameters.
     *
     * @param headers         (Map<String, String>) correspond to headers passed in
     *                        request.
     * @param url             (String) correspond to the URL you want to request
     * @param body            (String) correspond to request body
     * @param applicationType (String) header application type
     * @return Content response content
     * @throws UnsupportedEncodingException
     */
    public static Response doRequest(Map<String, String> headers, String url, String body, String applicationType)
            throws UnsupportedEncodingException {

        return doRequest(headers, null, url, body, false, applicationType);
    }

    /**
     * doRequest() make a simple HTTP request to the chosen URL with optional
     * parameters.
     *
     * @param headers         (Map<String, String>) correspond to headers passed in
     *                        request.
     * @param queryParams     (Map<String, String>) correspond to parameters passed
     *                        in the url.
     * @param url             (String) correspond to the URL you want to request
     * @param body            (String) correspond to request body
     * @param debugMessage    (boolean) correspond to debug message boolean
     * @param applicationType (String) header application type
     * @return Content response content
     * @throws UnsupportedEncodingException
     */
    public static Response doRequest(Map<String, String> headers, Map<String, String> queryParams, String url,
                                     String body, boolean debugMessage, String applicationType) throws UnsupportedEncodingException {

        return doRequest("POST", headers, queryParams, url, body, debugMessage, applicationType);
    }

    /**
     * doRequest() make a simple HTTP request to the chosen URL with optional
     * parameters.
     *
     * @param headers         (Map<String, String>) correspond to headers passed in
     *                        request.
     * @param url             (String) correspond to the URL you want to request
     * @param body            (String) correspond to request body
     * @param debugMessage    (boolean) correspond to debug message boolean
     * @param applicationType (String) header application type
     * @return Content response content
     * @throws UnsupportedEncodingException
     */
    public static Response doRequest(Map<String, String> headers, String url, String body, boolean debugMessage,
                                     String applicationType) throws UnsupportedEncodingException {

        return doRequest(headers, null, url, body, debugMessage, applicationType);
    }

    /**
     * doRequest() make a simple HTTP request to the chosen URL with optional
     * parameters.
     *
     * @param headers         (Map<String, String>) correspond to headers passed in
     *                        request.
     * @param queryParams     (Map<String, String>) correspond to parameters passed
     *                        in the url.
     * @param url             (String) correspond to the URL you want to request
     * @param body            (String) correspond to request body
     * @param debugMessage    (boolean) to display the debug message.
     * @param applicationType (String) header application type
     * @return Content response content
     * @throws UnsupportedEncodingException
     */
    public static Response doRequest(String method, Map<String, String> headers, Map<String, String> queryParams,
                                     String url, String body, boolean debugMessage, String applicationType) throws UnsupportedEncodingException {

        StringEntity strEntity = new StringEntity(body);
        HttpClient httpClient = HttpClientBuilder.create().build();

        if (queryParams != null) {
            url = addQueryParam(url, queryParams);
        }

        try {
            HttpEntityEnclosingRequestBase request = null;

            switch (method) {
                case "POST" -> request = new HttpPost(url);
                case "PATCH" -> request = new HttpPatch(url);
                default -> request = new HttpPut(url);
            }

            request = addHeaders(request, headers, debugMessage, applicationType);
            request.setEntity(strEntity);
            HttpResponse httpResponse = httpClient.execute(request);

            Logger.getGlobal().log(Level.INFO,
                    "Request url: " + url + " status code :" + httpResponse.getStatusLine().getStatusCode());

            return getResponse(httpResponse, debugMessage);

        } catch (Exception e) {
            Logger.getGlobal().log(Level.SEVERE, e.toString());
            return null;
        }
    }

    /**
     * addHeaders(), add headers to the request.
     *
     * @param headers         (Map<String, String>) correspond to headers passed in
     *                        request.
     * @param debugMessage    (boolean) to display the debug message.
     * @param applicationType (String) header application type
     * @return request with headers.
     */
    private static HttpEntityEnclosingRequestBase addHeaders(HttpEntityEnclosingRequestBase request,
                                                             Map<String, String> headers, boolean debugMessage, String applicationType) {

        request.addHeader("Content-Type", applicationType);
        request.addHeader("charset", charset.toString());

        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                if (debugMessage) {
                    String message = header.getKey() + " : " + headers.get(header.getKey());
                    Logger.getGlobal().log(Level.WARNING, "Headers -> {0}", message);
                }
                request.addHeader(header.getKey(), headers.get(header.getKey()));
            }
        }
        return request;
    }

    /**
     * getResponse(), get the content response from http response.
     *
     * @param httpResponse (HttpResponse>) Response received by http.
     * @param debugMessage (boolean) to display the debug message.
     * @return Content of the response.
     */
    private static Response getResponse(HttpResponse httpResponse, boolean debugMessage)
            throws ParseException, IOException {

        if (httpResponse.getStatusLine().getStatusCode() >= 200 && httpResponse.getStatusLine().getStatusCode() < 300
                || (httpResponse.getStatusLine().getStatusCode() >= 400
                && httpResponse.getStatusLine().getStatusCode() < 500)) {

            String content = null;
            if (httpResponse.getEntity() != null) {
                content = EntityUtils.toString(httpResponse.getEntity());
            }

            Response response = new Response(content, httpResponse.getStatusLine().getStatusCode());

            if (debugMessage) {
                Logger.getGlobal().log(Level.WARNING, "Response -> {0}", content);
            }

            return response;
        } else {
            return null;
        }
    }

    /**
     * sendFile() make an http request with an image as parameter.
     *
     * @param headers   (Map<String, String>) correspond to headers passed in
     *                  request.
     * @param url       (String) correspond to the URL you want to request
     * @param imageId   (String) correspond to the ID given to the image when
     *                  imported in boby
     * @param imagePath (String) correspond to the location the image is stored
     * @return Content response content
     * @throws UnsupportedEncodingException
     */
    public static Response sendFile(Map<String, String> headers, String url, String imageId, String imagePath)
            throws UnsupportedEncodingException {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            org.apache.hc.client5.http.classic.methods.HttpPost request = new org.apache.hc.client5.http.classic.methods.HttpPost(
                    url);
            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    request.addHeader(header.getKey(), headers.get(header.getKey()));
                }
            }
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody(imageId, new File(imagePath), ContentType.IMAGE_PNG, imageId + ".png");
            request.setEntity(builder.build());
            CloseableHttpResponse httpResponse = httpClient.execute(request);
            Logger.getGlobal().log(Level.INFO, "Request url: " + url + " status code :" + httpResponse.getCode());

            String content = null;
            if (httpResponse.getEntity() != null) {
                content = org.apache.hc.core5.http.io.entity.EntityUtils.toString(httpResponse.getEntity());
            }

            Response response = new Response(content, httpResponse.getCode());
            return response;

        } catch (Exception e) {
            Logger.getGlobal().log(Level.SEVERE, e.toString());
            return null;
        }
    }

    public static class Response {
        private final String content;
        private final int code;

        public Response(String content, int code) {
            this.content = content;
            this.code = code;
        }

        public String getContent() {
            return content;
        }

        public int getCode() {
            return code;

        }

        @Override
        public String toString() {
            return "\nResponse ='" + content + '\'' + ", Code ='" + code + '\'' + '}';

        }
    }
}