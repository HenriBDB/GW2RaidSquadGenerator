package view;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import problem.SquadPlan;
import problem.SquadSolution;
import signups.Commander;
import signups.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper class for the BorderPane holding the App.
 * Contains the list of commanders and trainees being used.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class App extends BorderPane {

    private ArrayList<Player> traineeList, selectedCommanderList, selectedTraineeList;
    private ArrayList<Commander> commanderList;
    private final List<SquadSolution> savedSolutions = new ArrayList<>();
    private SquadPlan solution;
    private Button home, commanderSelect, solvingScreen, resultScreen, storedSolutions;

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

    public void setAndInitCenter(AppContent content) {
        menuBarStyling(getCenter(), true);
        setCenter(((Node) content));
        content.init();
        updateMenubar();
        menuBarStyling(getCenter(), false);
    }

    /**
     * Create a menu bar with buttons to each of the different pages.
     * @return The menu bar.
     */
    private HBox menuBar() {
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

        return menu;
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
}
