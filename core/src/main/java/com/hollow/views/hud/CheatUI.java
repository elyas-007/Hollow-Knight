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

public class CheatUI {
    public Stage stage;
    private HollowKnight game;
    private ButtonController controller;
    private Runnable onClose;

    public CheatUI(HollowKnight game, Runnable onClose) {
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
        Label.LabelStyle descStyle = new Label.LabelStyle(game.assetLoader.subFont, Color.WHITE);

        Table main = new Table();
        main.setFillParent(true);
        Table content = new Table();

        Table title = new Table();
        Label t = new Label("CHEATS", titleStyle);
        t.setFontScale(1.5f);
        title.add(t).row();
        title.add(new Image(game.assetLoader.top_menu));

        content.add(title).colspan(3).padBottom(60).row();


        String s1 = "Press [F1] | Boss Arena Teleport: Instantly teleport to the beginning of the boss fight arena.";
        String s2 = "Press [F2] | Noclip / Spectator Mode: Toggle flight and free movement through walls and obstacles without gravity.";
        String s3 = "Press [F3] | Emergency Heal: Instantly receive one health mask (if your health is empty, it will immediately revive you).";
        String s4 = "Press [F4] | Refill Soul Vessel: Instantly and completely refill the soul vessel.";
        String s5 = "Press [F5] | God Mode: Toggle invincibility (complete immunity to enemy attacks and spikes).";
        String s6 = "Press [F6] | Insta-Kill: Toggle the ability to destroy all enemies and bosses with a single strike.";
        Label cheat1 = new Label(s1, descStyle);
        Label cheat2 = new Label(s2, descStyle);
        Label cheat3 = new Label(s3, descStyle);
        Label cheat4 = new Label(s4, descStyle);
        Label cheat5 = new Label(s5, descStyle);
        Label cheat6 = new Label(s6, descStyle);
        cheat1.setAlignment(Align.left);
        cheat2.setAlignment(Align.left);
        cheat3.setAlignment(Align.left);
        cheat4.setAlignment(Align.left);
        cheat5.setAlignment(Align.left);
        cheat6.setAlignment(Align.left);
        content.add(cheat1).left().row();
        content.add(cheat2).left().row();
        content.add(cheat3).left().row();
        content.add(cheat4).left().row();
        content.add(cheat5).left().row();
        content.add(cheat6).left().row();

        TextButton backBtn = new TextButton("Back", style);
        backBtn.setUserObject((Runnable) () -> onClose.run());

        main.add(backBtn).left().pad(20).padLeft(50).row();
        main.add(content).top().expand().fill();
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

    public void draw() {
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        if (stage != null) stage.dispose();
    }
}
