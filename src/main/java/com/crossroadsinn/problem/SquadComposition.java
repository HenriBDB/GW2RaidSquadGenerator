package com.crossroadsinn.problem;

import com.crossroadsinn.problem.entities.Player;
import com.crossroadsinn.problem.entities.Squads;

import java.util.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Given an initial state and a list of players to sort,
 * Fill squads with players to sort until all squads contain
 * 10 players and constraints are satisfied.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class SquadComposition implements CSP {

    List<Player> playersToSort;
    List<List<Player>> squads;
	ArrayList<String> squadTypes;

    /**
     * Constructors.
     * Since Player objects will not be modified throughout this CSP,
     * Shallow copies of lists can be created keeping references to
     * the original Player objects.
     */
    public SquadComposition(List<Player> playersToSort, List<List<Player>> squads, ArrayList<String> squadTypes) {
        // Shallow copy lists.
        this.playersToSort = new ArrayList<>(playersToSort);
        this.squads = squads.stream().map(ArrayList::new).collect(Collectors.toList());
		this.squadTypes = squadTypes;
    }

    public SquadComposition(SquadComposition other) {
        // Shallow copy constructor.
        this.playersToSort = new ArrayList<>(other.getPlayersToSort());
        this.squads = other.getSquads().stream().map(ArrayList::new).collect(Collectors.toList());
		this.squadTypes = other.squadTypes;
    }

    public List<Player> getPlayersToSort() {
        return playersToSort;
    }

    public List<List<Player>> getSquads() {
        return squads;
    }

    /**
     * Place first player of list into squad at given index.
     * @param squadIndex The index of the squad.
     * @return Whether the resulting state of the CSP satisfies all constraints.
     */
    public boolean setNextPlayer(int squadIndex) {
        if (squadIndex >= squads.size() || playersToSort.isEmpty()) return false;
        squads.get(squadIndex).add(playersToSort.remove(0));
        return isValid();
    }

    /**
     * Sets the constraints of the CSP.
     * @return whether all constraints are satisfied or not.
     */
    private boolean isValid() {
		int i = 0;
        for (List<Player> squad : squads) {
            // Squad size check.
            if (squad.size() > 10) return false;
            // Commander and aide check.
            long commCount = squad.stream().filter(p -> p.getTier().toLowerCase().contains("commander")).count();
            long aideCount = squad.stream().filter(p -> p.getTier().toLowerCase().contains("aide")).count();
            if (commCount > 2) return false;
            if (aideCount > 2) return false;
            if (commCount + aideCount > 2) return false;

            // Role check.
			// Set Constraints based on squad
			Hashtable<String, Integer> reqBoons = new Hashtable<String, Integer>();
			Hashtable<String, Integer> reqSpecialRoles = new Hashtable<String, Integer>();
			
			for (Map.Entry<String, Integer> entry : Squads.getSquad(squadTypes.get(i)).getReqSpecialRoles().entrySet()) {
				reqSpecialRoles.put(entry.getKey(),entry.getValue());
			}
				
//			for (Map.Entry<String, Integer> entry : Squads.getSquad(squadTypes.get(i)).getReqBoons().entrySet()) {
//				reqBoons.put(entry.getKey(),entry.getValue());
//			}
			
//			//remove every special role for each player and check if there is too many of said role
//			//same for boons
//			for (Player player:squad) {
//				for (String key:reqSpecialRoles.keySet()) {
//					reqSpecialRoles.put(key,reqSpecialRoles.get(key)-player.getAssignedRoleObj().getIfRole(key));
//					if (reqSpecialRoles.get(key)<0) return false;
//				}
//				for (String key:reqBoons.keySet()) {
//					reqBoons.put(key,reqBoons.get(key)-player.getAssignedRoleObj().getBoonAmount(key));
//					if (reqBoons.get(key)<0) return false;
//				}
//			}
			
			//check if any role is not fulfilled if squad has 10 peepos
			if (squad.size() == 10) {
				for (int value:reqSpecialRoles.values()) {
					if (value != 0) return false;
				}
				for (int value:reqBoons.values()) {
					if (value != 0) return false;
				}
			}
			i++;
        }
        return true;
    }

    /**
     * Apply the transition function and return any valid children.
     * @return The valid children of this state.
     */
    public ArrayList<CSP> getChildren() {
        ArrayList<CSP> children = new ArrayList<>();
        for (int i = 0; i < squads.size(); ++i) {
            SquadComposition copy = new SquadComposition(this);
            if (copy.setNextPlayer(i)) children.add(copy);
        }
        Collections.shuffle(children); return children;
    }

    public int heuristic() {
        // Spots left to fill.
        return (squads.size() * 10) - squads.stream().mapToInt(List::size).sum();
    }

    public boolean isSolution() {
        return (heuristic() == 0);
    }
}
