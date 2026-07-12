package com.hollow.views.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hollow.HollowKnight;
import com.hollow.loader.*;
import com.hollow.models.entities.enemy.Enemy;
import com.hollow.controllers.engine.Game;
import com.hollow.models.*;
import com.hollow.models.entities.boss.FalseKnight;
import com.hollow.models.entities.boss.IdleBehavior;
import com.hollow.models.entities.knight.Knight;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.Color;
import com.hollow.models.entities.zote.Zote;
import com.hollow.models.entities.knight.KnightState;
import com.hollow.views.render.BossRenderer;
import com.hollow.views.render.EntityRenderer;
import com.hollow.views.ui.hud.*;

public class GameScreen implements Screen {
    private static final float VIEWPORT_WIDTH = 20f;
    private static final float VIEWPORT_HEIGHT = 11.25f;
    public static final float UNIT_SCALE = 1f / 64f;
    private static final float CAM_LERP = 0.08f;

    private final HollowKnight game;
    public Knight knight;
    private Game controller;
    public GameHud hud;
    public EndingUI endingUI;

    private OrthographicCamera camera;
    private FitViewport viewport;
    public final TiledMap map;
    public TiledMapHelper helper;
    private OrthogonalTiledMapRenderer renderer;
    private EntityRenderer entityRenderer;

    private Array<SolidBlock> groundRecs;
    private Array<SolidBlock> spikeRecs;
    private Array<BreakableWall> breakableWalls;
    private Array<AmbientObject> ambientObjects;

    private float mapPixelWidth;
    private float mapPixelHeight;
    private ShapeRenderer shapeRenderer;

    private String mapPath;
    private Vector2 customSpawn;
    private boolean isFadingOut = false;
    private boolean isFadingIn;
    private float fadeAlpha;
    private String nextMap;
    private TransitionZone transitionZone;

    private FalseKnight falseKnight;
    private BossRenderer bossRenderer;

    public float arenaMinX, arenaMaxX, arenaY, arenaHeight;
    public boolean bossFightActive = false;

    private static float shakeTimer = 0f;
    private static float shakeIntensity = 0f;
    private float smoothShakeTime = 0f;

    public DialogueBox dialogueBox;
    public Zote zote;
    public InventoryUI inventoryUI;
    private PauseUI pauseUI;

    private boolean isInventoryOpen = false;
    public boolean isPaused = false;
    public InputMultiplexer multiplexer;

    private boolean isTransition;
    private boolean passedInstaKill = false;
    public boolean isEndingSequence = false;
    private boolean isWhiteFadingOut = false;
    private boolean isWhiteFadingIn = false;
    private float whiteFadeAlpha = 0f;

    public Vector2 endingSpawnPoint;
    public Rectangle endingStatueRect;
    public Rectangle speedrunRect;

    public GameScreen(HollowKnight game, String mapPath, float spawnX, float spawnY, TiledMap preloadedMap, Knight existingKnight, boolean instaKill) {
        this.game = game;
        this.mapPath = mapPath;
        this.customSpawn = new Vector2(spawnX, spawnY);
        this.map = preloadedMap;
        this.isTransition = true;
        this.knight = existingKnight;
        this.passedInstaKill = instaKill;
    }

    public GameScreen(HollowKnight game, String mapPath, TiledMap preloadedMap, Knight existingKnight, boolean instaKill) {
        this.game = game;
        if (mapPath.equals("CROSSROAD")) this.mapPath = "map/cross_road.tmx";
        else this.mapPath = "map/green_path.tmx";
        this.customSpawn = null;
        this.map = preloadedMap;
        this.isTransition = false;
        this.knight = existingKnight;
        this.passedInstaKill = instaKill;
    }


    @Override
    public void show() {
        initCameraAndMap();
        initPlayer();
        initEnemiesAndBoss();
        initUIAndAudio();
    }

