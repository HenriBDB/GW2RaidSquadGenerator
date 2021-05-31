package com.crossroadsinn.settings;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;


/**
 * Currently available roles
 * @author moon
 * @version 1.1
 */
public class Roles {
	private static final Hashtable<String, Integer> roleToNumber = new Hashtable<>();
	private static final Hashtable<Integer, Role> roles = new Hashtable<>();
	private static int roleCounter = 1;
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
		roleToNumber.put(roleHandle,roleCounter);
		roles.put(roleCounter,new Role(roleCounter,roleHandle,roleName,boons,specialRoles, commRole));
		roleCounter = roleCounter*2;		
	}
	
	public static int getRoleNumber(String roleHandle) {
		return ((roleToNumber.getOrDefault(roleHandle, 0)));
	}
	
	public static String getRoleName(int roleNumber) {
		return ((roles.containsKey(roleNumber)) ? roles.get(roleNumber).getRoleName() : "");
	}
	
	public static Role getRole(int roleNumber) {
		return roles.get(roleNumber);
	}
	
	public static ArrayList<Role> getAllRoles() {
		return new ArrayList<>(roles.values());
	}
	
	public static int getRoleCounter() {
		return roleCounter;
	}
	
	public static int[] getAllRolesNumbers() {
		int[] rolesNumbers = new int[roles.size()];
		int i = 0;
		for(int key:roles.keySet()) {
			rolesNumbers[i] = key;
			i++;
		}
		//shuffle dat bitch so roles come in a random order for more randomness when trying to assign
		int index;
		Random random = new Random();
		for (i = rolesNumbers.length - 1; i > 0; i--)
		{
			index = random.nextInt(i + 1);
			if (index != i)
			{
				rolesNumbers[index] ^= rolesNumbers[i];
				rolesNumbers[i] ^= rolesNumbers[index];
				rolesNumbers[index] ^= rolesNumbers[i];
			}
		}
		return rolesNumbers;
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
		Boolean commRole = (roleLine[4].toLowerCase().contains("true")) ? true : false;
		addRole(roleHandle,roleName,boons,specialRoles,commRole);		
	}
		
}