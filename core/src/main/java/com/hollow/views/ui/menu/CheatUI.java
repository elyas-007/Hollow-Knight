package com.hollow.views.ui.menu;

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
import com.hollow.models.language.LanguageObserver;
import com.hollow.views.ui.UI;

public class CheatUI implements LanguageObserver, UI {
    public Stage stage;
    private HollowKnight game;
    private ButtonController controller;
    private Runnable onClose;

    public CheatUI(HollowKnight game, Runnable onClose) {
        this.game = game;
        this.onClose = onClose;
        stage = new Stage(new FitViewport(game.SCREEN_WIDTH, game.SCREEN_HEIGHT));
        game.languageManager.addObserver(this);
        setupUI();
    }

    @Override
    public void setupUI() {
        TextButtonStyle style = new TextButtonStyle();
        style.font = game.assetLoader.font;
        style.fontColor = Color.WHITE;

        LabelStyle titleStyle = new LabelStyle(game.assetLoader.font, Color.WHITE);
        Label.LabelStyle descStyle = new Label.LabelStyle(game.assetLoader.subFont, Color.WHITE);

        Table main = new Table();
        main.setFillParent(true);
        Table content = new Table();

        Table title = new Table();
        Label t = new Label(game.languageManager.get("cheatsTitle"), titleStyle);
        t.setFontScale(1.5f);
        title.add(t).row();
        title.add(new Image(game.assetLoader.top_menu));

        content.add(title).colspan(3).padBottom(60).row();


        String s1 = game.languageManager.get("cheat1");
        String s2 = game.languageManager.get("cheat2");
        String s3 = game.languageManager.get("cheat3");
        String s4 = game.languageManager.get("cheat4");
        String s5 = game.languageManager.get("cheat5");
        String s6 = game.languageManager.get("cheat6");
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
        content.add(cheat1).left().padBottom(15).row();
        content.add(cheat2).left().padBottom(15).row();
        content.add(cheat3).left().padBottom(15).row();
        content.add(cheat4).left().padBottom(15).row();
        content.add(cheat5).left().padBottom(15).row();
        content.add(cheat6).left().padBottom(15).row();

        TextButton backBtn = new TextButton(game.languageManager.get("back"), style);
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

    @Override
    public void act(float delta) {
        stage.act(delta);
        if (controller != null) controller.update(delta);
    }

    @Override
    public void draw() {
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
    }

    @Override
    public void onLanguageChanged() {
        stage.clear();
        setupUI();
    }
}
