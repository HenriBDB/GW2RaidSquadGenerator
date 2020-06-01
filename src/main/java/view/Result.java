package view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import problem.SquadPlan;
import signups.Player;
import signups.SquadSaver;

import java.util.ArrayList;
import java.util.Collection;
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
        PlayerListView commanders = new PlayerListView(FXCollections.observableList(players.stream()
                .filter(p -> p.getTier().toLowerCase().contains("commander"))
                .collect(Collectors.toList())));
        PlayerListView aides = new PlayerListView(FXCollections.observableList(players.stream()
                .filter(p -> p.getTier().toLowerCase().contains("aide"))
                .collect(Collectors.toList())));
        PlayerListView trainees = new PlayerListView(FXCollections.observableList(players.stream()
                .filter(p -> p.getTier().matches("[0123]") )
                .collect(Collectors.toList())));
        HBox assignedPlayerList = new HBox(10);
        VBox c = new VBox(10); c.getChildren().addAll(new Label("Commanders: "), commanders);
        VBox a = new VBox(10); a.getChildren().addAll(new Label("Aides: "), aides);
        VBox t = new VBox(10); t.getChildren().addAll(new Label("Trainees: "), trainees);
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
}
