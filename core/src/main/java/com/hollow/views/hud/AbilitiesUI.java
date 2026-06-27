package com.hollow.views.hud;

import com.badlogic.gdx.Input;
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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hollow.HollowKnight;
import com.hollow.controllers.ButtonController;

public class AbilitiesUI {
    public Stage stage;
    private HollowKnight game;
    private ButtonController controller;
    private Runnable onClose;

    public AbilitiesUI(HollowKnight game, Runnable onClose) {
        this.game = game;
        this.onClose = onClose;
        stage = new Stage(new FitViewport(game.SCREEN_WIDTH, game.SCREEN_HEIGHT));
        setupUI();
    }

    private void setupUI() {
        TextButtonStyle style = new TextButtonStyle();
        style.font = game.assetLoader.font;
        style.fontColor = Color.WHITE;

        LabelStyle titleStyle = new LabelStyle(game.assetLoader.font, Color.WHITE);
        LabelStyle descStyle = new LabelStyle(game.assetLoader.subFont, Color.GOLD);

        Table main = new Table();
        main.setFillParent(true);
        Table content = new Table();

        Table title = new Table();
        Label t = new Label("ABILITIES", titleStyle);
        t.setFontScale(1.5f);
        title.add(t).row();
        title.add(new Image(game.assetLoader.top_menu));
        content.add(title).colspan(3).padTop(20).padBottom(60).row();

        String mechanicsText =
            "SOUL SYSTEM:\n" +
                "Strike enemies with your Nail to gather Soul.\n\n" +
                "HEALING (FOCUS):\n" +
                "Hold the Focus button to consume collected Soul and restore shattered Health Masks.\n\n" +
                "CHARMS:\n" +
                "Find Charms hidden in the world and equip them to enhance your abilities.";

        Label mechanicsLabel = new Label(mechanicsText, descStyle);
        mechanicsLabel.setWrap(true);
        mechanicsLabel.setAlignment(Align.left);
        content.add(mechanicsLabel).width(1000).row();

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

        TextButton[] menuButtons = new TextButton[]{ backBtn };
        controller = new ButtonController(game, stage, menuButtons);
    }

    public void act(float delta) {
        stage.act(delta);
        if (controller != null) controller.update(delta);
    }
    public void draw() { stage.draw(); }
    public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    public void dispose() { if (stage != null) stage.dispose(); }
}
