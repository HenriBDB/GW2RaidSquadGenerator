package com.crossroadsinn.problem;

import com.crossroadsinn.problem.entities.*;
import org.apache.commons.math3.util.CombinatoricsUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Assign players a role and squad so that all squad requirements are fulfilled
 * Different squads may have different requirements
 */
public class SquadCreationCSP2 implements CSP {

    // Maps squad handles to number required
    private final List<Squad> squads;
    private final List<Player> players;
    private final List<Player> commanders;
    private final BoonCounter boonCounter;

    public SquadCreationCSP2(Map<String, Integer> squadHandles, List<Player> playerBase, List<Player> commanders) {
        this.players = new ArrayList<>(playerBase);
        this.commanders = new ArrayList<>(commanders);
        squads = new ArrayList<>();
        boonCounter = new BoonCounter();
        squadHandles.forEach((squadHandle,amount) -> {
            for(int i = 0; i<amount; ++i) squads.add(Squads.getSquad(squadHandle));
        });
        setAvailableBoons();
    }

    public SquadCreationCSP2(SquadCreationCSP2 copy) {
        squads = copy.squads.stream().map(Squad::new).collect(Collectors.toList());
        players = new ArrayList<>(copy.players);
        commanders = new ArrayList<>(copy.commanders);
        boonCounter = new BoonCounter(copy.boonCounter);
    }

    /**
     * State transition function
     * May utilises different strategies
     * @return children states given a chosen strategy
     */
    public List<CSP> getChildren() {
        for (Squad s : squads) {
            if (!s.getReqRoles().isEmpty())
                return assignAllPlayersToSquadThatMatchRoleConstraint(s.getReqRoles().keySet().stream().findFirst().get(), true);
            else if (!s.getReqSpecialRoles().isEmpty())
                return assignAllPlayersToSquadThatMatchRoleConstraint(s.getReqSpecialRoles().keySet().stream().findFirst().get(), false);
        }
        return assignAllRolesOfOnePlayerToAllSquads();
    }

    public void assignPlayerRole(Player player, Role role, Squad modelSquad) {
        for (Squad squad : squads) {
            if (squad.equals(modelSquad)) {
                squad.addPlayer(player, role);
                break;
            }
        }
    }

    public void assignPlayerReqRole(Player player, Role role) {
        for (Squad squad : squads) {
            if (squad.isValidCandidate(role)) {
                squad.addPlayer(player, role);
                break;
            }
        }
    }

    /*
    State Transition Strategies
     */

    private List<CSP> assignAllRolesOfOnePlayerToAllSquads() {
        List<CSP> successorStates = new ArrayList<>();
        // Pick next player
        Player playerToAssign = choseUnassignedPlayer();
        // Empty list if no more players to assign
        if (playerToAssign == null) return successorStates;
        // Assign to Squad X Role Y
        // Assign all roles possible ina  squad, then remove player from that squad on orig and try for next squad etc...
        // Must be completed sequentially
        for (Squad squad : squads.stream().distinct().collect(Collectors.toList())) {
            for (Role role : playerToAssign.getRoles()) {
                if (squad.isValidCandidate(role)) {
                    SquadCreationCSP2 newState = new SquadCreationCSP2(this);
                    newState.assignPlayerRole(playerToAssign, role, squad);
                    //TODO Not much benefit in doing that
                    if (newState.isValidState())
                        successorStates.add(newState);
                }
            }
        }
        // Return children state
        if (successorStates.isEmpty()) successorStates.add(new SquadCreationCSP2(this));
        return successorStates;
    }

