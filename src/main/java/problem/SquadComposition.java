package problem;

import signups.Player;

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

    /**
     * Constructors.
     * Since Player objects will not be modified throughout this CSP,
     * Shallow copies of lists can be created keeping references to
     * the original Player objects.
     */
    public SquadComposition(List<Player> playersToSort, List<List<Player>> squads) {
        // Shallow copy lists.
        this.playersToSort = new ArrayList<>(playersToSort);
        this.squads = squads.stream().map(ArrayList::new).collect(Collectors.toList());
    }

    public SquadComposition(SquadComposition other) {
        // Shallow copy constructor.
        this.playersToSort = new ArrayList<>(other.getPlayersToSort());
        this.squads = other.getSquads().stream().map(ArrayList::new).collect(Collectors.toList());
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
        for (List<Player> squad : squads) {
            // Squad size check.
            if (squad.size() > 10) return false;
            // Commander and aide check.
            long commCount = squad.stream().filter(p -> p.getTier().toLowerCase().contains("commander")).count();
            long aideCount = squad.stream().filter(p -> p.getTier().toLowerCase().contains("aide")).count();
            if (commCount > 2) return false;
            if (aideCount > 2) return false;
            if (commCount + aideCount > 2) return false;
            if (squad.size() == 10 && commCount == 0 && aideCount == 0) return false;
            // Role check.
            long offhealCount, healReneCount, healFBCount, quickFBCount, quickChronoCount, alacrigadeCount, cSuppCount;
            if (squad.stream().filter(p -> p.getAssignedRole().equals("DPS")).count() > 5) return false;
            if (squad.stream().filter(p -> p.getAssignedRole().equals("Chrono Tank")).count() > 1) return false;
            if (squad.stream().filter(p -> p.getAssignedRole().equals("Banners")).count() > 1) return false;
            if (squad.stream().filter(p -> p.getAssignedRole().equals("Druid")).count() > 1) return false;
            if ((offhealCount = squad.stream().filter(p -> p.getAssignedRole().equals("Offheal")).count()) > 1) return false;
            if ((healReneCount = squad.stream().filter(p -> p.getAssignedRole().equals("Heal Renegade")).count()) > 1) return false;
            if ((healFBCount = squad.stream().filter(p -> p.getAssignedRole().equals("Heal FB")).count()) > 1) return false;
            if ((alacrigadeCount = squad.stream().filter(p -> p.getAssignedRole().equals("Alacrigade")).count()) > 1) return false;
            if ((quickFBCount = squad.stream().filter(p -> p.getAssignedRole().equals("Quickness FB")).count()) > 1) return false;
            if ((quickChronoCount = squad.stream().filter(p -> p.getAssignedRole().equals("Quickness Chrono")).count()) > 1) return false;
            if ((cSuppCount = squad.stream().filter(p -> p.getAssignedRole().equals("Power Boon Chrono")).count()) > 1) return false;
            if ((offhealCount & healReneCount) == 1 || (offhealCount & healFBCount) == 1 || (healFBCount & healReneCount) == 1)
                return false; // At least 2 offheals...
            if ((cSuppCount & quickFBCount) == 1 || (cSuppCount & alacrigadeCount) == 1 || (quickFBCount & alacrigadeCount) == 1)
                return false; // At least 2 DPS boons players.
            if ((alacrigadeCount & healReneCount) == 1 || (quickFBCount+quickChronoCount & healFBCount) == 1 ||
                    (quickFBCount+quickChronoCount & offhealCount) == 1 || (alacrigadeCount & offhealCount) == 1)
                return false; // Wrong support pairs.
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
