package view;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import problem.SquadPlan;
import problem.SquadSolution;
import signups.Commander;
import signups.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Wrapper class for the BorderPane holding the App.
 * Contains the list of commanders and trainees being used.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class App extends BorderPane {

    private final static String[] bossLevels = {"Beginner", "Intermediate", "Advanced"};

    private ArrayList<Player> traineeList, selectedCommanderList, selectedTraineeList;
    private ArrayList<Commander> commanderList;
    private final List<SquadSolution> savedSolutions = new ArrayList<>();
    private SquadPlan solution;
    private Button home, commanderSelect, solvingScreen, resultScreen, storedSolutions;
    private HBox bossLevelMenu;
    private ComboBox<String> bossLevelChoice;
    private Label numTrainees;
    Spinner<Integer> numSquads;

    public App() {
        setTop(menuBar());
        updateMenubar();
    }

    public ArrayList<Commander> getCommanderList() {
        return commanderList;
    }

    public void setCommanderList(ArrayList<Commander> commanderList) {
        this.commanderList = commanderList;
        updateMenubar();
    }

    public ArrayList<Player> getSelectedCommanderList() {
        return selectedCommanderList;
    }

    public void setSelectedCommanderList(ArrayList<Player> selectedCommanderList) {
        this.selectedCommanderList = selectedCommanderList;
        updateMenubar();
    }

    public ArrayList<Player> getTraineeList() {
        return traineeList;
    }

    public void setTraineeList(ArrayList<Player> traineeList) {
        this.traineeList = traineeList;
        updateAvailableTrainees();
        updateMenubar();
    }

    public ArrayList<Player> getSelectedTraineeList() {
        return selectedTraineeList;
    }

    public void setSelectedTraineeList(ArrayList<Player> selectedTraineeList) {
        this.selectedTraineeList = selectedTraineeList;
    }

    public SquadPlan getSolution() {
        return solution;
    }

    public void setSolution(SquadPlan solution) {
        this.solution = solution;
        updateMenubar();
    }

    public List<SquadSolution> getSavedSolutions() {
        return savedSolutions;
    }

    public void storeSolution(SquadSolution savedSolution) {
        this.savedSolutions.add(savedSolution);
    }

    public String getBossLevelChoice() {
        return bossLevelChoice.getValue();
    }

    public Integer getMaxSquads() {
        return numSquads.getValue();
    }

    public void setAndInitCenter(AppContent content) {
        menuBarStyling(getCenter(), true);
        setCenter(((Node) content));
        content.init();
        updateMenubar();
        menuBarStyling(getCenter(), false);
        bossLevelMenu.setVisible(content instanceof CommanderSelect || content instanceof Solving);
    }

    /**
     * Create a menu bar with buttons to each of the different pages.
     * @return The menu bar.
     */
    private VBox menuBar() {
        home = new Button("Home");
        commanderSelect = new Button("Select Commanders");
        solvingScreen = new Button("Squad Generation");
        resultScreen = new Button("Squad Composition");
        storedSolutions = new Button("Saved Squad Compositions");

        home.setOnAction(e -> {
            if (!(getCenter() instanceof PlayerListSelect)) setAndInitCenter(new PlayerListSelect());
        });
        commanderSelect.setOnAction(e -> {
            if (!(getCenter() instanceof CommanderSelect)) setAndInitCenter(new CommanderSelect());
        });
        solvingScreen.setOnAction(e -> {
            if (!(getCenter() instanceof Solving)) setAndInitCenter(new Solving());
        });
        resultScreen.setOnAction(e -> {
            if (!(getCenter() instanceof Result)) setAndInitCenter(new Result());
        });
        storedSolutions.setOnAction(e -> {
            if (!(getCenter() instanceof SavedCompositions)) setAndInitCenter(new SavedCompositions());
        });

        HBox menu = new HBox(10);
        menu.setPadding(new Insets(10));
        menu.getChildren().addAll(home, commanderSelect, solvingScreen, resultScreen, storedSolutions);

        bossLevelMenu = new HBox(10);
        bossLevelChoice = new ComboBox<>();
        bossLevelChoice.getItems().addAll(bossLevels);
        bossLevelChoice.getSelectionModel().select(0);
        bossLevelChoice.setOnAction(e -> updateAvailableTrainees());
        Label bossLevelMsg = new Label("Training Boss Level: ");
        numTrainees = new Label();
        Label numSquadsMsg = new Label("Max squads: (0 = no restrictions) ");
        numSquads = new Spinner<>(0, 50, 0);
        numSquads.setMaxWidth(75);
        bossLevelMenu.getChildren().addAll(bossLevelMsg, bossLevelChoice, numTrainees,
                new Separator(Orientation.VERTICAL), numSquadsMsg, numSquads);
        bossLevelMenu.setAlignment(Pos.CENTER);
        bossLevelMenu.setPadding(new Insets(0,10,10,10));

        VBox fullMenu = new VBox(0);
        fullMenu.getChildren().addAll(menu, bossLevelMenu);

        return fullMenu;
    }

    /**
     * Disable any links to pages that require currently unavailable data.
     */
    private void updateMenubar() {
        commanderSelect.setDisable(commanderList == null || traineeList == null);
        solvingScreen.setDisable(selectedCommanderList == null || traineeList == null);
        resultScreen.setDisable(selectedCommanderList == null || traineeList == null || solution == null);
    }

    /**
     * Add or remove a bold underline to tabs to indicate current page.
     */
    private void menuBarStyling(Node page, boolean remove) {
        if (page instanceof PlayerListSelect) setBtnStyling(home, remove);
        else if (page instanceof CommanderSelect) setBtnStyling(commanderSelect, remove);
        else if (page instanceof Solving) setBtnStyling(solvingScreen, remove);
        else if (page instanceof Result) setBtnStyling(resultScreen, remove);
        else if (page instanceof SavedCompositions) setBtnStyling(storedSolutions, remove);
    }

    private void setBtnStyling(Button btn, boolean remove) {
        if (remove) btn.setStyle(null);
        else {
            btn.setStyle("-fx-border-color: black; -fx-border-width: 0px 0px 5px 0px;");
        }
    }

    /**
     * Filter out trainees based on their tier and the chosen
     * boss level for the training session.
     */
    private void updateAvailableTrainees() {
        if (traineeList != null) {
            selectedTraineeList = traineeList.stream().filter(p -> {
                if (bossLevelChoice.getSelectionModel().getSelectedItem().equals(bossLevels[1])) {
                    return p.getTier().matches("[123]") && (p.getBossLvlChoice() & 2) != 0; // Intermediate
                } else if (bossLevelChoice.getSelectionModel().getSelectedItem().equals(bossLevels[2])) {
                    return p.getTier().matches("[23]") && (p.getBossLvlChoice() & 4) != 0; // Advanced
                } else return (p.getBossLvlChoice() & 1) != 0; // Beginner
            }).collect(Collectors.toCollection(ArrayList::new));
            numTrainees.setText(String.format("%d valid trainees.", selectedTraineeList.size()));
        }
    }
}