    private List<CSP> assignAllPlayersToSquadThatMatchRoleConstraint(String roleHandle, boolean isRole) {
        // Get num req
        int numRequired;
        if (isRole) numRequired = squads.stream()
                .mapToInt(e -> e.getReqRoles().getOrDefault(roleHandle, 0))
                .sum();
        else numRequired = squads.stream()
                .mapToInt(e -> e.getReqSpecialRoles().getOrDefault(roleHandle, 0))
                .sum();

        // Find combinations of valid players
        //TODO make more efficient
        List<Role> validRoles;
        if (isRole) { // Main role
            validRoles = new ArrayList<>();
            validRoles.add(Roles.getRole(roleHandle));
        } else { // special role
            validRoles = Roles.getAllRoles().stream()
                    .filter(r -> r.getRoleHandle().equals(roleHandle) || r.getSpecialRoles().contains(roleHandle))
                    .collect(Collectors.toList());
        }
        List<Player> validPlayers = players.stream()
                .filter(p -> p.getRoles().stream().anyMatch(validRoles::contains))
                .collect(Collectors.toList());
        if (validPlayers.size() < numRequired) return new ArrayList<>();
        Iterator<int[]> it = CombinatoricsUtils.combinationsIterator(validPlayers.size(), numRequired);
        List<Player[]> combinations = new ArrayList<>();
        while (it.hasNext()) {
            combinations.add(Arrays.stream(it.next()).mapToObj(validPlayers::get).toArray(Player[]::new));
        }

        // Transform combinations into states amd prune invalid states
        // Return children state
        return combinations.stream()
                .map(c -> playerCombinationToState(c, validRoles))
                .filter(Objects::nonNull)
                .filter(SquadCreationCSP2::isValidState)
                .collect(Collectors.toList());
    }

    private List<CSP> assignAllPlayersToSquadThatMatchBoonConstraint() {
        List<CSP> successorStates = new ArrayList<>();
        // Return children state
        return successorStates;
    }

    @Override
    public int heuristic() {
        return squads.stream().mapToInt(Squad::getFreeSpots).sum();
    }

    @Override
    public boolean isSolution() {
        return squads.stream().allMatch(Squad::isComplete);
    }

    /*
     * Helper Methods
     */

    private SquadCreationCSP2 playerCombinationToState(Player[] combi, List<Role> validRoles) {
        if (validRoles.isEmpty()) return null;
        SquadCreationCSP2 newState = new SquadCreationCSP2(this);
        if (validRoles.size() == 1) {
            for (Player p : combi) {
                newState.assignPlayerReqRole(p, validRoles.get(0));
            }
        } else {
            for (Player p : combi) {
                // Assumes valid role will be found based on previous filtering
                Role role = p.getRoles().stream().filter(validRoles::contains).findFirst().get();
                newState.assignPlayerReqRole(p, role);
            }
        }
        return newState;
    }

    /**
     * Uses constraints to determine the validity of the current state.
     * @return whether a solution is still possible or not.
     */
    //TODO Gains drastically drop as search space increases
    public boolean isValidState() {
        BoonCounter squadBoonCounter = new BoonCounter();
        squads.forEach(e -> squadBoonCounter.addBoonCounter(e.getRequiredBoons()));
        return boonCounter.containsAllBoons(squadBoonCounter) &&  // Enough boons left to distribute to satisfy squad constraints
                squads.stream().allMatch(Squad::isValid) && // All squads are valid
                squads.stream().mapToInt(Squad::getFreeSpots).sum() <= (players.size()+commanders.size()); // Enough players to fill remaining spots
    }

    private Player choseUnassignedPlayer() {
        if (!players.isEmpty())
            return players.remove(new Random().nextInt(players.size()));
        else if (!commanders.isEmpty())
            return commanders.remove(new Random().nextInt(commanders.size()));
        return null;
    }

    /**
     * For each player, add to the boon counter the boons provided by their available roles.
     */
    private void setAvailableBoons() {
        players.forEach(p -> {
            p.getRoles().forEach(r -> {
                r.getBoons().forEach((boon,amount) -> boonCounter.addBoon(boon, amount));
            });
        });
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        int i = 1;
        for (Squad squad : squads) {
            stringBuilder.append(String.format("Squad %d: \n", i));
            stringBuilder.append(squad);
            ++i;
        }
        return stringBuilder.toString();
    }

    public boolean equals(Object aOther) {
        if (this == aOther) return true;
        else if (!(aOther instanceof SquadCreationCSP2)) return false;
        SquadCreationCSP2 other = (SquadCreationCSP2) aOther;
        boolean squadsMatch = new HashSet<>(this.squads).equals(new HashSet<>(other.squads));
        return this.players.equals(other.players) && this.commanders.equals(other.commanders) &&
                squadsMatch;
    }
}