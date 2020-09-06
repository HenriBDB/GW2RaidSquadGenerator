package com.crossroadsinn.problem;

import com.crossroadsinn.signups.Player;

import java.util.ArrayList;
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
        this.squads = new ArrayList<>(squads);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<List<Player>> getSquads() {
        return squads;
    }
}
