package com.hollow.models;

import com.badlogic.gdx.utils.Array;
import com.hollow.models.entities.Knight.Charm;

public class SlotData {
    private int id;
    private boolean isEmpty = true;
    private String location = null; //CROSSROAD OR GREENPATH
    private int mask = 0;
    private boolean falseKnightDefeated = false;
    private float falseKnightDeathX = 0f;
    private float falseKnightDeathY = 0f;
    private final Array<Charm> unlockedCharms = new Array<>();
    private final Array<Charm> equippedCharms = new Array<>();
    private final Array<String> killedEnemyTypes = new Array<>();
    private float playTime = 0; // sec


    public SlotData() {}

    public SlotData(int id) {
        this.id = id;
        this.location = "CROSSROAD";
        this.mask = 5;
        this.playTime = 0;

        unlockedCharms.addAll(
            Charm.SOUL_CATCHER, Charm.DASH_MASTER, Charm.UNBREAKABLE_STRENGTH,
            Charm.QUICK_SLASH, Charm.QUICK_FOCUS, Charm.HEAVY_BLOW,
            Charm.SHARP_SHADOW
        );
    }

    public void addUnlockedCharm(Charm charm) {
        unlockedCharms.add(charm);
    }

    public  void addEquippedCharm(Charm charm) {
        equippedCharms.add(charm);
    }

    public void removeEquippedCharm(Charm charm) {
        equippedCharms.removeValue(charm, true);
    }

    public void registerEnemyKill(String enemyType) {
        if (!killedEnemyTypes.contains(enemyType, false)) {
            killedEnemyTypes.add(enemyType);
        }
    }

    public int getId() {return id;}
    public boolean isEmpty() {return isEmpty;}
    public void setEmpty(boolean empty) {isEmpty = empty;}
    public String getLocation() {return location;}
    public void setLocation(String location) {this.location = location;}
    public int getMask() {return mask;}
    public void setMask(int mask) {this.mask = mask;}
    public boolean isFalseKnightDefeated() {return falseKnightDefeated;}
    public void setFalseKnightDefeated(boolean falseKnightDefeated) {this.falseKnightDefeated = falseKnightDefeated;}
    public float getFalseKnightDeathX() {return falseKnightDeathX;}
    public void setFalseKnightDeathX(float falseKnightDeathX) {this.falseKnightDeathX = falseKnightDeathX;}
    public float getFalseKnightDeathY() {return falseKnightDeathY;}
    public void setFalseKnightDeathY(float falseKnightDeathY) {this.falseKnightDeathY = falseKnightDeathY;}
    public Array<Charm> getUnlockedCharms() {return unlockedCharms;}
    public Array<Charm> getEquippedCharms() {return equippedCharms;}
    public int getTotalEnemyKilled() {return killedEnemyTypes.size;}
    public Array<String> getKilledEnemyTypes() {return killedEnemyTypes;}
    public float getPlayTime() {return playTime;}
    public void setPlayTime(float playTime) {this.playTime = playTime;}
}
