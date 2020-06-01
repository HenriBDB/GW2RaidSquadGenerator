package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import problem.SquadPlan;
import signups.Player;
import signups.SquadSaver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Result Screen to display a solution state of a SquadPlan.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class Result extends BorderPane implements AppContent{

    ArrayList<Player> players;
    SquadPlan solution;
    List<ListView<Player>> squads = new ArrayList<>();
    ObservableList<Player> commanders, aides, trainees;
    Button saveBtn;
    Label saveMsg;

    public Result(SquadPlan solution) {
        this.solution = solution;
    }

    /**
     * Initialise the view with data from the parent.
     */
    public void init() {
        App parent = (App) getParent();
        if (parent.traineeList != null && parent.selectedCommanderList != null) {
            this.players = Stream.of(parent.traineeList, parent.selectedCommanderList).flatMap(Collection::stream).collect(Collectors.toCollection(ArrayList::new));

            setPadding(new Insets(10));

            VBox content = new VBox(10);
            content.getChildren().addAll(makeAssignedPlayerLists(), new Separator(), makeSquadViews());
            content.setAlignment(Pos.CENTER);
            setCenter(content);

            setRight(controlPanel());

            saveMsg = new Label();
            saveBtn = new Button("Save Squad Composition to CSV");
            saveBtn.setOnAction(e -> saveToCSV());
            HBox bottomPane = new HBox(10);
            bottomPane.setPadding(new Insets(10));
            bottomPane.getChildren().addAll(saveBtn, saveMsg);
            bottomPane.setAlignment(Pos.CENTER);
            setBottom(bottomPane);
        }
    }

    /**
     * Create a list view for every squad formed
     * to allow user to drag and drop players into squads.
     * @return The HBox containing the squad ListViews.
     */
    private HBox makeSquadViews() {
        HBox squadViews = new HBox(10);
        for (int i = 0; i < solution.getNumSquads(); ++i) {
            VBox squad = new VBox(10);
            PlayerListView playerListView = new PlayerListView();
            squads.add(playerListView);
            squad.getChildren().addAll(new Label("Squad " + (i+1)), playerListView);
            squad.setAlignment(Pos.TOP_CENTER);
            squadViews.getChildren().add(squad);
        }
        squadViews.setAlignment(Pos.CENTER);
        return squadViews;
    }

    /**
     * Generate list views containing names of players and commanders
     * assigned and their corresponding assigned role.
     * @return An HBox containing the ListViews.
     */
    private HBox makeAssignedPlayerLists() {
        players = solution.getAssigned().stream().map(p -> {
            players.get(p[0]).setAssignedRole(p[1]);
            return players.get(p[0]);
        }).collect(Collectors.toCollection(ArrayList::new));
        commanders = FXCollections.observableList(players.stream()
                .filter(p -> p.getTier().toLowerCase().contains("commander"))
                .collect(Collectors.toList()));
        aides = FXCollections.observableList(players.stream()
                .filter(p -> p.getTier().toLowerCase().contains("aide"))
                .collect(Collectors.toList()));
        trainees = FXCollections.observableList(players.stream()
                .filter(p -> p.getTier().matches("[0123]") )
                .collect(Collectors.toList()));
        HBox assignedPlayerList = new HBox(10);
        VBox c = new VBox(10); c.getChildren().addAll(new Label("Commanders: "), new PlayerListView(commanders));
        VBox a = new VBox(10); a.getChildren().addAll(new Label("Aides: "), new PlayerListView(aides));
        VBox t = new VBox(10); t.getChildren().addAll(new Label("Trainees: "), new PlayerListView(trainees));
        assignedPlayerList.getChildren().addAll(c, a, t);
        assignedPlayerList.setAlignment(Pos.CENTER);
        return assignedPlayerList;
    }

    /**
     * Save the squad comp formed by the user into CSV.
     */
    private void saveToCSV() {
        saveBtn.setDisable(true);
        saveMsg.setText("Saving to CSV...");
        List<List<Player>> squadList = new ArrayList<>();
        for (ListView<Player> listView : squads) {
            if (!listView.getItems().isEmpty()) squadList.add(listView.getItems());
        }
        if (!squadList.isEmpty()) {
            SquadSaver.saveToCSV(squadList);
            saveMsg.setText("Successfully saved to CSV.");
        }
        else saveMsg.setText("No squad found, did not save to CSV.");
        saveBtn.setDisable(false);
    }

    /**
     * Generates a VBox containing utility buttons.
     * @return the VBox.
     */
    private VBox controlPanel() {
        Button clearComp = new Button("Clear Squad Composition");
        Button autoFill = new Button("Auto-fill Trainees");
        Button reRunSolver = new Button("Find a Different Setup");

        reRunSolver.setOnAction(e -> findNewSetup());
        clearComp.setOnAction(e -> clearSquadComp());
        autoFill.setOnAction(e -> {
            autoFillTrainees(); // Run twice to ensure better results.
            autoFillTrainees();
        });

        VBox panel = new VBox(10);
        panel.getChildren().addAll(clearComp, autoFill, reRunSolver);
        panel.setAlignment(Pos.TOP_CENTER);

        return panel;
    }

    /**
     * Goes back to the solving screen to
     * attempt to find a different setup.
     */
    private void findNewSetup() {
        Solving solvingScreen = new Solving();
        ((App) getParent()).setAndInitCenter(solvingScreen);
        solvingScreen.toggleSolving();
    }

    /**
     * Remove all players from squad compositions
     * and put them back in their starting lists.
     */
    private void clearSquadComp() {
        for (ListView<Player> squad : squads) {
            for (Player player : squad.getItems()) {
                if (player.getTier().toLowerCase().contains("commander")) commanders.add(player);
                else if (player.getTier().toLowerCase().contains("aide")) aides.add(player);
                else trainees.add(player);
            }
            squad.getItems().clear();
        }
    }

    /**
     * Fill trainees into squads automatically.
     */
    private void autoFillTrainees() {
        if (!squads.isEmpty()) {
            Iterator<Player> itr = trainees.iterator();
            while (itr.hasNext()) {
                Player player = itr.next();
                boolean success;
                int i = 0;
                while (!(success = fillPlayer(player, squads.get(i)))) {
                    ++i;
                    if (i == squads.size()) break;
                }
                if (success) itr.remove();
            }
        }
    }

    /**
     * Check if player fits in a squad. If yes, add him and return true.
     * If not return false.
     * @param player The player to add.
     * @param squad The squad to add the player to.
     * @return Whether or not the player could be added to the squad.
     */
    private boolean fillPlayer(Player player, ListView<Player> squad) {
        ObservableList<Player> squadPlayers = squad.getItems();
        switch (player.getAssignedRole()) {
            case "DPS":
                if (squadPlayers.stream().filter(p -> p.getAssignedRole().equals("DPS")).count() >= 5) return false;
                else {
                    squadPlayers.add(player);
                    return true;
                }
            case "Banners":
                if (squadPlayers.stream().anyMatch(p -> p.getAssignedRole().equals("Banners"))) return false;
                else {
                    squadPlayers.add(player);
                    return true;
                }
            case "Offheal":
                if (squadPlayers.stream().anyMatch(p -> p.getAssignedRole().equals("Power Boon Chrono"))) {
                    if (squadPlayers.stream().noneMatch(p -> p.getAssignedRole().equals("Offheal"))) {
                        squadPlayers.add(player);
                        return true;
                    } else return false;
                } else if (!containsSupport(squadPlayers)) {
                    squadPlayers.add(player);
                    return true;
                } else return false;
            case "Heal Renegade":
                if (squadPlayers.stream().anyMatch(p -> p.getAssignedRole().equals("Quickness FB"))) {
                    if (squadPlayers.stream().noneMatch(p -> p.getAssignedRole().equals("Heal Renegade"))) {
                        squadPlayers.add(player);
                        return true;
                    } else return false;
                } else if (!containsSupport(squadPlayers)) {
                    squadPlayers.add(player);
                    return true;
                } else return false;
            case "Heal FB":
                if (squadPlayers.stream().anyMatch(p -> p.getAssignedRole().equals("Alacrigade"))) {
                    if (squadPlayers.stream().noneMatch(p -> p.getAssignedRole().equals("Heal FB"))) {
                        squadPlayers.add(player);
                        return true;
                    } else return false;
                } else if (!containsSupport(squadPlayers)) {
                    squadPlayers.add(player);
                    return true;
                } else return false;
            case "Druid":
                if (squadPlayers.stream().anyMatch(p -> p.getAssignedRole().equals("Druid"))) return false;
                else {
                    squadPlayers.add(player);
                    return true;
                }
            case "Alacrigade":
                if (squadPlayers.stream().anyMatch(p -> p.getAssignedRole().equals("Heal FB"))) {
                    if (squadPlayers.stream().noneMatch(p -> p.getAssignedRole().equals("Alacrigade"))) {
                        squadPlayers.add(player);
                        return true;
                    } else return false;
                } else if (!containsSupport(squadPlayers)) {
                    squadPlayers.add(player);
                    return true;
                } else return false;
            case "Quickness FB":
                if (squadPlayers.stream().anyMatch(p -> p.getAssignedRole().equals("Heal Renegade"))) {
                    if (squadPlayers.stream().noneMatch(p -> p.getAssignedRole().equals("Quickness FB"))) {
                        squadPlayers.add(player);
                        return true;
                    } else return false;
                } else if (!containsSupport(squadPlayers)) {
                    squadPlayers.add(player);
                    return true;
                } else return false;
            case "Power Boon Chrono":
                if (!containsSupport(squadPlayers)) {
                    squadPlayers.add(player);
                    return true;
                } else return false;
            case "Chrono Tank":
                if (squadPlayers.stream().anyMatch(p -> p.getAssignedRole().equals("Chrono Tank"))) return false;
                else {
                    squadPlayers.add(player);
                    return true;
                }
        }
        return false;
    }

    /**
     * Check if a list of players contains any classes from the support group.
     * @param playerList The list of players.
     * @return Whether or not that list contains classes from the support group.
     */
    private boolean containsSupport(List<Player> playerList) {
        return playerList.stream().anyMatch(p -> p.getAssignedRole().equals("Offheal") ||
                p.getAssignedRole().equals("Power Boon Chrono") || p.getAssignedRole().equals("Heal Renegade") ||
                p.getAssignedRole().equals("Heal FB") || p.getAssignedRole().equals("Alacrigade") ||
                p.getAssignedRole().equals("Quickness FB"));
    }

}
