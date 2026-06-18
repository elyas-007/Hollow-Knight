package com.hollow.models;

import com.badlogic.gdx.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.hollow.models.enums.Language;

public class GameSettings {
    public float musicVolume = 0.5f;
    public float brightness = 0.6f;
    public boolean isMusicOn = true;
    public boolean isSfxOn = true;

    public Language lang = Language.EN;

    public int keyLeft = Input.Keys.LEFT;
    public int keyRight = Input.Keys.RIGHT;
    public int keyUp = Input.Keys.UP;
    public int keyDown = Input.Keys.DOWN;
    public int keyJump = Input.Keys.Z;
    public int keyDash = Input.Keys.C;
    public int keyAttack =  Input.Keys.X;
    public int keyFocus=  Input.Keys.A;



    private static final String PATH = "save/settings.json";

    public void save() {
        try {
            Json json = new Json();
            String data = json.prettyPrint(this);
            Gdx.files.local(PATH).writeString(data, false);
        } catch (Exception e) {
            Gdx.app.error("Game Settings", "Could not save settings to file");
        }
    }

    public static GameSettings load() {
        try {
            FileHandle fh = Gdx.files.local(PATH);
            if (fh.exists()) {
                Json json = new Json();
                return json.fromJson(GameSettings.class, fh.readString());
            }
        } catch (Exception e) {
            Gdx.app.error("GameSettings", "Failed to load: " + e.getMessage());
        }
        return new GameSettings();
    }

    public void resetAudio() {
        musicVolume = 0.5f;
        brightness = 1.0f;
        isMusicOn = true;
        isSfxOn = true;
    }

    public void resetKey() {
        keyLeft = Input.Keys.LEFT;
        keyRight = Input.Keys.RIGHT;
        keyUp = Input.Keys.UP;
        keyDown = Input.Keys.DOWN;
        keyJump = Input.Keys.Z;
        keyDash = Input.Keys.C;
        keyAttack =  Input.Keys.X;
        keyFocus=  Input.Keys.A;
    }

    public void resetBrightness() {
        brightness = 0.6f;
    }
}
