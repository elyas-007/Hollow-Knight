package com.hollow.views.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hollow.HollowKnight;
import com.hollow.controllers.ButtonController;
import com.hollow.views.hud.GuideUI;
import com.hollow.views.hud.SettingsUI;

public class MainMenuScreen implements Screen {
    private final HollowKnight game;
    private Stage stage;
    private FitViewport viewport;
    private ButtonController controller;

    private InputMultiplexer multiplexer;
    private SettingsUI settingsUI;
    private GuideUI guideUI;

    private boolean isSettingsOpen = false;
    private boolean isGuideOpen = false;

    public MainMenuScreen(HollowKnight game) {
        this.game = game;
    }

    @Override
    public void show() {
        viewport = new FitViewport(game.SCREEN_WIDTH, game.SCREEN_HEIGHT);
        stage = new Stage(viewport);
        multiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(multiplexer);
        multiplexer.addProcessor(stage);

        settingsUI = new SettingsUI(game, () -> {
            isSettingsOpen = false;
            multiplexer.removeProcessor(settingsUI.stage);
            multiplexer.addProcessor(stage);
        });

        guideUI = new GuideUI(game, () -> {
            isGuideOpen = false;
            multiplexer.removeProcessor(guideUI.stage);
            multiplexer.addProcessor(stage);
        }, multiplexer);

        TextButtonStyle styleBtn = new TextButtonStyle();
        styleBtn.font = game.assetLoader.font;
        styleBtn.fontColor = Color.WHITE;

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        Image gameLogo = new Image(game.assetLoader.hollowKnightLogo);

        TextButton startBtn = new TextButton("Start Game", styleBtn);
        TextButton settingsBtn = new TextButton("Settings", styleBtn);
        TextButton guideBtn = new TextButton("Guide", styleBtn);
        TextButton achievementsBtn = new TextButton("achievements", styleBtn);
        TextButton quitBtn = new TextButton("Quit Game", styleBtn);

        startBtn.setUserObject((Runnable) () -> game.setScreen(new StartGameMenuScreen(game, this)));
        settingsBtn.setUserObject((Runnable) () -> {
            isSettingsOpen = true;
            multiplexer.removeProcessor(stage);
            multiplexer.addProcessor(settingsUI.stage);
        });
        guideBtn.setUserObject((Runnable) () -> {
            isGuideOpen = true;
            multiplexer.removeProcessor(stage);
            multiplexer.addProcessor(guideUI.stage);
        });
        quitBtn.setUserObject((Runnable) () -> Gdx.app.exit());


        rootTable.add(gameLogo).height(400).width(900).padBottom(60).row();
        rootTable.add(startBtn).padBottom(20).row();
        rootTable.add(settingsBtn).padBottom(20).row();
        rootTable.add(guideBtn).padBottom(20).row();
        rootTable.add(achievementsBtn).padBottom(20).row();
        rootTable.add(quitBtn).padBottom(20).row();

        TextButton[] menuButtons = new TextButton[]{startBtn, settingsBtn, guideBtn, achievementsBtn, quitBtn};
        controller = new ButtonController(game, stage, menuButtons);

        if (game.assetLoader.titleTheme != null && !game.assetLoader.titleTheme.isPlaying() && game.settings.isMusicOn) {
            game.assetLoader.titleTheme.setLooping(true);
            game.assetLoader.titleTheme.setVolume(game.settings.musicVolume);
            game.assetLoader.titleTheme.play();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        boolean showLight = !isSettingsOpen && !isGuideOpen;

        game.batch.setProjectionMatrix(stage.getCamera().combined);
        game.batch.begin();
        game.menuBackground.updateAndDraw(game.batch, delta, game.settings.brightness, showLight);
        game.batch.end();


        if (isSettingsOpen) {
            settingsUI.act(delta);
            settingsUI.draw();
        } else if (isGuideOpen) {
            guideUI.act(delta);
            guideUI.draw();
        } else {
            stage.act(delta);
            if (controller != null) controller.update(delta);
            stage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        if (settingsUI != null) settingsUI.resize(width, height);
        if (guideUI != null) guideUI.resize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);

        if (game.assetLoader.titleTheme != null) {
            game.assetLoader.titleTheme.stop();
        }
    }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (settingsUI != null) settingsUI.dispose();
        if (guideUI != null) guideUI.dispose();
    }
}
