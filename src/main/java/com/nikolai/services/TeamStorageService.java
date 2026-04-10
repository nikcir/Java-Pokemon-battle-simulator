package com.nikolai.services;

import com.nikolai.pokemon.team.PlayerTeam;
import com.nikolai.pokemon.team.TeamPokemon;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class TeamStorageService {
    // Service for saving and loading player teams to/from a local text file.
    // Format per team:
    //   TEAM:<teamName>
    //   <name>|<heldItem>|<nature>|<level>|<move1>,<move2>,...
    //   END

    private static final String DEFAULT_FILE_PATH = "src/main/resources/saves/teams.txt";
    private final String filePath;

    public TeamStorageService() {
        this.filePath = DEFAULT_FILE_PATH;
    }

    public TeamStorageService(String filePath) {
        this.filePath = filePath;
    }

    // seialization 

    private String serializeTeams(Map<String, List<TeamPokemon>> teams) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<TeamPokemon>> entry : teams.entrySet()) {
            sb.append("TEAM:").append(entry.getKey()).append("\n");
            for (TeamPokemon p : entry.getValue()) {
                sb.append(p.getName()).append("|")
                  .append(p.getHeldItem()).append("|")
                  .append(p.getNature()).append("|")
                  .append(p.getLevel()).append("|")
                  .append(String.join(",", p.getMoves())).append("\n");
            }
            sb.append("END\n");
        }
        return sb.toString();
    }

    // deserialization
    private Map<String, List<TeamPokemon>> parseTeams(String content) {
        Map<String, List<TeamPokemon>> teams = new LinkedHashMap<>();
        String currentTeamName = null;
        List<TeamPokemon> currentList = null;

        for (String raw : content.split("\n")) {
            String line = raw.trim();
            if (line.isEmpty()) continue;

            if (line.startsWith("TEAM:")) {
                currentTeamName = line.substring(5);
                currentList = new ArrayList<>();
            } else if (line.equals("END")) {
                if (currentTeamName != null && currentList != null) {
                    teams.put(currentTeamName, currentList);
                }
                currentTeamName = null;
                currentList = null;
            } else if (currentList != null) {
                String[] parts = line.split("\\|", 5);
                if (parts.length < 5) continue;
                String name     = parts[0];
                String heldItem = parts[1];
                String nature   = parts[2];
                int    level    = Integer.parseInt(parts[3]);
                List<String> moves = new ArrayList<>(Arrays.asList(parts[4].split(",")));
                currentList.add(new TeamPokemon(name, heldItem, moves, nature, level));
            }
        }
        return teams;
    }

    // Loads from file
    private Map<String, List<TeamPokemon>> loadAllTeams() throws IOException {
        if (!Files.exists(Paths.get(filePath))) return new LinkedHashMap<>();
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        return parseTeams(content);
    }

    // Saves to file
    private void writeAllTeams(Map<String, List<TeamPokemon>> teams) throws IOException {
        Path parent = Paths.get(filePath).getParent();
        if (parent != null) Files.createDirectories(parent);
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(serializeTeams(teams));
        }
    }

    
    //Fetches all teams, and adds the new one to the map, then writes back to file.
    public void saveTeam(PlayerTeam team) throws IOException {
        Map<String, List<TeamPokemon>> teams = loadAllTeams();
        teams.put(team.getTeamName(), team.getTeam());
        writeAllTeams(teams);
    }

    public List<TeamPokemon> loadTeam(String teamName) throws IOException {
        return loadAllTeams().get(teamName);
    }
    //Fetches all teams, removes the specified one from the map, then writes back to file.
    public void deleteTeam(String teamName) throws IOException {
        Map<String, List<TeamPokemon>> teams = loadAllTeams();
        teams.remove(teamName);
        writeAllTeams(teams);
    }
    // Fetches all teams, and returns a list of their names for display in the UI.
    public List<String> getTeamNames() throws IOException {
        return new ArrayList<>(loadAllTeams().keySet());
    }
}
