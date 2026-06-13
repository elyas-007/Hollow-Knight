package com.hollow.views.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hollow.HollowKnight;
import com.hollow.controllers.ButtonController;

public class MainMenuScreen implements Screen {
    private final HollowKnight game;
    private Stage stage;
    private FitViewport viewport;
    private TextButton[] menuButtons;
    private ButtonController controller;

    public MainMenuScreen(HollowKnight game) {
        this.game = game;
    }

    @Override
    public void show() {
        viewport = new FitViewport(1280, 720);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        TextButtonStyle styleBtn = new TextButtonStyle();
        styleBtn.font = game.assetLoader.font;
        styleBtn.fontColor = Color.GRAY;
        styleBtn.overFontColor = Color.WHITE;

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        Image gameLogo = new Image(game.assetLoader.hollowKnightLogo);

        TextButton startBtn = new TextButton("Start Game", styleBtn);
        TextButton settingsBtn = new TextButton("Settings", styleBtn);
        TextButton guideBtn = new TextButton("Guide", styleBtn);
        TextButton achievementsBtn = new TextButton("achievements", styleBtn);
        TextButton quitBtn = new TextButton("Quit Game", styleBtn);

        startBtn.setUserObject((Runnable) () -> game.setScreen(new StartGameMenuScreen(game)));
        settingsBtn.setUserObject((Runnable) () -> game.setScreen(new SettingsMenuScreen(game)));
        guideBtn.setUserObject((Runnable) () -> game.setScreen(new GuideScreen()));
        achievementsBtn.setUserObject((Runnable) () -> game.setScreen(new AchievementsScreen()));
        quitBtn.setUserObject((Runnable) () -> Gdx.app.exit());


        rootTable.add(gameLogo).height(400).width(900).padBottom(60).row();
        rootTable.add(startBtn).padBottom(20).row();
        rootTable.add(settingsBtn).padBottom(20).row();
        rootTable.add(guideBtn).padBottom(20).row();
        rootTable.add(achievementsBtn).padBottom(20).row();
        rootTable.add(quitBtn).padBottom(20).row();

        menuButtons = new TextButton[]{startBtn, settingsBtn, guideBtn, achievementsBtn, quitBtn};
        controller = new ButtonController(game, stage, menuButtons);

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
        game.batch.draw(game.assetLoader.background, 0, 0, 1280, 720);
        game.batch.end();


        stage.act(delta);

        if (controller != null)
            controller.update(delta);

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
        if (stage != null)
            stage.dispose();
    }
}