    private void initCameraAndMap() {
        helper = new TiledMapHelper();
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera);
        isFadingIn = true;
        fadeAlpha = 1f;
        shapeRenderer = new ShapeRenderer();
        renderer = new OrthogonalTiledMapRenderer(map, UNIT_SCALE);
        entityRenderer = new EntityRenderer(game);

        int tileW = map.getProperties().get("tilewidth", Integer.class);
        int tileH = map.getProperties().get("tileheight", Integer.class);
        mapPixelWidth = map.getProperties().get("width", Integer.class) * tileW * UNIT_SCALE;
        mapPixelHeight = map.getProperties().get("height", Integer.class) * tileH * UNIT_SCALE;

        groundRecs = helper.getSolidRectangles(map, UNIT_SCALE);
        spikeRecs = helper.getSolidRectangles(map, UNIT_SCALE);
        breakableWalls = helper.getBreakableWall(map, UNIT_SCALE);
        groundRecs.addAll(breakableWalls);
        transitionZone = helper.getTransitionZone(map, UNIT_SCALE);
        ambientObjects = helper.getAmbientObjects(map, UNIT_SCALE, game.assetLoader);

        endingSpawnPoint = helper.getEndPointSpawn(map, UNIT_SCALE);
        endingStatueRect = helper.getEndingRoom(map, UNIT_SCALE);
        speedrunRect = helper.getSpeedrunRect(map, UNIT_SCALE);
    }

    private void initPlayer() {
        Vector2 spawnPoint = (customSpawn != null) ? customSpawn : helper.findSpawnPoint(map, UNIT_SCALE);
        if (knight == null) {
            knight = new Knight(spawnPoint.x, spawnPoint.y, game.data, game.audioManager);
        } else {
            knight.getPosition().set(spawnPoint.x, spawnPoint.y);
            knight.getHitbox().setPosition(spawnPoint.x, spawnPoint.y);
            knight.getVelocity().set(0, 0);
            knight.setOnGround(false);
            knight.state = KnightState.AIRBORNE;
            knight.activeEffects.clear();
        }
        knight.isGrassTerrain = mapPath.equals("map/green_path.tmx");
        KnightAnimationLoader.loadAllAnimations(knight);

        float camStartX = Math.max(VIEWPORT_WIDTH / 2f, Math.min(mapPixelWidth - VIEWPORT_WIDTH / 2f, knight.getX()));
        float camStartY = Math.max(VIEWPORT_HEIGHT / 2f, Math.min(mapPixelHeight - VIEWPORT_HEIGHT / 2f, knight.getY()));
        camera.position.set(camStartX, camStartY, 0);
        camera.update();
    }

    private void initEnemiesAndBoss() {
        Array<Enemy> mapEnemies = new Array<>();
        mapEnemies.addAll(helper.getTiktikSpawns(map, UNIT_SCALE));
        mapEnemies.addAll(helper.getCrawLidSpawn(map, UNIT_SCALE));
        mapEnemies.addAll(helper.getHuskHornHead(map, UNIT_SCALE));

        Array<com.hollow.models.entities.enemy.Mosquito> mapMosquito = helper.getMosquito(map, UNIT_SCALE);
        for (com.hollow.models.entities.enemy.Mosquito m : mapMosquito) { m.audioManager = game.audioManager; mapEnemies.add(m); }
        mapEnemies.addAll(helper.getMosscreep(map, UNIT_SCALE));
        mapEnemies.addAll(helper.getCrystallized(map, UNIT_SCALE));

        for (com.hollow.models.entities.enemy.Enemy e : mapEnemies) EnemyAnimationLoader.loadAnimationsFor(e); // فرض بر وجود یک لودر کلی

        if (mapPath.equals("map/cross_road.tmx")) {
            if (game.data.getActiveSlot().isFalseKnightDefeated()) {
                falseKnight = new FalseKnight(game.data.getActiveSlot().getFalseKnightDeathX(),
                    game.data.getActiveSlot().getFalseKnightDeathY(), game.audioManager);
                BossAnimationLoader.loadAllAnimations(falseKnight);
                falseKnight.setupAsCorpse();
            } else {
                Vector2 bossPos = helper.findBossSpawnPoint(map, UNIT_SCALE);
                falseKnight = new FalseKnight(bossPos.x, bossPos.y, game.audioManager);
                BossAnimationLoader.loadAllAnimations(falseKnight);
                falseKnight.changeBehavior(new IdleBehavior());
            }
            bossRenderer = new BossRenderer(game);
        }

        Rectangle rect = helper.getBossRoom(map, UNIT_SCALE);
        if (rect != null) { arenaMinX = rect.x; arenaMaxX = rect.x + rect.width; arenaY = rect.y; arenaHeight = rect.height; }

        Vector2 zoteVec = helper.findZoteSpawnPoint(map, UNIT_SCALE);
        if (zoteVec != null) {
            zote = new Zote(zoteVec.x, zoteVec.y);
            ZoteAnimationLoader.loadZoteAnimations(zote);
        }

        controller = new Game(game, knight, groundRecs, spikeRecs, mapEnemies, transitionZone,
            this, game.data, falseKnight, breakableWalls);
        controller.instaKillMode = this.passedInstaKill;
        controller.voidHeartPos = helper.getVoidHeartPos(map, UNIT_SCALE);
    }

    private void initUIAndAudio() {
        hud = new GameHud(game, knight);
        dialogueBox = new DialogueBox(game);
        inventoryUI = new InventoryUI(game, game.data);
        multiplexer = new InputMultiplexer();
        pauseUI = new PauseUI(game, this, multiplexer);
        endingUI = new EndingUI(game, this, multiplexer);
        Gdx.input.setInputProcessor(multiplexer);

        if (mapPath.equals("map/cross_road.tmx")) {
            game.audioManager.playDynamicMusic(null, game.audioManager.audioLoader.crossroadsMain,
                game.audioManager.audioLoader.crossroadsBass, isTransition);
        } else if (mapPath.equals("map/green_path.tmx")) {
            game.audioManager.playDynamicMusic(game.audioManager.audioLoader.greenpathAtmos,
                game.audioManager.audioLoader.greenpathMain, game.audioManager.audioLoader.greenpathBass, isTransition);
        }
        game.audioManager.setCombatState(false);
    }

    public void render(float delta) {
        handleInputs();
        updateCamera();

        if (bossFightActive && controller.boss != null && controller.boss.currentState != FalseKnight.State.DEATH) {
            game.audioManager.setCombatState(false);
        } else if (controller != null) {
            game.audioManager.setCombatState(controller.inCombat);
        }

        game.audioManager.update(delta);

        if (!isInventoryOpen && !isPaused) {
            controller.update(delta);
            knight.updateAnimations(delta);
            dialogueBox.update(delta);

            for (BreakableWall wall : breakableWalls) wall.update(delta);
            if (breakableWalls.isEmpty()) {
                MapLayer coverLayer = map.getLayers().get("secret_room_cover");
                if (coverLayer != null) coverLayer.setVisible(false);
            }
            for (AmbientObject obj : ambientObjects) obj.update(delta);
        }

        drawWorld();
        renderDebugHitboxes();

        hud.update(knight, delta);
        if (!isInventoryOpen) hud.draw();
        dialogueBox.draw();

        drawUIOverlays(delta);
        handleFadeEffect(delta);
        handleWhiteFadeEffect(delta);
    }

    private void handleInputs() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.I) && !isPaused) {
            isInventoryOpen = !isInventoryOpen;
            if (isInventoryOpen) multiplexer.addProcessor(inventoryUI.stage);
            else multiplexer.removeProcessor(inventoryUI.stage);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (isInventoryOpen) {
                isInventoryOpen = false;
                multiplexer.removeProcessor(inventoryUI.stage);
            } else togglePause();
        }
    }

    private void drawUIOverlays(float delta) {
        if (endingUI.isVisible || isInventoryOpen || isPaused) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, endingUI.isVisible ? 0.6f : 0.7f);
            shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);

            if (endingUI.isVisible) { endingUI.act(delta); endingUI.draw(); }
            else if (isInventoryOpen) { inventoryUI.act(delta); inventoryUI.draw(); }
            else if (isPaused) { pauseUI.act(delta); pauseUI.draw(); }
        }
    }

    public void drawWorld() {
        Gdx.gl.glClearColor(0.04f, 0.07f, 0.15f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setView(camera);
        renderer.render();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        if (ambientObjects != null) {
            game.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
            for (AmbientObject obj : ambientObjects) {
                TextureRegion frame = obj.getCurrentFrame();
                if (frame != null) {
                    if (obj.isFacingRight) game.batch.draw(frame, obj.x, obj.y, obj.width, obj.height);
                    else game.batch.draw(frame, obj.x + obj.width, obj.y, -obj.width, obj.height);
                }
            }
            game.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }

        entityRenderer.renderEnemies(controller);
        entityRenderer.renderZote(zote);
        entityRenderer.renderKnight(knight);
        if (falseKnight != null) bossRenderer.render(falseKnight, Gdx.graphics.getDeltaTime());

        entityRenderer.renderProjectiles(controller, knight);
        entityRenderer.renderEffects(knight);
        entityRenderer.renderWraiths(controller, knight);
        entityRenderer.renderWorldEffects(controller, Gdx.graphics.getDeltaTime());
        entityRenderer.renderInstantLasers(controller);
        entityRenderer.renderCollectibles(controller, breakableWalls.isEmpty());

        game.batch.end();
    }

    private void renderDebugHitboxes() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GREEN);

        if (knight != null) shapeRenderer.rect(knight.getHitbox().x, knight.getHitbox().y,
            knight.getHitbox().width, knight.getHitbox().height);

        if (controller.getEnemies() != null) {
            for (Enemy enemy : controller.getEnemies()) {
                shapeRenderer.rect(enemy.hitbox.x, enemy.hitbox.y, enemy.hitbox.width, enemy.hitbox.height);
            }
        }

        if (groundRecs != null) {
            for (SolidBlock ground : groundRecs) {
                if (!ground.isDeadly) shapeRenderer.rect(ground.bounds.x, ground.bounds.y,
                    ground.bounds.width, ground.bounds.height);
            }
        }

        if (spikeRecs != null) {
            shapeRenderer.setColor(Color.RED);
            for (SolidBlock spike : spikeRecs) {
                if (spike.isDeadly) shapeRenderer.rect(spike.bounds.x, spike.bounds.y,
                    spike.bounds.width, spike.bounds.height);
            }
        }

        if (controller.boss != null) {
            shapeRenderer.setColor(Color.MAGENTA);
            shapeRenderer.rect(controller.boss.hitbox.x, controller.boss.hitbox.y,
                controller.boss.hitbox.width, controller.boss.hitbox.height);
        }

        shapeRenderer.end();
    }

    private void updateCamera() {
        float targetX, targetY, targetZoom = 1f;

        if (isEndingSequence) {
            targetX = Math.max(VIEWPORT_WIDTH * 1.35f / 2f,
                Math.min(mapPixelWidth - VIEWPORT_WIDTH * 1.35f / 2f, knight.getX()));
            targetY = Math.max(VIEWPORT_HEIGHT * 1.35f / 2f,
                Math.min(mapPixelHeight - VIEWPORT_HEIGHT * 1.35f / 2f, knight.getY() + 3.0f));
            targetZoom = 1.35f;
        } else if (bossFightActive && controller.boss != null && controller.boss.currentState != FalseKnight.State.DEATH) {
            targetX = arenaMinX + (arenaMaxX - arenaMinX) / 2f;
            targetY = arenaY + arenaHeight / 2f;
            float zoomX = (arenaMaxX - arenaMinX) / VIEWPORT_WIDTH;
            float zoomY = arenaHeight / VIEWPORT_HEIGHT;
            targetZoom = Math.max(1f, Math.max(zoomX, zoomY)) * 1.05f;
        } else {
            float cameraOffsetY = (knight.getState() == KnightState.LOOK_UP || knight.getState() == KnightState.UP_CASTING) ? 4f :
                (knight.getState() == KnightState.LOOK_DOWN) ? -4f : 0f;
            targetX = Math.max(VIEWPORT_WIDTH * camera.zoom / 2f,
                Math.min(mapPixelWidth - VIEWPORT_WIDTH * camera.zoom / 2f, knight.getX()));
            targetY = Math.max(VIEWPORT_HEIGHT * camera.zoom / 2f,
                Math.min(mapPixelHeight - VIEWPORT_HEIGHT * camera.zoom / 2f, knight.getY() + cameraOffsetY));

            if (knight.getState() == KnightState.FOCUSING || knight.getState() == KnightState.FOCUSING_START) targetZoom = 0.88f;
        }

        camera.position.x += (targetX - camera.position.x) * CAM_LERP;
        camera.position.y += (targetY - camera.position.y) * CAM_LERP;
        camera.zoom += (targetZoom - camera.zoom) * (CAM_LERP * 0.6f);
        camera.update();

        applyCameraShake(Gdx.graphics.getDeltaTime());
    }

    private void applyCameraShake(float delta) {
        if (knight.getState() == KnightState.FOCUSING ||
            knight.getState() == KnightState.FOCUSING_START ||
            knight.getState() == KnightState.FOCUSING_GET) {

            smoothShakeTime += delta * 20f;
            camera.position.add((float) Math.sin(smoothShakeTime) * 0.03f, (float) Math.cos(smoothShakeTime * 1.2f) * 0.03f, 0);
            camera.update();
        }

        if (shakeTimer > 0) {
            shakeTimer -= delta;
            camera.position.add(com.badlogic.gdx.math.MathUtils.random(-shakeIntensity, shakeIntensity),
                com.badlogic.gdx.math.MathUtils.random(-shakeIntensity, shakeIntensity), 0);
            camera.update();
        }
    }

    public void togglePause() {
        isPaused = !isPaused;
        game.audioManager.stopAllSfxLoops();

        if (isPaused) {
            game.audioManager.pauseMusic();
            if (pauseUI.isSettingsOpen) {
                multiplexer.addProcessor(pauseUI.settingsUI.stage);
            } else if (pauseUI.isGuideOpen) {
                if (pauseUI.guideUI.isControlsOpen) multiplexer.addProcessor(pauseUI.guideUI.controlsUI.stage);
                else if (pauseUI.guideUI.isAbilitiesOpen) multiplexer.addProcessor(pauseUI.guideUI.abilitiesUI.stage);
                else if (pauseUI.guideUI.isCheatOpen) multiplexer.addProcessor(pauseUI.guideUI.cheatUI.stage);
                else multiplexer.addProcessor(pauseUI.guideUI.stage);
            } else if (pauseUI.isCheatOpen) {
                multiplexer.addProcessor(pauseUI.cheatUI.stage);
            } else {
                multiplexer.addProcessor(pauseUI.stage);
            }
        } else {
            game.audioManager.resumeMusic();
            multiplexer.removeProcessor(pauseUI.stage);
            if (pauseUI.settingsUI != null) multiplexer.removeProcessor(pauseUI.settingsUI.stage);
            if (pauseUI.cheatUI != null) multiplexer.removeProcessor(pauseUI.cheatUI.stage);

            if (pauseUI.guideUI != null) {
                multiplexer.removeProcessor(pauseUI.guideUI.stage);
                if (pauseUI.guideUI.controlsUI != null) multiplexer.removeProcessor(pauseUI.guideUI.controlsUI.stage);
                if (pauseUI.guideUI.abilitiesUI != null) multiplexer.removeProcessor(pauseUI.guideUI.abilitiesUI.stage);
                if (pauseUI.guideUI.cheatUI != null) multiplexer.removeProcessor(pauseUI.guideUI.cheatUI.stage);
            }
        }
    }

    public void removeWallTiles(Rectangle bounds) {
        MapLayer layer = map.getLayers().get("wall_visuals");

        if (layer instanceof TiledMapTileLayer) {
            TiledMapTileLayer visualLayer = (TiledMapTileLayer) layer;

            float tileWorldWidth = map.getProperties().get("tilewidth", Integer.class) * UNIT_SCALE;
            float tileWorldHeight = map.getProperties().get("tileheight", Integer.class) * UNIT_SCALE;

            int startX = (int) (bounds.x / tileWorldWidth);
            int startY = (int) (bounds.y / tileWorldHeight);
            int endX = (int) ((bounds.x + bounds.width - 0.01f) / tileWorldWidth);
            int endY = (int) ((bounds.y + bounds.height - 0.01f) / tileWorldHeight);

            for (int x = startX; x <= endX; x++) {
                for (int y = startY; y <= endY; y++) {
                    visualLayer.setCell(x, y, null);
                }
            }
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
                game.setScreen(new LoadingScreen(game, nextMap, 0, 0, knight, controller.instaKillMode));
            }
        }

        shapeRenderer.setColor(new Color(0, 0, 0, fadeAlpha));
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void handleWhiteFadeEffect(float delta) {
        if (!isWhiteFadingOut && !isWhiteFadingIn) return;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if (isWhiteFadingOut) {
            whiteFadeAlpha += delta * 0.8f;
            if (whiteFadeAlpha >= 1f) {
                whiteFadeAlpha = 1f;
                isWhiteFadingOut = false;

                if (endingSpawnPoint != null) {
                    knight.getPosition().set(endingSpawnPoint.x, endingSpawnPoint.y);
                    knight.getHitbox().setPosition(endingSpawnPoint.x, endingSpawnPoint.y);
                    knight.getVelocity().setZero();
                    knight.isEndingMode = true;
                }
                isWhiteFadingIn = true;
            }
        } else if (isWhiteFadingIn) {
            whiteFadeAlpha -= delta * 0.8f;
            if (whiteFadeAlpha <= 0f) {
                whiteFadeAlpha = 0f;
                isWhiteFadingIn = false;
            }
        }
        shapeRenderer.setColor(new Color(1f, 1f, 1f, whiteFadeAlpha));
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public static void triggerShake(float duration, float intensity) {
        shakeTimer = duration; shakeIntensity = intensity;
    }

    public void startTransition(String targetMap) {
        if (!isFadingOut) { game.audioManager.stopAllSfxLoops(); this.nextMap = targetMap; this.isFadingOut = true; }
    }

    public void startEndingSequence() {
        game.audioManager.stopAllSfxLoops(); game.audioManager.stopMusic();
        isWhiteFadingOut = true; isEndingSequence = true;
        game.audioManager.playDynamicMusic(null, game.audioManager.audioLoader.victoryTheme, null, true);
    }

    public void playBossMusic() {
        if (game.audioManager.audioLoader.bossFight != null) game.audioManager.playMusic(game.audioManager.audioLoader.bossFight, true, true);
    }

    public void stopBossMusic() {
        if (isEndingSequence) return;
        game.audioManager.playDynamicMusic(
            mapPath.equals("map/green_path.tmx") ? game.audioManager.audioLoader.greenpathAtmos : null,
            mapPath.equals("map/cross_road.tmx") ? game.audioManager.audioLoader.crossroadsMain : game.audioManager.audioLoader.greenpathMain,
            mapPath.equals("map/cross_road.tmx") ? game.audioManager.audioLoader.crossroadsBass : game.audioManager.audioLoader.greenpathBass, true);
        game.audioManager.setCombatState(false);
    }

    @Override public void resize(int width, int height) { viewport.update(width, height, false); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        if (map != null) map.dispose();
        if (renderer != null) renderer.dispose();
    }
}

