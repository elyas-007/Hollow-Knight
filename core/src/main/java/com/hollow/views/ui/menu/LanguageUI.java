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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hollow.HollowKnight;
import com.hollow.controllers.ButtonController;
import com.hollow.models.language.LanguageObserver;
import com.hollow.controllers.manager.SaveManager;
import com.hollow.models.language.Language;
import com.hollow.views.ui.UI;

public class LanguageUI implements LanguageObserver, UI {
    public Stage stage;
    private HollowKnight game;
    private ButtonController controller;
    private final Array<TextButton> menuButtons = new Array<>();
    private Runnable onClose;

    public LanguageUI(HollowKnight game, Runnable onClose) {
        this.game = game;
        this.onClose = onClose;
        stage = new Stage(new FitViewport(game.SCREEN_WIDTH, game.SCREEN_HEIGHT));
        game.languageManager.addObserver(this);
        setupUI();
    }

    @Override
    public void setupUI() {
        menuButtons.clear();
        LabelStyle titleStyle = new LabelStyle(game.assetLoader.font, Color.WHITE);
        LabelStyle infoStyle = new LabelStyle(game.assetLoader.subFont, Color.WHITE);

        TextButtonStyle btnStyle = new TextButtonStyle();
        btnStyle.font = game.assetLoader.font;
        btnStyle.fontColor = Color.WHITE;
        btnStyle.overFontColor = new Color(0.85f, 0.85f, 0.85f, 1f);


        Table main = new Table();
        main.setFillParent(true);
        stage.addActor(main);

        Table content = new Table();

        Table title = new Table();
        Label t = new Label(game.languageManager.get("languageTitle"), titleStyle);
        t.setFontScale(1.5f);
        title.add(t).center().padTop(-40).padBottom(20).row();
        title.add(new Image(game.assetLoader.titleBottom)).center();
        content.add(title).padBottom(100).center().padTop(-80).row();

        Table splitTable = new Table();

        Table leftCol = new Table();
        leftCol.padBottom(30);
        addLangButton(leftCol, "English", Language.EN, btnStyle);
        addLangButton(leftCol, "Español", Language.ES, btnStyle);
        addLangButton(leftCol, "Français", Language.FR, btnStyle);
        addLangButton(leftCol, "Deutsch", Language.DE, btnStyle);
        addLangButton(leftCol, "Italiano", Language.IT, btnStyle);

        Table rightCol = new Table();
        Label currentLangTitle = new Label(game.languageManager.get("currentLanguage"), infoStyle);

        String currentLangStr = getLanguageDisplayName(game.data.getSettings().getLang());
        Label currentLangValue = new Label(currentLangStr, titleStyle);
        currentLangValue.setColor(new Color(0.9f, 0.8f, 0.5f, 1f));

        rightCol.add(currentLangTitle).center().padBottom(15).row();
        rightCol.add(currentLangValue).center();

        splitTable.add(leftCol).width(400).top().left().padRight(150);
        splitTable.add(rightCol).width(400).center();

        content.add(splitTable).padBottom(50).row();

        TextButton backBtn = new TextButton(game.languageManager.get("back"), btnStyle);
        menuButtons.add(backBtn);
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

        controller = new ButtonController(game, stage, menuButtons.toArray(TextButton.class));
    }

    private void addLangButton(Table table, String displayName, Language langEnum, TextButtonStyle style) {
        TextButton btn = new TextButton(displayName, style);
        menuButtons.add(btn);

        btn.setUserObject((Runnable) () -> {
            game.data.getSettings().setLang(langEnum);

            game.languageManager.load(langEnum);

            SaveManager.save(game.data);
        });

        table.add(btn).left().padLeft(20).padBottom(35).row();
    }

    private String getLanguageDisplayName(Language lang) {
        switch (lang) {
            case EN: return "English";
            case ES: return "Español";
            case FR: return "Français";
            case DE: return "Deutsch";
            case IT: return "Italiano";
            default: return "Unknown";
        }
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
    }

    @Override
    public void resize(int width, int height) {
        if (stage != null) stage.getViewport().update(width, height, true);
    }


    @Override
    public void onLanguageChanged() {
        stage.clear();
        setupUI();
    }
}
