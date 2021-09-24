package com.crossroadsinn.problem.entities;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class EntitySetup {

    public static List<Player> generateRandomPlayerList() {
        return generateRandomPlayerList(50);
    }

    public static List<Player> generateRandomPlayerList(int numPlayers) {
        Roles.init();
        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < numPlayers; ++i) {
            //Player(String gw2Account, String discordName, String tier, String comments, List<Role> roles, String[] bossLvlChoice)
            String name = "Player"+i;
            List<Role> roles = new ArrayList<>();
            String[] bossLvlChoice = {};
            Player player = new Player(name, name, "1", "", roles, bossLvlChoice);
            playerList.add(player);
        }
        return playerList;
    }

    private static String getName() {
        char[] alphabet = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','n','o','p','q','r','s','t','u','v','w','x','y','z'};
        int nameLength = (int) Math.round(Math.random() * 7) + 3;
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < nameLength; ++i) {
            char letter = alphabet[(int) Math.round(Math.random() * (alphabet.length-1))];
            name.append(letter);
        }
        return name.toString();
    }

    @Test
    public void testNameGen() {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            names.add(getName());
        }
        System.out.println(names);
    }
}
