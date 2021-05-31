package com.crossroadsinn.view;

import com.crossroadsinn.problem.SquadPlan;
import com.crossroadsinn.problem.SquadSolution;
import com.crossroadsinn.settings.Settings;
import com.crossroadsinn.signups.Commander;
import com.crossroadsinn.signups.Player;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import jfxtras.styles.jmetro.JMetroStyleClass;

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

    private final ArrayList<String> bossLevels = new ArrayList<>();

    private Button homeButton, playerSelectButton, commanderSelectButton, solvingScreenButton, resultScreenButton, storedSolutionsButton, settingsButton;
    private HBox bossLevelMenu;
    private ComboBox<String> bossLevelChoice;
    private Label numTrainees;
    private Spinner<Integer> numSquads;
    private AppContent homePage, playerListSelect, commanderSelect, solvingUI, savedCompositions, settingsPage;
    private Result resultUI;
    private ArrayList<Player> traineeList, selectedCommanderList, selectedTraineeList;
    private ArrayList<Commander> commanderList;
    private final List<SquadSolution> savedSolutions = new ArrayList<>();
    private SquadPlan solution;

    public App() {
        getStyleClass().add(JMetroStyleClass.BACKGROUND);
        getStylesheets().add(Settings.getAssetFilePath("style/main.css"));
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
		updateBossLevels();
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
        ((Pane) getTop()).getChildren().remove(bossLevelMenu);
        if (content instanceof CommanderSelect || content instanceof Solving) ((Pane) getTop()).getChildren().add(bossLevelMenu);
    }

    /**
     * Refresh the results page.
     */
    public void cleanupResults() {
        if (resultUI != null) resultUI.cleanup();
    }

    /**
     * Create a menu bar with buttons to each of the different pages.
     * @return The menu bar.
     */
    private VBox menuBar() {
        homeButton = new Button("Home");
        playerSelectButton = new Button("Upload Players");
        commanderSelectButton = new Button("Select Commanders");
        solvingScreenButton = new Button("Squad Generation");
        resultScreenButton = new Button("Squad Composition");
        Region region = new Region();
        storedSolutionsButton = new Button("Saved Squad Compositions");
        settingsButton = new Button("Settings");

        homeButton.setOnAction(e -> {
            if (!(getCenter() instanceof HomePage)) navigateHome();
        });
        playerSelectButton.setOnAction(e -> {
            if (!(getCenter() instanceof PlayerListSelect)) navigatePlayerListSelect();
        });
        commanderSelectButton.setOnAction(e -> {
            if (!(getCenter() instanceof CommanderSelect)) navigateCommanderSelect();
        });
        solvingScreenButton.setOnAction(e -> {
            if (!(getCenter() instanceof Solving)) navigateSolving();
        });
        resultScreenButton.setOnAction(e -> {
            if (!(getCenter() instanceof Result)) navigateResult();
        });
        storedSolutionsButton.setOnAction(e -> {
            if (!(getCenter() instanceof SavedCompositions)) navigateSavedCompositions();
        });
        settingsButton.setOnAction(e -> {
            if (!(getCenter() instanceof  SettingsPage)) navigateSettingsPage();
        });

        HBox menu = new HBox();
        menu.getChildren().addAll(homeButton, playerSelectButton, commanderSelectButton,
                solvingScreenButton, resultScreenButton, storedSolutionsButton,
                region, settingsButton);
        menu.getChildren().forEach(btn -> {
            btn.getStyleClass().add("menuButton");
            HBox.setHgrow(btn, Priority.ALWAYS);
            if (btn instanceof Button) ((Button) btn).setDefaultButton(true);
        });

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

        VBox fullMenu = new VBox(10);
        fullMenu.getChildren().addAll(menu, bossLevelMenu);
        fullMenu.setPadding(new Insets(10,10,10,10));

        return fullMenu;
    }

    /**
     * Disable any links to pages that require currently unavailable data.
     */
    private void updateMenubar() {
        commanderSelectButton.setDisable(commanderList == null || traineeList == null);
        solvingScreenButton.setDisable(selectedCommanderList == null || traineeList == null);
        resultScreenButton.setDisable(selectedCommanderList == null || selectedTraineeList == null);
    }

    /**
     * Get available BossLevels from Traineelist
     */
    private void updateBossLevels() {
		bossLevelChoice.getItems().clear();
		bossLevels.clear();
		for (Player x:getTraineeList()) {
			for (String y:x.getBossLvlChoice()) {
				if (!bossLevels.contains(y) && y != null && !y.isEmpty()) bossLevels.add(y);
			}
        }
		java.util.Collections.sort(bossLevels);
		bossLevelChoice.getItems().addAll(bossLevels);
		bossLevelChoice.getSelectionModel().select(0);
    }


    /**
     * Add or remove a bold underline to tabs to indicate current page.
     */
    private void menuBarStyling(Node page, boolean remove) {
        if (page instanceof HomePage) setBtnStyling(homeButton, remove);
        else if (page instanceof PlayerListSelect) setBtnStyling(playerSelectButton, remove);
        else if (page instanceof CommanderSelect) setBtnStyling(commanderSelectButton, remove);
        else if (page instanceof Solving) setBtnStyling(solvingScreenButton, remove);
        else if (page instanceof Result) setBtnStyling(resultScreenButton, remove);
        else if (page instanceof SavedCompositions) setBtnStyling(storedSolutionsButton, remove);
        else if (page instanceof SettingsPage) setBtnStyling(settingsButton, remove);
    }

    private void setBtnStyling(Button btn, boolean remove) {
        if (remove) btn.setId("");
        else {
            btn.setId("currentPage");
        }
    }

    /**
     * Filter out trainees based on their tier and the chosen
     * boss level for the training session.
     */
    public void updateAvailableTrainees() {
        if (traineeList != null) {
			if (bossLevelChoice.getSelectionModel().getSelectedItem() == null) {
				selectedTraineeList = traineeList;
			} else {
				selectedTraineeList = traineeList.stream().filter(p -> {
					if (bossLevelChoice.getSelectionModel().getSelectedItem().contains("Intermediate")) { // Intermediate
						return p.getTier().matches("^([123]|(trainee))$") && p.getBossLvlChoiceAsString().contains(bossLevelChoice.getSelectionModel().getSelectedItem());
					} else if (bossLevelChoice.getSelectionModel().getSelectedItem().contains("Advanced")) {  // Advanced
						return p.getTier().matches("^([23]|(trainee))$") && p.getBossLvlChoiceAsString().contains(bossLevelChoice.getSelectionModel().getSelectedItem());
					} else return p.getBossLvlChoiceAsString().contains(bossLevelChoice.getSelectionModel().getSelectedItem()); // Beginner
				}).collect(Collectors.toCollection(ArrayList::new));
			}
            numTrainees.setText(String.format("%d valid trainees.", selectedTraineeList.size()));
        }
    }

    /**
     * Navigate to the different pages on this app.
     * If a given page has not been created yet, create new.
     */
    public void navigateHome() {
        if (homePage == null) homePage = new HomePage(this);
        setAndInitCenter(homePage);
    }

    public void navigatePlayerListSelect() {
        if (playerListSelect == null) playerListSelect = new PlayerListSelect(this);
        setAndInitCenter(playerListSelect);
    }

    public void navigateCommanderSelect() {
        if (commanderSelect == null) commanderSelect = new CommanderSelect(this);
        setAndInitCenter(commanderSelect);
    }

    public void navigateSolving() {
        if (solvingUI == null) solvingUI = new Solving(this);
        setAndInitCenter(solvingUI);
    }

    public void navigateResult() {
        if (resultUI == null) resultUI = new Result(this);
        setAndInitCenter(resultUI);
    }

    public void navigateSavedCompositions() {
        if (savedCompositions == null) savedCompositions = new SavedCompositions(this);
        setAndInitCenter(savedCompositions);
    }

    public void navigateSettingsPage() {
        if (settingsPage == null) settingsPage = new SettingsPage();
        setAndInitCenter(settingsPage);
    }

    public AppContent getSolvingUI() {
        return solvingUI;
    }
}
