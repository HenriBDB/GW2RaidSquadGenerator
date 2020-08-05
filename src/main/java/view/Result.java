package view;

import Components.PlayerListView;
import Components.RoleStatRow;
import Components.RolesStatTable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import problem.SquadComposition;
import problem.SquadPlan;
import problem.SquadSolution;
import search.BestFirstSearchTask;
import signups.Player;
import signups.SquadSaver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
    ObservableList<Player> commandersAndAides, trainees;
    HBox squadViews;
    VBox statsView;
    Button saveBtn, autoFill;
    Label saveMsg;
    BestFirstSearchTask solver;
    TextField compName;
    HashMap<String, RoleStatRow> stats = new HashMap<>();

    public Result() {}

    /**
     * Initialise the view with data from the parent.
     */
    public void init() {
        App parent = (App) getParent();
        if (parent.getSelectedTraineeList() != null && parent.getSelectedCommanderList() != null) {
            this.solution = parent.getSolution();

            players = Stream.of(parent.getSelectedTraineeList(), parent.getSelectedCommanderList())
                    .flatMap(Collection::stream).collect(Collectors.toCollection(ArrayList::new));
            if (solution != null) {
                players = solution.getAssigned().stream().map(p -> {
                    players.get(p[0]).setAssignedRole(p[1]);
                    return players.get(p[0]);
                }).collect(Collectors.toCollection(ArrayList::new));
            }

            generateStats();
            setPadding(new Insets(10));

            VBox content = new VBox(10);
            content.getChildren().addAll(makeAssignedPlayerLists(), new Separator(), makeSquadViews());
            content.setAlignment(Pos.CENTER);
            setCenter(content);

            setRight(controlPanel());

            compName = new TextField(parent.getBossLevelChoice());
            saveMsg = new Label();
            saveBtn = new Button("Save this Composition\nand Create New");
            saveBtn.setOnAction(e -> createNewComp());
            HBox bottomPane = new HBox(10);
            bottomPane.setPadding(new Insets(10));
            bottomPane.getChildren().addAll(compName, saveBtn, saveMsg);
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
        squadViews = new HBox(10);
        if (solution != null) {
            for (int i = 0; i < solution.getNumSquads(); ++i) {
                VBox squad = makeSquadDisplay(i);
                squadViews.getChildren().add(squad);
            }
        } else {
            // Make just one sample squad view.
            squadViews.getChildren().add(makeSquadDisplay(0));
        }
        squadViews.setAlignment(Pos.CENTER);
        return squadViews;
    }

    private VBox makeSquadDisplay(int squadIndex) {
        VBox squad = new VBox(10);
        PlayerListView playerListView = new PlayerListView();
        squads.add(playerListView.getItems());
        squad.getChildren().addAll(new Label("Squad " + (squadIndex+1)), playerListView);
        squad.setAlignment(Pos.TOP_CENTER);
        HBox.setHgrow(squad, Priority.ALWAYS);
        return squad;
    }

    /**
     * Generate list views containing names of players and commanders
     * assigned and their corresponding assigned role.
     * @return An HBox containing the ListViews.
     */
    private HBox makeAssignedPlayerLists() {
        commandersAndAides = FXCollections.observableList(players.stream()
                .filter(p -> p.getTier().toLowerCase().contains("commander")
                || p.getTier().toLowerCase().contains("aide"))
                .collect(Collectors.toList()));
        trainees = FXCollections.observableList(players.stream()
                .filter(p -> p.getTier().matches("[0123]") )
                .collect(Collectors.toList()));
        HBox assignedPlayerList = new HBox(10);
        statsView = new VBox(10);
        statsView.getChildren().add(new RolesStatTable(FXCollections.observableArrayList(stats.values())));
        VBox c = new VBox(10); c.getChildren().addAll(new Label("Commanders and Aides: "), new PlayerListView(commandersAndAides));
        VBox t = new VBox(10); t.getChildren().addAll(new Label("Trainees: "), new PlayerListView(trainees));
        HBox.setHgrow(c, Priority.ALWAYS);
        HBox.setHgrow(t, Priority.ALWAYS);
        assignedPlayerList.getChildren().addAll(statsView, c, t);
        assignedPlayerList.setAlignment(Pos.CENTER);
        return assignedPlayerList;
    }

    /**
     * Save the squad comp formed by the user into CSV.
     */
    private void saveToCSV() {
        if (!squads.isEmpty()) {
            SquadSaver.saveToCSV(squads,
                    Stream.of(getLeftovers(), trainees).flatMap(Collection::stream).collect(Collectors.toList()));
            saveMsg.setText("Successfully saved to CSV.");
        }
    }

    /**
     * Generate left and assigned stats.
     */
    private void generateStats() {
        for (String role : Player.ROLES) stats.put(role, new RoleStatRow(role));
        for (Player player : players) {
            if (player.getAssignedRole() == null) {
                updateStatsClearedPlayer(player, null);
            } else updateStatsAssignedPlayer(player, false);

            player.assignedRoleProperty().addListener((e, oldVal, newVal) -> {
                if (newVal == null) updateStatsClearedPlayer(player, oldVal);
                else updateStatsAssignedPlayer(player, true);
            });
        }
    }

    private void updateStatsClearedPlayer(Player player, String oldValue) {
        for (String availableRole : player.getSimpleRoleList()) {
            stats.get(availableRole).incrementLeft();
        }
        if (oldValue != null) stats.get(oldValue).decrementAssigned();
    }

    private void updateStatsAssignedPlayer(Player player, boolean wasUnassigned) {
        stats.get(player.getAssignedRole()).incrementAssigned();
        if (wasUnassigned) {
            for (String availableRole : player.getSimpleRoleList()) {
                stats.get(availableRole).decrementLeft();
            }
        }
    }

    /**
     * Generates a VBox containing utility buttons.
     * @return the VBox.
     */
    private VBox controlPanel() {
        Button clearComp = new Button("Clear Squad Composition");
        autoFill = new Button(AUTO_FILL_TEXT);
        Button reRunSolver = new Button("Find a Different Setup");
        Button saveToCSVBtn = new Button("Save Squad Composition to CSV");
        Button sortSquads = new Button("Sort squads the Kez way");
        Button removeSolution = new Button("Delete this Solution");

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
        saveToCSVBtn.setOnAction(e -> saveToCSV());
        sortSquads.setOnAction(e -> sortPlayerOrder());
        removeSolution.setOnAction(e -> {
            App parent = (App) getParent();
            parent.setSolution(null);
            for (Player player : players) player.setAssignedRole(null);
            parent.setAndInitCenter(new Solving());
        });

        HBox squadsControl = new HBox(10);
        squadsControl.setAlignment(Pos.CENTER);
        Button addSquad = new Button("+");
        Button removeSquad = new Button("-");
        addSquad.setOnAction(e -> {
            int numSquads = squadViews.getChildren().size();
            squadViews.getChildren().add(makeSquadDisplay(numSquads));
            removeSquad.setDisable(false);
        });
        removeSquad.setOnAction(e -> {
            int numSquads = squadViews.getChildren().size();
            if (numSquads == 0) return;
            squads.remove(numSquads - 1);
            squadViews.getChildren().remove(numSquads - 1);
            if (numSquads == 1) removeSquad.setDisable(true);
        });
        squadsControl.getChildren().addAll(removeSquad, addSquad);

        VBox panel = new VBox(10);
        panel.getChildren().addAll(clearComp, autoFill, reRunSolver, saveToCSVBtn, sortSquads, removeSolution, new Region(), new Label("Squads:"), squadsControl);
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setPadding(new Insets(0,0,0,10));

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
                if (player.getTier().toLowerCase().contains("commander")
                        || player.getTier().toLowerCase().contains("aide")) commandersAndAides.add(player);
                else trainees.add(player);
            }
            squad.clear();
        }
    }

    private void sortPlayerOrder() {
        squads.forEach(PlayerListView::sortPlayerList);
    }

    /**
     * Fill trainees into squads automatically.
     */
    private void autoFillTrainees() {
        SquadComposition initialState = new SquadComposition(Stream.of(commandersAndAides, trainees)
                .flatMap(Collection::stream).collect(Collectors.toList()), squads);
        solver = new BestFirstSearchTask(initialState);
        solver.setOnSucceeded(t -> {
            if (solver.getValue() == null) setSquads(null);
            else setSquads(((SquadComposition) solver.getValue()).getSquads());
            autoFill.setText(AUTO_FILL_TEXT);
            solver = null;
        });
        Thread thread = new Thread(solver);
        thread.start();
    }

    /**
     * Save this composition in the program.
     * Remove any used players and trainees from the lists.
     * Redirect to the page displaying the saved composition.
     */
    private void createNewComp() {
        if (compName.getText().isBlank()) {
            saveMsg.setText("Please type in a name for the composition.");
            return;
        }

        SquadSolution thisSolution = new SquadSolution(squads, compName.getText());
        App parent = (App) getParent();

        parent.setTraineeList(getLeftovers());
        parent.setSelectedTraineeList(null);

        List<Player> commandersUsed = new ArrayList<>(parent.getSelectedCommanderList());
        commandersUsed.removeAll(commandersAndAides);
        List<String> commandersUsedNames = commandersUsed.stream().map(Player::getName).collect(Collectors.toList());
        parent.setCommanderList(parent.getCommanderList()
                .stream().filter(c -> !(commandersUsedNames.contains(c.getName())))
                .collect(Collectors.toCollection(ArrayList::new)));
        parent.setSelectedCommanderList(null);
        parent.setSolution(null);

        parent.storeSolution(thisSolution);
        parent.setAndInitCenter(new SavedCompositions());
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
            // The or operators will make java evaluate until the first that returns true, avoiding unnecessary calls.
            boolean ignore = (trainees.remove(p) || commandersAndAides.remove(p));
        });
    }

    /**
     * Return all players from the trainee list that are
     * not used in the current composition.
     * @return List of all left over players.
     */
    private ArrayList<Player> getLeftovers() {
        App parent = (App) getParent();
        ArrayList<Player> players = new ArrayList<>(parent.getTraineeList());
        for (List<Player> chosenOnes : squads) {
            players.removeAll(chosenOnes);
        }
        return players;
    }

}
