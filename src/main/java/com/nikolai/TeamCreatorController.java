package com.nikolai;

import javafx.fxml.FXML;
// import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class TeamCreatorController {

    @FXML private Label teamLabel;

    @FXML private TextField pokemonField;
    @FXML private TextField itemField;
    @FXML private TextField moveField1;
    @FXML private TextField moveField2;
    @FXML private TextField moveField3;
    @FXML private TextField moveField4;

    @FXML private TextField teamNameField;

    private List<TeamPokemon> team = new ArrayList<>();

    private final TeamStorageService storageService = new TeamStorageService();

    @FXML
    public void goBack() throws IOException {
        App.switchTo("main.fxml");
    }

    @FXML
    public void onAddPokemon() {
        String pokemonName = pokemonField.getText().trim();
        String heldItem = itemField.getText().trim();
        List<String> moves = List.of(
            moveField1.getText().trim(),
            moveField2.getText().trim(),
            moveField3.getText().trim(),
            moveField4.getText().trim()
        );

        if (pokemonName.isEmpty()) return;

        TeamPokemon newPokemon = new TeamPokemon(pokemonName, heldItem, moves);
        team.add(newPokemon);
        teamLabel.setText(teamLabel.getText() + "\n" + newPokemon.getName() + " @ " + newPokemon.getHeldItem() + " with moves: " + String.join(", ", newPokemon.getMoves()));
    }

    @FXML
    public void onSaveTeam() throws Exception {
        String teamName = teamNameField.getText().trim();
        if (teamName.isEmpty()) {
            teamLabel.setText("Please enter a team name");
            return;
        }

        PlayerTeam playerTeam = new PlayerTeam(teamName, team);
        storageService.saveTeam(playerTeam);
        teamLabel.setText("Team saved successfully");
    }
}