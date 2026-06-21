package com.hollow;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.hollow.assets.AssetLoader;
import com.hollow.models.GameData;
import com.hollow.models.GameSettings;
import com.hollow.models.SaveManager;
import com.hollow.views.screens.MainMenuScreen;

public class HollowKnight extends Game {
    public SpriteBatch batch;
    public AssetLoader assetLoader;
    public GameSettings settings;
    public GameData activeSave;


    @Override
    public void create() {
        batch = new SpriteBatch();
        assetLoader = new AssetLoader(this);
        assetLoader.loadMainMenu();
        settings = GameSettings.load();

        Pixmap pixmap = new Pixmap(Gdx.files.internal("icon and cursor/cursor_new_resized.png"));

        int xHotspot = 0;
        int yHotspot = 0;
        Cursor customCursor = Gdx.graphics.newCursor(pixmap, xHotspot, yHotspot);
        Gdx.graphics.setCursor(customCursor);
        pixmap.dispose();

        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        float safeDelta = Math.min(delta, 1f / 30f);

        if (screen != null)
            screen.render(safeDelta);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (settings != null) settings.save();
        if (activeSave != null) SaveManager.save(activeSave);
        batch.dispose();
        assetLoader.dispose();
   }

    public void playSound(Sound sound) {
        if (settings.isSfxOn && sound != null) {
            sound.play();
        }
    }
}
