package com.hollow.views.ui.menu;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hollow.HollowKnight;
import com.hollow.controllers.ButtonController;
import com.hollow.models.language.LanguageObserver;
import com.hollow.controllers.manager.SaveManager;
import com.hollow.models.data.SettingData;
import com.hollow.views.ui.UI;

public class KeyBoardUI implements LanguageObserver, UI {
    public Stage stage;
    private SettingData settings;
    private final HollowKnight game;
    private ButtonController controller;
    private Array<TextButton> menuButtons = new Array<>();
    private final Runnable onClose;
    private boolean wait = false;
    private String rebindTarget = null;
    private Label rebindLabel;

    private TextButtonStyle keyBtnStyle;

    public KeyBoardUI(HollowKnight game, Runnable onClose) {
        this.game = game;
        this.onClose = onClose;
        stage = new Stage(new FitViewport(game.SCREEN_WIDTH, game.SCREEN_HEIGHT));
        game.languageManager.addObserver(this);

        setupUI();
    }

    @Override
    public void setupUI() {
        settings = game.data.getSettings();

        LabelStyle titleStyle = new LabelStyle(game.assetLoader.font, Color.WHITE);
        LabelStyle actionStyle = new LabelStyle(game.assetLoader.subFont, Color.WHITE);

        TextButtonStyle btnStyle = new TextButtonStyle();
        btnStyle.font = game.assetLoader.font;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.overFontColor = new Color(0.85f, 0.85f, 0.85f, 1f);


        keyBtnStyle = makeKeyButtonStyle();

        Table main = new Table();
        main.setFillParent(true);
        Table content = new Table();

        Table title = new Table();
        Label t = new Label(game.languageManager.get("keyboardTitle"), titleStyle);  //TODO:
        t.setFontScale(1.5f);
        title.add(t).center().padTop(-40).padBottom(20).row();
        title.add(new Image(game.assetLoader.titleBottom)).center();

        content.add(title).colspan(3).padBottom(100).padTop(-40).row();

        Table leftCol = new Table();
        Table rightCol = new Table();

        addKeyRow(leftCol, "up", game.languageManager.get("lookUp"), settings.getKeyUp(), actionStyle);
        addKeyRow(leftCol, "down", game.languageManager.get("lookDown"), settings.getKeyDown(), actionStyle);
        addKeyRow(leftCol, "left", game.languageManager.get("moveLeft"), settings.getKeyLeft(), actionStyle);
        addKeyRow(leftCol, "right", game.languageManager.get("moveRight"), settings.getKeyRight(), actionStyle);

        addKeyRow(rightCol, "jump", game.languageManager.get("jump"), settings.getKeyJump(), actionStyle);
        addKeyRow(rightCol, "attack", game.languageManager.get("attack"), settings.getKeyAttack(), actionStyle);
        addKeyRow(rightCol, "dash", game.languageManager.get("dash"), settings.getKeyDash(), actionStyle);
        addKeyRow(rightCol, "focus", game.languageManager.get("focus"), settings.getKeyFocus(), actionStyle);

        content.add(leftCol).top().padRight(150);
        content.add(rightCol).top().row();

        TextButton resetBtn = new TextButton(game.languageManager.get("resetKeys"), btnStyle);
        TextButton backBtn = new TextButton(game.languageManager.get("back"), btnStyle);
        menuButtons.add(resetBtn);
        menuButtons.add(backBtn);

        resetBtn.setUserObject((Runnable) () -> {
            settings.resetKey();
            SaveManager.save(game.data);
            stage.clear();
            setupUI();
        });

        backBtn.setUserObject((Runnable) () -> {
            SaveManager.save(game.data);
            onClose.run();
        });

        content.add(resetBtn).colspan(2).center().padTop(50).padBottom(20).row();

        rebindLabel = new Label("", actionStyle);
        content.add(rebindLabel).colspan(5).padTop(80).center().row();

        main.add(backBtn).left().pad(20).padLeft(50).row();
        main.add(content).top().expand().fill();
        stage.addActor(main);

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (wait && rebindTarget != null) {
                    if (keycode == Input.Keys.ESCAPE) {
                        cancelRebind();
                        return true;
                    }
                    applyRebind(keycode);
                    SaveManager.save(game.data);
                    return true;
                }

                if (keycode == Input.Keys.ESCAPE) {
                    SaveManager.save(game.data);
                    onClose.run();
                    return true;
                }
                return false;
            }
        });
        TextButton[] buttons = menuButtons.toArray(TextButton.class);
        controller = new ButtonController(game, stage, buttons);
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
        SaveManager.save(game.data);
    }

    private void addKeyRow(Table col, String actionId, String displayName, int currentKey, LabelStyle style) {
        col.add(new Label(displayName, style)).left().width(250).padBottom(18).padRight(100);
        col.add(makeKeyButton(actionId, displayName, currentKey)).size(250, 42).padBottom(18).row();
    }

    private TextButton makeKeyButton(String actionId, String displayName, int currentKey) {
        TextButton btn = new TextButton(Input.Keys.toString(currentKey), keyBtnStyle);
        menuButtons.add(btn);

        btn.setUserObject((Runnable) () -> {
            rebind(actionId, displayName, btn);
        });

        return btn;
    }

    private void rebind(String actionId, String displayName, TextButton targetButton) {
        wait = true;
        rebindTarget = actionId;
        rebindLabel.setText(game.languageManager.get("pressAnyKey") + " " + displayName + " " + game.languageManager.get("escToCancel"));
        rebindLabel.setUserObject(targetButton);
    }

    private void applyRebind(int keycode) {
        switch (rebindTarget) {
            case "up" -> settings.setKeyUp(keycode);
            case "down" -> settings.setKeyDown(keycode);
            case "right" -> settings.setKeyRight(keycode);
            case "left" -> settings.setKeyLeft(keycode);
            case "jump" -> settings.setKeyJump(keycode);
            case "focus" -> settings.setKeyFocus(keycode);
            case "attack" -> settings.setKeyAttack(keycode);
            case "dash" -> settings.setKeyDash(keycode);
        }
        SaveManager.save(game.data);
        if (rebindLabel.getUserObject() instanceof TextButton) {
            ((TextButton) rebindLabel.getUserObject()).setText(Input.Keys.toString(keycode));
        }
        cancelRebind();
    }

    private void cancelRebind() {
        wait = false;
        rebindTarget = null;
        rebindLabel.setText("");
        rebindLabel.setUserObject(null);
    }

    private TextButtonStyle makeKeyButtonStyle() {
        Pixmap bg = new Pixmap(90, 42, Pixmap.Format.RGBA8888);
        bg.setColor(0f, 0f, 0f, 0f);
        bg.fill();
        bg.setColor(Color.WHITE);
        bg.drawRectangle(0, 0, 90, 42);
        bg.drawRectangle(1, 1, 88, 40);

        Pixmap bgPressed = new Pixmap(90, 42, Pixmap.Format.RGBA8888);
        bgPressed.setColor(0.3f, 0.3f, 0.3f, 0.6f);
        bgPressed.fill();
        bgPressed.setColor(Color.LIGHT_GRAY);
        bgPressed.drawRectangle(0, 0, 90, 42);

        TextButtonStyle style = new TextButtonStyle();
        style.font = game.assetLoader.subFont;
        style.fontColor = Color.WHITE;
        style.up = new TextureRegionDrawable(new TextureRegion(new Texture(bg)));
        style.down = new TextureRegionDrawable(new TextureRegion(new Texture(bgPressed)));
        style.over = style.down;

        bg.dispose();
        bgPressed.dispose();
        return style;
    }

    @Override
    public void onLanguageChanged() {
        stage.clear();
        setupUI();
    }
}
