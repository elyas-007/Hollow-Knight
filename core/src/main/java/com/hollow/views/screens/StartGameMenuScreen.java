package com.hollow.views.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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
import com.hollow.models.GameData;
import com.hollow.models.SaveManager;

public class StartGameMenuScreen implements Screen {
    private final HollowKnight game;
    private Stage stage;
    private FitViewport viewport;
    private ButtonController controller;
    private Screen lastScreen;

    public StartGameMenuScreen(HollowKnight game, Screen lastScreen) {
        this.game = game;
        this.lastScreen = lastScreen;
    }

    @Override
    public void show() {
        viewport = new FitViewport(game.SCREEN_WIDTH, game.SCREEN_HEIGHT);
        stage = new Stage(viewport, game.batch);
        Gdx.input.setInputProcessor(stage);

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        TextButtonStyle styleBtn = new TextButtonStyle();
        styleBtn.font = game.assetLoader.font;
        styleBtn.fontColor = Color.WHITE;

        LabelStyle titleStyle = new LabelStyle(game.assetLoader.font, Color.WHITE);
        LabelStyle infoStyle = new LabelStyle(game.assetLoader.subFont, Color.WHITE);

        root.add(new Label("SELECT PROFILE", titleStyle)).padBottom(5).row();
        root.add(new Image(game.assetLoader.under_SelectProfile)).padBottom(40).row();

        Array<TextButton> buttons = new Array<>();

        for (int i = 1; i <= 4; i++) {
            final int slotId = i;

            GameData data = SaveManager.load(slotId);

            Table rowTable = new Table();

            TextButton profileBox = new TextButton("", styleBtn);
            profileBox.clearChildren();
            profileBox.setUserObject((Runnable) () -> startGame(data));
            buttons.add(profileBox);


            Stack layerStack = new Stack();
            layerStack.setFillParent(true);

            if (!data.isEmpty) {
                NinePatchDrawable bgDrawable = new NinePatchDrawable(
                    data.location.equals("GREENPATH") ? game.assetLoader.saveBackground_greenPath : game.assetLoader.saveBackground_forgotten
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

            if (!data.isEmpty) {
                Table leftInfo = new Table();
                leftInfo.add(new Label(slotId + ".", titleStyle)).left().padLeft(10).padRight(5);
                leftInfo.add(new Image(game.assetLoader.healthFrame)).size(60, 60).padRight(15);

                Table stateTable = new Table();
                Table maskRow = new Table();
                for (int m = 0; m < data.mask; m++)
                    maskRow.add(new Image(game.assetLoader.mask)).size(20, 20).padRight(2);

                stateTable.add(maskRow).left().padBottom(5).row();


                leftInfo.add(stateTable).left();

                Table rightInfo = new Table();
                rightInfo.add(new Label(data.location.toUpperCase(), infoStyle)).right().padBottom(5).row();
                String timeStr = formatPlayTime(data.playTime); //evaluateTime
                rightInfo.add(new Label(timeStr, infoStyle)).right();

                content.add(leftInfo).left();
                content.add().expandX().fillX();
                content.add(rightInfo).right().padRight(20);

            } else {
                content.add(new Label("NEW GAME", titleStyle)).left().padLeft(15);
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

            if (!data.isEmpty) {
                TextButton clearBtn = new TextButton("CLEAR SAVE", styleBtn);

                clearBtn.setUserObject((Runnable) () -> {
                    SaveManager.clearSave(slotId);
                    game.setScreen(this);
                });
                buttons.add(clearBtn);

                rowTable.add(clearBtn).width(150).padLeft(50);
            } else {
                rowTable.add().width(150).padLeft(50);
            }

            root.add(rowTable).padBottom(30).row();
        }

        TextButton backBtn = new TextButton("BACK", styleBtn);
        backBtn.setUserObject((Runnable) () -> {
            game.setScreen(lastScreen);
        });
        buttons.add(backBtn);
        root.add(backBtn).padTop(30);

        TextButton[] menuItems = buttons.toArray(TextButton.class);
        controller = new ButtonController(game, stage, menuItems);

        if (game.assetLoader.titleTheme != null && !game.assetLoader.titleTheme.isPlaying() && game.settings.isMusicOn) {
            game.assetLoader.titleTheme.setLooping(true);
            game.assetLoader.titleTheme.setVolume(game.settings.musicVolume);
            game.assetLoader.titleTheme.play();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(stage.getCamera().combined);
        game.batch.begin();
        game.menuBackground.updateAndDraw(game.batch, delta, game.settings.brightness, false);
        game.batch.end();

         stage.act(delta);

         if (controller != null)
             controller.update(delta);

         stage.draw();
    }


    @Override public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
    @Override public void pause() {

    }
    @Override public void resume() {

    }
    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);

        if (game.assetLoader.titleTheme != null) {
            game.assetLoader.titleTheme.stop();
        }
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
    }

    private void startGame(GameData data) {
        if (data.isEmpty) data.isEmpty = false;

        game.activeSave = data;
        game.setScreen(new GameScreen(game, data.location));
    }

    private String formatPlayTime(float time) {
        int h = (int) time / 60;
        int m = (int) time % 60;
        return h + "H " + m + "M";
    }
}
