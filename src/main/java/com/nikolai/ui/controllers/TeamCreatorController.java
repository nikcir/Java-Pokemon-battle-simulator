package com.nikolai.ui.controllers;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.nikolai.pokemon.core.Pokemon;
import com.nikolai.pokemon.core.PokemonNatures;
import com.nikolai.pokemon.team.PlayerTeam;
import com.nikolai.pokemon.team.TeamPokemon;
import com.nikolai.services.PokeApiService;
import com.nikolai.services.TeamStorageService;
import com.nikolai.ui.App;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.ToggleButton;


public class TeamCreatorController {

    @FXML private Label teamLabel;
    @FXML private TextField pokemonField;
    @FXML private TextField itemField;
    @FXML private TextField teamNameField;

    @FXML private ComboBox<String> abilityDropdown;
    @FXML private ComboBox<String> natureDropdown;
    @FXML private ComboBox<String> moveDropdown1;
    @FXML private ComboBox<String> moveDropdown2;
    @FXML private ComboBox<String> moveDropdown3;
    @FXML private ComboBox<String> moveDropdown4;

    @FXML private ImageView spriteImageView;
    @FXML private ToggleButton shinyToggle;

    private List<TeamPokemon> team = new ArrayList<>();
    private final TeamStorageService storageService = new TeamStorageService();
    private final PokeApiService pokeApiService = new PokeApiService();

    private Pokemon currentPokemon;
    private boolean showingShiny = false;

    @FXML
    public void initialize() {
        natureDropdown.setItems(FXCollections.observableArrayList(PokemonNatures.NATURES));
    }

    @FXML
    public void onLoadPokemon() {
        String pokemonName = pokemonField.getText().trim();
        if (pokemonName.isEmpty()) return;

        Task<Pokemon> task = new Task<>() {
            @Override
            protected Pokemon call() throws Exception {
                return pokeApiService.getPokemon(pokemonName);
            }
        };

        task.setOnSucceeded(e -> {
            Pokemon pokemon = task.getValue();

            List<String> abilities = pokemon.getAbilities().stream()
                    .map(a -> a.getAbility().getName())
                    .collect(Collectors.toList());
            abilityDropdown.setItems(FXCollections.observableArrayList(abilities));

            List<String> moves = pokemon.getMoves().stream()
                    .map(m -> m.getMove().getName())
                    .collect(Collectors.toList());
            moveDropdown1.setItems(FXCollections.observableArrayList(moves));
            moveDropdown2.setItems(FXCollections.observableArrayList(moves));
            moveDropdown3.setItems(FXCollections.observableArrayList(moves));
            moveDropdown4.setItems(FXCollections.observableArrayList(moves));

            Image image = new Image(pokemon.getSprite().getFrontDefaultUrl());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(200);
            imageView.setPreserveRatio(true);
            spriteImageView.setImage(image);


            currentPokemon = pokemon;  // ← STORE IT
            showingShiny = false;      // ← RESET TO NORMAL
            shinyToggle.setSelected(false);
            shinyToggle.setText("Normal");

            // Set sprite to default
            pokemon.getSprite().setFront(pokemon.getSprite().getFrontDefaultUrl());
            updateSpriteDisplay();

        });

        task.setOnFailed(e -> {
            teamLabel.setText("Pokemon not found");
            System.err.println("Failed: " + task.getException());
        });

        new Thread(task).start();
    }

    @FXML
    public void onShinyToggleChanged() {
        showingShiny = shinyToggle.isSelected();
        shinyToggle.setText(showingShiny ? "Shiny" : "Normal"); 
        updateSpriteDisplay();
    }

    private void updateSpriteDisplay() {
        if (currentPokemon == null || currentPokemon.getSprite() == null) return;

        if (showingShiny) {
            currentPokemon.getSprite().setFront(currentPokemon.getSprite().getFrontShinyUrl());
        } else {
            currentPokemon.getSprite().setFront(currentPokemon.getSprite().getFrontDefaultUrl());
        }
        
        Image image = currentPokemon.getSprite().getFront();
        spriteImageView.setImage(image);
    }

    @FXML
    public void onAddPokemon() {
        String pokemonName = pokemonField.getText().trim();
        String heldItem = itemField.getText().trim();
        String ability = abilityDropdown.getValue();
        String nature = natureDropdown.getValue();

        List<String> moves = List.of(
            moveDropdown1.getValue() != null ? moveDropdown1.getValue() : "",
            moveDropdown2.getValue() != null ? moveDropdown2.getValue() : "",
            moveDropdown3.getValue() != null ? moveDropdown3.getValue() : "",
            moveDropdown4.getValue() != null ? moveDropdown4.getValue() : ""
        );

        if (pokemonName.isEmpty()) return;

        TeamPokemon newPokemon = new TeamPokemon(pokemonName, heldItem, moves, nature);
        team.add(newPokemon);
        teamLabel.setText(teamLabel.getText() + "\n" + newPokemon.getName()
                + " @ " + heldItem
                + " | ability: " + ability
                + " | nature: " + nature
                + " | moves: " + String.join(", ", moves));
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

    @FXML
    public void goBack() throws IOException {
        App.switchTo("main.fxml");
    }
}