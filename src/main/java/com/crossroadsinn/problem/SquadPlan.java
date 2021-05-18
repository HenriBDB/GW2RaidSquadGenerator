package com.crossroadsinn.problem;

import com.crossroadsinn.settings.Roles;
import com.crossroadsinn.settings.Squad;
import com.crossroadsinn.settings.Squads;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Defines the CSP structure for Squad Generation.
 * Includes arc dependency checks and forward chaining.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class SquadPlan implements CSP {
    private int numSquads;
    private final int trainerStartIndex;
    private final ArrayList<Integer[]> trainers;
    private ArrayList<Integer[]> players;
    private ArrayList<Integer[]> assigned = new ArrayList<>();
	
	//what squad types are allowed to use up to which amounts, 0 being endless
	private Hashtable<String, Integer> squadTypeAllowed = new Hashtable<String, Integer>();
	
	//save what squads have been used to generate to parse it on for SquadComposition.java
	private ArrayList<String> squadTypes = new ArrayList<>();
	
	//requirements
	private int reqPlayers = 10;
    private Hashtable<String, Integer> reqBoons = new Hashtable<String, Integer>();
    private Hashtable<String, Integer> reqSpecialRoles = new Hashtable<String, Integer>();
	
	//still available
	private int availPlayers = 0;
	private Hashtable<String, Integer> availBoons = new Hashtable<String, Integer>();
    private Hashtable<String, Integer> availSpecialRoles = new Hashtable<String, Integer>();
	
	//count amount of assigned dps for heuristic to prefer more dps players
	private int assignedDPS = 0;
	


    public SquadPlan(ArrayList<Integer[]> trainees, ArrayList<Integer[]> trainers, Hashtable<String, Integer> squadTypeAllowed) {
        this(trainees, trainers, 0,squadTypeAllowed);
    }

    public SquadPlan(ArrayList<Integer[]> trainees, ArrayList<Integer[]> trainers, int numSquads, Hashtable<String, Integer> squadTypeAllowed) {
        trainerStartIndex = trainers.stream().mapToInt(e -> { return e[0];} ).min().getAsInt();
        this.trainers = trainers.stream().map(Integer[]::clone).collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(this.trainers);
        this.players = Stream.of(trainees, trainers).flatMap(Collection::stream).collect(Collectors.toCollection(ArrayList::new));
        parsePlayers(players.stream().mapToInt(p -> p[1]).toArray());
		this.numSquads = numSquads;
		this.squadTypeAllowed = squadTypeAllowed;
		calculateRequirements();
	}
		
	private void calculateRequirements() {
		//temp part, will be replaced with proper passing of settings from UI later
		//get all allowed SquadTypes and check if there's a max amount of squads from there. if yes, check if bigger than numSquads and apply
		int maxSquadsSelected = 0;
		for (int i:squadTypeAllowed.values()) {
			if (i == 0) {
				maxSquadsSelected = 0;
				break;
			}
			maxSquadsSelected += i;
		}
		if (maxSquadsSelected > 0 && maxSquadsSelected < numSquads) numSquads = maxSquadsSelected;
		
		//max number of squads possible guessing
		//max number was set in UI, check if enough players even exist and enough squads are allowed, if not, assume we have no max squads setting and set numSquads based on available players
		if (numSquads != 0) {
			if (numSquads*10>availPlayers) {
				numSquads = 0;
			}
		};
		if (numSquads == 0) {
			numSquads = (availPlayers/10);
			if (maxSquadsSelected > 0 && maxSquadsSelected < numSquads) numSquads = maxSquadsSelected;
		}
		
		//set amount of required players
		reqPlayers = numSquads*10;

		//build requirements from a random composition of available squadtypes, max 1k tries, then go lower numSquads amount
		boolean foundSquads = false;
		while(numSquads>squadTypes.size()) {
			int i = 0;
			while (i<1000) {
				if (buildSquadRequirements()) {
					foundSquads = true;
					break;
				} else {
					//reset variables and try again
					this.squadTypes = new ArrayList<String>();
					this.reqBoons = new Hashtable<String, Integer>();
					this.reqSpecialRoles = new Hashtable<String, Integer>();
				}
				i++;
			}
			if (!foundSquads) {
				numSquads-= 1;
				//set amount of required players
				reqPlayers = this.numSquads*10;
			}			
		}
	}

    /**
     * Copy constructor. Creates a deepcopy.
     * @param other SquadPlan to copy
     */
    public SquadPlan(SquadPlan other) {
        this.numSquads = other.numSquads;
        this.trainers = other.trainers.stream().map(Integer[]::clone).collect(Collectors.toCollection(ArrayList::new));
        this.players = other.players.stream().map(Integer[]::clone).collect(Collectors.toCollection(ArrayList::new));
        this.assigned = other.assigned.stream().map(Integer[]::clone).collect(Collectors.toCollection(ArrayList::new));
        this.trainerStartIndex = other.trainerStartIndex;
		this.squadTypeAllowed = cloneHashTable(other.squadTypeAllowed);
        this.squadTypes = other.squadTypes; //add proper deepcopy here
		this.reqPlayers = other.reqPlayers;
		this.reqBoons = cloneHashTable(other.reqBoons);
		this.reqSpecialRoles = cloneHashTable(other.reqSpecialRoles);
		this.availPlayers = other.availPlayers;
		this.availBoons = cloneHashTable(other.availBoons);
		this.availSpecialRoles = cloneHashTable(other.availSpecialRoles);
		this.assignedDPS = other.assignedDPS;
    }
	
	public Hashtable<String,Integer> cloneHashTable(Hashtable<String, Integer> original) {
		Hashtable<String, Integer> copy = new Hashtable<String, Integer>();
		for(Map.Entry<String, Integer> entry : original.entrySet()) {
			copy.put(entry.getKey(), entry.getValue());
		}
		return copy;
	}

    public int getNumSquads() {
        return numSquads;
    }

    public ArrayList<Integer[]> getAssigned() {
        return assigned;
    }

    public ArrayList<String> getSquadTypes() {
        return squadTypes;
    }
	
	//function to build squad requirements based on allowed squads
	private boolean buildSquadRequirements() {
		//add a squad and check if it's possible
		int i = 0;
		while (i<numSquads) {
			i++;
			//get a random squad
			List<String> valuesList = new ArrayList<String>(squadTypeAllowed.keySet());
			int randomIndex = new Random().nextInt(valuesList.size());
			String randomValue = valuesList.get(randomIndex);
			
			//add requirements - boons
			for (String key:Squads.getSquad(randomValue).getReqBoons().keySet()) {
				int value = Squads.getSquad(randomValue).getReqBoons().get(key);
				if (reqBoons.containsKey(key)) {
					value += reqBoons.get(key);
				}
				reqBoons.put(key,value);
			}
			
			//add requirements - roles
			for (String key:Squads.getSquad(randomValue).getReqSpecialRoles().keySet()) {
				int value = Squads.getSquad(randomValue).getReqSpecialRoles().get(key);
				if (reqSpecialRoles.containsKey(key)) {
					value += reqSpecialRoles.get(key);
				}
				reqSpecialRoles.put(key,value);
			}
			
			//add to squad amounts
			squadTypes.add(randomValue);
			
			//check dependencies, return false if can't satisfy the squad combination requirements
			if (!checkArcDependencies()) return false;
		}
		//completed while loop without returning false, so squad combination should be fulfillable
		return true;
	}

    /**
     * Update availabilities for each player.
     * @param players list of players.
     */
    private void parsePlayers(int[] players) {
        for ( int player : players ) {
			availPlayers += 1;
			for ( int role : Roles.getAllRolesNumbers() ) {
				if ( (player & role) > 0 ) {
					for(String boon:Roles.getRole(role).getBoons().keySet()) {
						int prevBoons = ((availBoons.containsKey(boon)) ? availBoons.get(boon) : 0);
						availBoons.put(boon,prevBoons+Roles.getRole(role).getBoonAmount(boon));
					}
					for(String specialRole:Roles.getRole(role).getSpecialRoles()) {
						int prevRoles = ((availSpecialRoles.containsKey(specialRole)) ? availSpecialRoles.get(specialRole) : 0);
						availSpecialRoles.put(specialRole,prevRoles+1);
					}
				}
			}
        }
    }

    /**
     * Checks that sufficient roles are available to satisfy current needs.
     * @return whether this squad plan is currently valid or not.
     */
    public boolean checkArcDependencies() {
        // Enough dps still available
        if (reqPlayers > availPlayers) return false;
		// enough special roles still available
		for (String key:reqSpecialRoles.keySet()) {
			if (!availSpecialRoles.containsKey(key)) return false;
			if (reqSpecialRoles.get(key) > availSpecialRoles.get(key)) return false;
		}
		// enough boons still available
		for (String key:reqBoons.keySet()) {
			if (!availBoons.containsKey(key)) return false;
			if (reqBoons.get(key) > availBoons.get(key)) return false;
		}
		//should be enough there, return true;
        return true;
    }

    /**
     * Set a role for a player. Return true if arc dependencies remain satisfied.
     * This method is to be used with basic roles only.
     * @return whether or not arc dependencies are satisfied.
     */
    private boolean setPlayer(int playerIndex, int playerRole) {
		//check if even still need a player
		if (reqPlayers < 1) return false;
		
		//special role still needed
		for (String key:Roles.getRole(playerRole).getSpecialRoles()) {
			//workaround for dps in special roles
			if (key == "dps") continue;
			if (!reqSpecialRoles.containsKey(key)) return false;
			if (Roles.getRole(playerRole).getIfRole(key) > reqSpecialRoles.get(key)) return false;
		}
		// enough boons still available
		for (String key:Roles.getRole(playerRole).getBoons().keySet()) {
			if (Roles.getRole(playerRole).getBoonAmount(key) > reqBoons.get(key)) return false;
		}
		
        // Find player
        Integer[] player = null;
        for (int i = 0; i < players.size(); ++i) {
            if (players.get(i)[0] == playerIndex) {
                player = players.remove(i);
                break;
            }
        }
        if (player == null) return false;
		
		// Role not found in player
        if ((player[1] & playerRole) == 0) return false;
		
        // Assign player and remove it from available
        removeAvailabilities(player[1]);
        player[1] = playerRole;
        assigned.add(player);
		assignedDPS += Roles.getRole(playerRole).getDPS();
		
		// remove player from requirements
		reqPlayers -= 1;
		for(String specialRole:Roles.getRole(playerRole).getSpecialRoles()) {
			if (!reqSpecialRoles.containsKey(specialRole)) continue;
			reqSpecialRoles.put(specialRole,reqSpecialRoles.get(specialRole)-1);
		}
		for(String boon:Roles.getRole(playerRole).getBoons().keySet()) {
			if (!reqBoons.containsKey(boon)) continue;
			reqBoons.put(boon,reqBoons.get(boon)-Roles.getRole(playerRole).getBoonAmount(boon));
		}
        // Return validity of state
        return checkArcDependencies();
    }

    /**
     * Pick a random player and for each of it's availabilities whether a valid plan can be generated.
     * If not, remove player from list (soz broski, maybe next week will be your week)
     * @return valid SquadPlans generated.
     */

    public ArrayList<CSP> getChildren() {
        ArrayList<CSP> children = new ArrayList<>();
        boolean shouldForwardChain = true;

        while (children.isEmpty() && players.size() > 0) {
            Integer[] player;
            if (!trainers.isEmpty()) {
                // Start with commanders:
                player = trainers.remove(0);
                shouldForwardChain = false;
            } else {
                // Pick random player:
                int randomIndex = new Random().nextInt(players.size());
                player = players.get(randomIndex);
            }

            // Check all availabilities of chosen player:
            for (int role : Roles.getAllRolesNumbers() ) {
                if ((player[1] & role) > 0 ) {
                    SquadPlan copy = new SquadPlan(this);
                    if (copy.setPlayer(player[0], role)) {
                        if (shouldForwardChain && copy.forwardChain()) children.add(copy);
                        else if (!shouldForwardChain) children.add(copy);
                    }
                }
            }

            // Player has no valid availabilities:
            if (children.isEmpty()) players.remove(player);
        }
        Collections.shuffle(children); return children;
    }
	
    /**
     * Equal operators for when using memorization in search algorithms.
     * @param other The problem.SquadPlan to compare with.
     * @return where their states are equal or not.
     */
    public boolean equals(SquadPlan other) {
        return numSquads == other.numSquads && assigned.equals(other.assigned) && players.equals(other.players);
    }

    /**
     * Calculates the heuristic for the problem.SquadPlan based on remaining spots to fill.
     * @return the heuristic value for this plan.
     */
    public int heuristic() {
		//old legacy code
        // Double chrono is bad! Avoid it at all costs.
        //int numChronoSupp = (int) assigned.stream().filter(p -> p[1] == 512).count();
        // Commander not on DPS is bad. NOTE: This part was written while under the sea. *Insert song from The Little Mermaid*
        //int numCommNotAsDPS = (int) assigned.stream().filter(e -> e[0] >= trainerStartIndex).filter(e -> e[1] > 3).count();

        //return IntStream.of(left).sum() + numChronoSupp + numCommNotAsDPS;
		
		//as many people as possibleas dps
		int numNotAsDPS = (int) (assigned.stream().filter(e -> Roles.getRole(e[1]).getDPS() == 0).count());
		
		//comm on DPS == bad
		int numCommNotAsDPS = (int) assigned.stream().filter(e -> e[0] >= trainerStartIndex).filter(e -> Roles.getRole(e[1]).getDPS() == 0).count();
		
		return reqPlayers+numNotAsDPS+numCommNotAsDPS;
		
		//i want as many dps players as possible
		//int assignedCount = (int) assigned.stream().count();
        //return reqPlayers+(((assignedCount-assignedDPS))/(numSquads));
        //return reqPlayers;
    }

    /**
     * @return whether or not this plan is a solution.
     */
    public boolean isSolution() {
		
		//must not need any more players
		if (reqPlayers != 0) return false;
		//must not need any more special roles
		for (int amount:reqSpecialRoles.values()) {
			if (amount != 0) return false;
		}
		//must not need any more boons
		for (int amount:reqBoons.values()) {
			if (amount != 0) return false;
		}
		//everything seems to be in order
		return true;
    }

    /**
     * Remove the provided availabilities from the problem.SquadPlan
     * @param playerAvailabilities the availabilities to remove.
     */
    private void removeAvailabilities(int playerAvailabilities) {
		availPlayers -= 1;
		for ( int role : Roles.getAllRolesNumbers() ) {
			if ( (playerAvailabilities & role) > 0 ) {
				for(String boon:availBoons.keySet()) {
					availBoons.put(boon,availBoons.get(boon)-Roles.getRole(role).getBoonAmount(boon));
				}
				for(String specialRole:availSpecialRoles.keySet()) {
					availSpecialRoles.put(specialRole,availSpecialRoles.get(specialRole)-Roles.getRole(role).getIfRole(specialRole));
				}
			}
		}
    }

    /**
     * Apply obvious changes that do not have any alternative.
     * @return whether the resulting plan is valid or not.
     */

    private boolean forwardChain() {
		//check if any special role has required == available
		for (String key:reqSpecialRoles.keySet()) {
			if (reqSpecialRoles.get(key) == availSpecialRoles.get(key)) {
				if (!setSpecialRole(key)) return false;
			}
		}
		for (String key:reqBoons.keySet()) {
			if (reqBoons.get(key) == availBoons.get(key)) {
				if (!setBoonRole(key)) return false;
			}
		}
        // Check Arc Dependencies.
        if (!checkArcDependencies()) return false;
        return true;
    }

    /**
     * Find and set all players to a role that fills the special role requirement
     * @param key The key for the required special role.
     * @return true if new plan is valid, false if not.
     */
	 private boolean setSpecialRole(String key) {
		ArrayList<Integer> acceptedRoles = new ArrayList<Integer>();
		for (int role:Roles.getAllRolesNumbers()) {
			 if (Roles.getRole(role).getIfRole(key) == 1) {
				 acceptedRoles.add(role);
			 }
		}
		if (acceptedRoles.size() > 1) Collections.shuffle(acceptedRoles);

		while(reqSpecialRoles.get(key)>0) {
			//get all players for the current first accepted role
			ArrayList<Integer[]> playerList = players.stream().filter(p -> ((p[1] & acceptedRoles.get(0)) > 0)).collect(Collectors.toCollection(ArrayList::new));
			//if size of list is 0, i have no more players but need more. result: fail
			if (playerList.size() == 0) return false;
			//assign all players fulfilling role criteria unless there is multiple acceptedRoles, than only process one and randomize roles again
			for (Integer[] player : playerList) {
				if (!setPlayer(player[0], acceptedRoles.get(0))) return false;
				if (acceptedRoles.size() > 1) {
					Collections.shuffle(acceptedRoles);
					break;
				}
			}
		}
		//do a check if plan is valid
		if (checkArcDependencies()) {
			return true;
		} else {
			return false;
		}
	 }

    /**
     * Find and set all players to a role that fills the boon requirement
     * @param key The key for the required boon.
     * @return true if new plan is valid, false if not.
     */
	private boolean setBoonRole(String key) {
		ArrayList<Integer> acceptedRoles = new ArrayList<Integer>();
		for (int role:Roles.getAllRolesNumbers()) {
			 if (Roles.getRole(role).getBoonAmount(key) > 0) {
				 acceptedRoles.add(role);
			 }
		}
		if (acceptedRoles.size() > 1) Collections.shuffle(acceptedRoles);

		while(reqBoons.get(key)>0) {
			//get all players for the current first accepted role
			ArrayList<Integer[]> playerList = players.stream().filter(p -> ((p[1] & acceptedRoles.get(0)) > 0)).collect(Collectors.toCollection(ArrayList::new));
			//if size of list is 0, i have no more players but need more. result: fail
			if (playerList.size() == 0) return false;
			//assign all players fulfilling role criteria unless there is multiple acceptedRoles, than only process one and randomize roles again
			for (Integer[] player : playerList) {
				if (!setPlayer(player[0], acceptedRoles.get(0))) return false;
				if (acceptedRoles.size() > 1) {
					Collections.shuffle(acceptedRoles);
					break;
				}
			}
		}
		//do a check if plan is valid
		if (checkArcDependencies()) {
			return true;
		} else {
			return false;
		}
	 }
}
