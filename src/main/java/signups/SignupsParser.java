package signups;

import com.opencsv.CSVReader;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 * Uses OpenCSV to parse players in a sign-up sheet.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class SignupsParser {

    // private static final String[] columns = {"gw2 account", "discord account", "tier", "comments", "tank", "druid", "offheal", "chrono", "alacrigade", "quickbrand", "banners", "dps"};
    private final int[] columnIndices = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    private final int[] bossLvlIndices = {-1, -1, -1};
    private boolean isSaturday;

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
            // Empty player list = invalid file.
            if (!getColumnIndices(line)) return players;
            while ((line = parser.readNext()) != null)
            {
                if ((player = parsePlayer(line)) != null) players.add(player);
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
    private Player parsePlayer(String[] playerLine) {
        String gw2Account = playerLine[columnIndices[0]];
        String discordName = playerLine[columnIndices[1]];
        String tier = playerLine[columnIndices[2]];
        String comments = playerLine[columnIndices[3]];
        int roles = 0;
        if (!playerLine[columnIndices[4]].isBlank()) roles += 1024; // cTank
        if (!playerLine[columnIndices[5]].isBlank()) roles += 64; // Druid
        if (!playerLine[columnIndices[6]].isBlank()) roles += 8; // Offheal
        if (playerLine[columnIndices[7]].toLowerCase().contains("offchrono")) roles += 512; // cSupp
        if (playerLine[columnIndices[7]].toLowerCase().contains("quickchrono")) roles += 2048; // cSupp
        if (playerLine[columnIndices[8]].toLowerCase().contains("dps")) roles += 128; // Alacrigade
        if (playerLine[columnIndices[8]].toLowerCase().contains("healer")) roles += 16; // Heal Rene
        if (playerLine[columnIndices[9]].toLowerCase().contains("dps")) roles += 256; // qFB
        if (playerLine[columnIndices[9]].toLowerCase().contains("healer")) roles += 32; // hFB
        if (!playerLine[columnIndices[10]].isBlank()) roles += 4; // BS, add dps roles too to allow bs to play dps.
        if (!playerLine[columnIndices[11]].isBlank()) { // dps
            if (playerLine[columnIndices[11]].toLowerCase().contains("power")) roles += 2; // pdps
            if (playerLine[columnIndices[11]].toLowerCase().contains("condition")) roles += 1; // cdps
        }
        if (roles == 0) return null;
        int bossLvlChoice = 0;
        if (isSaturday) {
            if (!playerLine[bossLvlIndices[0]].isBlank()) ++bossLvlChoice;
            if (!playerLine[bossLvlIndices[1]].isBlank()) bossLvlChoice += 2;
            if (!playerLine[bossLvlIndices[2]].isBlank()) bossLvlChoice += 4;
        } else bossLvlChoice = 7;
        return new Player(gw2Account, discordName, tier, comments, roles, bossLvlChoice);
    }

    private boolean getColumnIndices(String[] headerLine) {
        // {"gw2 account", "discord account", "tier", "comments", "tank", "druid", "offheal", "chrono", "alacrigade", "quickbrand", "banners", "dps"}
        for (int i = 0; i < headerLine.length; ++i) {
            String header = headerLine[i].toLowerCase();
            if (header.contains("gw2 account")) columnIndices[0] = i;
            else if (header.contains("discord account")) columnIndices[1] = i;
            else if (header.contains("tier")) columnIndices[2] = i;
            else if (header.contains("comments")) columnIndices[3] = i;
            else if (header.contains("tank")) columnIndices[4] = i;
            else if (header.contains("druid")) columnIndices[5] = i;
            else if (header.contains("offheal")) columnIndices[6] = i;
            else if (header.contains("chrono")) columnIndices[7] = i;
            else if (header.contains("alacrigade")) columnIndices[8] = i;
            else if (header.contains("quickbrand")) columnIndices[9] = i;
            else if (header.contains("banners")) columnIndices[10] = i;
            else if (header.contains("dps")) columnIndices[11] = i;
            else if (header.contains("beginner")) bossLvlIndices[0] = i;
            else if (header.contains("intermediate")) bossLvlIndices[1] = i;
            else if (header.contains("advanced")) bossLvlIndices[2] = i;
        }
        // Sign-up includes boss levels and is thus a saturday sheet.
        isSaturday = IntStream.of(bossLvlIndices).noneMatch(e -> e == -1);
        // Valid file, no column missing.
        return IntStream.of(columnIndices).noneMatch(e -> e == -1);
    }

}
