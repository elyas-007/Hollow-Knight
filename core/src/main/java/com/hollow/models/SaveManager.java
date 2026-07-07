package com.hollow.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

public class SaveManager {
    private static final Json json = new Json();
    private static final String SAVE_DIR = "save/game";

    static {
        json.setOutputType(JsonWriter.OutputType.json);
    }

    public static void save(GameData data) {
        FileHandle file = Gdx.files.local(SAVE_DIR + ".json");
        file.writeString(json.prettyPrint(data), false);
    }

    public static GameData load() {
        FileHandle file = Gdx.files.local(SAVE_DIR + ".json");

        if (file.exists()) {
            try {
                return json.fromJson(GameData.class, file.readString());
            } catch (Exception e) {
                Gdx.app.error("SaveManager", "Error parsing save file. Generating new game data.", e);
                return new GameData();
            }
        }
        return new GameData();
    }
}
