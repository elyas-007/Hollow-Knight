package com.hollow.views.ui.hud;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hollow.HollowKnight;
import com.hollow.models.language.LanguageObserver;
import com.hollow.controllers.manager.SaveManager;
import com.hollow.views.screen.GameScreen;
import com.hollow.views.screen.MainMenuScreen;
import com.hollow.views.ui.UI;

public class EndingUI implements LanguageObserver, UI {
    public Stage stage;
    public boolean isVisible = false;
    private HollowKnight game;
    private GameScreen screen;

    private Label timeLabel;
    private Label killsLabel;
    private Label deathsLabel;
    private TextButton continueBtn;
    private TextButton menuBtn;

    public EndingUI(HollowKnight game, GameScreen screen, InputMultiplexer multiplexer) {
        this.game = game;
        this.screen = screen;
        this.stage = new Stage(new FitViewport(game.SCREEN_WIDTH, game.SCREEN_HEIGHT), game.batch);


        setupUI();
    }

    @Override
    public void setupUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        Label.LabelStyle style = new Label.LabelStyle(game.assetLoader.font, Color.WHITE);
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = game.assetLoader.font;
        btnStyle.fontColor = Color.LIGHT_GRAY;
        btnStyle.overFontColor = Color.WHITE;

        timeLabel = new Label("", style);
        killsLabel = new Label("", style);
        deathsLabel = new Label("", style);

        continueBtn = new TextButton(game.languageManager.get("Continue Game"), btnStyle);
        menuBtn = new TextButton(game.languageManager.get("Return to Main Menu"), btnStyle);

        timeLabel.getColor().a = 0f;
        killsLabel.getColor().a = 0f;
        deathsLabel.getColor().a = 0f;
        continueBtn.getColor().a = 0f;
        menuBtn.getColor().a = 0f;

        table.add(timeLabel).padBottom(20).row();
        table.add(killsLabel).padBottom(20).row();
        table.add(deathsLabel).padBottom(60).row();

        Table btnTable = new Table();
        btnTable.add(continueBtn).padRight(40);
        btnTable.add(menuBtn);

        table.add(btnTable);

        continueBtn.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                screen.isEndingSequence = false;
                screen.knight.isEndingMode = false;
                isVisible = false;
                float respawnX = game.data.getActiveSlot().getFalseKnightDeathX();
                float respawnY = game.data.getActiveSlot().getFalseKnightDeathY();
                screen.knight.fullRespawn(respawnX, respawnY);
                screen.stopBossMusic();
            }
        });

        menuBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                SaveManager.save(game.data);
                game.setScreen(new MainMenuScreen(game));
            }
        });
    }

    public void showUI(InputMultiplexer multiplexer) {
        isVisible = true;
        multiplexer.addProcessor(stage);

        float rawTime = game.data.getActiveSlot().getPlayTime();
        int minutes = (int) (rawTime / 60);
        int seconds = (int) (rawTime % 60);
        String timeFormatted = String.format("%02d:%02d", minutes, seconds);

        timeLabel.setText(game.languageManager.get("Total Time Played") + ": " + timeFormatted);
        killsLabel.setText(game.languageManager.get("Enemies Defeated") + ": " + game.data.getTotalEnemyKilled());
        deathsLabel.setText(game.languageManager.get("Total Deaths") + ": " + game.data.getActiveSlot().getDeathCount());

        timeLabel.addAction(Actions.sequence(Actions.delay(0.5f), Actions.fadeIn(1f, Interpolation.fade)));
        killsLabel.addAction(Actions.sequence(Actions.delay(1.5f), Actions.fadeIn(1f, Interpolation.fade)));
        deathsLabel.addAction(Actions.sequence(Actions.delay(2.5f), Actions.fadeIn(1f, Interpolation.fade)));

        continueBtn.addAction(Actions.sequence(Actions.delay(4.0f), Actions.fadeIn(1.5f)));
        menuBtn.addAction(Actions.sequence(Actions.delay(4.0f), Actions.fadeIn(1.5f)));
    }

    public void act(float delta) {
        stage.act(delta);
    }

    public void draw() {
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void onLanguageChanged() {
        stage.clear();
        setupUI();
    }
}
