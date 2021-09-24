package com.crossroadsinn.problem.entities;

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
	private static final Hashtable<String, Squad> squads = new Hashtable<>();
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
		
	public static void addSquad(String squadHandle, String squadName, String reqBoons, String reqRoles, String reqSpecialRoles, int maxPlayers) {
		squads.put(squadHandle,new Squad(squadHandle, squadName, maxPlayers, reqBoons, reqRoles, reqSpecialRoles));
    }
	
	public static Squad getSquad(String squadHandle) {
		return squads.get(squadHandle);
	}

	public static Squad getSquadCopy(String squadHandle) {
		return new Squad(squads.get(squadHandle));
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
		String reqRoles = squadLine[3].trim();
		String reqSpecialRoles = squadLine[4].trim();
		int maxPlayers = Integer.parseInt(squadLine[5].trim());
		addSquad(squadHandle,squadName,reqBoons,reqRoles,reqSpecialRoles,maxPlayers);
	}
}