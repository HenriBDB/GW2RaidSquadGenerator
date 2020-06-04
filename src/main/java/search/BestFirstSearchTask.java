package search;

import javafx.concurrent.Task;
import problem.CSP;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Task implementation of the GreedyBestFirstSearch algorithm.
 * Allows for concurrency when running the solver.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class BestFirstSearchTask extends Task<CSP> implements SearchAlgorithm{
    PriorityQueue<CSP> Q;
    int nodes;

    public BestFirstSearchTask(CSP initialState) {
        init(initialState);
    }

    /**
     * Reset the search object with a new initial state.
     * @param initialState the new initial state.
     */
    public void init(CSP initialState) {
        Q = new PriorityQueue<>(50, CSPComparactors.sortByHeuristicComparator());
        nodes = 0;
        if ( Q.isEmpty() ) Q.add(initialState);
    }

    @Override
    protected CSP call() {
        return solve();
    }

    /**
     * Look for solution by prioritizing lowest heuristics.
     * @return the solution if found, null otherwise.
     */
    public CSP solve() {
        while (!Q.isEmpty()) {
            if (isCancelled()) return null;
            if (Q.peek().isSolution()) {
                return Q.peek();
            }
            ++nodes;
            ArrayList<CSP> expandedNodes = Q.poll().getChildren();
            Q.addAll(expandedNodes);
        }
        // No solution found.
        return null;
    }

    public int getNodes() {
        return nodes;
    }
}
