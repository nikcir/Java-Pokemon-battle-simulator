package com.nikolai.ui;

import javafx.scene.image.Image;

public class Sprite {
    // Class to hold sprite URLs and provide methods to get JavaFX Image objects.
    private String front_default;
    private String front_shiny;
    private String front;
    
    private String back_default;
    private String back_shiny;
    private String back;

    public Image getFrontDefault() { 
        if (front_default == null || front_default.isEmpty()) {
            return null;
        }
        return new Image(front_default, 96, 96, true, true);
    }
    
    public Image getFrontShiny() { 
        if (front_shiny == null || front_shiny.isEmpty()) {
            return null;
        }
        return new Image(front_shiny, 96, 96, true, true);
    }

    public Image getBackDefault() { 
        if (back_default == null || back_default.isEmpty()) {
            return null;
        }
        return new Image(back_default, 96, 96, true, true);
    }
    
    public Image getBackShiny() { 
        if (back_shiny == null || back_shiny.isEmpty()) {
            return null;
        }
        return new Image(back_shiny, 96, 96, true, true);
    }

    public Image getFront() { 
        if (front == null || front.isEmpty()) {
            return null;
        }
        return new Image(front, 96, 96, true, true);
    }

    public Image getBack() { 
        if (back == null || back.isEmpty()) {
            return null;
        }
        return new Image(back, 96, 96, true, true);
    }

    public void setFront(String url) { this.front = url; }
    public void setBack(String url) { this.back = url; }

    public String getFrontDefaultUrl() { return front_default; }
    public String getFrontShinyUrl() { return front_shiny; }
    public String getBackDefaultUrl() { return back_default; }
    public String getBackShinyUrl() { return back_shiny; }
}