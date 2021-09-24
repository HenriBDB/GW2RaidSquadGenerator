package com.crossroadsinn.problem.entities;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 * Currently available roles
 * @author moon
 * @version 1.1
 */
public class Roles {
	private static final HashMap<String, Role> roles = new HashMap<>();
	public static void init() {
		try {
			File csvFile = new File("roles.csv");
			FileInputStream csvFileInputStream = new FileInputStream(csvFile);
			InputStreamReader csvInputStreamReader = new InputStreamReader(csvFileInputStream,StandardCharsets.UTF_8);
			parse(csvInputStreamReader);
		} catch (IOException e) {
            e.printStackTrace();
        }
	}
		
	public static void addRole(String roleHandle, String roleName, String boons, String specialRoles) {
		addRole(roleHandle, roleName, boons, specialRoles, true);
    }
	
	public static void addRole(String roleHandle, String roleName, String boons, String specialRoles, boolean commRole) {
		roles.put(roleHandle,new Role(roleHandle,roleName,boons,specialRoles, commRole));
	}
	
	public static Role getRole(String roleHandle) {
		return roles.get(roleHandle);
	}
	
	public static List<Role> getAllRoles() {
		return new ArrayList<>(roles.values());
	}
	
	public static String[] getAllRolesAsStrings() {
		String[] rolesString = new String[roles.size()];
		int i = 0;
		for(Role value:roles.values()) {
			rolesString[i] = value.getRoleName();
			i++;
		}
		return rolesString;
	}

	public static List<Role> getAllSpecifiedRoles(String... roleHandles) {
		List<Role> roleList = new ArrayList<>();
		for (String role : roleHandles) {
			roleList.add(roles.get(role));
		}
		return roleList;
	}
	
    /**
     * Parse a given CSV and generate a list of roles it contains.
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
                parseRole(line);
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
     * @param roleLine The line containing the role info.
     */
	private static void parseRole(String[] roleLine) {
		String roleHandle = roleLine[0].trim();
		String roleName = roleLine[1].trim();
		String boons = roleLine[2].trim();
		String specialRoles = roleLine[3].trim();
		boolean commRole = roleLine[4].toLowerCase().contains("true");
		addRole(roleHandle,roleName,boons,specialRoles,commRole);		
	}
		
}