package com.utils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class Url {

    private Url() {
    }

    /**
     * Split all the value in the url.
     *
     * @return the map with all the value.
     * @throws UnsupportedEncodingException
     **/
    public static Map<String, String> splitUrl(String url, boolean isQuery) throws UnsupportedEncodingException {

        final URI uri = URI.create(url);

        Map<String, String> queryPairs = new LinkedHashMap<>();

        String query = null;
        if (isQuery)
            query = uri.getQuery();
        else
            query = uri.getFragment();

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            queryPairs.put(URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8), URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8));
        }
        return queryPairs;
    }
}