package com.nikolai;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.util.stream.Collectors;

public class PokemonController {

    @FXML private TextField searchField;
    @FXML private Label nameLabel;
    @FXML private Label typesLabel;
    @FXML private Label statsLabel;
    @FXML private Label abilityLabel;
    @FXML private Label movesLabel;

    private final PokeApiService pokeApiService = new PokeApiService();

    @FXML
    public void goBack() throws IOException {
        App.switchTo("main.fxml");
    }

    @FXML
    public void onSearchButtonClick() {
        String searchInput = searchField.getText().trim();
        if (searchInput.isEmpty()) {
            nameLabel.setText("Please enter a Pokemon name or ID");
            return;
        };

        Task<Pokemon> task = new Task<>() {
            @Override
            protected Pokemon call() throws Exception {
                return pokeApiService.getPokemon(searchInput);
            }
        };

        task.setOnSucceeded(e -> {
            Pokemon pokemon = task.getValue();
            nameLabel.setText(pokemon.getName());

            String types = pokemon.getTypes().stream()
                    .map(t -> t.getType().getName())
                    .collect(Collectors.joining(", "));
            typesLabel.setText("TYPES: " + types);

            String stats = pokemon.getStats().stream()
                    .map(s -> s.getStat().getName() + ": " + s.getBaseStat())
                    .collect(Collectors.joining(", "));
            statsLabel.setText("STATS: " + stats);

            String abilities = pokemon.getAbilities().stream()
                    .map(a -> a.getAbility().getName())
                    .collect(Collectors.joining(", "));
            abilityLabel.setText("ABILITY: " + abilities);

            String moves = pokemon.getMoves().stream()
                    .map(m -> m.getMove().getName())
                    .collect(Collectors.joining(", "));
            movesLabel.setText("AVAILABLE MOVES: " + moves);
        });

        task.setOnFailed(e -> {
            System.err.println("Failed to fetch pokemon: " + task.getException());
            nameLabel.setText("Pokemon not found");
        });

        new Thread(task).start();
    }
}