package com.utils;

/**
 * Methods for data conversion
 * @author ffrik
 */ 

public class Conversion 
{

    private Conversion() {
    }

	/**
	 * Converts value (monetary, percentage...) to Float
	 * @param value a value
	 * @return a number corresponding to the value
	 * @throws Exception
	 */
    public static String valueToNumber(String value) 
    {
        
        //remove all that is not number comma or dot
    	String pattern = "[^0-9,.]";        
    	value = value.replaceAll(pattern, "");  	
    	

        // Replace all dots with commas
    	value = value.replace("\\.", ",");

        // If fractions exist, the separator must be a dot
        if(value.length()>=3) {
            char[] chars = value.toCharArray();
            if(chars[chars.length-2] == ',') {
                chars[chars.length-2] = '.';
            } else if(chars[chars.length-3] == ',') {
                chars[chars.length-3] = '.';
            }
            value = new String(chars);
        }

        // Remove all commas      
        return value.replace(",", "");
        
    }
    
}