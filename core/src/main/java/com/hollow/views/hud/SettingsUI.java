package com.hollow.views.hud;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hollow.HollowKnight;
import com.hollow.controllers.ButtonController;
import com.hollow.models.GameSettings;
import com.hollow.models.enums.Language;

public class SettingsUI {
    public Stage stage;
    private HollowKnight game;
    private ButtonController controller;
    private TextButton[] menuButtons;
    private boolean wait = false;
    private String rebindTarget = null;
    private Label rebindLabel;
    private Runnable onClose;

    public SettingsUI(HollowKnight game, Runnable onClose) {
        this.game = game;
        this.onClose = onClose;
        stage = new Stage(new FitViewport(game.SCREEN_WIDTH, game.SCREEN_HEIGHT));
        setupUI();
    }

    private void setupUI() {
        GameSettings setting = game.settings;
        BitmapFont font = game.assetLoader.font;

        TextButton.TextButtonStyle styleBtn = new TextButton.TextButtonStyle();
        styleBtn.font = font;
        styleBtn.fontColor = Color.WHITE;

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label.LabelStyle dimStyle = new Label.LabelStyle(font, Color.GRAY);

        Table contentTable = new Table();
        contentTable.top().padTop(20);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().left();

        ScrollPane scrollPane = new ScrollPane(contentTable, game.assetLoader.scrollerSkin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        stage.addActor(scrollPane);

        Label title = new Label("SETTINGS", new Label.LabelStyle(font, Color.WHITE));
        title.setFontScale(1.5f);
        title.layout();
        float titleWidth = title.getGlyphLayout().width * title.getFontScaleX();
        Image settingBottom = new Image(game.assetLoader.settingBottom);
        settingBottom.setScaling(com.badlogic.gdx.utils.Scaling.fit);

        Table titleGroup = new Table();
        titleGroup.add(title).row();
        titleGroup.add(settingBottom).width(titleWidth).height(200).padTop(-75f).row();
        contentTable.add(titleGroup).colspan(2).padBottom(30).row();

        contentTable.add(new Label("Music Volume", labelStyle)).left().padRight(30).padBottom(18);
        Slider musicSlider = new Slider(0f, 1f, 0.05f, false, game.assetLoader.sliderSkin, "menuSlider");
        musicSlider.setValue(setting.musicVolume);
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setting.musicVolume = musicSlider.getValue();
                applyMusicVolume();
            }
        });
        contentTable.add(musicSlider).width(300).padBottom(18).row();

        contentTable.add(new Label("Music", labelStyle)).left().padRight(30).padBottom(18);
        TextButton musicToggle = new TextButton(setting.isMusicOn ? "ON" : "OFF", styleBtn);
        musicToggle.setUserObject((Runnable) () -> {
            setting.isMusicOn = !setting.isMusicOn;
            musicToggle.setText(setting.isMusicOn ? "ON" : "OFF");
            applyMusicToggle();
        });
        contentTable.add(musicToggle).left().padBottom(18).row();

        contentTable.add(new Label("Sound Effect", labelStyle)).left().padRight(30).padBottom(18);
        TextButton sfxToggle = new TextButton(setting.isSfxOn ? "ON" : "OFF", styleBtn);
        sfxToggle.setUserObject((Runnable) () -> {
            setting.isSfxOn = !setting.isSfxOn;
            sfxToggle.setText(setting.isSfxOn ? "ON" : "OFF");
        });
        contentTable.add(sfxToggle).left().padBottom(18).row();

        TextButton resetAudio = new TextButton("Reset Audio", styleBtn);
        resetAudio.setUserObject((Runnable) () -> {
            setting.resetAudio();
            musicSlider.setValue(setting.musicVolume);
            musicToggle.setText("ON");
            sfxToggle.setText("ON");
            applyMusicToggle();
            applyMusicVolume();
        });
        contentTable.add(resetAudio).colspan(2).padBottom(28).row();

        contentTable.add(new Label("Brightness", labelStyle)).left().padRight(30).padBottom(18);
        Slider btSlider = new Slider(0.2f, 1f, 0.05f, false, game.assetLoader.sliderSkin, "menuSlider");
        btSlider.setValue(setting.brightness);
        btSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setting.brightness = btSlider.getValue();
            }
        });
        contentTable.add(btSlider).width(300).padBottom(18).row();

        TextButton resetBrightness = new TextButton("Reset Brightness", styleBtn);
        resetBrightness.setUserObject((Runnable) () -> {
            setting.resetBrightness();
            btSlider.setValue(setting.brightness);
        });
        contentTable.add(resetBrightness).colspan(2).padBottom(28).row();

        contentTable.add(new Label("Language", labelStyle)).left().padRight(30).padBottom(28);
        TextButton lanBtn = new TextButton(setting.lang.toString(), styleBtn);
        lanBtn.setUserObject((Runnable) () -> {
            setting.lang = setting.lang.equals(Language.DE) ? Language.EN : Language.DE;
            lanBtn.setText(setting.lang.toString());
        });
        contentTable.add(lanBtn).left().padBottom(28).row();

        Label keyTitle = new Label("KeyBoard", new Label.LabelStyle(font, Color.WHITE));
        keyTitle.setFontScale(1.1f);
        contentTable.add(keyTitle).colspan(2).padBottom(14).row();

        TextButton upBtn = addNewKey(contentTable, "Up", "up", setting, labelStyle, styleBtn);
        TextButton downBtn = addNewKey(contentTable, "Down", "down", setting, labelStyle, styleBtn);
        TextButton rightBtn = addNewKey(contentTable, "Right", "right", setting, labelStyle, styleBtn);
        TextButton leftBtn = addNewKey(contentTable, "Left", "left", setting, labelStyle, styleBtn);
        TextButton jumpBtn = addNewKey(contentTable, "Jump", "jump", setting, labelStyle, styleBtn);
        TextButton attackBtn = addNewKey(contentTable, "Attack", "attack", setting, labelStyle, styleBtn);
        TextButton dashBtn = addNewKey(contentTable, "Dash", "dash", setting, labelStyle, styleBtn);
        TextButton focusBtn = addNewKey(contentTable, "Focus", "focus", setting, labelStyle, styleBtn);

        TextButton resetKey = new TextButton("Reset Keys", styleBtn);
        resetKey.setUserObject((Runnable) () -> {
            setting.resetKey();
            setting.save();
            onClose.run();
        });
        contentTable.add(resetKey).colspan(2).padBottom(28).row();

        rebindLabel = new Label("", dimStyle);
        contentTable.add(rebindLabel).colspan(2).padBottom(14).row();

        TextButton backBtn = new TextButton("Back", styleBtn);
        backBtn.setUserObject((Runnable) () -> {
            setting.save();
            onClose.run();
        });

        mainTable.add(backBtn).left().pad(20).padLeft(50).row();
        mainTable.add(scrollPane).expand().fill();
        stage.addActor(mainTable);

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (wait && rebindTarget != null) {
                    if (keycode == Input.Keys.ESCAPE) {
                        cancelRebind();
                        return true;
                    }
                    applyRebind(keycode);
                    return true;
                }

                if (keycode == Input.Keys.ESCAPE) {
                    setting.save();
                    onClose.run();
                    return true;
                }
                return false;
            }
        });

        menuButtons = new TextButton[]{
            musicToggle, sfxToggle, resetAudio, resetBrightness,
            lanBtn, upBtn, downBtn, rightBtn, leftBtn, jumpBtn,
            attackBtn, dashBtn, focusBtn, resetKey, backBtn
        };
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

    private TextButton addNewKey(Table root, String action, String target, GameSettings setting, Label.LabelStyle labelStyle, TextButton.TextButtonStyle btnStyle) {
        Table r = new Table();
        r.add(new Label(action, labelStyle)).width(120).left();

        Label keyName = new Label(Input.Keys.toString(getKey(setting, target)), labelStyle);
        r.add(keyName).width(100).center();
        TextButton changeBtn = new TextButton("Change", btnStyle);
        changeBtn.setUserObject((Runnable) () -> {
            rebind(target, keyName);
        });
        r.add(changeBtn).padLeft(30);

        root.add(r).colspan(2).padBottom(10).row();
        return changeBtn;
    }

    private void rebind(String target, Label keyName) {
        wait = true;
        rebindTarget = target;
        rebindLabel.setText("Press any key to set [" + target + "] ... (ESC to cancel)");
        rebindLabel.setUserObject(keyName);
    }

    private int getKey(GameSettings setting, String target) {
        return switch (target) {
            case "right" -> setting.keyRight;
            case "left" -> setting.keyLeft;
            case "up" -> setting.keyUp;
            case "down" -> setting.keyDown;
            case "jump" -> setting.keyJump;
            case "focus" -> setting.keyFocus;
            case "attack" -> setting.keyAttack;
            case "dash" -> setting.keyDash;
            default -> 0;
        };
    }

    private void applyRebind(int keycode) {
        GameSettings s = game.settings;
        switch (rebindTarget) {
            case "up" -> s.keyUp = keycode;
            case "down" -> s.keyDown = keycode;
            case "right" -> s.keyRight = keycode;
            case "left" -> s.keyLeft = keycode;
            case "jump" -> s.keyJump = keycode;
            case "focus" -> s.keyFocus = keycode;
            case "attack" -> s.keyAttack = keycode;
            case "dash" -> s.keyDash = keycode;
        }
        if (rebindLabel.getUserObject() instanceof Label) {
            ((Label) rebindLabel.getUserObject()).setText(Input.Keys.toString(keycode));
        }
        cancelRebind();
    }

    private void cancelRebind() {
        wait = false;
        rebindTarget = null;
        rebindLabel.setText("");
        rebindLabel.setUserObject(null);
    }

    private void applyMusicToggle() {
        if (game.assetLoader.titleTheme == null) return;
        if (game.settings.isMusicOn) {
            if (!game.assetLoader.titleTheme.isPlaying()) {
                game.assetLoader.titleTheme.setLooping(true);
                game.assetLoader.titleTheme.play();
            }
        } else {
            game.assetLoader.titleTheme.pause();
        }
    }

    private void applyMusicVolume() {
        if (game.assetLoader.titleTheme != null) {
            game.assetLoader.titleTheme.setVolume(game.settings.musicVolume);
        }
    }
}
