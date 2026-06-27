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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hollow.HollowKnight;
import com.hollow.controllers.ButtonController;

public class GuideUI {
    public Stage stage;
    private HollowKnight game;
    private ButtonController controller;
    private Runnable onClose;
    private InputMultiplexer multiplexer;

    public ControlsUI controlsUI;
    public AbilitiesUI abilitiesUI;
    public CheatUI cheatUI;

    public boolean isControlsOpen = false;
    public boolean isAbilitiesOpen = false;
    public boolean isCheatOpen = false;

    public GuideUI(HollowKnight game, Runnable onClose, InputMultiplexer multiplexer) {
        this.game = game;
        this.onClose = onClose;
        this.multiplexer = multiplexer;
        stage = new Stage(new FitViewport(game.SCREEN_WIDTH, game.SCREEN_HEIGHT));

        setupSubUIs();
        setupUI();
    }

    private void setupSubUIs() {
        controlsUI = new ControlsUI(game, () -> {
            isControlsOpen = false;
            multiplexer.removeProcessor(controlsUI.stage);
            multiplexer.addProcessor(stage);
        });

        abilitiesUI = new AbilitiesUI(game, () -> {
            isAbilitiesOpen = false;
            multiplexer.removeProcessor(abilitiesUI.stage);
            multiplexer.addProcessor(stage);
        });

        cheatUI = new CheatUI(game, () -> {
            isCheatOpen = false;
            multiplexer.removeProcessor(cheatUI.stage);
            multiplexer.addProcessor(stage);
        });
    }

    private void setupUI() {
        TextButtonStyle style = new TextButtonStyle();
        style.font = game.assetLoader.font;
        style.fontColor = Color.WHITE;

        Label.LabelStyle titleStyle = new Label.LabelStyle(game.assetLoader.font, Color.GOLD);

        Table main = new Table();
        main.setFillParent(true);
        Table content = new Table();

        Label t = new Label("GUIDE", titleStyle);
        t.setFontScale(1.5f);

        content.add(t).center().padBottom(50).row();
        TextButton controlsBtn = new TextButton("Controls", style);
        TextButton abilitiesBtn = new TextButton("Abilities", style);
        TextButton cheatsBtn = new TextButton("Cheat Codes", style);

        controlsBtn.setUserObject((Runnable) () -> {
            isControlsOpen = true;
            multiplexer.removeProcessor(stage);
            multiplexer.addProcessor(controlsUI.stage);
        });

        abilitiesBtn.setUserObject((Runnable) () -> {
            isAbilitiesOpen = true;
            multiplexer.removeProcessor(stage);
            multiplexer.addProcessor(abilitiesUI.stage);
        });

        cheatsBtn.setUserObject((Runnable) () -> {
            isCheatOpen = true;
            multiplexer.removeProcessor(stage);
            multiplexer.addProcessor(cheatUI.stage);
        });

        content.add(controlsBtn).padBottom(20).center().row();
        content.add(abilitiesBtn).padBottom(20).center().row();
        content.add(cheatsBtn).padBottom(20).center().row();

        TextButton backBtn = new TextButton("Back", style);
        backBtn.setUserObject((Runnable) () -> onClose.run());

        main.add(backBtn).left().pad(20).padLeft(50).row();
        main.add(content).expand().center().fill();
        stage.addActor(main);

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

        TextButton[] menuButtons = new TextButton[]{ controlsBtn, abilitiesBtn, cheatsBtn, backBtn };
        controller = new ButtonController(game, stage, menuButtons);
    }

    public void act(float delta) {
        if (isControlsOpen) controlsUI.act(delta);
        else if (isAbilitiesOpen) abilitiesUI.act(delta);
        else if (isCheatOpen) cheatUI.act(delta);
        else {
            stage.act(delta);
            if (controller != null) controller.update(delta);
        }
    }

    public void draw() {
        if (isControlsOpen) controlsUI.draw();
        else if (isAbilitiesOpen) abilitiesUI.draw();
        else if (isCheatOpen) cheatUI.draw();
        else stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        if (controlsUI != null) controlsUI.resize(width, height);
        if (abilitiesUI != null) abilitiesUI.resize(width, height);
        if (cheatUI != null) cheatUI.resize(width, height);
    }

    public void dispose() {
        if (stage != null) stage.dispose();
        if (controlsUI != null) controlsUI.dispose();
        if (abilitiesUI != null) abilitiesUI.dispose();
        if (cheatUI != null) cheatUI.dispose();
    }
}
