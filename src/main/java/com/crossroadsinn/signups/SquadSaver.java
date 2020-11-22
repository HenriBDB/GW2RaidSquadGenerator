package com.crossroadsinn.signups;

import com.crossroadsinn.Main;
import com.opencsv.CSVWriter;
import com.crossroadsinn.problem.SquadSolution;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
    public static boolean saveToCSV(List<List<Player>> squadList, List<Player> leftOvers) {
        CSVWriter writer = null;
        String chosenDir = chooseDir();
        if (chosenDir == null) return false;
        try {
            File csv = new File(chosenDir);
            csv.createNewFile();
            writer = new CSVWriter(new FileWriter(csv));

            writeSquad("", squadList, writer);
            writeLeftOvers(leftOvers, writer);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (writer != null) writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Save multiple squad compositions as CSV.
     * @param squadComps The list of formed squads.
     */
    public static boolean saveCompsToCSV(List<SquadSolution> squadComps, List<Player> leftOvers) {
        CSVWriter writer = null;
        String chosenDir = chooseDir();
        if (chosenDir == null) return false;
        try {
            File csv = new File(chosenDir);
            csv.createNewFile();
            writer = new CSVWriter(new FileWriter(csv));

            for (SquadSolution squadComp : squadComps) {
                writeSquad(squadComp.getName(), squadComp.getSquads(), writer);
            }

            writeLeftOvers(leftOvers, writer);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (writer != null) writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean exportToCSV(List<SquadSolution> squadComps, List<Player> leftOvers, String day) {
        CSVWriter writer = null;
        String chosenDir = chooseDir();
        if (chosenDir == null) return false;
        try {
            File csv = new File(chosenDir);
            csv.createNewFile();
            writer = new CSVWriter(new FileWriter(csv));

            writer.writeNext(new String[]{"Player name", "Discord name", "Day", "Squad", "Squad Type", "Assigned Role", "Tier", "Roles"});

            for (SquadSolution squadComp : squadComps) {
                writeSquad(squadComp.getName(), squadComp.getSquads(), day, writer);
            }

            for (Player player : leftOvers) {
                writer.writeNext(playerLine(player, day, "", ""));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (writer != null) writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Write all provided squads to the CSV.
     * @param compName The name of the composition.
     * @param squadList The squads in the composition.
     * @param writer The CSV writer object.
     */
    private static void writeSquad(String compName, List<List<Player>> squadList, CSVWriter writer) {
        writer.writeNext(new String[]{compName});
        writer.writeNext(new String[0]);
        for (int i = 0; i < squadList.size(); ++i) {
            writer.writeNext(new String[]{"Squad " + (i + 1)});
            for (Player player : squadList.get(i)) {
                writer.writeNext(new String[]{player.getGw2Account(), player.getDiscordName(),
                        player.getAssignedRole(), player.getTier()});
            }
            writer.writeNext(new String[0]); // Empty line;
        }
    }

    /**
     * Write all provided squads to the CSV.
     * @param compName The name of the composition.
     * @param squadList The squads in the composition.
     * @param writer The CSV writer object.
     * @param day The day of the training.
     */
    private static void writeSquad(String compName, List<List<Player>> squadList, String day, CSVWriter writer) {
        for (int i = 0; i < squadList.size(); ++i) {
            for (Player player : squadList.get(i)) {
                writer.writeNext(playerLine(player, day, i+1+"", compName));
            }
        }
    }

    private static String[] playerLine(Player player, String day, String squad, String squadType) {
        boolean isComm = player.getTier().toLowerCase().equals("commander") || player.getTier().toLowerCase().equals("aide");
        String[] roles = player.getRoleList();
        ArrayList<String> line = new ArrayList<>();
        line.add(isComm ? player.getTier() : player.getGw2Account());
        line.add(player.getDiscordName().isEmpty() ? player.getGw2Account() : player.getDiscordName());
        line.add(day);
        line.add(squad);
        line.add(squadType);
        line.add(player.getAssignedRole() != null ? player.getAssignedRole() : "");
        line.add(isComm ? "-" : player.getTier());
        line.addAll(Arrays.asList(roles));
        return line.toArray(new String[0]);
    }

    /**
     * Write in CSV list of all unused sign-ups.
     * @param leftOvers The list of players that were not picked in a squad.
     * @param writer The CSV writer object.
     */
    private static void writeLeftOvers(List<Player> leftOvers, CSVWriter writer) {
        writer.writeNext(new String[]{"Left Overs: "});
        for (Player player : leftOvers) {
            ArrayList<String> line = new ArrayList<>(Arrays.asList(player.getGw2Account(), player.getDiscordName(),
                    player.getTier(), Integer.toBinaryString(player.getBossLvlChoice())));
            line.addAll(Arrays.asList(player.getRoleList()));
            writer.writeNext(line.toArray(new String[0]));
        }
    }

    /**
     * Allow user to select preferred location for saving squads.
     * @return User chosen location
     */
    private static String chooseDir() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("squad-compositions.csv");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Where should I save squad compositions?");
        File userChosenFile = fileChooser.showSaveDialog(Main.getPrimaryStage());
        if (userChosenFile == null) return null;
        else {
            String filePath = userChosenFile.getAbsolutePath();
            if (!filePath.endsWith(".csv")) filePath += ".csv";
            return filePath;
        }
    }
}