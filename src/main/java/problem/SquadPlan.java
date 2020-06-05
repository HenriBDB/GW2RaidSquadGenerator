package problem;

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
    // private static final int[] roles = {1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024};
    private static final int[] basicRoles = {2, 1, 4, 64, 1024};
    private static final int[] supportRoles = {8, 16, 32, 128, 256, 512};

    private final int numSquads;
    private ArrayList<Integer[]> trainers;
    private ArrayList<Integer[]> players;
    private ArrayList<Integer[]> assigned = new ArrayList<>();
    //                    dps, bs, cTank, druid, suppGroup
    private int[] left = { 5,   1,   1,     1,      1};
    //                         dps, bs, cTank, druid, offheal, cSupp, qFB, hFB, alacrigade, heal renegade
    private int[] available = { 0,   0,   0,     0,      0,      0,    0,   0,      0,          0};

    public SquadPlan(ArrayList<Integer[]> trainees, ArrayList<Integer[]> trainers) {
        this.trainers = trainers.stream().map(Integer[]::clone).collect(Collectors.toCollection(ArrayList::new));
        this.players = Stream.of(trainees, trainers).flatMap(Collection::stream).collect(Collectors.toCollection(ArrayList::new));
        parsePlayers(players.stream().mapToInt(p -> p[1]).toArray());

        // Find max squads possible based on availability.
        // TODO Optimize by finding max pairs of support too.
        this.numSquads = Arrays.stream(new int[]{players.size() / 10, available[0] / 5, available[1], available[2], available[3]}).min().getAsInt();

        for(int i = 0; i < left.length; ++i) { left[i] = left[i] * numSquads; }
    }

    public SquadPlan(ArrayList<Integer[]> trainees, ArrayList<Integer[]> trainers, int numSquads) {
        this.trainers = trainers;
        this.players = Stream.of(trainees, trainers).flatMap(Collection::stream).collect(Collectors.toCollection(ArrayList::new));
        parsePlayers(players.stream().mapToInt(p -> p[1]).toArray());

        this.numSquads = numSquads;
        for(int i = 0; i < left.length; ++i) { left[i] = left[i] * numSquads; }
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
        this.left = Arrays.copyOf(other.left, other.left.length);
        this.available = Arrays.copyOf(other.available, other.available.length);
    }

    public int getNumSquads() {
        return numSquads;
    }

    public ArrayList<Integer[]> getAssigned() {
        return assigned;
    }

    /**
     * Update availabilities for each player.
     * @param players list of players.
     */
    private void parsePlayers(int[] players) {
        for ( int player : players ) {
            // dps
            if ( (player & 3) > 0) available[0] += 1;
            // bs
            if ( (player & 4) > 0) available[1] += 1;
            // cTank
            if ( (player & 1024) > 0) available[2] += 1;
            // druid
            if ( (player & 64) > 0) available[3] += 1;
            // offheal
            if ( (player & 8) > 0) available[4] += 1;
            // cSupp
            if ( (player & 512) > 0) available[5] += 1;
            // qFB
            if ( (player & 256) > 0) available[6] += 1;
            // hFB
            if ( (player & 32) > 0) available[7] += 1;
            // alacrigade
            if ( (player & 128) > 0) available[8] += 1;
            // heal renegade
            if ( (player & 16) > 0) available[9] += 1;
        }
    }

    /**
     * Checks that sufficient roles are available to satisfy current needs.
     * @return whether this squad plan is currently valid or not.
     */
    public boolean checkArcDependencies() {
        // Enough dps still available
        if (left[0] > available[0]) return false;
        // Enough bs still available
        if (left[1] > available[1]) return false;
        // Enough cTank still available
        if (left[2] > available[2]) return false;
        // Enough druid still available
        if (left[3] > available[3]) return false;
        // Enough support groups left
        int qFBhealAlacPairs = Math.min(available[6], available[9]);
        int hFBalacrigadePairs = Math.min(available[7], available[8]);
        int offhealLeft = available[4] + Math.abs(available[9] - available[6]) + Math.abs(available[8] - available[7]) + Math.abs(available[3] - left[3]);
        int cSuppOffhealPairs = Math.min(offhealLeft, available[5]);
        if (left[4] > (qFBhealAlacPairs + hFBalacrigadePairs + cSuppOffhealPairs)) return false;
        // Enough players are left to complete squads
        players = players.stream().filter(p -> p[1] != 0).collect(Collectors.toCollection(ArrayList::new));
        return (IntStream.of(left).sum() + left[4] < players.size());
    }

    /**
     * Set a role for a player. Return true if arc dependencies remain satisfied.
     * This method is to be used with basic roles only.
     * @return whether or not arc dependencies are satisfied.
     */
    private boolean setPlayer(int playerIndex, int role) {
        // Check that role is a basic role.
        if ((role & 1095) == 0) return false;
        // Check if role needed:
        if ((role & 3) > 0 && left[0] == 0) return false;
        else if ((role & 4) > 0 && left[1] == 0) return false;
        else if ((role & 1024) > 0 && left[2] == 0) return false;
        else if ((role & 64) > 0 && left[3] == 0) return false;

        // Find player
        Integer[] player = null;
        for (int i = 0; i < players.size(); ++i) {
            if (players.get(i)[0] == playerIndex) {
                player = players.remove(i);
                break;
            }
        }
        if (player == null) return false;
        // Role not found in player.
        if ((player[1] & role) == 0) return false;

        // Assign player and remove it from available
        removeAvailabilities(player[1]);
        player[1] = role;
        assigned.add(player);

        if ((role & 3) > 0) left[0] -= 1;
        else if ((role & 4) > 0) left[1] -= 1;
        else if ((role & 1024) > 0) left[2] -= 1;
        else if ((role & 64) > 0) left[3] -= 1;

        // Return validity of state
        return checkArcDependencies();
    }

    /**
     * Set a role for a player. Return true if arc dependencies remain satisfied.
     * This method is to be used with support roles only.
     * @param playerIndex1 id of first player.
     * @param role1 role assigned to first player.
     * @param playerIndex2 id of second player.
     * @param role2 role assigned to second player.
     * @return whether or not arc dependencies are satisfied.
     */
    private boolean setPlayerPair(int playerIndex1, int role1, int playerIndex2, int role2) {
        // Check support group is needed:
        if (left[4] <= 0) return false;
        // Check support roles provided (chrono supp, qFB, alacrigade, druid, heal FB, heal Renegade, other heal):
        if (Math.min(role1 & 1016, role2 & 1016) == 0) return false;
        // Find players:
        Integer[] player1 = null;
        Integer[] player2 = null;
        Iterator<Integer[]> itr = players.iterator();
        while (itr.hasNext()) {
            Integer[] p = itr.next();
            if (playerIndex1 == p[0]) {
                player1 = p;
                itr.remove();
            } else if (playerIndex2 == p[0]) {
                player2 = p;
                itr.remove();
            }
            if (player1 != null && player2 != null) break;
        }
        // At least one player not found.
        if (player1 == null || player2 == null) return false;
        // Role not found in at least one player.
        if (Math.min(player1[1] & role1, player2[1] & role2) == 0) return false;

        // Assign players and remove them from available
        removeAvailabilities(player1[1]);
        removeAvailabilities(player2[1]);
        player1[1] = role1;
        player2[1] = role2;
        assigned.add(player1);
        assigned.add(player2);

        // Add the support group.
        left[4] -= 1;

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

        while (children.isEmpty() && players.size() > 0) {
            Integer[] player;
            if (!trainers.isEmpty()) {
                // Start with commanders:
                player = trainers.remove(0);
            } else {
                // Pick random player:
                int randomIndex = new Random().nextInt(players.size());
                player = players.get(randomIndex);
            }

            // Check all availabilities of chosen player:
            for (int role : SquadPlan.basicRoles ) {
                if ((player[1] & role) > 0 ) {
                    SquadPlan copy = new SquadPlan(this);
                    if (copy.setPlayer(player[0], role) && copy.forwardChain()) children.add(copy);
                }
            }
            for (int role : SquadPlan.supportRoles) {
                if ((player[1] & role) > 0 ) {
                    ArrayList<Integer[]> candidates = findSupportPair(player[0], role);
                    candidates.forEach(candidate -> {
                        SquadPlan copy = new SquadPlan(this);
                        if (copy.setPlayerPair(player[0], role, candidate[0], candidate[1]) && copy.forwardChain()) children.add(copy);
                    });
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
        // Double chrono is bad! Avoid it at all costs.
        int numChronoSupp = (int) assigned.stream().filter(p -> p[1] == 512).count();
        return IntStream.of(left).sum() + numChronoSupp;
    }

    /**
     * @return whether or not this plan is a solution.
     */
    public boolean isSolution() {
        return IntStream.of(left).sum() == 0;
    }

    /**
     * Remove the provided availabilities from the problem.SquadPlan
     * @param playerAvailabilities the availabilities to remove.
     */
    private void removeAvailabilities(int playerAvailabilities) {
        // dps
        if ( (playerAvailabilities & 3) > 0) available[0] -= 1;
        // bs
        if ( (playerAvailabilities & 4) > 0) available[1] -= 1;
        // cTank
        if ( (playerAvailabilities & 1024) > 0) available[2] -= 1;
        // druid
        if ( (playerAvailabilities & 64) > 0) available[3] -= 1;
        // offheal
        if ( (playerAvailabilities & 8) > 0) available[4] -= 1;
        // cSupp
        if ( (playerAvailabilities & 512) > 0) available[5] -= 1;
        // qFB
        if ( (playerAvailabilities & 256) > 0) available[6] -= 1;
        // hFB
        if ( (playerAvailabilities & 32) > 0) available[7] -= 1;
        // alacrigade
        if ( (playerAvailabilities & 128) > 0) available[8] -= 1;
        // heal renegade
        if ( (playerAvailabilities & 16) > 0) available[9] -= 1;
    }

    /**
     * Apply obvious changes that do not have any alternative.
     * @return whether the resulting plan is valid or not.
     */
    private boolean forwardChain() {
        int changed = 1;
        while (changed > 0) {
            int changedDps = checkRole(0, 3);
            if (changedDps == -1) return false;
            int changedBs = checkRole(1, 4);
            if (changedBs == -1) return false;
            int changedTank = checkRole(2, 1024);
            if (changedTank == -1) return false;
            int changedDruid = checkRole(3, 64);
            if (changedDruid == -1) return false;
            // Check Arc Dependencies.
            if (!checkArcDependencies()) return false;
            changed = changedDps + changedBs + changedTank + changedDruid;
        }
        return true;
    }

    /**
     * The following methods check if the number of a role available matches the number still required for that role.
     * If that is the case, all players with that role are assigned it.
     * @param roleIndex The index of the role in left static array.
     * @param roleValue The bit of the role. I.e. Druid = 64 = 00001000000
     * @return -1 if failure, 0 if no change, 1 if change occurred.
     */
    private int checkRole(int roleIndex, int roleValue) {
        if (left[roleIndex] != 0 && available[roleIndex] == left[roleIndex]){
            ArrayList<Integer[]> playerList = players.stream().filter(p -> ((p[1] & roleValue) > 0)).collect(Collectors.toCollection(ArrayList::new));
            for (Integer[] player : playerList) {
                if (!setPlayer(player[0], roleValue)) return -1;
            }
            return 1;
        }
        return 0;
    }

    /**
     * This method creates support group pairs. Given an initial support player, it looks for any valid partner.
     * Valid pairs are: qFB, Heal Rene | hFB, alacrigade | chronosupp, any heal
     * @param player1 The index of the player.
     * @param role1 The role of one player in the pair.
     * @return a list of possible partners.
     */
    private ArrayList<Integer[]> findSupportPair(int player1, int role1) {
        ArrayList<Integer[]> candidates = new ArrayList<>();
        // Recursive start a new search on all possible pair options.
        int role2;
        // qFB and Heal Alac
        if (role1 == 256) role2 = 16;
        else if (role1 == 16) role2 = 256;
        // hFB and alacrigade
        else if (role1 == 128) role2 = 32;
        else if (role1 == 32) role2 = 128;
        // chronosupp and offheal
        else if (role1 == 8) role2 = 512;
        // Any heal with cSupp.
        else if (role1 == 512) role2 = 56;
        else return candidates;

        // Case not cSupp: only one candidate role.
        if (role1 != 512) {
            players.forEach(p -> {
                if ((p[1] & role2) > 0 && p[0] != player1) candidates.add(new Integer[]{p[0], role2});
            });
        }
        // Case cSupp: multiple candidate roles.
        else {
            for (int role : Arrays.stream(supportRoles).filter(r -> (r & role2) > 0).toArray()) {
                players.forEach(p -> {
                    if ((p[1] & role) > 0 && p[0] != player1) candidates.add(new Integer[]{p[0], role});
                });
            }
        }
        return candidates;
    }
}
