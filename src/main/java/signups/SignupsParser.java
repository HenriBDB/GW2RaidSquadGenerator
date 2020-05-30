package signups;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Uses OpenCSV to parse players in a sign-up sheet.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class SignupsParser {

    public ArrayList<Player> parse(String fileLoc) {
        return parse(new File(fileLoc));
    }

    /**
     * Parse a given CSV and generate a list of players it contains.
     * @param file The csv to parse.
     * @return The list of generated players.
     */
    public ArrayList<Player> parse(File file) {
        ArrayList<Player> players = new ArrayList<>();
        CSVReader parser = null;
        try {
            parser = new CSVReader(new FileReader(file));
            String [] line;
            Player player;
            // Ignore first line
            parser.readNext();
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
        String gw2Account = playerLine[1];
        String discordName = playerLine[2];
        String tier = playerLine[3];
        String comments = playerLine[4];
        int roles = 0;
        if (!playerLine[5].isBlank()) roles += 1024; // cTank
        if (!playerLine[6].isBlank()) roles += 64; // Druid
        if (!playerLine[7].isBlank()) roles += 8; // Offheal
        if (!playerLine[8].isBlank()) roles += 512; // cSupp
        if (playerLine[9].toLowerCase().contains("dps")) roles += 128; // Alacrigade
        if (playerLine[9].toLowerCase().contains("healer")) roles += 16; // Heal Rene
        if (playerLine[10].toLowerCase().contains("dps")) roles += 256; // qFB
        if (playerLine[10].toLowerCase().contains("healer")) roles += 32; // hFB
        if (!playerLine[11].isBlank()) roles += 7; // BS, add dps roles too to allow bs to play dps.
        else if (!playerLine[12].isBlank()) { // dps
            if (playerLine[12].toLowerCase().contains("power")) roles += 2; // pdps
            if (playerLine[12].toLowerCase().contains("condition")) roles += 1; // cdps
        }
        if (roles == 0) return null;
        return new Player(gw2Account, discordName, tier, comments, roles);
    }


}
