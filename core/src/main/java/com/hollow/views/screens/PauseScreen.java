package com.hollow.views.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hollow.HollowKnight;
import com.hollow.controllers.ButtonController;
import com.hollow.models.SaveManager;

public class PauseScreen implements Screen {
    private HollowKnight game;
    private GameScreen gameScreen;
    private Stage stage;
    private FitViewport viewport;
    private ShapeRenderer shapeRenderer;
    private ButtonController controller;

    public PauseScreen(HollowKnight game, GameScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;
    }

    @Override
    public void show() {
        viewport = new FitViewport(1920, 1080);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        shapeRenderer = new ShapeRenderer();

        TextButton.TextButtonStyle styleBtn = new TextButton.TextButtonStyle();
        styleBtn.font = game.assetLoader.font;
        styleBtn.fontColor = Color.WHITE;

        Table root = new Table();
        root.setFillParent(true);
        Table content = new Table();
        root.add(new Image(game.assetLoader.pauseTop)).colspan(2).top().padBottom(30).center().row();

        TextButton continueBtn = new TextButton("Continue", styleBtn);
        TextButton settingsBtn = new TextButton("Settings", styleBtn);
        TextButton guideBtn = new TextButton("Guide", styleBtn);
        TextButton cheatBtn = new TextButton("Cheat", styleBtn);
        TextButton quitBtn = new TextButton("Quit To Menu", styleBtn);

        continueBtn.setUserObject((Runnable) this::resumeGame);
        settingsBtn.setUserObject((Runnable) () -> game.setScreen(new SettingsMenuScreen(game, this)));
        guideBtn.setUserObject((Runnable) () -> game.setScreen(new GuideScreen()));
        cheatBtn.setUserObject((Runnable) () -> game.setScreen(new AchievementsScreen()));
        quitBtn.setUserObject((Runnable) this::quitGame);



        content.add(continueBtn).center().fillX().uniformX().padTop(10).padLeft(40).row();
        content.add(settingsBtn).center().fillX().uniformX().padTop(10).padLeft(40).row();
        content.add(guideBtn).center().fillX().uniformX().padTop(10).padLeft(40).row();
        content.add(cheatBtn).center().fillX().uniformX().padTop(10).padLeft(40).row();
        content.add(quitBtn).center().fillX().uniformX().padTop(10).padLeft(40).row();

        TextButton[] menuButtons = new TextButton[]{continueBtn, settingsBtn, guideBtn, cheatBtn, quitBtn};
        controller = new ButtonController(game, stage, menuButtons);

        root.add(content).center().row();

        root.add(new Image(game.assetLoader.pauseBottom)).colspan(2).padTop(30).bottom().center().row();

        stage.addActor(root);

    }

    @Override
    public void render(float delta) {
        gameScreen.drawWorld();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 0, 0, 0.7f));
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        stage.act(delta);

        if (controller != null)
            controller.update(delta);

        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            resumeGame();
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        stage.dispose();
        shapeRenderer.dispose();
    }

    private void resumeGame() {
        game.setScreen(gameScreen);
    }

    public void quitGame() {
        if (game.activeSave != null) {
            SaveManager.save(game.activeSave);
            game.activeSave = null;
        }
        game.setScreen(new MainMenuScreen(game));
    }
}
