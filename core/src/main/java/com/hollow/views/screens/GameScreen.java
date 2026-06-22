package com.hollow.views.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.hollow.assets.EnemyAnimationLoader;
import com.hollow.assets.KnightAnimationLoader;
import com.hollow.assets.TiledMapHelper;
import com.hollow.models.Game;
import com.hollow.models.GameData;
import com.hollow.models.SolidBlock;
import com.hollow.models.TransitionZone;
import com.hollow.models.entities.Enemy.Tiktik;
import com.hollow.models.entities.Knight.Knight;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.Color;
import com.hollow.models.enums.KnightState;
import com.hollow.views.hud.GameHud;

public class GameScreen implements Screen {
    private static final float VIEWPORT_WIDTH  = 20f;
    private static final float VIEWPORT_HEIGHT = 11.25f;
    private static final float UNIT_SCALE      = 1f / 64f;
    private static final float CAM_LERP = 0.08f;

    private final HollowKnight game;
    private Knight knight;
    private Game controller;
    private GameHud hud;

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

    private ShapeRenderer shapeRenderer;

    private String mapPath;
    private Vector2 customSpawn;
    private boolean isFadingOut = false;
    private boolean isFadingIn;
    private float fadeAlpha;
    private String nextMap;
    private TransitionZone transitionZone;


    public GameScreen(HollowKnight game, String mapPath, float spawnX, float spawnY) {
        this.game = game;
        this.mapPath = mapPath;
        this.customSpawn = new Vector2(spawnX, spawnY);
    }

    public GameScreen(HollowKnight game, String mapPath) {
        this.game = game;
        if (mapPath.equals("CROSSROAD")) this.mapPath = "map/cross_road.tmx";
        else this.mapPath = "map/green_path.tmx";
        this.customSpawn = null;
    }


    @Override
    public void show() {
        helper = new TiledMapHelper();
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera);

        isFadingIn = true;
        fadeAlpha = 1f;
        shapeRenderer = new ShapeRenderer();

        map = helper.loadMap(this.mapPath);
        renderer = new OrthogonalTiledMapRenderer(map, UNIT_SCALE);

        int tileWidth = map.getProperties().get("tilewidth", Integer.class);
        int tileHeight = map.getProperties().get("tileheight", Integer.class);
        int mapCols = map.getProperties().get("width", Integer.class);
        int mapRows = map.getProperties().get("height", Integer.class);
        mapPixelWidth = mapCols * tileWidth * UNIT_SCALE;
        mapPixelHeight = mapRows * tileHeight * UNIT_SCALE;

        Vector2 spawnPoint;
        if (this.customSpawn != null) {
            spawnPoint = findCustomSpawnPoint();
        } else {
            spawnPoint = findSpawnPoint();
        }
        knight = new Knight(spawnPoint.x, spawnPoint.y);

        KnightAnimationLoader.loadAllAnimations(knight);

        float knightStartX = knight.getX() + knight.getWidth() / 2f;
        float knightStartY = knight.getY() + knight.getHeight() / 2f;
        float halfW = VIEWPORT_WIDTH / 2f;
        float halfH = VIEWPORT_HEIGHT / 2f;
        float camStartX = Math.max(halfW, Math.min(mapPixelWidth - halfW, knightStartX));
        float camStartY = Math.max(halfH, Math.min(mapPixelHeight - halfH, knightStartY));
        camera.position.set(camStartX, camStartY, 0);
        camera.update();

        groundRecs = helper.getSolidRectangles();
        spikeRecs = helper.getSolidRectangles();
        transitionZone = helper.getTransitionZone(map, UNIT_SCALE);
        Array<Tiktik> mapTiktiks = helper.getTiktikSpawns(map, UNIT_SCALE);
        for (Tiktik t : mapTiktiks) {
            EnemyAnimationLoader.loadTiktikAnimations(t);
        }

        controller = new Game(game, knight, groundRecs, spikeRecs, mapTiktiks, transitionZone, this, game.activeSave);

