package com.hollow.views.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.hollow.HollowKnight;
import com.hollow.loader.TiledMapHelper;
import com.hollow.controllers.manager.SaveManager;
import com.hollow.models.entities.knight.Knight;

public class LoadingScreen implements Screen {
    private final HollowKnight game;
    private final String nextMap;
    private String actualMapPath;
    private SpriteBatch batch;
    private Animation<TextureRegion> loadingAnimation;
    private float stateTime;

    private AssetManager assetManager;

    private Knight existingKnight;
    private boolean existingInstaKill;

    private float timer = 0f;
    private static final float MINIMUM_LOAD_TIME = 1.5f;

    private boolean useCustomSpawn = false;
    private float spawnX = 0f;
    private float spawnY = 0f;

    public LoadingScreen(HollowKnight game, String nextMap) {
        this.game = game;
        this.nextMap = nextMap;
        this.useCustomSpawn = false;
        this.existingKnight = null;
        this.existingInstaKill = false;
        init();
    }

    public LoadingScreen(HollowKnight game, String nextMap,
                         float spawnX, float spawnY,
                         Knight knight, boolean instaKill) {
        this.game = game;
        this.nextMap = nextMap;
        this.useCustomSpawn = true;

        this.spawnX = spawnX;
        this.spawnY = spawnY;

        this.existingKnight = knight;
        this.existingInstaKill = instaKill;

        init();
    }

    private void init() {
        this.batch = new SpriteBatch();

        if (nextMap.equals("CROSSROAD")) {
            actualMapPath = "map/cross_road.tmx";
        } else {
            actualMapPath = "map/green_path.tmx";
        }

        assetManager = new AssetManager();
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetManager.load(actualMapPath, TiledMap.class);

        setupAnimation();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);

        stateTime += delta;
        timer += delta;

        boolean isMapLoaded = assetManager.update();

        TextureRegion currentFrame = loadingAnimation.getKeyFrame(stateTime, true);

        batch.begin();

        float iconWidth = currentFrame.getRegionWidth() * 0.5f;
        float iconHeight = currentFrame.getRegionHeight() * 0.5f;

        float drawX = Gdx.graphics.getWidth() - iconWidth - 50;
        float drawY = 50;

        batch.draw(currentFrame, drawX, drawY, iconWidth, iconHeight);
        batch.end();

        if (timer >= MINIMUM_LOAD_TIME && isMapLoaded) {
            TiledMap preloadedMap = assetManager.get(actualMapPath, TiledMap.class);

            game.data.getActiveSlot().setLocation(nextMap);
            SaveManager.save(game.data);

            if (useCustomSpawn) {
                TiledMapHelper helper = new TiledMapHelper();
                Vector2 newSpawn = helper.findCustomSpawnPoint(preloadedMap, 1f / 64f);

                if (newSpawn != null) {
                    game.setScreen(new GameScreen(game, actualMapPath, newSpawn.x, newSpawn.y, preloadedMap, existingKnight, existingInstaKill));
                } else {
                    game.setScreen(new GameScreen(game, actualMapPath, spawnX, spawnY, preloadedMap, existingKnight, existingInstaKill));
                }
            } else {
                game.setScreen(new GameScreen(game, nextMap, preloadedMap, existingKnight, existingInstaKill));
            }
        }
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (assetManager != null) assetManager.dispose();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    private void setupAnimation() {
        Texture loadingSheet = new Texture(Gdx.files.internal(
            "effect/SpriteAtlasTexture-Load_Icon_Knight-512x256-fmt12.png"));

        int frameCols = 4;
        int frameRows = 2;

        TextureRegion[][] tmp = TextureRegion.split(loadingSheet,
            loadingSheet.getWidth() / frameCols,
            loadingSheet.getHeight() / frameRows);

        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < frameRows; i++) {
            for (int j = 0; j < frameCols; j++) {
                frames.add(tmp[i][j]);
            }
        }
        loadingAnimation = new Animation<>(0.05f, frames, Animation.PlayMode.LOOP);
        stateTime = 0f;
    }
}
