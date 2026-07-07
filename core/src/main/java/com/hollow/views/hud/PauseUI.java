package com.hollow.views.hud;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hollow.HollowKnight;
import com.hollow.controllers.ButtonController;
import com.hollow.models.LanguageManager;
import com.hollow.models.LanguageObserver;
import com.hollow.models.SaveManager;
import com.hollow.views.screens.*;

public class PauseUI implements LanguageObserver, UI {
    public Stage stage;
    private final HollowKnight game;
    private final GameScreen gameScreen;
    private ButtonController controller;

    public BaseSettingUI settingsUI;
    public GuideUI guideUI;
    public CheatUI cheatUI;

    public boolean isSettingsOpen = false;
    public boolean isGuideOpen = false;
    public boolean isCheatOpen = false;

    private InputMultiplexer multiplexer;

    public PauseUI(HollowKnight game, GameScreen gameScreen, InputMultiplexer multiplexer) {
        this.game = game;
        this.gameScreen = gameScreen;
        this.multiplexer = multiplexer;

        stage = new Stage(new FitViewport(game.SCREEN_WIDTH, game.SCREEN_HEIGHT));
        game.languageManager.addObserver(this);
        setupSubUIs();
        setupUI();
    }

    private void setupSubUIs() {
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

        cheatUI = new CheatUI(game, () -> {
            isCheatOpen = false;
            multiplexer.removeProcessor(cheatUI.stage);
            multiplexer.addProcessor(stage);
        });
    }

    @Override
    public void setupUI() {
        TextButtonStyle styleBtn = new TextButtonStyle();
        styleBtn.font = game.assetLoader.font;
        styleBtn.fontColor = Color.WHITE;

        Table root = new Table();
        root.setFillParent(true);
        Table content = new Table();

        root.add(new Image(game.assetLoader.pauseTop)).colspan(2).top().padBottom(30).center().row();

        TextButton continueBtn = new TextButton(game.languageManager.get("continue"), styleBtn);
        TextButton settingsBtn = new TextButton(game.languageManager.get("settings"), styleBtn);
        TextButton guideBtn = new TextButton(game.languageManager.get("guide"), styleBtn);
        TextButton cheatBtn = new TextButton(game.languageManager.get("cheat"), styleBtn);
        TextButton quitBtn = new TextButton(game.languageManager.get("quitToMenu"), styleBtn);

        continueBtn.setUserObject((Runnable) () -> gameScreen.togglePause());

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

        cheatBtn.setUserObject((Runnable) () -> {
            isCheatOpen = true;
            multiplexer.removeProcessor(stage);
            multiplexer.addProcessor(cheatUI.stage);
        });

        quitBtn.setUserObject((Runnable) this::quitGame);

        content.add(continueBtn).center().fillX().uniformX().padTop(10).padLeft(40).row();
        content.add(settingsBtn).center().fillX().uniformX().padTop(10).padLeft(40).row();
        content.add(guideBtn).center().fillX().uniformX().padTop(10).padLeft(40).row();
        content.add(cheatBtn).center().fillX().uniformX().padTop(10).padLeft(40).row();
        content.add(quitBtn).center().fillX().uniformX().padTop(10).padLeft(40).row();

        TextButton[] menuButtons = new TextButton[]{continueBtn, settingsBtn, guideBtn, cheatBtn, quitBtn};
        controller = new ButtonController(game, stage, menuButtons);

        root.add(content).center().row();
        root.add(new Image(game.assetLoader.pauseBottom)).colspan(2).padTop(30).bottom().center().row();

        stage.addActor(root);
    }

    @Override
    public void act(float delta) {
        if (isSettingsOpen) settingsUI.act(delta);
        else if (isGuideOpen) guideUI.act(delta);
        else if (isCheatOpen) cheatUI.act(delta);
        else {
            stage.act(delta);
            if (controller != null) controller.update(delta);
        }
    }

    @Override
    public void draw() {
        if (isSettingsOpen) settingsUI.draw();
        else if (isGuideOpen) guideUI.draw();
        else if (isCheatOpen) cheatUI.draw();
        else stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        if (settingsUI != null) settingsUI.resize(width, height);
        if (guideUI != null) guideUI.resize(width, height);
        if (cheatUI != null) cheatUI.resize(width, height);
    }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (settingsUI != null) settingsUI.dispose();
        if (guideUI != null) guideUI.dispose();
        if (cheatUI != null) cheatUI.dispose();
        LanguageManager.removeObserver(this);
    }

    @Override
    public void onLanguageChanged() {
        stage.clear();
        setupUI();
    }

    public void quitGame() {
        if (game.data != null) {
            SaveManager.save(game.data);
        }
        game.setScreen(new MainMenuScreen(game));
    }
}
