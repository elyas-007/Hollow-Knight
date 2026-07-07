package com.hollow.views.hud;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hollow.HollowKnight;
import com.hollow.controllers.ButtonController;
import com.hollow.models.LanguageManager;
import com.hollow.models.LanguageObserver;

public class BaseSettingUI implements LanguageObserver, UI {
    public Stage stage;
    private final HollowKnight game;
    private ButtonController controller;
    private TextButton[] menuButtons;
    private final Runnable onClose;
    private InputMultiplexer multiplexer;

    public AudioUI audioUI;
    public BrightnessUI videoUI;
    public KeyBoardUI keyBoardUI;
    public LanguageUI languageUI;

    public boolean isAudioOpen = false;
    public boolean isVideoOpen = false;
    public boolean isKeyBoardOpen = false;
    public boolean isLanguageOpen = false;


    public BaseSettingUI(HollowKnight game, Runnable onClose, InputMultiplexer multiplexer) {
        this.game = game;
        this.onClose = onClose;
        this.multiplexer = multiplexer;
        stage = new Stage(new FitViewport(game.SCREEN_WIDTH, game.SCREEN_HEIGHT));
        game.languageManager.addObserver(this);
        setupSubUIs();
        setupUI();
    }

    @Override
    public void setupUI() {
        LabelStyle titleStyle = new LabelStyle(game.assetLoader.font, Color.WHITE);

        TextButton.TextButtonStyle styleBtn = new TextButton.TextButtonStyle();
        styleBtn.font = game.assetLoader.font;
        styleBtn.fontColor = Color.WHITE;

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        Table content = new Table();

        Table title = new Table();
        Label t = new Label(game.languageManager.get("settingTitle"), titleStyle);  //TODO:
        t.setFontScale(1.5f);
        title.add(t).center().padTop(-40).padBottom(20).row();
        title.add(new Image(game.assetLoader.titleBottom)).center();

        content.add(title).padBottom(100).padTop(-80).row();

        TextButton audioBtn = new TextButton(game.languageManager.get("audioBtn"), styleBtn); //TODO
        TextButton videoBtn = new TextButton(game.languageManager.get("videoBtn"), styleBtn); //TODO
        TextButton keyBoardBtn = new TextButton(game.languageManager.get("keyBoardBtn"), styleBtn); //TODO
        TextButton languageBtn = new TextButton(game.languageManager.get("languageBtn"), styleBtn); //TODO
        TextButton backBtn = new TextButton(game.languageManager.get("back"), styleBtn);

        audioBtn.setUserObject((Runnable) () -> {
            isAudioOpen = true;
            multiplexer.removeProcessor(stage);
            multiplexer.addProcessor(audioUI.stage);
        });

        videoBtn.setUserObject((Runnable) () -> {
            isVideoOpen = true;
            multiplexer.removeProcessor(stage);
            multiplexer.addProcessor(videoUI.stage);
        });

        keyBoardBtn.setUserObject((Runnable) () -> {
            isKeyBoardOpen = true;
            multiplexer.removeProcessor(stage);
            multiplexer.addProcessor(keyBoardUI.stage);
        });

        languageBtn.setUserObject((Runnable) () -> {
            isLanguageOpen = true;
            multiplexer.removeProcessor(stage);
            multiplexer.addProcessor(languageUI.stage);
        });

        backBtn.setUserObject((Runnable) () -> onClose.run());



        content.add(audioBtn).center().padBottom(30).row();
        content.add(videoBtn).center().padBottom(30).row();
        content.add(keyBoardBtn).center().padBottom(30).row();
        content.add(languageBtn).center().padBottom(30);

        mainTable.add(backBtn).left().pad(20).padLeft(50).row();
        mainTable.add(content).top().expand().fill();

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    onClose.run();
                    return true;
                }
                return false;
            }
        });

        menuButtons = new TextButton[]{audioBtn , videoBtn, keyBoardBtn, languageBtn, backBtn};
        controller = new ButtonController(game, stage, menuButtons);

    }

    private void setupSubUIs() {
        audioUI = new AudioUI(game, () -> {
            isAudioOpen = false;
            multiplexer.removeProcessor(audioUI.stage);
            multiplexer.addProcessor(stage);
        });

        videoUI = new BrightnessUI(game, () -> {
            isVideoOpen = false;
            multiplexer.removeProcessor(videoUI.stage);
            multiplexer.addProcessor(stage);
        });

        keyBoardUI = new KeyBoardUI(game, () -> {
            isKeyBoardOpen = false;
            multiplexer.removeProcessor(keyBoardUI.stage);
            multiplexer.addProcessor(stage);
        });

        languageUI = new LanguageUI(game, () -> {
            isLanguageOpen = false;
            multiplexer.removeProcessor(languageUI.stage);
            multiplexer.addProcessor(stage);
        });
    }

    @Override
    public void act(float delta) {
        if (isAudioOpen) audioUI.act(delta);
        else if (isVideoOpen) videoUI.act(delta);
        else if (isKeyBoardOpen) keyBoardUI.act(delta);
        else if (isLanguageOpen) languageUI.act(delta);
        else {
            stage.act(delta);
            if (controller != null) controller.update(delta);
        }
    }

    @Override
    public void draw() {
        if (isAudioOpen) audioUI.draw();
        else if (isVideoOpen) videoUI.draw();
        else if (isKeyBoardOpen) keyBoardUI.draw();
        else if (isLanguageOpen) languageUI.draw();
        else stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        if (audioUI != null) audioUI.resize(width, height);
        if (videoUI != null) videoUI.resize(width, height);
        if (keyBoardUI != null) keyBoardUI.resize(width, height);
        if (languageUI != null) languageUI.resize(width, height);
    }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (audioUI != null) audioUI.dispose();
        if (videoUI != null) videoUI.dispose();
        if (keyBoardUI != null) keyBoardUI.dispose();
        if (languageUI != null) languageUI.dispose();
    }

    @Override
    public void onLanguageChanged() {
        stage.clear();
        setupUI();
    }
}
