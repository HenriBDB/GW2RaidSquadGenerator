package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import problem.SquadComposition;
import problem.SquadPlan;
import search.BestFirstSearchTask;
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

    private static final String AUTO_FILL_TEXT;
    static {
        AUTO_FILL_TEXT = "Auto-fill Trainees";
    }

    ArrayList<Player> players;
    SquadPlan solution;
    List<List<Player>> squads = new ArrayList<>();
    ObservableList<Player> commanders, aides, trainees;
    Button saveBtn, autoFill;
    Label saveMsg;
    BestFirstSearchTask solver;

    public Result(SquadPlan solution) {
        this.solution = solution;
    }

    public Result() {}

    /**
     * Initialise the view with data from the parent.
     */
    public void init() {
        App parent = (App) getParent();
        if (parent.getSelectedTraineeList() != null && parent.getSelectedCommanderList() != null && parent.getSolution() != null) {
            this.solution = parent.getSolution();
            this.players = Stream.of(parent.getSelectedTraineeList(), parent.getSelectedCommanderList())
                    .flatMap(Collection::stream).collect(Collectors.toCollection(ArrayList::new));

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
            squads.add(playerListView.getItems());
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
        for (List<Player> listView : squads) {
            if (!listView.isEmpty()) squadList.add(listView);
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
        autoFill = new Button(AUTO_FILL_TEXT);
        Button reRunSolver = new Button("Find a Different Setup");

        reRunSolver.setOnAction(e -> findNewSetup());
        clearComp.setOnAction(e -> clearSquadComp());
        autoFill.setOnAction(e -> {
            if (solver == null) {
                autoFillTrainees();
                autoFill.setText("Cancel");
            }
            else {
                solver.cancel(); solver = null;
                autoFill.setText(AUTO_FILL_TEXT);
            }
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
        for (List<Player> squad : squads) {
            for (Player player : squad) {
                if (player.getTier().toLowerCase().contains("commander")) commanders.add(player);
                else if (player.getTier().toLowerCase().contains("aide")) aides.add(player);
                else trainees.add(player);
            }
            squad.clear();
        }
    }

    /**
     * Fill trainees into squads automatically.
     */
    private void autoFillTrainees() {
        SquadComposition initialState = new SquadComposition(Stream.of(commanders, aides, trainees)
                .flatMap(Collection::stream).collect(Collectors.toList()), squads);
        solver = new BestFirstSearchTask(initialState);
        solver.setOnSucceeded(t -> {
            setSquads(((SquadComposition) solver.getValue()).getSquads());
            autoFill.setText(AUTO_FILL_TEXT);
            solver = null;
        });
        Thread thread = new Thread(solver);
        thread.start();
    }

    /**
     * Replace the current squad composition with the provided one.
     * @param squadsToSet The squad composition to set.
     */
    private void setSquads(List<List<Player>> squadsToSet) {
        if (squadsToSet != null && squadsToSet.size() == squads.size()) {
            for (int i = 0; i < squads.size(); ++i) {
                squads.get(i).clear();
                squads.get(i).addAll(squadsToSet.get(i));
            }
        }
        squads.stream().flatMap(List::stream).forEach(p -> {
            // The or operators will make java evaluate until the first that return true avoiding unnecessary calls.
            boolean ignore = (trainees.remove(p) || commanders.remove(p) || aides.remove(p));
        });
    }

}
