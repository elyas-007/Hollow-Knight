package com.hollow.views.hud;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hollow.HollowKnight;
import com.hollow.controllers.ButtonController;

public class ControlsUI {
    public Stage stage;
    private HollowKnight game;
    private ButtonController controller;
    private Runnable onClose;

    public ControlsUI(HollowKnight game, Runnable onClose) {
        this.game = game;
        this.onClose = onClose;
        stage = new Stage(new FitViewport(1920, 1080));
        setupUI();
    }

    private void setupUI() {
        TextButtonStyle style = new TextButtonStyle();
        style.font = game.assetLoader.font;
        style.fontColor = Color.WHITE;

        LabelStyle titleStyle = new LabelStyle(game.assetLoader.font, Color.WHITE);

        Table main = new Table();
        main.setFillParent(true);
        Table content = new Table();

        Table title = new Table();
        Label t = new Label("CONTROLS", titleStyle);
        t.setFontScale(1.5f);
        title.add(t).row();
        title.add(new Image(game.assetLoader.top_menu));

        content.add(title).colspan(3).padTop(20).padBottom(60).row();

        Table keysTable = new Table();
        keysTable.add(createKeyRow("Move Left", game.settings.keyLeft, titleStyle, style)).pad(10);
        keysTable.add(createKeyRow("Move Right", game.settings.keyRight, titleStyle, style)).pad(10).row();
        keysTable.add(createKeyRow("Look Up", game.settings.keyUp, titleStyle, style)).pad(10);
        keysTable.add(createKeyRow("Look Down", game.settings.keyDown, titleStyle, style)).pad(10).row();
        keysTable.add(createKeyRow("Jump", game.settings.keyJump, titleStyle, style)).pad(10);
        keysTable.add(createKeyRow("Dash", game.settings.keyDash, titleStyle, style)).pad(10).row();
        keysTable.add(createKeyRow("Attack", game.settings.keyAttack, titleStyle, style)).pad(10);
        keysTable.add(createKeyRow("Focus", game.settings.keyFocus, titleStyle, style)).pad(10).row();

        content.add(keysTable).row();

        TextButton backBtn = new TextButton("Back", style);
        backBtn.setUserObject((Runnable) () -> onClose.run());

        main.add(backBtn).left().pad(20).padLeft(50).row();
        main.add(content).expand().fill();
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
        controller = new ButtonController(game, stage, new TextButton[]{ backBtn });
    }

    private Table createKeyRow(String actionName, int keyCode, Label.LabelStyle labelStyle, TextButton.TextButtonStyle btnStyle) {
        Table row = new Table();
        Label actionLabel = new Label(actionName + " :", labelStyle);
        actionLabel.setAlignment(Align.right);
        row.add(actionLabel).width(200).padRight(20);

        String keyString = Input.Keys.toString(keyCode);
        TextButton keyCap = new TextButton(keyString, btnStyle);
        keyCap.setTouchable(Touchable.disabled);
        keyCap.setColor(Color.LIGHT_GRAY);
        keyCap.pad(10, 20, 10, 20);
        row.add(keyCap).minWidth(80).center();

        return row;
    }

    public void act(float delta) {
        stage.act(delta);
        if (controller != null) controller.update(delta);
    }
    public void draw() { stage.draw(); }
    public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    public void dispose() { if (stage != null) stage.dispose(); }
}
