package com.crossroadsinn.search;

import javafx.concurrent.Task;
import com.crossroadsinn.problem.SquadPlan;
import com.crossroadsinn.signups.Player;
import com.crossroadsinn.settings.Squads;
import com.crossroadsinn.settings.Squad;


import java.util.*;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Solves Squad Generation with any commander list,
 * player list and search algorithm.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class SolveSquadPlanTask extends Task<SquadPlan> {

    private final ArrayList<Player> commanders, trainees;
    private final SearchAlgorithm searchAlgorithm;
    private int maxSquads;
	private Hashtable<String, Integer> squadTypeAllowed = new Hashtable<String, Integer>();

    public SolveSquadPlanTask(ArrayList<Player> commanders, ArrayList<Player> trainees, SearchAlgorithm searchAlgorithm) {
        this.commanders = commanders;
        this.trainees = trainees;
        this.searchAlgorithm = searchAlgorithm;
    }

    public SolveSquadPlanTask(ArrayList<Player> commanders, ArrayList<Player> trainees, SearchAlgorithm searchAlgorithm, int maxSquads) {
        this(commanders, trainees, searchAlgorithm);
        this.maxSquads = maxSquads;
    }

    /**
     * Generate Squads given a list of trainees and trainers and a SearchAlgorithm.
     * @return The solution state if any.
     */
    protected SquadPlan call() {

        ArrayList<Integer[]> traineeRoles = IntStream.range(0, trainees.size())
                .mapToObj(i -> new Integer[]{i, trainees.get(i).getRoles()})
                .collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Integer[]> trainerRoles = IntStream.range(0, commanders.size())
                .mapToObj(i -> new Integer[]{i + traineeRoles.size(), commanders.get(i).getRoles()})
                .collect(Collectors.toCollection(ArrayList::new));
		
		//setup squad types
		for (Squad squad:Squads.getSquads()) {
			if (squad.getEnabled()) {
				squadTypeAllowed.put(squad.getSquadHandle(),squad.getMaxAmount());
			}
		}
				
		//fallback to default squad if no squadtypes found
		if (squadTypeAllowed.isEmpty()) squadTypeAllowed.put("default",0);
		

        SquadPlan initialSate = maxSquads == 0 ?
                new SquadPlan(traineeRoles, trainerRoles, squadTypeAllowed) :
                new SquadPlan(traineeRoles, trainerRoles, maxSquads, squadTypeAllowed) ;
        int numSquads = initialSate.getNumSquads();
        searchAlgorithm.init(initialSate);
        SquadPlan solution = null;

        while (solution == null && numSquads > 0) {
            if (isCancelled()) return null;
            System.out.println("Solving for " + numSquads + " squads...");
            long startTime = System.currentTimeMillis();
            solution = (SquadPlan) searchAlgorithm.solve();
            System.out.println("Expanded " + searchAlgorithm.getNodes() + " nodes.");
            long endTime = System.currentTimeMillis();
            if (solution == null) {
                System.out.println("Failed in: " + (endTime-startTime) / 1000.0 + " seconds.");
                --numSquads;
                searchAlgorithm.init(new SquadPlan(traineeRoles, trainerRoles, numSquads, squadTypeAllowed));
            }
            else {
                System.out.println("Successful in: " + (endTime-startTime) / 1000.0 + " seconds.");
            }
        }
        return solution;
    }
}
