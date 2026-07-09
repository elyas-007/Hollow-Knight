package com.hollow.views.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hollow.HollowKnight;
import com.hollow.controllers.ButtonController;
import com.hollow.models.LanguageManager;
import com.hollow.models.LanguageObserver;
import com.hollow.views.hud.*;

public class MainMenuScreen implements Screen, LanguageObserver {
    private final HollowKnight game;
    private Stage stage;
    private FitViewport viewport;
    private ButtonController controller;
    private InputMultiplexer multiplexer;

    private BaseSettingUI settingsUI;
    private GuideUI guideUI;
    private AchievementsUI achievementsUI;
    private StartGameUI startGameUI;

    private boolean isStartGameOpen = false;
    private boolean isAchievementsOpen = false;
    private boolean isSettingsOpen = false;
    private boolean isGuideOpen = false;

    public MainMenuScreen(HollowKnight game) {
        this.game = game;
        game.languageManager.addObserver(this);
    }

    @Override
    public void show() {
        viewport = new FitViewport(game.SCREEN_WIDTH, game.SCREEN_HEIGHT);
        stage = new Stage(viewport);
        multiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(multiplexer);
        multiplexer.addProcessor(stage);

        startGameUI = new StartGameUI(game, () -> {
            isStartGameOpen = false;
            multiplexer.removeProcessor(startGameUI.stage);
            multiplexer.addProcessor(stage);
        });

        settingsUI = new BaseSettingUI(game, () -> {
            isSettingsOpen = false;
            multiplexer.removeProcessor(settingsUI.stage);
            multiplexer.addProcessor(stage);
        }, multiplexer);

        guideUI = new GuideUI(game, () -> {
            isGuideOpen = false;
            multiplexer.removeProcessor(guideUI.stage);
            multiplexer.addProcessor(stage);
        }, multiplexer);

        achievementsUI = new AchievementsUI(game, () -> {
            isAchievementsOpen = false;
            multiplexer.removeProcessor(achievementsUI.stage);
            multiplexer.addProcessor(stage);
        });

        setupUI();
        game.audioManager.playMusic(game.audioManager.audioLoader.titleTheme, true, false);
    }

    public void setupUI() {
        TextButtonStyle styleBtn = new TextButtonStyle();
        styleBtn.font = game.assetLoader.font;
        styleBtn.fontColor = Color.WHITE;

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        Image gameLogo = new Image(game.assetLoader.hollowKnightLogo);

        TextButton startBtn = new TextButton(game.languageManager.get("startGame"), styleBtn);
        TextButton settingsBtn = new TextButton(game.languageManager.get("settings"), styleBtn);
        TextButton guideBtn = new TextButton(game.languageManager.get("guide"), styleBtn);
        TextButton achievementsBtn = new TextButton(game.languageManager.get("achievements"), styleBtn);
        TextButton quitBtn = new TextButton(game.languageManager.get("quitGame"), styleBtn);

        Image changeBgBtn = new Image(game.assetLoader.changeBgIcon);

        changeBgBtn.setColor(0.7f, 0.7f, 0.7f, 1f);

        changeBgBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.menuBackground.changeBackground();
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                if (pointer == -1) {
                    changeBgBtn.clearActions();
                    changeBgBtn.addAction(Actions.color(Color.WHITE, 0.15f));
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                if (pointer == -1) {
                    changeBgBtn.clearActions();
                    changeBgBtn.addAction(Actions.color(new Color(0.7f, 0.7f, 0.7f, 1f), 0.15f));
                }
            }
        });

        Table leftTable = new Table();
        leftTable.setFillParent(true);
        leftTable.left().bottom().padLeft(60).padBottom(60);

        leftTable.add(changeBgBtn).width(80).height(80);
        stage.addActor(leftTable);

        startBtn.setUserObject((Runnable) () -> {
            isStartGameOpen = true;
            multiplexer.removeProcessor(stage);
            multiplexer.addProcessor(startGameUI.stage);
        });
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
        achievementsBtn.setUserObject((Runnable) () -> {
            isAchievementsOpen = true;
            multiplexer.removeProcessor(stage);
            multiplexer.addProcessor(achievementsUI.stage);
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
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        boolean showLight = !isSettingsOpen && !isGuideOpen && !isAchievementsOpen && !isStartGameOpen;

        game.batch.setProjectionMatrix(stage.getCamera().combined);
        game.batch.begin();
        game.menuBackground.updateAndDraw(game.batch, delta, game.data.getSettings().getBrightness(), showLight);
        game.batch.end();

        if (isStartGameOpen) {
            startGameUI.act(delta);
            startGameUI.draw();
        } else if (isSettingsOpen) {
            settingsUI.act(delta);
            settingsUI.draw();
        } else if (isGuideOpen) {
            guideUI.act(delta);
            guideUI.draw();
        } else if (isAchievementsOpen) {
            achievementsUI.act(delta);
            achievementsUI.draw();
        } else {
            stage.act(delta);
            if (controller != null) controller.update(delta);
            stage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        if (startGameUI != null) startGameUI.resize(width, height);
        if (settingsUI != null) settingsUI.resize(width, height);
        if (guideUI != null) guideUI.resize(width, height);
        if (achievementsUI != null) achievementsUI.resize(width, height);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        game.audioManager.stopMusic();
    }

    @Override
    public void dispose() {
        LanguageManager.removeObserver(this);

        if (stage != null) stage.dispose();
        if (startGameUI != null) startGameUI.dispose();
        if (settingsUI != null) settingsUI.dispose();
        if (guideUI != null) guideUI.dispose();
        if (achievementsUI != null) achievementsUI.dispose();
    }

    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void onLanguageChanged() {
        stage.clear();
        setupUI();
    }
}
