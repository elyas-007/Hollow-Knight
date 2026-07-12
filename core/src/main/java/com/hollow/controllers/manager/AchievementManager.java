package com.hollow.controllers.manager;

import com.badlogic.gdx.utils.Array;
import com.hollow.HollowKnight;
import com.hollow.models.achievement.Achievement;
import com.hollow.models.achievement.AchievementData;
import com.hollow.models.achievement.AchievementObserver;

public class AchievementManager {
    private static HollowKnight game;
    private static AchievementManager instance;
    private Array<AchievementObserver> observers;
    private AchievementData globalData;


    private AchievementManager() {
        observers = new Array<>();
        globalData = game.data.getAchievements();
    }


    public static AchievementManager getInstance() {
        if (instance == null) {
            throw new RuntimeException("Achievement Manager not initialized! Call init() first.");
        }
        return instance;
    }

    public static void init(HollowKnight hollowGame) {
        if (instance == null) {
            game = hollowGame;
        }
        instance = new AchievementManager();
    }

    public void addObserver(AchievementObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(AchievementObserver observer) {
        observers.removeValue(observer, true);
    }

    public void notifyAchievementUnlocked(Achievement a) {
        for (AchievementObserver o : observers)
            o.onAchievementsUnlocked(a);
    }

    public boolean isUnlocked(Achievement achievement) {
        return globalData.unlockedAchievements.contains(achievement.name(), false);
    }

    public void unlockAchievement(Achievement achievement) {
        if (!isUnlocked(achievement)) {
            globalData.unlockedAchievements.add(achievement.name());
            SaveManager.save(game.data);
            notifyAchievementUnlocked(achievement);
        }
    }
}
