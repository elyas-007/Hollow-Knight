package com.hollow.models;

import com.hollow.HollowKnight;

public class GameData {
    public int slot;
    public boolean isEmpty = true;
    public String location = null; //CROSSROAD OR GREENPATH
    public int geo = 0;
    public int mask = 0;
    public int playTime = 0; // minute

    public GameData() {
    }

    public GameData(int slot) {
        this.slot = slot;
        this.location = "CROSSROAD";
        this.geo = 0;
        this.mask = 5;
        this.playTime = 0;
    }
}
