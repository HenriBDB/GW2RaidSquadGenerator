package com.crossroadsinn.signups;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Wrapper class for a player with an Integer property that
 * determines chosen roles to allow to chose roles for
 * a commander.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class Commander extends Player {

    IntegerProperty chosenRoles;

    public Commander(String gw2Account, String discordName, String tier, String comments, int roles, String[] ComBossLevelChoice) {
        super(gw2Account, discordName, tier, comments, roles, ComBossLevelChoice);
        chosenRoles = new SimpleIntegerProperty(0);
    }

    public Commander(Player player) {
        this(player.getGw2Account(), player.getDiscordName(), player.getTier(), player.getComments(), player.getRoles(), player.getBossLvlChoice());
    }

    public IntegerProperty getChosenRoles() {
        return chosenRoles;
    }

    public void setChosenRoles(int chosenRoles) {
        this.chosenRoles.set(chosenRoles);
    }
}
