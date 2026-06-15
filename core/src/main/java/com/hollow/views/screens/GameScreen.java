package com.hollow.views.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hollow.HollowKnight;
import com.hollow.assets.KnightAnimationLoader;
import com.hollow.assets.TiledMapHelper;
import com.hollow.models.Game;
import com.hollow.models.SolidBlock;
import com.hollow.models.entities.Knight.Knight;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.Color;

public class GameScreen implements Screen {
    private static final float VIEWPORT_WIDTH  = 20f;
    private static final float VIEWPORT_HEIGHT = 11.25f;
    private static final float UNIT_SCALE      = 1f / 64f;
    private static final float CAM_LERP = 0.08f;

    private final HollowKnight game;
    private Knight knight;
    private Game controller;

    private OrthographicCamera camera;
    private FitViewport viewport;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private Array<SolidBlock> groundRecs;
    private Array<SolidBlock> spikeRecs;

    private float mapPixelWidth;
    private float mapPixelHeight;

    private TiledMapHelper helper;
    private final int[] mainLayer = {1};

    public GameScreen(HollowKnight game) {
        this.game = game;
    }


    @Override
    public void show() {
        helper = new TiledMapHelper();
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera);

        map = helper.loadMap("map/test.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, UNIT_SCALE);

        int tileWidth = map.getProperties().get("tilewidth", Integer.class);
        int tileHeight = map.getProperties().get("tileheight", Integer.class);
        int mapCols = map.getProperties().get("width", Integer.class);
        int mapRows = map.getProperties().get("height", Integer.class);
        mapPixelWidth = mapCols * tileWidth * UNIT_SCALE;
        mapPixelHeight = mapRows * tileHeight * UNIT_SCALE;

        Vector2 spawnPoint = findSpawnPoint();
        knight = new Knight(spawnPoint.x, spawnPoint.y);

        KnightAnimationLoader.loadAllAnimations(knight);

        camera.position.set(spawnPoint.x, spawnPoint.y, 0);
        camera.update();

        groundRecs = helper.getSolidRectangles();
        spikeRecs = helper.getSolidRectangles();
        controller = new Game(game, knight, groundRecs, spikeRecs);
    }

    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 1f, 0.3f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateCamera();
        renderer.setView(camera);
        renderer.render();
        controller.update(delta);
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        renderKnight(delta);
        game.batch.end();

    }

    @Override public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
    @Override public void pause() {

    }
    @Override public void resume() {

    }
    @Override public void hide() {

    }

    @Override
    public void dispose() {
        if (map  != null) map.dispose();
        if (renderer != null) renderer.dispose();
        // dispose animation
    }

    private Vector2 findSpawnPoint() {
        MapLayer layer = map.getLayers().get("logic");
        MapObject spawnPoint = layer.getObjects().get("spwan");

        float x = spawnPoint.getProperties().get("x", Float.class) * UNIT_SCALE;
        float y = spawnPoint.getProperties().get("y", Float.class) * UNIT_SCALE;

        return new Vector2(x, y);
    }

    private void renderKnight(float delta) {
        knight.updateAnimations(delta);

        var frame = knight.getCurrentFrame();
        if (frame == null)
            return;

        boolean facingLeft = !knight.isFacingRight();
        float x = knight.getX();
        float y = knight.getY();
        float hitboxW = knight.getWidth();
        float hitboxH = knight.getHeight();

        float scaleMultiplier = 2.5f;
        float spriteW = hitboxW * scaleMultiplier;
        float spriteH = hitboxH * scaleMultiplier;

        float offsetX = (spriteW - hitboxW) / 2f;
        float offsetY = (spriteH - hitboxH) / 2f;

        float drawX = x - offsetX;
        float drawY = y - offsetY;

        if (!facingLeft) {
            game.batch.draw(frame, drawX + spriteW, drawY, -spriteW, spriteH);
        } else {
            game.batch.draw(frame, drawX, drawY, spriteW, spriteH);
        }
    }

    private void updateCamera() {
        float knightX = knight.getX() + knight.getWidth()  / 2f;
        float knightY = knight.getY() + knight.getHeight() / 2f;

        float targetX = camera.position.x + (knightX - camera.position.x) * CAM_LERP;
        float targetY = camera.position.y + (knightY - camera.position.y) * CAM_LERP;

        float halfW = VIEWPORT_WIDTH  / 2f;
        float halfH = VIEWPORT_HEIGHT / 2f;
        targetX = Math.max(halfW,  Math.min(mapPixelWidth  - halfW,  targetX));
        targetY = Math.max(halfH,  Math.min(mapPixelHeight - halfH, targetY));

        camera.position.set(targetX, targetY, 0);
        camera.update();
    }
}
