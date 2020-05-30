package search;

import problem.CSP;

public interface SearchAlgorithm {

    // Solve the CSP problem.
    public CSP solve();

    // Reset search object and set initial state.
    public void init(CSP initialState);

    // Number of expanded nodes.
    public int getNodes();
}
