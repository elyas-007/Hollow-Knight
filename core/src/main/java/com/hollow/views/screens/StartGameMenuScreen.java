package com.hollow.views.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Cullable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hollow.HollowKnight;
import com.hollow.controllers.ButtonController;
import com.hollow.models.GameData;
import com.hollow.models.SaveManager;

public class StartGameMenuScreen implements Screen {
    private final HollowKnight game;
    private Stage stage;
    private FitViewport viewport;
    private TextButton[] menuButtons;
    private ButtonController controller;

    public StartGameMenuScreen(HollowKnight game) {
        this.game = game;
    }

    @Override
    public void show() {
        viewport = new  FitViewport(1280, 720);
        stage = new Stage(viewport, game.batch);
        Gdx.input.setInputProcessor(stage);

        Table root = new Table();
        root.setFillParent(true);
        Table content = new Table();
        stage.addActor(root);

        TextButton.TextButtonStyle styleBtn = new TextButton.TextButtonStyle();
        styleBtn.font = game.assetLoader.font;
        styleBtn.fontColor = Color.WHITE;

        LabelStyle titleStyle = new LabelStyle(game.assetLoader.font, Color.WHITE);
        LabelStyle infoStyle = new LabelStyle(game.assetLoader.subFont, Color.WHITE);

        Label title = new Label("SELECT PROFILE", titleStyle);
        root.add(title).padBottom(10).colspan(3).row();
        root.add(new Image(game.assetLoader.under_SelectProfile)).colspan(3).padBottom(30).row();

        for (int i = 1; i <= 4; i++) {
            final int slotId = i;

            GameData data = SaveManager.load(slotId);

            Table rowTable = new Table();
            Table numberBox = new Table();
            numberBox.add(new Image(game.assetLoader.profile_fleur)).left();
            Label numberLabel = new Label(slotId + ".", titleStyle);
            numberBox.add(numberLabel).padRight(15);
            rowTable.add(numberBox).width(80).right();

            Table profileBox = new Table();
            profileBox.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    startGame(data);
                }
            });

            if (!data.isEmpty) {
                if (data.location.equals("GREENPATH")) {
                    rowTable.setBackground(new NinePatchDrawable(game.assetLoader.saveBackground_greenPath));
                } else if (data.location.equals("CROSSROAD")) {
                    rowTable.setBackground(new NinePatchDrawable(game.assetLoader.saveBackground_forgotten));
                }
                Table leftInfo = new Table();
                leftInfo.add(new Image(game.assetLoader.healthFrame)).size(64, 64).left();
                Table stateTable = new Table();
                Table maskRow = new Table();
                for (int m = 0; m < data.mask; m++)
                    maskRow.add(new Image(game.assetLoader.mask)).size(24, 24).padRight(2);

                stateTable.add(maskRow).left().padBottom(5).row();
                Table geoRow = new Table();
                geoRow.add(new Image(game.assetLoader.geoHud)).size(20, 20).padRight(5);
                geoRow.add(new Label(String.valueOf(data.geo), infoStyle));
                stateTable.add(geoRow).left();

                leftInfo.add(stateTable).padLeft(15);

                Table rightInfo = new Table();
                rightInfo.add(new Label(data.location.toUpperCase(), infoStyle)).right().top().row();

                String timeStr = formatPlayTime(data.playTime); //evaluateTime
                rightInfo.add(new Label(timeStr, infoStyle)).right().bottom().padTop(15);

                profileBox.add(leftInfo).expand().fill().left().padLeft(20);
                profileBox.add(rightInfo).expand().fill().right().padRight(20);

                rowTable.add(profileBox).size(650, 90).padLeft(10);

                TextButton clearBtn = new TextButton("CLEAR SAVE", styleBtn);
                clearBtn.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        SaveManager.clearSave(slotId);
                        game.setScreen(new StartGameMenuScreen(game));
                    }
                });
                rowTable.add(clearBtn).width(150).padLeft(30).right();
            } else {
                profileBox.add(new Image(game.assetLoader.profile_fleur)).left();
                profileBox.add(new Label("NEW GAME", titleStyle)).left().padLeft(30);
                rowTable.add(profileBox).size(650, 90).padLeft(10);
                rowTable.add().width(150).padLeft(30);
            }
            root.add(rowTable).padBottom(15).row();
        }

        TextButton backBtn = new TextButton("BACK", styleBtn);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        root.add(backBtn).colspan(3).padTop(30);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
         game.batch.begin();
         game.batch.draw(game.assetLoader.background, 0, 0, 1280, 720);
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

    }

    private void startGame(GameData data) {
        if (data.isEmpty) {
            data.isEmpty = false;
            data.mask = 5;
            data.location = "CROSSROAD";
            data.geo = 0;
            data.playTime = 0;
            SaveManager.save(data);
        }
        game.setScreen(new GameScreen(game));
    }

    private String formatPlayTime(float time) {
        int h = (int) time / 60;
        int m = (int) time % 60;
        return h + "H " + m + "M";
    }
}
