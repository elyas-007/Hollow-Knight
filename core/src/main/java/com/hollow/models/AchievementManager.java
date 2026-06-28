package com.hollow.models;

import com.badlogic.gdx.utils.Array;

public class AchievementManager {
    private static AchievementManager instance;
    private Array<AchievementObserver> observers;
    private AchievementData globalData;


    private AchievementManager() {
        observers = new Array<>();
        globalData = AchievementSaveManager.load();
    }


    public static AchievementManager getInstance() {
        if (instance == null)
            instance = new AchievementManager();

        return instance;
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
            AchievementSaveManager.save(globalData);

            notifyAchievementUnlocked(achievement);
        }
    }
}
