package com.nikolai;

import javafx.fxml.FXML;

public class MainController {

    @FXML
    public void showPokemonSearch() throws Exception {
        App.switchTo("pokemon.fxml");
    }

    @FXML
    public void createTeam() throws Exception {
        App.switchTo("teamCreator.fxml");
    }

    @FXML
    public void loadTeam() throws Exception {
        App.switchTo("teamLoader.fxml");
    }
}