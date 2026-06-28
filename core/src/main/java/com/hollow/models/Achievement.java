package com.hollow.models;

public enum Achievement {
    COMPLETION("Completion", "Finish the game"),
    SPEEDRUN("Speedrun", "Finish under target time"),
    TRUE_HUNTER("True Hunter", "Kill all enemy types"),
    DEFEAT_FALSE_KNIGHT("Defeat Boss", "Defeat the first boss"),
    SHADOW_MASTER("Shadow Master", "Cast a shadow spell");

    public final String title;
    public final String dec;

    Achievement(String title, String dec) {
        this.title = title;
        this.dec = dec;
    }
}
