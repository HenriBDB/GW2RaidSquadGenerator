package com.crossroadsinn.problem.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A class that can hold information about a squad.
 * @author moon
 * @version 1.1
 */
public class Squad {
	private final String squadHandle;
	private final String squadName;
    private final BoonCounter requiredBoons;
    private final HashMap<String, Integer> reqSpecialRoles;
    private final HashMap<String, Integer> reqRoles;
    private final int maxPlayers;
	private final HashMap<Player, Role> players;

    public Squad(String squadHandle, String squadName, int numPlayers, String boons, String roles, String specialRoles) {
        this.squadHandle = squadHandle;
        this.squadName = squadName;
        this.maxPlayers = numPlayers;
        this.requiredBoons = new BoonCounter();
		this.reqRoles = new HashMap<>();
		this.reqSpecialRoles = new HashMap<>();
        this.players = new HashMap<>();
        if (!boons.isEmpty()) {
			for (String part : boons.split("\\s*,\\s*")) {
				String[] BoonsValuePair = part.split("\\s*:\\s*");
				requiredBoons.addBoon(BoonCounter.Boon.valueOf(BoonsValuePair[0].toUpperCase()), Integer.parseInt(BoonsValuePair[1]));
			}
		}
		if (!roles.isEmpty()) {
			for (String r : roles.split("\\s*,\\s*")) {
				String[] roleValuePair = r.split("\\s*:\\s*");
				reqRoles.put(roleValuePair[0],
						reqRoles.getOrDefault(roleValuePair[0], 0) + Integer.parseInt(roleValuePair[1]));
			}
		}
		if (!specialRoles.isEmpty()) {
			for (String sR : specialRoles.split("\\s*,\\s*")) {
				String[] roleValuePair = sR.split("\\s*:\\s*");
				reqSpecialRoles.put(roleValuePair[0],
						reqSpecialRoles.getOrDefault(roleValuePair[0], 0) + Integer.parseInt(roleValuePair[1]));
			}
		}
    }

	/**
	 * Deep copy a squad
	 * @param copy squad to copy
	 */
	public Squad(Squad copy) {
    	squadHandle = copy.squadHandle;
    	squadName = copy.squadName;
    	maxPlayers = copy.maxPlayers;
    	requiredBoons = new BoonCounter(copy.getRequiredBoons());
    	reqSpecialRoles = new HashMap<>(copy.reqSpecialRoles);
    	reqRoles = new HashMap<>(copy.reqRoles);
    	// Shallow copy players
    	players = new HashMap<>(copy.players);
	}

    public String getSquadHandle() {
        return squadHandle;
	}
	
    public String getSquadName() {
        return squadName;
	}
	
	public String getName() {
		return squadName;
	}

	public BoonCounter getRequiredBoons() {
		return requiredBoons;
	}

	public HashMap<String, Integer> getReqSpecialRoles() {
        return reqSpecialRoles;
    }

	public HashMap<String, Integer> getReqRoles() {
		return reqRoles;
	}

	public int getFreeSpots() {
		return maxPlayers - players.size();
	}

    public boolean isFull() {
    	return players.size() == maxPlayers;
	}

	public boolean isValid() {
    	return reqSpecialRoles.size() <= maxPlayers - players.size();
	}

	public boolean isComplete() {
		return isFull() && requiredBoons.isEmpty() && reqSpecialRoles.isEmpty() && reqRoles.isEmpty();
	}

	public boolean isValidCandidate(Role role) {
    	if (isFull()) return false;
    	if (!role.getBoons().isEmpty()) {
			for (Map.Entry<BoonCounter.Boon, Integer> playerProvidedBoons : role.getBoons().entrySet()) {
				if (requiredBoons.getCount(playerProvidedBoons.getKey()) < playerProvidedBoons.getValue()) return false;
			}
			return true;
		}
    	return reqRoles.containsKey(role.getRoleHandle()) ||
				role.getSpecialRoles().stream().anyMatch(reqSpecialRoles::containsKey) ||
				role.getRoleHandle().equalsIgnoreCase("dps");
	}

	// Irreversible?
    public boolean addPlayer(Player player, Role role) {
    	if (players.size() >= maxPlayers) return false;
    	players.put(player, role);
    	// Handle new role
		if (reqRoles.containsKey(role.getRoleHandle())) {
			int newVal = reqRoles.get(role.getRoleHandle()) - 1;
			if (newVal <= 0) reqRoles.remove(role.getRoleHandle());
			else reqRoles.put(role.getRoleHandle(), newVal);
		}
		for (String sR : role.getSpecialRoles()) {
			if (reqSpecialRoles.containsKey(sR)) {
				int newVal = reqSpecialRoles.get(sR) - 1;
				if (newVal <= 0) reqSpecialRoles.remove(sR);
				else reqSpecialRoles.put(sR, newVal);
			}
		}
    	// Handle new boons
		role.getBoons().forEach(requiredBoons::removeBoon);
    	return true;
	}

	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof Squad)) return false;
		Squad otherSquad = (Squad) other;
		return otherSquad.squadHandle.equals(this.squadHandle) && otherSquad.players.size() == this.players.size() &&
				otherSquad.players.entrySet().stream().allMatch(e -> this.players.get(e.getKey()) == e.getValue());
	}

	public int hashCode() {
		return Objects.hash(squadHandle, players);
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		players.forEach((p,r) -> stringBuilder.append(String.format("%s on %s\n", p.getName(), r.getRoleName())));
		return stringBuilder.toString();
	}
}