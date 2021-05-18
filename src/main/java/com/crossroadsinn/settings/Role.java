package com.crossroadsinn.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
/**
 * A class that can hold information about a role.
 * @author moon
 * @version 1.1
 */
 
public class Role {
	private final int roleBit;
    private final String roleHandle;
    private final String roleName;
    private final boolean commRole;
	private Hashtable<String, Integer> boons = new Hashtable<String, Integer>();
    private ArrayList<String> specialRoles = new ArrayList<>();

    public Role(int roleBit, String roleHandle, String roleName, String Boons, String specialRoles, boolean commRole) {
		this.roleBit = roleBit;
        this.roleHandle = roleHandle;
        this.roleName = roleName;
        this.commRole = commRole;
		if (!Boons.isEmpty()) {
			for (String part:Boons.split(", ")) {
				String[] BoonsValuePair = part.split(":");
				if (boons.containsKey(BoonsValuePair[0])) {
					boons.put(BoonsValuePair[0],boons.get(BoonsValuePair[0])+Integer.parseInt(BoonsValuePair[1]));
				} else {
					boons.put(BoonsValuePair[0],Integer.parseInt(BoonsValuePair[1]));
				}
			}
		}
		if (!specialRoles.isEmpty()) {
			this.specialRoles.addAll(Arrays.asList(specialRoles.split(", ")));
		}
    }
	
	public String toString() {
		return roleName;
	}

    public String getRoleHandle() {
        return roleHandle;
    }
    public String getRoleName() {
        return roleName;
    }	
	public int getRoleBit() {
        return roleBit;
    }	
	public boolean getCommRole() {
        return commRole;
    }	
	
	public int getDPS() {
        return ((specialRoles.contains("dps")) ? 1 : 0);
    }	
	
	public int getBoonAmount(String boon) {
		return ((boons.containsKey(boon)) ? boons.get(boon) : 0);
	}
	
	public int getIfRole(String role) {
		return ((specialRoles.contains(role)) ? 1 : 0);
	}
	
	public Hashtable<String, Integer> getBoons() {
        return boons;
    }
    public ArrayList<String> getSpecialRoles() {
        return specialRoles;
    }
}