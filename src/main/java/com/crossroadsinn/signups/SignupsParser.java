package com.crossroadsinn.signups;

import com.crossroadsinn.problem.entities.Player;
import com.crossroadsinn.problem.entities.Role;
import com.crossroadsinn.problem.entities.Roles;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Uses OpenCSV to parse players in a sign-up sheet.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class SignupsParser {

    // private static final String[] columns = {"gw2 account", "discord account", "tier", "comments", "tank", "druid", "offheal", "chrono", "alacrigade", "quickbrand", "banners", "dps"};
    private final int[] bossLvlIndices = {-1, -1, -1};
    private final boolean isSaturday = true;

    /**
     * Parse a given CSV and generate a list of players it contains.
     * @param reader The csv stream to parse.
     * @return The list of generated players.
     */
    public ArrayList<Player> parse(InputStreamReader reader) {
        ArrayList<Player> players = new ArrayList<>();
        CSVReader parser = null;
        try {
            parser = new CSVReader(reader);
            String [] line;
            Player player;
            // Ignore first line
            line = parser.readNext();
            // Invalid file.
            Map<String, Integer> columnIndices = getColumnIndices(line);
            if (columnIndices == null) return null;
            while ((line = parser.readNext()) != null) {
                if ((player = parsePlayer(line, columnIndices)) != null) players.add(player);
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
        return players;
    }

    /**
     * Generate player given the information contained in a line.
     * @param playerLine The line containing the player info.
     * @return The Player object.
     */
    private Player parsePlayer(String[] playerLine, Map<String, Integer> columnIndices) {
        List<String> playerRoleNames = Arrays.asList(playerLine[columnIndices.get("roles")].split("\\s*,\\s*"));
        List<Role> playerRoles = Roles.getAllRoles().stream().filter(e ->
                playerRoleNames.contains(e.getRoleName())
        ).collect(Collectors.toList());
        if (playerRoles.isEmpty()) return null;

        String gw2Account = playerLine[columnIndices.get("gw2Account")];
        String discordName = playerLine[columnIndices.get("discordAccount")];
        String discordPing = playerLine[columnIndices.get("discordPing")];
        String tier = playerLine[columnIndices.get("tier")];
        String comments = playerLine[columnIndices.get("comments")];

		String[] bossLvlChoice = playerLine[columnIndices.get("trainingLevel")].split("\\s*,\\s*");

        return new Player(gw2Account, discordName, discordPing, tier, comments, playerRoles, bossLvlChoice);
    }

    public Map<String, Integer> getColumnIndices(String[] headerLine) {
        Map<String, Integer> columnIndices = new HashMap<>();
        // {"gw2 account", "discord account", "tier", "comments", "tank", "druid", "offheal", "chrono", "alacrigade", "quickbrand", "banners", "dps"}
        // Timestamp should be column 0
        List<String> lowerCaseHeaderLine = Arrays.stream(headerLine).map(String::toLowerCase).map(String::trim).collect(Collectors.toList());
        columnIndices.put("gw2Account", lowerCaseHeaderLine.indexOf("gw2 account"));
        columnIndices.put("discordAccount", lowerCaseHeaderLine.indexOf("discord account"));
        columnIndices.put("discordPing", lowerCaseHeaderLine.indexOf("discord ping"));
        columnIndices.put("comments", lowerCaseHeaderLine.indexOf("comments"));
        columnIndices.put("trainingLevel", lowerCaseHeaderLine.indexOf("training name"));
        columnIndices.put("roles", lowerCaseHeaderLine.indexOf("roles"));
        columnIndices.put("tier", lowerCaseHeaderLine.indexOf("tier"));

        // Valid file, no column missing.
        if (columnIndices.entrySet().stream().noneMatch(e -> e.getValue() == -1)) {
            return columnIndices;
        } else return null;
    }

}
