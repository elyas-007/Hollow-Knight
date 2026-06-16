package com.hollow.views.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hollow.HollowKnight;
import com.hollow.controllers.ButtonController;
import com.hollow.models.GameSettings;
import com.hollow.models.enums.Language;


public class SettingsMenuScreen implements Screen {
    private final HollowKnight game;
    private Stage stage;
    private FitViewport viewport;
    private ButtonController controller;
    private TextButton[] menuButtons;
    private boolean wait = false;
    private String rebindTarget = null;
    private Label rebindLabel;


    public SettingsMenuScreen(HollowKnight game) {
        this.game = game;
    }

    @Override
    public void show() {
        viewport = new FitViewport(1280, 720);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        GameSettings setting = game.settings;
        BitmapFont font = game.assetLoader.font;


        TextButtonStyle styleBtn = new TextButtonStyle();
        styleBtn.font = font;
        styleBtn.fontColor = Color.LIGHT_GRAY;
        styleBtn.overFontColor = Color.WHITE;

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label.LabelStyle dimStyle = new Label.LabelStyle(font, Color.GRAY);

        SliderStyle sliderStyle = new SliderStyle();
        sliderStyle.background = createColorDrawable(new Color(0.3f, 0.3f, 0.3f, 1f), 300, 10);
        sliderStyle.knob = createColorDrawable(Color.WHITE, 16, 16);
        sliderStyle.knobOver = createColorDrawable(Color.LIGHT_GRAY, 18, 18);


        Table contentTable = new Table();
//        rootTable.setFillParent(true);
        contentTable.top().padTop(20);
//        stage.addActor(contentTable);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().left();

        ScrollPane scrollPane = new ScrollPane(contentTable);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        stage.addActor(scrollPane);


        Label title = new Label("SETTINGS", new Label.LabelStyle(font, Color.WHITE));
        title.setFontScale(1.5f);
        title.layout();
        float titleWidth = title.getGlyphLayout().width * title.getFontScaleX();
        Image settingBottom = new Image(game.assetLoader.settingBottom);
        settingBottom.setScaling(com.badlogic.gdx.utils.Scaling.fit);

//        contentTable.add(settingBottom).width(titleWidth).height(20);
        Table titleGroup = new Table();
        titleGroup.add(title).row();
        titleGroup.add(settingBottom).width(titleWidth).height(200).padTop(-75f).row();
        contentTable.add(titleGroup).colspan(2).padBottom(30).row();

        contentTable.add(new Label("Music Volume", labelStyle)).left().padRight(30).padBottom(18);
        Slider musicSlider = new Slider(0f, 1f, 0.05f, false, sliderStyle);
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
        addHoverEffect(musicToggle);
        musicToggle.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setting.isMusicOn = !setting.isMusicOn;
                musicToggle.setText(setting.isMusicOn ? "ON" : "OFF");
                applyMusicToggle();
                playSfx();
            }
        });
        contentTable.add(musicToggle).left().padBottom(18).row();


        contentTable.add(new Label("Sound Effect", labelStyle)).left().padRight(30).padBottom(18);
        TextButton sfxToggle = new TextButton(setting.isSfxOn ? "ON" : "OFF", styleBtn);
        addHoverEffect(sfxToggle);
        sfxToggle.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setting.isSfxOn = !setting.isSfxOn;
                sfxToggle.setText(setting.isSfxOn ? "ON" : "OFF");
                playSfx();
            }
        });
        contentTable.add(sfxToggle).left().padBottom(18).row();

        TextButton resetAudio = new TextButton("Reset Audio", styleBtn);
        addHoverEffect(resetAudio);
        resetAudio.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setting.resetAudio();
                musicSlider.setValue(setting.musicVolume);
                musicToggle.setText("ON");
                sfxToggle.setText("ON");
                applyMusicVolume();
                applyMusicToggle();
                playSfx();
            }
        });
        contentTable.add(resetAudio).colspan(2).padBottom(28).row();


        contentTable.add(new Label("Brightness", labelStyle)).left().padRight(30).padBottom(18);
        Slider btSlider = new Slider(0.5f, 1.5f, 0.05f, false, sliderStyle);
        btSlider.setValue(setting.brightness);
        btSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setting.brightness = btSlider.getValue();
            }
        });
        contentTable.add(btSlider).width(300).padBottom(18).row();

        TextButton resetBrightness = new TextButton("Reset Brightness", styleBtn);
        addHoverEffect(resetBrightness);
        resetBrightness.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setting.resetBrightness();
                playSfx();
            }
        });
        contentTable.add(resetBrightness).colspan(2).padBottom(28).row();

        contentTable.add(new Label("Language", labelStyle)).left().padRight(30).padBottom(28);
        TextButton lanBtn =  new TextButton(setting.lang.toString(), styleBtn);
        addHoverEffect(lanBtn);
        lanBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setting.lang = setting.lang.equals(Language.DE) ? Language.EN : Language.DE;
                lanBtn.setText(setting.lang.toString());
                playSfx();
            }
        });
        contentTable.add(lanBtn).left().padBottom(28).row();


        Label keyTitle = new Label("KeyBoard", new Label.LabelStyle(font, Color.WHITE));
        keyTitle.setFontScale(1.1f);
        contentTable.add(keyTitle).colspan(2).padBottom(14).row();

        addNewKey(contentTable, "Up", "up", setting, labelStyle, styleBtn);
        addNewKey(contentTable, "Down", "down", setting, labelStyle, styleBtn);
        addNewKey(contentTable, "Right", "right", setting, labelStyle, styleBtn);
        addNewKey(contentTable, "Left", "left", setting, labelStyle, styleBtn);
        addNewKey(contentTable, "Jump", "jump", setting, labelStyle, styleBtn);
        addNewKey(contentTable, "Attack", "attack", setting, labelStyle, styleBtn);
        addNewKey(contentTable, "Dash", "dash", setting, labelStyle, styleBtn);
        addNewKey(contentTable, "Focus", "focus", setting, labelStyle, styleBtn);


        TextButton resetKey = new TextButton("Reset Keys", styleBtn);
        addHoverEffect(resetKey);
        resetKey.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setting.resetKey();
                game.setScreen(new SettingsMenuScreen(game));
                playSfx();
            }
        });
        contentTable.add(resetKey).colspan(2).padBottom(28).row();

        rebindLabel = new Label("", dimStyle);
        contentTable.add(rebindLabel).colspan(2).padBottom(14).row();


        TextButton backBtn  = new TextButton("Back", styleBtn);
        addHoverEffect(backBtn);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setting.save();
                playSfx();
                game.setScreen(new MainMenuScreen(game)); // add feature later
            }
        });

        mainTable.add(backBtn).left().pad(20).row();
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
                    game.setScreen(new SettingsMenuScreen(game)); // add feature later
                    return true;
                }

                return false;
            }
        });

        if (game.assetLoader.titleTheme != null && !game.assetLoader.titleTheme.isPlaying()) {
            game.assetLoader.titleTheme.setLooping(true);
            game.assetLoader.titleTheme.setVolume(0.5f);
            game.assetLoader.titleTheme.play();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(stage.getCamera().combined);

        game.batch.begin();
        float b = game.settings.brightness;
        game.batch.setColor(b, b, b, 1f);
        game.batch.draw(game.assetLoader.background, 0, 0, 1280, 720);
        game.batch.setColor(Color.WHITE);
        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
    }


    private void addNewKey(Table root, String action, String target, GameSettings setting, Label.LabelStyle labelStyle, TextButtonStyle btnStyle) {
        Table r = new Table();
        r.add(new Label(action, labelStyle)).width(120).left();

        Label keyName = new Label(Input.Keys.toString(getKey(setting, target)), labelStyle);
        r.add(keyName).width(100).center();
        TextButton changeBtn = new TextButton("Change", btnStyle);
        addHoverEffect(changeBtn);
        changeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                rebind(target, keyName);
                playSfx();
            }
        });
        r.add(changeBtn).padLeft(16);

        root.add(r).colspan(2).padBottom(10).row();
    }

    private void rebind(String target, Label keyName) {
        wait = true;
        rebindTarget = target;
        rebindLabel.setText("Press any key to set [" + target + "] … (ESC to cancel)");
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

    private Drawable createColorDrawable(Color color, int w, int h) {
        Pixmap pm = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pm.setColor(color);
        pm.fill();
        TextureRegionDrawable d = new TextureRegionDrawable(new TextureRegion(new Texture(pm)));
        pm.dispose();
        return d;
    }



    private void playSfx() {
        if (game.settings.isSfxOn && game.assetLoader.buttonClick != null) {
            game.assetLoader.buttonClick.stop();
            game.assetLoader.buttonClick.play();
        }
    }

    private void applyMusicToggle() {
        if (game.assetLoader.titleTheme == null)
            return;

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

    private void addHoverEffect(Actor actor) {
        actor.setOrigin(Align.center);

        if (actor instanceof Group) {
            ((Group) actor).setTransform(true);
        }

        actor.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                if (pointer == -1) {
                    actor.clearActions();
                    actor.addAction(Actions.scaleTo(1.1f, 1.1f, 0.1f));
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                if (pointer == -1) {
                    actor.clearActions();
                    actor.addAction(Actions.scaleTo(1f, 1f, 0.1f));
                }
            }
        });
    }
}
