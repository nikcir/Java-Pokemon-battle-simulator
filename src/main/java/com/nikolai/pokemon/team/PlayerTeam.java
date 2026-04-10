package com.nikolai.pokemon.team;

import java.util.List;

public class PlayerTeam {
    // Class representing a player's team of Pokemon, containing the team name and a list of TeamPokemon objects.
    private String teamName;
    private List<TeamPokemon> team;

    public PlayerTeam(String teamName, List<TeamPokemon> team) {
        this.teamName = teamName;
        this.team = team;
    }

    public String getTeamName() { return teamName; }
    public List<TeamPokemon> getTeam() { return team; }
}