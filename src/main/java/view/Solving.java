package view;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import problem.SquadPlan;
import search.GreedyBestFirstSearch;
import search.SolveSquadPlanTask;
import signups.Player;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Transition Screen that contains a squad generation button
 * and the possibility to interrupt it.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class Solving extends VBox implements AppContent{

    Label msg = new Label();
    Button mainBtn;
    Task<SquadPlan> solver;

    public Solving() {
        super(10);
    }

    /**
     * Initialise the view with data from the parent.
     */
    public void init() {
        App parent = (App) getParent();
        if (parent.getSelectedCommanderList() != null && parent.getTraineeList() != null) {

            setAlignment(Pos.CENTER);
            mainBtn = new Button("Generate Squads");
            mainBtn.setOnAction(e -> toggleSolving());
            getChildren().addAll(mainBtn, msg);
        }
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
     * Create a thread for the Search algorithm to run on
     * and start the thread.
     */
    private void startSolving() {
        App parent = (App) getParent();
        solver = new SolveSquadPlanTask(parent.getSelectedCommanderList(),
                parent.getTraineeList(), new GreedyBestFirstSearch());
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
            App parent = (App) getParent();
            parent.setSolution(solution);
            parent.setAndInitCenter(new Result());
        }
    }
}
