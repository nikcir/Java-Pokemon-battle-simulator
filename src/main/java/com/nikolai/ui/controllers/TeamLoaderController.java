package com.nikolai.ui.controllers;

import javafx.fxml.FXML;
// import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.util.List;
import com.nikolai.pokemon.team.TeamPokemon;
import com.nikolai.services.TeamStorageService;
import com.nikolai.ui.App;

public class TeamLoaderController {

    @FXML private TextField teamName;
    @FXML private Label teamLabel;

    @FXML
    public void goBack() throws IOException {
        App.switchTo("main.fxml");
    }

    public void onLoadTeam() throws Exception {
        String name = teamName.getText().trim();
        if (name.isEmpty()) {
            teamLabel.setText("Please enter a team name");
            return;
        }

        TeamStorageService storageService = new TeamStorageService();
        List<TeamPokemon> loadedTeam = storageService.loadTeam(name);
        if (loadedTeam == null) {
            teamLabel.setText("Team not found");
            return;
        }

        teamLabel.setText("Loaded Team: " + name + "\n" +
                loadedTeam.stream()
                        .map(p -> p.getNature() + " " + p.getName() + " @ " + p.getHeldItem() + " with moves: " + String.join(", ", p.getMoves()))
                        .reduce((a, b) -> a + "\n" + b)
                        .orElse("No Pokemon in team"));
    }

    public void onDeleteTeam() throws Exception {
        String name = teamName.getText().trim();
        if (name.isEmpty()) {
            teamLabel.setText("Please enter a team name");
            return;
        }

        TeamStorageService storageService = new TeamStorageService();
        storageService.deleteTeam(name);
        teamLabel.setText("Team deleted successfully");
    }

    public void onShowAllTeams() throws Exception {
        TeamStorageService storageService = new TeamStorageService();
        java.util.List<String> teamNames = storageService.getTeamNames();
        if (teamNames.isEmpty()) {
            teamLabel.setText("No teams saved");
            return;
        }

        teamLabel.setText("Saved Teams:\n" + String.join("\n", teamNames));
    }
}