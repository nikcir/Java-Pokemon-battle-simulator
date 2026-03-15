package com.nikolai;

public class Move {
    private int accuracy;
    private int power;
    private int pp;
    private int priority;
    private String name;
    private Type type;
    private String effectEntry;

    public int getAccuracy() {
        return accuracy;
    }

    public int getPower() {
        return power;
    }

    public int getPp() {
        return pp;
    }

    public int getPriority() {
        return priority;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public String getEffectEntry() {
        return effectEntry;
    }

    public static class Type {
        private String name;
        public String getName() { return name; }
    }

    public static class EffectEntry {
        private String effect;
        public String getEffect() { return effect; }
    }
    

}
