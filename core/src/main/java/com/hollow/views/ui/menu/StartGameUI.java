package com.hollow.views.ui.menu;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hollow.HollowKnight;
import com.hollow.controllers.ButtonController;
import com.hollow.controllers.manager.LanguageManager;
import com.hollow.models.data.SlotData;
import com.hollow.models.language.LanguageObserver;
import com.hollow.views.ui.UI;
import com.hollow.views.screen.LoadingScreen;

public class StartGameUI implements LanguageObserver, UI {
    public Stage stage;
    private final HollowKnight game;
    private ButtonController controller;
    private final Runnable onClose;

    private final Array<TextButton> buttons = new Array<>();

    public StartGameUI(HollowKnight game, Runnable onClose) {
        this.game = game;
        this.onClose = onClose;
        stage = new Stage(new FitViewport(game.SCREEN_WIDTH, game.SCREEN_HEIGHT));
        game.languageManager.addObserver(this);
        setupUI();
    }

    @Override
    public void setupUI() {
        buttons.clear();

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        TextButtonStyle styleBtn = new TextButtonStyle();
        styleBtn.font = game.assetLoader.font;
        styleBtn.fontColor = Color.WHITE;

        LabelStyle titleStyle = new LabelStyle(game.assetLoader.font, Color.WHITE);
        LabelStyle infoStyle = new LabelStyle(game.assetLoader.subFont, Color.WHITE);

        Label title = new Label(game.languageManager.get("selectProfile"), titleStyle);
        title.setFontScale(1.5f);
        root.add(title).padBottom(5).padTop(-80f).row();
        root.add(new Image(game.assetLoader.under_SelectProfile)).padBottom(50).row();

        for (int i = 1; i <= 4; i++) {
            root.add(createSlotRow(i, styleBtn, titleStyle, infoStyle)).padBottom(30).row();
        }

        TextButton backBtn = new TextButton(game.languageManager.get("back"), styleBtn);
        backBtn.setUserObject(onClose);
        buttons.add(backBtn);
        root.add(backBtn).padTop(30);

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

        TextButton[] menuItems = buttons.toArray(TextButton.class);
        controller = new ButtonController(game, stage, menuItems);
    }

    private Table createSlotRow(int slotId, TextButtonStyle styleBtn, LabelStyle titleStyle, LabelStyle infoStyle) {
        SlotData data = game.data.getSlot(slotId);
        Table rowTable = new Table();

        TextButton profileBox = new TextButton("", styleBtn);
        profileBox.clearChildren();
        profileBox.setUserObject((Runnable) () -> startGame(slotId));
        buttons.add(profileBox);

        Stack layerStack = new Stack();
        layerStack.setFillParent(true);

        if (!data.isEmpty()) {
            NinePatchDrawable bgDrawable = new NinePatchDrawable(
                "GREEN_PATH".equals(data.getLocation()) ? game.assetLoader.saveBackground_greenPath :
                    game.assetLoader.saveBackground_forgotten
            );
            Image bgImage = new Image(bgDrawable);
            bgImage.setScaling(Scaling.stretch);
            Table bgTable = new Table();
            bgTable.add(bgImage).expand().fill().padLeft(20);
            layerStack.add(bgTable);
        }

        Table content = new Table();
        content.setTouchable(Touchable.disabled);
        content.padLeft(90).padRight(20);

        if (!data.isEmpty()) {
            Table leftInfo = buildLeftInfo(slotId, data, titleStyle);
            Table rightInfo = buildRightInfo(data, infoStyle);

            content.add(leftInfo).left();
            content.add().expandX().fillX();
            content.add(rightInfo).right().padRight(20);
        } else {
            content.add(new Label(game.languageManager.get("newGame"), titleStyle)).left().padLeft(15);
            content.add().expandX().fillX();
        }

        layerStack.add(content);

        Table frameTable = new Table();
        frameTable.setTouchable(Touchable.disabled);
        frameTable.add(new Image(game.assetLoader.profile_fleur)).left();
        frameTable.add().expandX();
        layerStack.add(frameTable);

        profileBox.add(layerStack).expand().fill();
        rowTable.add(profileBox).size(650, 95).padLeft(10);

        if (!data.isEmpty()) {
            TextButton clearBtn = new TextButton(game.languageManager.get("clearSave"), styleBtn);
            clearBtn.setUserObject((Runnable) () -> {
                game.data.clearSlot(slotId);
                stage.clear();
                setupUI();
            });
            buttons.add(clearBtn);
            rowTable.add(clearBtn).width(220).padLeft(80);
        } else {
            rowTable.add().width(220).padLeft(80);
        }

        return rowTable;
    }

    private Table buildLeftInfo(int slotId, SlotData data, LabelStyle titleStyle) {
        Table leftInfo = new Table();
        leftInfo.add(new Label(slotId + ".", titleStyle)).left().padLeft(10).padRight(5);
        leftInfo.add(new Image(game.assetLoader.healthFrame)).size(60, 60).padRight(15);

        Table stateTable = new Table();
        Table maskRow = new Table();
        for (int m = 0; m < data.getMask(); m++) {
            maskRow.add(new Image(game.assetLoader.mask)).size(20, 20).padRight(2);
        }

        stateTable.add(maskRow).left().padBottom(5).row();
        leftInfo.add(stateTable).left();

        return leftInfo;
    }

    private Table buildRightInfo(SlotData data, LabelStyle infoStyle) {
        Table rightInfo = new Table();
        rightInfo.add(new Label(data.getLocation().toUpperCase(), infoStyle)).right().padBottom(5).row();
        String timeStr = formatPlayTime(data.getPlayTime());
        rightInfo.add(new Label(timeStr, infoStyle)).right();
        return rightInfo;
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
        LanguageManager.removeObserver(this);
        if (stage != null) stage.dispose();
    }

    @Override
    public void onLanguageChanged() {
        stage.clear();
        setupUI();
    }

    private void startGame(int slotId) {
        game.data.setActiveSlotId(slotId);
        SlotData activeSlot = game.data.getActiveSlot();

        if (activeSlot.isEmpty()) {
            activeSlot.setEmpty(false);
        }

        game.setScreen(new LoadingScreen(game, activeSlot.getLocation()));
    }

    private String formatPlayTime(float time) {
        int m = (int) time / 60;
        int s = (int) time % 60;
        return m + "M " + s + "S";
    }
}
