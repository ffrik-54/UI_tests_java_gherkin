package com.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamException;

/**
 * Class that extracts info by parsing :
 * output of 'aapt dump badging'
 * output of 'adb shell getprop'  
 * 
 * @author ffrik
 */ 

public class PropertiesParser 
{ 

	private PropertiesParser() {
	}

    public static String get(String property, String aaptout, String limitstart, String limitend) 
    { 
    	Pattern p = Pattern.compile(property+limitstart+"(.*?)"+limitend); 
        Matcher m = p.matcher(aaptout); 
        if (m.find()) { 
            return m.group(1); 
        } else { 
            Logger.getGlobal().log(Level.INFO, "Failed to parse {0}", property);
            return null;
        } 
    }
}