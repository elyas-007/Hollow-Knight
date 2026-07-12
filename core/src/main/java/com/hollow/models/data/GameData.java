package com.hollow.models.data;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.hollow.controllers.manager.SaveManager;
import com.hollow.models.achievement.AchievementData;

public class GameData {
    private SettingData settings;
    private AchievementData achievements;
    private final IntMap<SlotData> slots = new IntMap<>();
    private final Array<String> killedEnemyTypes = new Array<>();
    private int activeSlotId;

    public GameData() {
        settings = new SettingData();
        achievements = new AchievementData();
        for (int i = 1; i <= 4; i++) {
            slots.put(i, new SlotData(i));
        }
    }

    public void clearSlot(int id) {
        slots.put(id, new SlotData(id));
        SaveManager.save(this);
    }

    public void registerEnemyKill(String enemyType) {
        if (!killedEnemyTypes.contains(enemyType, false)) {
            killedEnemyTypes.add(enemyType);
        }
    }

    public SettingData getSettings() {return settings;}
    public void setSettings(SettingData settings) {this.settings = settings;}
    public AchievementData getAchievements() {return achievements;}
    public void setAchievements(AchievementData achievements) {this.achievements = achievements;}
    public SlotData getSlot(int id) {return slots.get(id);}
    public int getActiveSlotId() { return activeSlotId; }
    public void setActiveSlotId(int activeSlotId) { this.activeSlotId = activeSlotId; }
    public SlotData getActiveSlot() {return slots.get(activeSlotId);}
    public int getTotalEnemyKilled() {return killedEnemyTypes.size;}
    public Array<String> getKilledEnemyTypes() {return killedEnemyTypes;}
}
