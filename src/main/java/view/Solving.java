package view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import problem.SquadPlan;
import search.GreedyBestFirstSearch;
import search.SolveSquadPlan;

import java.util.concurrent.Semaphore;

/**
 * Transition Screen that contains a squad generation button
 * and the possibility to interrupt it.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class Solving extends VBox implements AppContent{

    Label msg = new Label();
    Button mainBtn;
    Thread solver;
    SquadPlan solution;

    public Solving() {
        super(10);
    }

    /**
     * Initialise the view with data from the parent.
     */
    public void init() {
        App parent = (App) getParent();
        if (parent.selectedCommanderList != null && parent.traineeList != null) {
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
    private void toggleSolving() {
        if (solver != null) {
            solver.interrupt();
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
        Semaphore solutionFound = new Semaphore(0);
        App parent = (App) getParent();
        solver = new Thread(() -> {
            solution = SolveSquadPlan.solve(parent.selectedCommanderList, parent.traineeList, new GreedyBestFirstSearch());

            if (solution == null) {
                msg.setText("Failed to generate squads.");
                mainBtn.setText("Squad Generation Failed");
                mainBtn.setDisable(true);
            }
            else solutionFound.release();
        });
        solver.start();
        displaySquads(solutionFound);
    }

    /**
     * Once the solution is found, display the solution on a Result screen.
     * @param available the Semaphore released by the thread generating squads.
     */
    private void displaySquads(Semaphore available) {
        while (solution == null) {
            try {
                // Will Block thread until solution is available.
                available.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ((App) getParent()).setAndInitCenter(new Result(solution));
    }
}
