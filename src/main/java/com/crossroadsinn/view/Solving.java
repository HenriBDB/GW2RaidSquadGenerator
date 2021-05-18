package com.crossroadsinn.view;

import com.crossroadsinn.components.SquadsTable;
import com.crossroadsinn.settings.Squads;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import jfxtras.styles.jmetro.JMetroStyleClass;
import com.crossroadsinn.problem.SquadPlan;
import com.crossroadsinn.search.GreedyBestFirstSearch;
import com.crossroadsinn.search.SolveSquadPlanTask;

/**
 * Transition Screen that contains a squad generation button
 * and the possibility to interrupt it.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class Solving extends VBox implements AppContent {

    App parent;
    Label msg;
    Button mainBtn, manualBtn;
    Task<SquadPlan> solver;
	SquadsTable squadsTable;

    public Solving(App parent) {
        super(10);
        this.parent = parent;
        getStyleClass().add(JMetroStyleClass.BACKGROUND);
        setAlignment(Pos.CENTER);
		
		squadsTable = new SquadsTable(FXCollections.observableArrayList());
		squadsTable.getStyleClass().add("alternating-row-colors");
		
		StackPane content = new StackPane();
        content.getChildren().addAll(squadsTable);
        content.setPadding(new Insets(10));
		
        mainBtn = new Button();
        mainBtn.setOnAction(e -> {
            toggleSolving();
        });
        manualBtn = new Button("Make Squads Manually");
        msg = new Label();
        getChildren().addAll(content, mainBtn, msg, manualBtn);
        manualBtn.setOnAction(e -> makeSquadsManually());
    }

    /**
     * Initialise the view with data from the parent.
     */
    public void init() {
		if (Squads.getSquads() != null) {
			squadsTable.getItems().clear();
			squadsTable.getItems().addAll(Squads.getSquads());
		}
        if (solver != null) {
            solver.cancel();
            solver = null;
        }
        mainBtn.setText("Generate Squads");
        mainBtn.setDisable(false);
        msg.setText("");
    }

    /**
     * Start or interrupt the search algorithm
     * depending on whether it is started or not.
     */
    public void toggleSolving() {
        if (solver != null) {
            solver.cancel();
            solver = null;
            mainBtn.setText("Retry Generating Squads");
            msg.setText("Squad Generation has been halted.");
        } else {
            msg.setText("Squad Generation is in progress...");
            mainBtn.setText("Interrupt");
            startSolving();
        }
    }
	
    /**
     * Grab Data from Table and updatesquadtypes
     * 
     */
	 
	 public void updateSquadTypes() {
		
	 }
	 
    /**
     * Create a thread for the Search algorithm to run on
     * and start the thread.
     */
    private void startSolving() {
        solver = new SolveSquadPlanTask(parent.getSelectedCommanderList(),
                parent.getSelectedTraineeList(), new GreedyBestFirstSearch(), parent.getMaxSquads());
        solver.setOnSucceeded(t -> displaySquads(solver.getValue()));
        Thread thread = new Thread(solver);
        thread.start();
    }

    /**
     * Once the solution is found, display the solution on a Result screen.
     * If null, display failure message.
     */
    private void displaySquads(SquadPlan solution) {
        if (solution == null) {
            msg.setText("Failed to generate squads.");
            mainBtn.setText("Squad Generation Failed");
            mainBtn.setDisable(true);
        } else {
            parent.cleanupResults();
            parent.setSolution(solution);
            parent.navigateResult();
        }
    }

    private void makeSquadsManually() {
        parent.cleanupResults();
        parent.navigateResult();
    }
}
