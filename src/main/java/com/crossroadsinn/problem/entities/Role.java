package com.crossroadsinn.problem.entities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
/**
 * A class that can hold information about a role.
 * @author moon
 * @version 1.1
 */
 
public class Role {
    private final String roleHandle;
    private final String roleName;
    private final boolean commRole;
	private final HashMap<BoonCounter.Boon, Integer> boons = new HashMap<>();
    private final ArrayList<String> specialRoles = new ArrayList<>();

    public Role(String roleHandle, String roleName, String Boons, String specialRoles, boolean commRole) {
        this.roleHandle = roleHandle;
        this.roleName = roleName;
        this.commRole = commRole;
		if (!Boons.isEmpty()) {
			for (String part:Boons.split("\\s*,\\s*")) {
				String[] BoonsValuePair = part.split("\\s*:\\s*");
				if (boons.containsKey(BoonsValuePair[0])) {
					boons.put(BoonCounter.Boon.valueOf(BoonsValuePair[0].toUpperCase()),boons.get(BoonsValuePair[0])+Integer.parseInt(BoonsValuePair[1]));
				} else {
					boons.put(BoonCounter.Boon.valueOf(BoonsValuePair[0].toUpperCase()),Integer.parseInt(BoonsValuePair[1]));
				}
			}
		}
		if (!specialRoles.isEmpty()) {
			this.specialRoles.addAll(Arrays.asList(specialRoles.split("\\s*,\\s*")));
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
	public boolean getCommRole() {
        return commRole;
    }	
	
	public int getDPS() {
        return ((specialRoles.contains("dps")) ? 1 : 0);
    }	
	
	public int getBoonAmount(BoonCounter.Boon boon) {
		return boons.getOrDefault(boon, 0);
	}
	public int getBoonAmount(String name) {
    	return boons.getOrDefault(BoonCounter.Boon.valueOf(name), 0);
	}
	
	public int getIfRole(String role) {
		return ((specialRoles.contains(role)) ? 1 : 0);
	}
	
	public HashMap<BoonCounter.Boon, Integer> getBoons() {
        return boons;
    }
    public ArrayList<String> getSpecialRoles() {
        return specialRoles;
    }
}