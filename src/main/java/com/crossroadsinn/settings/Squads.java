package com.crossroadsinn.settings;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Currently available squads
 * @author moon
 * @version 1.1
 */
public class Squads {
	private static Hashtable<String, Squad> squads = new Hashtable<String, Squad>();
	public static void init() {
		addSquad("default","Default","alacrity:10, quickness:10","tank:1, druid:1, healer:2, banners:1", true);
		addSquad("doubleDruid","Double Druid Because I can","alacrity:10, quickness:10","tank:1, druid:2, healer:2, banners:1");
		addSquad("soloheal","Soloheal","alacrity:10, quickness:10","tank:1, druid:1, healer:1, banners:1");
		addSquad("qtp1","Qadim the Peerless","alacrity:10, quickness:10","tank:1, druid:1, healer:1, banners:1, qtpkite:3");
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
		ArrayList<Squad> squadsList = new ArrayList<Squad>();
		for (Squad squad:squads.values()) {
			squadsList.add(squad);
		}
		return squadsList;
	}
	
	public static String getSquadName(String squadHandle) {
		return ((squads.containsKey(squadHandle)) ? squads.get(squadHandle).getSquadName() : "Unknown Squad");
	}
}