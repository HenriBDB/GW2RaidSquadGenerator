package com.crossroadsinn.search;

import com.crossroadsinn.problem.CSP;
import javafx.concurrent.Task;

import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Task implementation of the GreedyBestFirstSearch algorithm.
 * Allows for concurrency when running the solver.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class BestFirstSearchTask extends Task<CSP> implements SearchAlgorithm{
    PriorityQueue<CSP> Q;
    Set<CSP> memory; // Should be limited to 200,000 elements
    int nodes;
    int maxDurationInMillis = 60*1000;

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
        memory = new HashSet<>();
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
        long startTime = System.currentTimeMillis();
        while (!Q.isEmpty()) {
            if (isCancelled()) return null;
            if (System.currentTimeMillis() > startTime + maxDurationInMillis) return null;
            if (Q.peek().isSolution()) {
                return Q.peek();
            }
            ++nodes;
            List<CSP> expandedNodes = Q.poll().getChildren();
            addStatesToQueue(expandedNodes);
        }
        // No solution found.
        return null;
    }

    /**
     * Method where successor states are added to the search queue.
     * Additional pruning can be done here like checking if states have already been encountered.
     * @param statesToAdd
     */
    private void addStatesToQueue(List<CSP> statesToAdd) {
        List<CSP> prunedList = statesToAdd.stream().filter(e -> !memory.contains(e)).collect(Collectors.toList());
        Q.addAll(prunedList);
        if (memory.size() < 150000) memory.addAll(prunedList);
    }

    public int getNodes() {
        return nodes;
    }
}
