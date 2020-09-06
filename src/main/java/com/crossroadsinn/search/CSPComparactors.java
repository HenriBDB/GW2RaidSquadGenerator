package com.crossroadsinn.search;

import com.crossroadsinn.problem.CSP;

import java.util.Comparator;

/**
 * Factory methods for creating CSP comparators.
 * Used with search algorithms to define priorities:
 * sort by heuristic, cost, both...
 */
public class CSPComparactors {

    public static SortByHeuristic sortByHeuristicComparator() {
        return new SortByHeuristic();
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