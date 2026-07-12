package com.hollow.views.ui.menu;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hollow.HollowKnight;
import com.hollow.controllers.ButtonController;
import com.hollow.models.language.LanguageObserver;
import com.hollow.controllers.manager.SaveManager;
import com.hollow.views.ui.UI;

public class BrightnessUI implements LanguageObserver, UI {
    public Stage stage;
    private HollowKnight game;
    private ButtonController controller;
    private Runnable onClose;

    public BrightnessUI(HollowKnight game, Runnable onClose) {
        this.game = game;
        this.onClose = onClose;
        stage = new Stage(new FitViewport(game.SCREEN_WIDTH, game.SCREEN_HEIGHT));
        game.languageManager.addObserver(this);
        setupUI();
    }

    @Override
    public void setupUI() {
        LabelStyle titleStyle = new LabelStyle(game.assetLoader.font, Color.WHITE);
        LabelStyle rowStyle = new LabelStyle(game.assetLoader.subFont, Color.WHITE);
        LabelStyle valStyle = new LabelStyle(game.assetLoader.subFont, Color.WHITE);

        TextButtonStyle btnStyle = new TextButtonStyle();
        btnStyle.font = game.assetLoader.font;
        btnStyle.fontColor = Color.WHITE;

        Table main = new Table();
        main.setFillParent(true);
        stage.addActor(main);

        Table content = new Table();

        Table title = new Table();
        Label t = new Label(game.languageManager.get("videoTitle"), titleStyle);  //TODO:
        t.setFontScale(1.5f);
        title.add(t).center().padTop(-40).padBottom(20).row();
        title.add(new Image(game.assetLoader.titleBottom)).center();

        content.add(title).padBottom(50).center().padTop(-80).row();

        Image brightnessImg = new Image(game.assetLoader.brightness);
        brightnessImg.setScaling(Scaling.fit);
        content.add(brightnessImg).size(600, 400).center().padBottom(30).row();

        Slider btSlider = new Slider(20, 100, 0.1f, false, game.assetLoader.sliderSkin, "menuSlider");
        Label brightnessVal = new Label(String.valueOf((int) (game.data.getSettings().getBrightness() * 100)), valStyle);

        btSlider.setValue(game.data.getSettings().getBrightness() * 100);
        btSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.data.getSettings().setBrightness(btSlider.getValue() / 100);
                brightnessVal.setText(String.valueOf((int) (game.data.getSettings().getBrightness() * 100)));
                SaveManager.save(game.data);
            }
        });


        Table sliderCol = new Table();

        sliderCol.add(btSlider).width(280).row();

        Table brightnessRow = new Table();
        brightnessRow.add(new Label(game.languageManager.get("brightness"), rowStyle)).width(260).left().spaceRight(100);
        brightnessRow.add(sliderCol).padLeft(30).padRight(20);
        brightnessRow.add(brightnessVal).width(40).left();
        content.add(brightnessRow).padBottom(80).row();

        TextButton resetBtn = new TextButton(game.languageManager.get("reset"), btnStyle);
        TextButton backBtn = new TextButton(game.languageManager.get("back"), btnStyle);

        resetBtn.setUserObject((Runnable) () -> {
            game.data.getSettings().resetBrightness();
            SaveManager.save(game.data);
            btSlider.setValue(game.data.getSettings().getBrightness() * 100);
        });

        content.add(resetBtn).padBottom(30).row();

        backBtn.setUserObject((Runnable) () -> {
            SaveManager.save(game.data);
            onClose.run();
        });

        main.add(backBtn).left().pad(20).padLeft(50).row();
        main.add(content).top().expand().fill();

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    SaveManager.save(game.data);
                    onClose.run();
                    return true;
                }
                return false;
            }
        });

        controller = new ButtonController(game, stage, new TextButton[]{resetBtn, backBtn});
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
    public void dispose() {
        if (stage != null) stage.dispose();
        SaveManager.save(game.data);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void onLanguageChanged() {
        stage.clear();
        setupUI();
    }
}
