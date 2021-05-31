package com.crossroadsinn.settings;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Hashtable;


/**
 * Currently available squads
 * @author moon
 * @version 1.1
 */
public class Squads {
	private static final Hashtable<String, Squad> squads = new Hashtable<String, Squad>();
	public static void init() {
		try {
			File csvFile = new File("squads.csv");
			FileInputStream csvFileInputStream = new FileInputStream(csvFile);
			InputStreamReader csvInputStreamReader = new InputStreamReader(csvFileInputStream,StandardCharsets.UTF_8);
			parse(csvInputStreamReader);
		} catch (IOException e) {
            e.printStackTrace();
        }
	}
		
	public static void addSquad(String squadHandle, String squadName, String reqBoons, String reqSpecialRoles) {
		squads.put(squadHandle,new Squad(squadHandle, squadName, reqBoons, reqSpecialRoles, false));
    }

	public static void addSquad(String squadHandle, String squadName, String reqBoons, String reqSpecialRoles, boolean isDefault) {
		squads.put(squadHandle,new Squad(squadHandle, squadName, reqBoons, reqSpecialRoles, isDefault));
    }
	
	public static Squad getSquad(String squadHandle) {
		return squads.get(squadHandle);
	}
	
	public static ArrayList<Squad> getSquads() {
		return new ArrayList<Squad>(squads.values());
	}
	
	public static String getSquadName(String squadHandle) {
		return ((squads.containsKey(squadHandle)) ? squads.get(squadHandle).getSquadName() : "Unknown Squad");
	}
	    /**
     * Parse a given CSV and generate a list of squads it contains.
     * @param reader The csv stream to parse.
     */
    public static void parse(InputStreamReader reader) {
        CSVReader parser = null;
        try {
            parser = new CSVReader(reader);
            String [] line;
            // Ignore first line
            parser.readNext();
            while ((line = parser.readNext()) != null) {
                parseSquad(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (parser != null) parser.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
	 
	
	/**
     * Generate role given the information contained in a line.
     * @param squadLine The line containing the role info.
     */
	private static void parseSquad(String[] squadLine) {
		String squadHandle = squadLine[0].trim();
		String squadName = squadLine[1].trim();
		String reqBoons = squadLine[2].trim();
		String reqSpecialRoles = squadLine[3].trim();
		boolean isDefault = squadLine[4].toLowerCase().contains("true");
		addSquad(squadHandle,squadName,reqBoons,reqSpecialRoles,isDefault);		
	}
}