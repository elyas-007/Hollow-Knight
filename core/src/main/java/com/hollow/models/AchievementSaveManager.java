package com.hollow.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class AchievementSaveManager {
    private static final String FILE_PATH = "save/global_achievements.json";
    private static final Json json = new Json();

    public static void save(AchievementData data) {
        FileHandle file = Gdx.files.local(FILE_PATH);
        file.writeString(json.toJson(data), false);
    }

    public static AchievementData load() {
        FileHandle file = Gdx.files.local(FILE_PATH);
        if (file.exists()) {
            return json.fromJson(AchievementData.class, file.readString());
        }
        return new AchievementData();
    }
}
