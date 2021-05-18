package com.crossroadsinn.signups;

import com.crossroadsinn.settings.Role;
import com.crossroadsinn.settings.Roles;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * A class that can hold information about a player.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class Player {

    //public static String[] ROLES = {"DPS", "Banners", "Offheal", "Heal Renegade", "Heal FB", "Druid", "Alacrigade", "Quickness FB", "Offchrono", "Chrono Tank", "Quickness Chrono"};
    private final String gw2Account;
    private final String discordName;
    private final String tier;
    private final String comments;
    private final String[] bossLvlChoice;
    private int roles;
    private final SimpleStringProperty assignedRole = new SimpleStringProperty();
    private ChangeListener<String> assignedRoleListener;
	private Role assignedRoleObj;
	private ArrayList<Role> availableRoleObj = new ArrayList<>();

    public Player(String gw2Account, String discordName, String tier, String comments, int roles, String[] bossLvlChoice) {
        this.gw2Account = gw2Account;
        this.discordName = discordName;
        this.tier = tier;
        this.comments = comments;
        this.roles = roles;
        this.bossLvlChoice = bossLvlChoice;
		for (Role role:Roles.getAllRoles()) {
			if ((roles & role.getRoleBit())>0) availableRoleObj.add(role);
		}
    }

    public Player(Player player) {
        this(player.getGw2Account(), player.getDiscordName(), player.getTier(), player.getComments(), player.getRoles(), player.getBossLvlChoice());
        this.assignedRole.set(player.getAssignedRole());
    }

    public String toString() {
        return assignedRole.get() == null ? getName() : String.format("%s - %s", getName(), assignedRole.get());
    }

    public String getGw2Account() {
        return gw2Account;
    }

    public String getDiscordName() {
        return discordName;
    }

    public String getTier() {
        return tier;
    }

    public String getComments() {
        return comments;
    }

    public int getRoles() {
        return roles;
    }

    public void setRoles(int roles) {
        this.roles = roles;
    }

    public void setAssignedRole(int role) {
		if (role == 0) {
			this.assignedRoleObj = null;
			this.assignedRole.set(null);
		} else {
			this.assignedRoleObj = Roles.getRole(role);
			this.assignedRole.set(Roles.getRoleName(role));
		}
    }
	
	public void setAssignedRole(Role role) {
		this.assignedRoleObj = role;
		this.assignedRole.set(role.getRoleName());
	}

    public void setAssignedRole(String role) {
        this.assignedRole.set(role);
    }

    public String getAssignedRole() {
        return assignedRole.get();
    }

    public Role getAssignedRoleObj() {
        return assignedRoleObj;
    }
	
	public SimpleStringProperty assignedRoleProperty() {
        return assignedRole;
    }

    public String[] getBossLvlChoice() {
        return bossLvlChoice;
    }

    public String getBossLvlChoiceAsString() {
        return String.join(", ",bossLvlChoice);
    }

    public String[] getRoleList() {
        ArrayList<String> roleList = new ArrayList<>();
		for (Role role:Roles.getAllRoles()) {
			if ((roles & role.getRoleBit())>0) roleList.add(role.getRoleName());
		}
        /*int power = 2;
        if ((roles & 2) > 0) roleList.add("Power DPS");
        if ((roles & 1) > 0) roleList.add("Condi DPS");
        while (power < Roles.getAllRolesAsStrings().length + 2) {
            int bitMask = (int) Math.pow(2, power);
            if (!((roles & bitMask) == 0)) roleList.add(Roles.getRoleName(bitMask));
            ++power;
        }*/
        return roleList.toArray(new String[roleList.size()]);
    }
	
	public ArrayList<Role> getRoleObj() {
		return availableRoleObj;
	}

    public Set<String> getSimpleRoleList() {
        HashSet<String> rolesAvailable = new HashSet<>();
        for (int i = 0; i < Roles.getAllRolesAsStrings().length + 2; ++i) {
            int roleNum = (int) Math.pow(2, i);
            if ((roles & roleNum) > 0) rolesAvailable.add(Roles.getRoleName(roleNum));
        }
        return rolesAvailable;
    }

    public void setRoleListener(ChangeListener<String> changeListener) {
        assignedRoleListener = changeListener;
        assignedRole.addListener(changeListener);
    }

    public void clearRoleListener() {
        assignedRole.removeListener(assignedRoleListener);
        assignedRoleListener = null;
    }

    public String getName() {
        return gw2Account.isBlank() ? discordName : gw2Account;
    }

    /**
     * Translate an integer role value into it's role name.
     * @param role The role value.
     * @return The name of the role.
     */
    /*public static String roleValToName(int role) {
        switch (role) {
            case 1:
            case 2:
            case 3:
                return ROLES[0];
            case 4:
                return ROLES[1];
            case 8:
                return ROLES[2];
            case 16:
                return ROLES[3];
            case 32:
                return ROLES[4];
            case 64:
                return ROLES[5];
            case 128:
                return ROLES[6];
            case 256:
                return ROLES[7];
            case 512:
                return ROLES[8];
            case 1024:
                return ROLES[9];
            case 2048:
                return ROLES[10];
            default:
                return null;
        }
    }*/
}
