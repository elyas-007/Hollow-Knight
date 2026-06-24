package com.hollow.models;

import com.badlogic.gdx.utils.Array;
import com.hollow.HollowKnight;
import com.hollow.models.entities.Knight.Charm;

public class GameData {
    public int slot;
    public boolean isEmpty = true;
    public String location = null; //CROSSROAD OR GREENPATH
    public int geo = 0;
    public int mask = 0;
    public int playTime = 0; // minute
    public boolean falseKnightDefeated = false;
    public float falseKnightDeathX = 0f;
    public float falseKnightDeathY = 0f;

    public Array<Charm> unlockedCharms = new Array<>();
    public Array<Charm> equippedCharms = new Array<>();

    public GameData() {
    }

    public GameData(int slot) {
        this.slot = slot;
        this.location = "CROSSROAD";
        this.geo = 0;
        this.mask = 5;
        this.playTime = 0;

        unlockedCharms.addAll(
            Charm.SOUL_CATCHER, Charm.DASH_MASTER, Charm.UNBREAKABLE_STRENGTH,
            Charm.QUICK_SLASH, Charm.QUICK_FOCUS, Charm.HEAVY_BLOW,
            Charm.SHARP_SHADOW, Charm.VOID_HEART
        );
    }
}
