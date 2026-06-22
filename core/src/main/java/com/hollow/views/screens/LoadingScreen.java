package com.hollow.views.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.hollow.HollowKnight;

public class LoadingScreen implements Screen {
    private HollowKnight game;
    private String nextMap;

    private SpriteBatch batch;
    private Texture loadingSheet;
    private Animation<TextureRegion> loadingAnimation;
    private float stateTime;

    private float timer = 0f;
    private final float MINIMUM_LOAD_TIME = 1.5f;

    public LoadingScreen(HollowKnight game, String nextMap) {
        this.game = game;
        this.nextMap = nextMap;
        this.batch = new SpriteBatch();

        setupAnimation();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT);

        stateTime += delta;
        timer += delta;

        TextureRegion currentFrame = loadingAnimation.getKeyFrame(stateTime, true);

        batch.begin();

        float iconWidth = currentFrame.getRegionWidth() * 0.5f;
        float iconHeight = currentFrame.getRegionHeight() * 0.5f;

        float drawX = Gdx.graphics.getWidth() - iconWidth - 50;
        float drawY = 50;

        batch.draw(currentFrame, drawX, drawY, iconWidth, iconHeight);
        batch.end();

        if (timer >= MINIMUM_LOAD_TIME) {
            if (game.activeSave.location.equals("CROSSROAD"))
                game.activeSave.location = "GREENPATH";
            else
                game.activeSave.location = "CROSSROAD";
            game.setScreen(new GameScreen(game, nextMap, 0, 0));
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();

    }


    private void setupAnimation() {
        loadingSheet = new Texture(Gdx.files.internal("effect/SpriteAtlasTexture-LongSaveIcon-2048x1024-fmt12.png"));

        int frameCols = 8;
        int frameRows = 5;

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
