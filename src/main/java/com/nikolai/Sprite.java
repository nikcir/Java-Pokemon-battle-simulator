package com.nikolai;

import javafx.scene.image.Image;

public class Sprite {
    private String front_default;
    private String front_shiny;

    public Image getFrontDefault() { return new Image(front_default); }
    public Image getFrontShiny() { return new Image(front_shiny); }
}