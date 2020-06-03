package view;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import problem.SquadPlan;
import signups.Commander;
import signups.Player;

import java.util.ArrayList;

/**
 * Wrapper class for the BorderPane holding the App.
 * Contains the list of commanders and trainees being used.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class App extends BorderPane {

    private ArrayList<Player> traineeList, selectedCommanderList;
    private ArrayList<Commander> commanderList;
    private SquadPlan solution;
    private Button home, commanderSelect, solvingScreen, resultScreen;

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

    public SquadPlan getSolution() {
        return solution;
    }

    public void setSolution(SquadPlan solution) {
        this.solution = solution;
        updateMenubar();
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

        HBox menu = new HBox(10);
        menu.setPadding(new Insets(10));
        menu.getChildren().addAll(home, commanderSelect, solvingScreen, resultScreen);

        return menu;
    }

    private void updateMenubar() {
        commanderSelect.setDisable(commanderList == null || traineeList == null);
        solvingScreen.setDisable(selectedCommanderList == null || traineeList == null);
        resultScreen.setDisable(selectedCommanderList == null || traineeList == null || solution == null);
    }

    private void menuBarStyling(Node page, boolean remove) {
        if (page instanceof PlayerListSelect) setBtnStyling(home, remove);
        else if (page instanceof CommanderSelect) setBtnStyling(commanderSelect, remove);
        else if (page instanceof Solving) setBtnStyling(solvingScreen, remove);
        else if (page instanceof Result) setBtnStyling(resultScreen, remove);

    }

    private void setBtnStyling(Button btn, boolean remove) {
        if (remove) btn.setStyle(null);
        else {
            btn.setStyle("-fx-border-color: black; -fx-border-width: 0px 0px 5px 0px;");
        }
    }
}