        hud = new GameHud(game, knight);
    }

    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new PauseScreen(game, this));
            return;
        }


        updateCamera();
        controller.update(delta);
        knight.updateAnimations(delta);

        drawWorld();

        renderDebugHitboxes();

        hud.update(knight, delta);
        hud.draw();

        handleFadeEffect(delta);
    }

    @Override public void resize(int width, int height) {
        viewport.update(width, height, false);
        if (hud !=  null)
            hud.resize(width, height);
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
        if (hud != null) hud.dispose();
        // dispose animation
    }

    public void drawWorld() {
        Gdx.gl.glClearColor(0f, 0f, 1f, 0.3f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setView(camera);
        renderer.render();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        renderEnemies();
        renderKnight();
        renderEffects();
        game.batch.end();
    }

    public Vector2 findSpawnPoint() {
        MapLayer layer = map.getLayers().get("logic");
        MapObject spawnPoint = layer.getObjects().get("spwanPoint");

        float x = spawnPoint.getProperties().get("x", Float.class) * UNIT_SCALE;
        float y = spawnPoint.getProperties().get("y", Float.class) * UNIT_SCALE;

        return new Vector2(x, y);
    }

    private void renderKnight() {
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
        float offsetY = 0.15f;

        float drawX = x - offsetX;
        float drawY = y - offsetY;

        if (knight.getState() == KnightState.WALL_SLIDE) {
            float wallCorrection = 0.4f;
            if (!facingLeft) {
                drawX += wallCorrection;
            } else {
                drawX -= wallCorrection;
            }
        }

        if (!facingLeft) {
            game.batch.draw(frame, drawX + spriteW, drawY, -spriteW, spriteH);
        } else {
            game.batch.draw(frame, drawX, drawY, spriteW, spriteH);
        }
    }

    private void renderEnemies() {
        if (controller.getTiktiks() == null) return;

        for (var enemy : controller.getTiktiks()) {
            var frame = enemy.getCurrentFrame();
            if (frame == null) continue;

            float x = enemy.position.x;
            float y = enemy.position.y;
            float hitboxW = enemy.hitbox.width;

            float spriteW = 1.4f;
            float spriteH = 0.9f;

            float offsetX = (spriteW - hitboxW) / 2f;
            float offsetY = 0.12f;

            float drawX = x - offsetX;
            float drawY = y - offsetY;

            if (enemy.isFacingRight) {
                game.batch.draw(frame, drawX + spriteW, drawY, -spriteW, spriteH);
            } else {
                game.batch.draw(frame, drawX, drawY, spriteW, spriteH);
            }
        }
    }

    private void updateCamera() {
        float knightX = knight.getX() + knight.getWidth()  / 2f;
        float knightY = knight.getY() + knight.getHeight() / 2f;

        float cameraOffsetY = 0f;
        if (knight.getState() == KnightState.LOOK_UP) {
            cameraOffsetY = 4f;
        } else if (knight.getState() == KnightState.LOOK_DOWN) {
            cameraOffsetY = -4f;
        }

        float targetX = camera.position.x + (knightX - camera.position.x) * CAM_LERP;
        float targetY = camera.position.y + ((knightY + cameraOffsetY) - camera.position.y) * CAM_LERP;

        float halfW = VIEWPORT_WIDTH  / 2f;
        float halfH = VIEWPORT_HEIGHT / 2f;
        targetX = Math.max(halfW,  Math.min(mapPixelWidth  - halfW,  targetX));
        targetY = Math.max(halfH,  Math.min(mapPixelHeight - halfH, targetY));

        camera.position.set(targetX, targetY, 0);
        camera.update();
    }

    private void renderDebugHitboxes() {
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GREEN);

        if (knight != null) {
            Rectangle kBox = knight.getHitbox();
            shapeRenderer.rect(kBox.x, kBox.y, kBox.width, kBox.height);
        }

        if (controller.getTiktiks() != null) {
            for (var enemy : controller.getTiktiks()) {
                Rectangle eBox = enemy.hitbox;
                shapeRenderer.rect(eBox.x, eBox.y, eBox.width, eBox.height);
            }
        }

        if (groundRecs != null) {
            for (SolidBlock ground : groundRecs) {
                if (!ground.isDeadly) {
                    Rectangle gBox = ground.bounds;
                    shapeRenderer.rect(gBox.x, gBox.y, gBox.width, gBox.height);
                }
            }
        }

        if (spikeRecs != null) {
            shapeRenderer.setColor(Color.RED);
            for (SolidBlock spike : spikeRecs) {
                if (spike.isDeadly) {
                    Rectangle sBox = spike.bounds;
                    shapeRenderer.rect(sBox.x, sBox.y, sBox.width, sBox.height);
                }
            }
        }

        shapeRenderer.end();
    }

    private void renderEffects() {
        for (var effect : knight.activeEffects) {
            var frame = effect.getCurrentFrame();

            if (frame == null)
                continue;

            float drawX = knight.getX() + effect.offsetX;
            float drawY = knight.getY() + effect.offsetY;
            float w = effect.width;
            float h = effect.height;

            if (effect.isFacingRight) {
                game.batch.draw(frame, drawX, drawY, w, h);
            } else {
                game.batch.draw(frame, drawX + w, drawY, -w, h);
            }
        }
    }


    public void startTransition(String targetMap) {
        if (!isFadingOut) {
            this.nextMap = targetMap;
            this.isFadingOut = true;
        }
    }

    private void handleFadeEffect(float delta) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if (isFadingIn) {
            fadeAlpha -= delta * 1.5f;
            if (fadeAlpha <= 0f) {
                fadeAlpha = 0f;
                isFadingIn = false;
            }
        } else if (isFadingOut) {
            fadeAlpha += delta * 1.5f;
            if (fadeAlpha >= 1f) {
                fadeAlpha = 1f;
                game.setScreen(new LoadingScreen(game, nextMap));
            }
        }

        shapeRenderer.setColor(new Color(0, 0, 0, fadeAlpha));
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private Vector2 findCustomSpawnPoint() {
        MapLayer layer = map.getLayers().get("transition");
        MapObject spawnPoint = layer.getObjects().get("custom_spawn");

        float x = spawnPoint.getProperties().get("x", Float.class) * UNIT_SCALE;
        float y = spawnPoint.getProperties().get("y", Float.class) * UNIT_SCALE;

        return new Vector2(x, y);
    }
}
