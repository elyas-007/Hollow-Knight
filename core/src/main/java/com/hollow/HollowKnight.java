package com.hollow;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.hollow.loader.AssetLoader;
import com.hollow.controllers.manager.AchievementManager;
import com.hollow.controllers.manager.AudioManager;
import com.hollow.controllers.manager.LanguageManager;
import com.hollow.controllers.manager.SaveManager;
import com.hollow.models.data.GameData;
import com.hollow.views.ui.menu.MenuBackground;
import com.hollow.views.screen.MainMenuScreen;

public class HollowKnight extends Game {
    public SpriteBatch batch;
    public AssetLoader assetLoader;
    public AudioManager audioManager;
    public AchievementManager achievementManager;
    public LanguageManager languageManager;
    public GameData data;
    public static final int SCREEN_WIDTH = 1920;
    public static final int SCREEN_HEIGHT = 1080;

    public MenuBackground menuBackground;
    public ShapeRenderer shapeRenderer;

    @Override
    public void create() {
        data = SaveManager.load();
        AssetLoader.init(this);
        AudioManager.init(this);
        AchievementManager.init(this);
        assetLoader = AssetLoader.getInstance();
        assetLoader.loadAssets();
        audioManager = AudioManager.getInstance();
        achievementManager = AchievementManager.getInstance();
        languageManager = LanguageManager.getInstance();
        languageManager.load(data.getSettings().getLang());

        setCursor();
        shapeRenderer = new ShapeRenderer();

        menuBackground = new MenuBackground(assetLoader, this);
        batch = new SpriteBatch();
        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        float safeDelta = Math.min(delta, 1f / 30f);

        if (screen != null)
            screen.render(safeDelta);

        applyGlobalBrightness();
    }

    private void applyGlobalBrightness() {
        if (data == null || data.getSettings() == null) return;

        float brightness = data.getSettings().getBrightness();
        if (brightness >= 0.99f) return;

        float darknessAlpha = 1f - brightness;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, darknessAlpha);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (data != null) SaveManager.save(data);
        if (batch != null) batch.dispose();
        if (assetLoader != null) assetLoader.dispose();
        if (audioManager != null) audioManager.dispose();
        if (menuBackground != null) menuBackground.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
   }

   private void setCursor() {
       Pixmap pixmap = new Pixmap(Gdx.files.internal("icon and cursor/cursor_new_resized.png"));

       int xHotspot = 0;
       int yHotspot = 0;
       Cursor customCursor = Gdx.graphics.newCursor(pixmap, xHotspot, yHotspot);
       Gdx.graphics.setCursor(customCursor);
       pixmap.dispose();
   }
}
