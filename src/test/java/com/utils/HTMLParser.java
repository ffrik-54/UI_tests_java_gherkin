package com.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Java Program to parse/read HTML documents from File using Jsoup library. Jsoup is an open source library which allows
 * Java developer to parse HTML files and extract elements, manipulate data, change style using DOM, CSS and JQuery like
 * method.
 *
 * @author pierredesporte
 */
public class HTMLParser {

    public static Document parseFile(String path) throws FileNotFoundException {

        Document htmlFile = null;
        try {
            htmlFile = Jsoup.parse(new File(path), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return htmlFile;
    }

    public static Elements getContent(Document html, String className) {
        return html.getElementsByClass(className);
    }
}