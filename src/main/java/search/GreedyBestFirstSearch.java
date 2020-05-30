package search;

import problem.CSP;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * A GreedBestFirstSearch implementation for a CSP.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class GreedyBestFirstSearch implements SearchAlgorithm{
    PriorityQueue<CSP> Q;
    int nodes;

    public GreedyBestFirstSearch() {
    }

    public GreedyBestFirstSearch(CSP initialState) {
        init(initialState);
    }

    /**
     * Reset the search object with a new initial state.
     * @param initialState the new initial state.
     */
    public void init(CSP initialState) {
        Q = new PriorityQueue<>(50, new SortByHeuristic());
        nodes = 0;
        if ( Q.isEmpty() ) Q.add(initialState);
    }

    /**
     * Look for solution by prioritizing lowest heuristics.
     * @return the solution if found, null otherwise.
     */
    public CSP solve() {
        while (!Q.isEmpty()) {
            if (Q.peek().isSolution()) {
                return Q.peek();
            }
            ++nodes;
            assert Q.peek() != null;
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

/**
 * Comparator that sorts CSPs by their heuristic from lowest to highest.
 */
class SortByHeuristic implements Comparator<CSP> {
    // Used for sorting in ascending order of heuristic.
    public int compare(CSP a, CSP b)
    {
        return a.heuristic() - b.heuristic();
    }
}