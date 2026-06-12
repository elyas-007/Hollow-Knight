package com.hollow;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.hollow.assets.AssetLoader;
import com.hollow.views.screens.MainMenuScreen;

public class HollowKnight extends Game {
    public SpriteBatch batch;
    public AssetLoader assetLoader;

    @Override
    public void create() {
        batch = new SpriteBatch();
        assetLoader = new AssetLoader(this);
        assetLoader.loadMainMenu();
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
        batch.dispose();
        assetLoader.dispose();
   }
}
