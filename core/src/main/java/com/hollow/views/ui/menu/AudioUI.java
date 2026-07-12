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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hollow.HollowKnight;
import com.hollow.controllers.ButtonController;
import com.hollow.models.language.LanguageObserver;
import com.hollow.controllers.manager.SaveManager;
import com.hollow.views.ui.UI;

public class AudioUI implements LanguageObserver, UI {
    public Stage stage;
    private HollowKnight game;
    private ButtonController controller;
    private Array<TextButton> buttons = new Array<>();
    private Runnable onClose;

    public AudioUI(HollowKnight game, Runnable onClose) {
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
        Label t = new Label(game.languageManager.get("audioTitle"), titleStyle);
        t.setFontScale(1.5f);
        title.add(t).center().padTop(-40).padBottom(20).row();
        title.add(new Image(game.assetLoader.titleBottom)).center();

        content.add(title).padBottom(100).center().padTop(-80).row();

        Slider soundSlider = new Slider(0, 100, 1, false, game.assetLoader.sliderSkin, "menuSlider");
        Slider musicSlider = new Slider(0, 100, 1, false, game.assetLoader.sliderSkin, "menuSlider");

        Label soundVal = new Label(String.valueOf((int) (game.data.getSettings().getSfxVolume() * 100)), valStyle);
        Label musicVal = new Label(String.valueOf((int) (game.data.getSettings().getMusicVolume() * 100)), valStyle);

        musicSlider.setValue(game.data.getSettings().getMusicVolume() * 100);
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.data.getSettings().setMusicVolume(musicSlider.getValue() / 100);
                SaveManager.save(game.data);
                musicVal.setText(String.valueOf((int) musicSlider.getValue()));
                applyMusicVolume();
            }
        });

        soundSlider.setValue(game.data.getSettings().getSfxVolume() * 100);
        soundSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.data.getSettings().setSfxVolume(soundSlider.getValue() / 100);
                SaveManager.save(game.data);
                soundVal.setText(String.valueOf((int) soundSlider.getValue()));
            }
        });


        content.add(buildRow(game.languageManager.get("musicVolume"), musicSlider, musicVal, rowStyle)).padBottom(30).row();
        content.add(buildRow(game.languageManager.get("soundVolume"), soundSlider, soundVal, rowStyle)).padBottom(90).row();



        Table toggleRow = new Table();
        toggleRow.add(new Label(game.languageManager.get("music"), rowStyle)).width(260).left().spaceRight(100);
        TextButton musicToggle = new TextButton(game.data.getSettings().isMusicOn() ? game.languageManager.get("on") : game.languageManager.get("off"), btnStyle);
        musicToggle.setUserObject((Runnable) () -> {
            game.data.getSettings().setMusicOn(!game.data.getSettings().isMusicOn());
            SaveManager.save(game.data);
            musicToggle.setText(game.data.getSettings().isMusicOn() ? game.languageManager.get("on") : game.languageManager.get("off"));
            applyMusicToggle();
        });
        toggleRow.add().width(280).padLeft(30).padRight(20);
        toggleRow.add(musicToggle).width(40).left();
        content.add(toggleRow).padBottom(30).row();
        buttons.add(musicToggle);


        toggleRow = new Table();
        toggleRow.add(new Label(game.languageManager.get("soundEffect"), rowStyle)).width(260).left().spaceRight(100);
        TextButton sfxToggle = new TextButton(game.data.getSettings().isSfxOn() ? game.languageManager.get("on") : game.languageManager.get("off"), btnStyle);
        sfxToggle.setUserObject((Runnable) () -> {
            game.data.getSettings().setSfxOn(!game.data.getSettings().isSfxOn());
            SaveManager.save(game.data);
            sfxToggle.setText(game.data.getSettings().isSfxOn() ? game.languageManager.get("on") : game.languageManager.get("off"));
        });
        toggleRow.add().width(280).padLeft(30).padRight(20);
        toggleRow.add(sfxToggle).width(40).left();
        content.add(toggleRow).padBottom(30).row();
        buttons.add(sfxToggle);

        TextButton resetBtn = new TextButton(game.languageManager.get("reset"), btnStyle);
        TextButton backBtn = new TextButton(game.languageManager.get("back"), btnStyle);
        buttons.add(resetBtn);
        buttons.add(backBtn);

        resetBtn.setUserObject((Runnable) () -> {
            game.data.getSettings().resetAudio();
            musicSlider.setValue(game.data.getSettings().getMusicVolume() * 100);
            musicToggle.setText(game.languageManager.get("on"));
            sfxToggle.setText(game.languageManager.get("on"));
            soundSlider.setValue(game.data.getSettings().getSfxVolume() * 100);
            applyMusicToggle();
            applyMusicVolume();
            SaveManager.save(game.data);
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

        controller = new ButtonController(game, stage, buttons.toArray(TextButton.class));
    }

    private Table buildRow(String labelText, Slider slider, Label valueLabel, Label.LabelStyle style) {
        Table row = new Table();
        row.add(new Label(labelText, style)).width(260).left().spaceRight(100);
        row.add(slider).width(280).padLeft(30).padRight(20);
        row.add(valueLabel).width(40).left();
        return row;
    }

    private void applyMusicToggle() {
        if (game.data.getSettings().isMusicOn()) {
            game.audioManager.resumeMusic();
        } else {
            game.audioManager.pauseMusic();
        }
    }

    private void applyMusicVolume() {
        game.audioManager.updateMusicVolume();
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
