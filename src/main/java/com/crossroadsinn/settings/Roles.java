package com.crossroadsinn.settings;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;

import java.util.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/**
 * Currently available roles
 * @author moon
 * @version 1.1
 */
public class Roles {
	private static Hashtable<String, Integer> roleToNumber = new Hashtable<String, Integer>();
	private static Hashtable<Integer, Role> roles = new Hashtable<Integer, Role>();
	private static int roleCounter = 1;
	public static void init() {
		addRole("ctank","Chrono Tank","quickness:5","tank");
		addRole("druid","Druid","","healer, druid");
		addRole("hfb","Heal FB","quickness:5","healer");
		addRole("quicknesschrono","Quickness Chrono","quickness:5","");
		addRole("qfb","Quickbrand","quickness:5","");
		addRole("alacgade","Alacrigade","alacrity:10","");
		addRole("banners","Banners","","banners");
		addRole("dps","DPS","","dps");
		addRole("healscrapper","Heal Scrapper","quickness:5","healer");
		addRole("scam","Staff Condi Alacrity Mirage","alacrity:10","");
		addRole("qtpkitede","Pylonkite","","qtpkite",false);
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
		return ((roleToNumber.containsKey(roleHandle)) ? (roleToNumber.get(roleHandle)) : 0);
	}
	
	public static String getRoleName(int roleNumber) {
		return ((roles.containsKey(roleNumber)) ? roles.get(roleNumber).getRoleName() : "");
	}
	
	public static Role getRole(int roleNumber) {
		return roles.get(roleNumber);
	}
	
	public static ArrayList<Role> getAllRoles() {
		ArrayList<Role> rolesArray = new ArrayList<Role>();
		for(Role value:roles.values()) {
			rolesArray.add(value);
		}
		return rolesArray;
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
}