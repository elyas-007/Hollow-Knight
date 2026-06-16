package com.hollow.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class SaveManager {
    private static final Json json = new Json();
    private static final String SAVE_DIR = "save/slot";

    public static void save(GameData data) {
        data.isEmpty = false;
        FileHandle file = Gdx.files.local(SAVE_DIR + data.slot + ".json");

        file.writeString(json.prettyPrint(data), false);
    }

    public static GameData load(int slot) {
        FileHandle file = Gdx.files.local(SAVE_DIR + slot + ".json");

        if (file.exists()) {
            return json.fromJson(GameData.class, file.readString());
        }

        GameData empty = new GameData();
        empty.slot = slot;
        return empty;
    }

    public static void clearSave(int slot) {
        FileHandle file = Gdx.files.local(SAVE_DIR + slot + ".json");
        if (file.exists())
            file.delete();
    }
}
