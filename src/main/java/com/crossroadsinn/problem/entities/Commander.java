package com.crossroadsinn.problem.entities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Wrapper class for a player with an Integer property that
 * determines chosen roles to allow to chose roles for
 * a commander.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class Commander extends Player {

    private Set<Role> chosenRoles = new HashSet<>();

    public Commander(String gw2Account, String discordName, String tier, String comments, List<Role> roles, String[] ComBossLevelChoice) {
        super(gw2Account, discordName, tier, comments, roles, ComBossLevelChoice);
    }

    public Commander(Player player) {
        this(player.getGw2Account(), player.getDiscordName(), player.getTier(), player.getComments(), player.getRoles(), player.getBossLvlChoice());
    }

    public Set<Role> getChosenRoles() {
        return chosenRoles;
    }

    public void setChosenRoles(Set<Role> chosenRoles) {
        this.chosenRoles = chosenRoles;
    }
}
