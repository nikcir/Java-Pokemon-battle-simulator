package com.nikolai.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nikolai.pokemon.team.PlayerTeam;
import com.nikolai.pokemon.team.TeamPokemon;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class TeamStorageService {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_PATH = "src/main/resources/saves/teams.json";

    private static final Type MAP_TYPE = new TypeToken<Map<String, List<TeamPokemon>>>(){}.getType();

    private Map<String, List<TeamPokemon>> loadAllTeams() throws IOException {
        if (!Files.exists(Paths.get(FILE_PATH))) {
            return new HashMap<>();
        }
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Map<String, List<TeamPokemon>> teams = gson.fromJson(reader, MAP_TYPE);
            return teams != null ? teams : new HashMap<>();
        }
    }

    public void saveTeam(PlayerTeam team) throws IOException {
        
            // ?
        Files.createDirectories(Paths.get("src/main/resources/saves"));

        Map<String, List<TeamPokemon>> teams = loadAllTeams();
        teams.put(team.getTeamName(), team.getTeam());

        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            writer.write(gson.toJson(teams));
        }
    }

    public List<TeamPokemon> loadTeam(String teamName) throws IOException {
        Map<String, List<TeamPokemon>> teams = loadAllTeams();
        return teams.get(teamName); // returns null if team doesn't exist
    }

    public void deleteTeam(String teamName) throws IOException {
        Map<String, List<TeamPokemon>> teams = loadAllTeams();
        teams.remove(teamName);
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            writer.write(gson.toJson(teams));
        }
    }

    public java.util.List<String> getTeamNames() throws IOException {
        Map<String, List<TeamPokemon>> teams = loadAllTeams();
        return new java.util.ArrayList<>(teams.keySet());
    }
}