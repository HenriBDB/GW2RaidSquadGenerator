package signups;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Implements methods that help save squad compositions as CSVs.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class SquadSaver {

    /**
     * Save a given squad composition as CSV.
     * Each List<Player> corresponds to a squad.
     * @param squadList The list of formed squads.
     */
    public static void saveToCSV(List<List<Player>> squadList) {
        CSVWriter writer = null;
        try {
            File csv = new File("squads.csv");
            csv.createNewFile();
            writer = new CSVWriter(new FileWriter(csv));

            for (int i = 0; i < squadList.size(); ++i) {
                writer.writeNext(new String[]{"Squad " + (i + 1)});
                for (Player player : squadList.get(i)) {
                    writer.writeNext(new String[]{player.getGw2Account(), player.getDiscordName(), player.getAssignedRole()});
                }
                writer.writeNext(new String[0]); // Empty line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}