package problem;

import signups.Player;

import java.util.List;

/**
 * Wrapper object that stores a squad composition and it's name.
 * @author Eren Bole.8720
 * @version 1.0
 */
public class SquadSolution {

    String name;
    List<List<Player>> squads;

    public SquadSolution(List<List<Player>> squads, String name) {
        this.squads = squads;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<List<Player>> getSquads() {
        return squads;
    }
}
