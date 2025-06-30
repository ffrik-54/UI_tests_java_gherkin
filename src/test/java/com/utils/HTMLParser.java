package com.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Java Program to parse/read HTML documents from File using Jsoup library. Jsoup is an open source library which allows
 * Java developer to parse HTML files and extract elements, manipulate data, change style using DOM, CSS and JQuery like
 * method.
 *
 * @author ffrik-54
 */
public class HTMLParser {

    public static Document parseFile(String path) throws FileNotFoundException {

        Document htmlFile = null;
        try {
            htmlFile = Jsoup.parse(new File(path), "UTF-8");
        } catch (IOException e) {
            Report.logError(e.getMessage());
        }
        return htmlFile;
    }

    public static Elements getContent(Document html, String className) {
        return html.getElementsByClass(className);
    }
}