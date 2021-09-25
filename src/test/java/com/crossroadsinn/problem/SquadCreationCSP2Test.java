package com.crossroadsinn.problem;

import com.crossroadsinn.problem.entities.*;
import com.crossroadsinn.search.BestFirstSearchTask;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SquadCreationCSP2Test {

    /*
    Deterministic tests used for QA and unit testing
     */

    /*
    Non-deterministic tests used for validation and efficiency testing
     */

    @Test
    public void testClassicSquadCreation() {
        testClassicSquadsCreation(1);
    }

    @Test
    public void test2ClassicSquadCreation() {
        testClassicSquadsCreation(2);
    }

    @Test
    public void test3ClassicSquadCreation() {
        testClassicSquadsCreation(3);
    }

    @Test
    public void test4ClassicSquadCreation() {
        testClassicSquadsCreation(4);
    }

    @Test
    public void test5ClassicSquadCreation() {
        testClassicSquadsCreation(5);
    }

    @Test
    public void testMultipleClassicSquadCreation() {
        int numSquads = 2;
        List<Player> playerList = create10ManDefaultSquadAndPlayers(numSquads);
        // Run CSP
        HashMap<String, Integer> squadReqs = new HashMap<>();
        squadReqs.put("default", numSquads);
        int tries = 10000;
        float success = 0;
        for (int i = 0; i<tries; ++i) {
            SquadCreationCSP2 csp = new SquadCreationCSP2(squadReqs, playerList, new ArrayList<>());
            BestFirstSearchTask search = new BestFirstSearchTask(csp);
            if (search.solve() != null) ++success;
            search = null;
            csp = null;
        }
        // Print out results
        System.out.println(String.format("Success rate is %d/%d (%.3f).", (int) success, tries, success/tries));
    }

    private void testClassicSquadsCreation(int numSquads) {
        // Create 50 players
        List<Player> playerList = create10ManDefaultSquadAndPlayers(numSquads);
        // Run CSP
        HashMap<String, Integer> squadReqs = new HashMap<>();
        squadReqs.put("default", numSquads);
        SquadCreationCSP2 csp = new SquadCreationCSP2(squadReqs, playerList, new ArrayList<>());
        BestFirstSearchTask search = new BestFirstSearchTask(csp);
        CSP result = search.solve();
        // Print out results
        System.out.println(result);
        System.out.println(String.format("Result in %d nodes.", search.getNodes()));
    }

    private List<Player> create10ManDefaultSquadAndPlayers() {
        return create10ManDefaultSquadAndPlayers(1);
    }

    private List<Player> create10ManDefaultSquadAndPlayers(int numSquads) {
        if (numSquads <= 0) return null;
        // Create numSquads*10 players
        List<Player> playerList = EntitySetup.generateRandomPlayerList(10*numSquads);
        for (int i = 0; i < numSquads; ++i) {
            playerList.get(i*10).setRoles(Roles.getAllSpecifiedRoles("druid", "dps", "banners"));
            playerList.get(i*10+1).setRoles(Roles.getAllSpecifiedRoles("ctank", "dps"));
            playerList.get(i*10+2).setRoles(Roles.getAllSpecifiedRoles("scam", "dps"));
            playerList.get(i*10+3).setRoles(Roles.getAllSpecifiedRoles("healscrapper"));
            playerList.get(i*10+4).setRoles(Roles.getAllSpecifiedRoles("banners", "dps"));
            playerList.get(i*10+5).setRoles(Roles.getAllSpecifiedRoles("ctank", "rrrenegade", "banners"));
            playerList.get(i*10+6).setRoles(Roles.getAllSpecifiedRoles("dps"));
            playerList.get(i*10+7).setRoles(Roles.getAllSpecifiedRoles("dps"));
            playerList.get(i*10+8).setRoles(Roles.getAllSpecifiedRoles("dps"));
            playerList.get(i*10+9).setRoles(Roles.getAllSpecifiedRoles("dps"));
        }
        // Create squad
        Squads.addSquad("default", "Default", "alacrity:10, quickness:10",
                "ctank:1, druid:1, banners:1", "", 10);
        return playerList;
    }

    private List<Player> create50ManDefaultSquadAndPlayers() {
        // Create 10 players
        List<Player> playerList = EntitySetup.generateRandomPlayerList(50);
        playerList.get(0).setRoles(Roles.getAllSpecifiedRoles("druid", "dps", "banners"));
        playerList.get(1).setRoles(Roles.getAllSpecifiedRoles("ctank", "dps"));
        playerList.get(2).setRoles(Roles.getAllSpecifiedRoles("scam", "dps"));
        playerList.get(3).setRoles(Roles.getAllSpecifiedRoles("healscrapper"));
        playerList.get(4).setRoles(Roles.getAllSpecifiedRoles("banners", "dps"));
        playerList.get(5).setRoles(Roles.getAllSpecifiedRoles("ctank", "rrrenegade", "banners"));
        playerList.get(6).setRoles(Roles.getAllSpecifiedRoles("dps"));
        playerList.get(7).setRoles(Roles.getAllSpecifiedRoles("dps"));
        playerList.get(8).setRoles(Roles.getAllSpecifiedRoles("dps"));
        playerList.get(9).setRoles(Roles.getAllSpecifiedRoles("dps"));
        playerList.get(10).setRoles(Roles.getAllSpecifiedRoles("druid", "dps", "banners"));
        playerList.get(11).setRoles(Roles.getAllSpecifiedRoles("ctank", "dps"));
        playerList.get(12).setRoles(Roles.getAllSpecifiedRoles("scam", "dps"));
        playerList.get(13).setRoles(Roles.getAllSpecifiedRoles("healscrapper"));
        playerList.get(14).setRoles(Roles.getAllSpecifiedRoles("banners", "dps"));
        playerList.get(15).setRoles(Roles.getAllSpecifiedRoles("ctank", "rrrenegade", "banners"));
        playerList.get(16).setRoles(Roles.getAllSpecifiedRoles("dps"));
        playerList.get(17).setRoles(Roles.getAllSpecifiedRoles("dps"));
        playerList.get(18).setRoles(Roles.getAllSpecifiedRoles("dps"));
        playerList.get(19).setRoles(Roles.getAllSpecifiedRoles("dps"));
        playerList.get(20).setRoles(Roles.getAllSpecifiedRoles("druid", "dps", "banners"));
        playerList.get(21).setRoles(Roles.getAllSpecifiedRoles("ctank", "dps"));
        playerList.get(22).setRoles(Roles.getAllSpecifiedRoles("scam", "dps"));
        playerList.get(23).setRoles(Roles.getAllSpecifiedRoles("healscrapper"));
        playerList.get(24).setRoles(Roles.getAllSpecifiedRoles("banners", "dps"));
        playerList.get(25).setRoles(Roles.getAllSpecifiedRoles("ctank", "rrrenegade", "banners"));
        playerList.get(26).setRoles(Roles.getAllSpecifiedRoles("dps"));
        playerList.get(27).setRoles(Roles.getAllSpecifiedRoles("dps"));
        playerList.get(28).setRoles(Roles.getAllSpecifiedRoles("dps", "druid"));
        playerList.get(29).setRoles(Roles.getAllSpecifiedRoles("dps"));
        playerList.get(30).setRoles(Roles.getAllSpecifiedRoles("druid", "dps", "banners"));
        playerList.get(31).setRoles(Roles.getAllSpecifiedRoles("ctank", "dps"));
        playerList.get(32).setRoles(Roles.getAllSpecifiedRoles("scam", "dps"));
        playerList.get(33).setRoles(Roles.getAllSpecifiedRoles("healscrapper"));
        playerList.get(34).setRoles(Roles.getAllSpecifiedRoles("banners", "dps"));
        playerList.get(35).setRoles(Roles.getAllSpecifiedRoles("ctank", "rrrenegade", "banners"));
        playerList.get(36).setRoles(Roles.getAllSpecifiedRoles("dps"));
        playerList.get(37).setRoles(Roles.getAllSpecifiedRoles("dps"));
        playerList.get(38).setRoles(Roles.getAllSpecifiedRoles("dps"));
        playerList.get(39).setRoles(Roles.getAllSpecifiedRoles("dps"));
        playerList.get(40).setRoles(Roles.getAllSpecifiedRoles("druid", "dps", "banners"));
        playerList.get(41).setRoles(Roles.getAllSpecifiedRoles("ctank", "dps"));
        playerList.get(42).setRoles(Roles.getAllSpecifiedRoles("scam", "dps"));
        playerList.get(43).setRoles(Roles.getAllSpecifiedRoles("healscrapper"));
        playerList.get(44).setRoles(Roles.getAllSpecifiedRoles("banners", "dps"));
        playerList.get(45).setRoles(Roles.getAllSpecifiedRoles("ctank", "rrrenegade", "banners"));
        playerList.get(46).setRoles(Roles.getAllSpecifiedRoles("dps"));
        playerList.get(47).setRoles(Roles.getAllSpecifiedRoles("dps"));
        playerList.get(48).setRoles(Roles.getAllSpecifiedRoles("dps"));
        playerList.get(49).setRoles(Roles.getAllSpecifiedRoles("dps"));
        // Create squad
        Squads.addSquad("default", "Default", "alacrity:10, quickness:10",
                "ctank:1, druid:1, banners:1", "", 10);
        return playerList;
    }
}
