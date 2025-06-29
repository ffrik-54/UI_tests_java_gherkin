package com.utils;

/**
 * Class to parse a CSV file. 
 * 
 * @author ffrik-54
 */

public class Csv {

	private Csv() {
	}

	private static final String DEFAULT_DELIMITER = ";";
	
// --Commented out by Inspection START (21.08.23, 17:06):
//	public static List<String[]> parseCsv(String path)
//	{
//		return parseCsv(path, DEFAULT_DELIMITER);
//	}
// --Commented out by Inspection STOP (21.08.23, 17:06)

// --Commented out by Inspection START (21.08.23, 17:16):
//	/**
//	 * Parse CSV File.
//	 *
//	 * @param  String Path.
//	 * @param  String delimiter the delimiter to split csv values.
//	 * @return List<String[]> list of String arrays containing the file values.
//	 *
//	 */
//	public static List<String[]> parseCsv(String path, String delimiter) {
//
//		List<String[]> list = new ArrayList<>();
//
//		try (BufferedReader fileReader = new BufferedReader(new FileReader(path))) {
//
//			String line = "";
//
//			while ((line = fileReader.readLine()) != null) {
//
//				String[] tokens = line.split(delimiter);
//				list.add(tokens);
//
//			}
//		} catch (Exception e) {
//			Logger.getGlobal().log(Level.SEVERE, e.toString());
//		}
//		return list;
//	}
// --Commented out by Inspection STOP (21.08.23, 17:16)
}
