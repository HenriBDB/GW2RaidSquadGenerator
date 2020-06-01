package view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import problem.SquadPlan;
import signups.Player;

import java.util.ArrayList;
import java.util.Collection;
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
        }
    }

    private HBox makeSquadViews() {
        HBox squads = new HBox(10);
        for (int i = 0; i < solution.getNumSquads(); ++i) {
            VBox squad = new VBox(10);
            squad.getChildren().addAll(new Label("Squad " + (i+1)), new PlayerListView());
            squad.setAlignment(Pos.TOP_CENTER);
            squads.getChildren().add(squad);
        }
        squads.setAlignment(Pos.CENTER);
        return squads;
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
}
