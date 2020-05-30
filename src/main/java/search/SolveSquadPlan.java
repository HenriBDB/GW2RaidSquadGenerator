package search;

import problem.SquadPlan;
import signups.Player;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Solves Squad Generation with any commander list,
 * player list and search algorithm.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class SolveSquadPlan {

    /**
     * Generate Squads given a list of trainees and trainers and a SearchAlgorithm.
     * @param commanders List of commanders to use.
     * @param trainees List of trainees to use.
     * @param searchAlgorithm Search algorithm to use.
     * @return The solution state if any.
     */
    public static SquadPlan solve(ArrayList<Player> commanders, ArrayList<Player> trainees, SearchAlgorithm searchAlgorithm) {

        ArrayList<Integer[]> traineeRoles = IntStream.range(0, trainees.size())
                .mapToObj(i -> new Integer[]{i, trainees.get(i).getRoles()})
                .collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Integer[]> trainerRoles = IntStream.range(0, commanders.size())
                .mapToObj(i -> new Integer[]{i + traineeRoles.size(), commanders.get(i).getRoles()})
                .collect(Collectors.toCollection(ArrayList::new));

        SquadPlan initialSate = new SquadPlan(traineeRoles, trainerRoles);
        int numSquads = initialSate.getNumSquads();
        searchAlgorithm.init(initialSate);
        SquadPlan solution = null;

        while (solution == null && numSquads > 0) {
            System.out.println("Solving for " + numSquads + " squads...");
            long startTime = System.currentTimeMillis();
            solution = (SquadPlan) searchAlgorithm.solve();
            System.out.println("Expanded " + searchAlgorithm.getNodes() + " nodes.");
            long endTime = System.currentTimeMillis();
            if (solution == null) {
                System.out.println("Failed in: " + (endTime-startTime) / 1000.0 + " seconds.");
                --numSquads;
                searchAlgorithm.init(new SquadPlan(traineeRoles, new ArrayList<>(), numSquads));
            }
            else {
                System.out.println("Successful in: " + (endTime-startTime) / 1000.0 + " seconds.");
            }
        }
        return solution;
    }
}
