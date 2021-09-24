package com.crossroadsinn.problem.entities;

import java.util.HashSet;
import java.util.List;
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
    private final String discordPing;
    private final String tier;
    private final String comments;
    private final String[] bossLvlChoice;
    private List<Role> roles;
    private Role assignedRole;

    public Player(String gw2Account, String discordName, String discordPing, String tier, String comments, List<Role> roles, String[] bossLvlChoice) {
        this.gw2Account = gw2Account;
        this.discordName = discordName;
        this.discordPing = discordPing;
        this.tier = tier;
        this.comments = comments;
        this.bossLvlChoice = bossLvlChoice;
        this.roles = roles;
    }
	
    public Player(String gw2Account, String discordName, String tier, String comments, List<Role> roles, String[] bossLvlChoice) {
        this(gw2Account, discordName, "@" + discordName, tier, comments, roles, bossLvlChoice);
    }

    // TODO WTF IS THIS COPY CONSTRUCTOR ?????
    public Player(Player player) {
        this(player.getGw2Account(), player.getDiscordName(), player.getDiscordPing(), player.getTier(), player.getComments(), player.getRoles(), player.getBossLvlChoice());
        this.assignedRole = player.assignedRole;
    }

    public String toString() {
        return assignedRole == null ? getName() : String.format("%s - %s", getName(), assignedRole);
    }

    public String getGw2Account() {
        return gw2Account;
    }

    public String getDiscordName() {
        return discordName;
    }
	
    public String getDiscordPing() {
        return discordPing;
    }

    public String getTier() {
        return tier;
    }

    public String getComments() {
        return comments;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public void setAssignedRole(Role role) {
		assignedRole = role;
    }

    public Role getAssignedRole() {
        return assignedRole;
    }

    public String[] getBossLvlChoice() {
        return bossLvlChoice;
    }

    public String getBossLvlChoiceAsString() {
        return String.join(", ",bossLvlChoice);
    }

    public String[] getRoleList() {
        return getSimpleRoleList().toArray(new String[0]);
    }

    public Set<String> getSimpleRoleList() {
        HashSet<String> roleList = new HashSet<>();
        roles.forEach(r -> roleList.add(r.getRoleName()));
        return roleList;
    }

    public String getName() {
        return gw2Account.isBlank() ? discordName : gw2Account;
    }
}
