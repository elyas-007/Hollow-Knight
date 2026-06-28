package com.hollow.views.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hollow.HollowKnight;
import com.hollow.controllers.ButtonController;
import com.hollow.models.Achievement;
import com.hollow.models.AchievementManager;

public class AchievementsUI {
    public Stage stage;
    private HollowKnight game;
    private Runnable onClose;
    private ButtonController controller;
    private TextButton[] menuButtons;


    public AchievementsUI(HollowKnight game, Runnable onClose) {
        this.game = game;
        stage = new Stage(new FitViewport(game.SCREEN_WIDTH, game.SCREEN_HEIGHT));
        this.onClose = onClose;
        setupUI();
    }

    private void setupUI() {
        Table main = new Table();
        main.setFillParent(true);
        main.top();

        Table topBar = new Table();

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = game.assetLoader.font;
        btnStyle.fontColor = Color.WHITE;

        TextButton backBtn = new TextButton("Back", btnStyle);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onClose.run();
            }
        });

        topBar.add(backBtn).left().pad(20).padLeft(50).padTop(0).row();

        Table titleTable = new Table();
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.assetLoader.font, Color.GOLD);
        Label t = new Label("ACHIEVEMENTS", titleStyle);
        t.setFontScale(1.5f);
        titleTable.add(t).row();
        titleTable.add(new Image(game.assetLoader.top_menu)).padTop(10);

        topBar.add(titleTable).expandX().center();

        main.add(topBar).fillX().padTop(30).padBottom(200).row();


        Table gridTable = new Table();
        Achievement[] achievements = Achievement.values();

        for (int i = 0; i < 3 && i < achievements.length; i++) {
            gridTable.add(createAchievementCard(achievements[i])).pad(20);
        }
        gridTable.row();

        Table secondRow = new Table();
        for (int i = 3; i < achievements.length; i++) {
            secondRow.add(createAchievementCard(achievements[i])).pad(20);
        }
        gridTable.add(secondRow).colspan(3).center().padTop(10).row();

        main.add(gridTable).expand().top();
        stage.addActor(main);

        TextButton[] menuButtons = new TextButton[]{ backBtn };
        controller = new ButtonController(game, stage, menuButtons);
    }

    private Table createAchievementCard(Achievement ach) {
        Table card = new Table();
        card.setTouchable(Touchable.enabled);
        Stack stack = new Stack();

        stack.setTransform(true);
        stack.setOrigin(Align.center);
        stack.setTouchable(Touchable.disabled);

        boolean isUnlocked = AchievementManager.getInstance().isUnlocked(ach);

        Image frameDecor = new Image(game.assetLoader.achievementFrameTex);
        if (!isUnlocked) {
            frameDecor.setColor(Color.DARK_GRAY);
        }
        stack.add(frameDecor);

        Table contentTable = new Table();
        contentTable.padLeft(55);

        Texture iconTexture = getTextureForAchievement(ach, isUnlocked);
        Image icon = new Image(iconTexture);
        if (!isUnlocked) {
            icon.setColor(Color.DARK_GRAY);
        }

        Color titleColor = isUnlocked ? Color.WHITE : Color.GRAY;
        Color descColor = isUnlocked ? Color.LIGHT_GRAY : Color.DARK_GRAY;

        Label nameLabel = new Label(ach.title, new Label.LabelStyle(game.assetLoader.font, titleColor));
        nameLabel.setAlignment(Align.left);

        Label descLabel = new Label(isUnlocked ? ach.dec : "Locked Achievement...", new Label.LabelStyle(game.assetLoader.font, descColor));
        descLabel.setFontScale(0.75f);
        descLabel.setAlignment(Align.left);
        descLabel.setWrap(true);

        Table topRow = new Table();
        topRow.add(icon).size(60, 60).padRight(15);
        topRow.add(nameLabel).left();

        contentTable.add(topRow).left().padBottom(12).row();
        contentTable.add(descLabel).width(240).left().padTop(12).padLeft(75);

        stack.add(contentTable);
        card.add(stack).width(400).height(140);

        card.addListener(new ClickListener() {
            private boolean isHovered = false;

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                if (pointer == -1 && !isHovered) {
                    isHovered = true;

                    if (game.assetLoader.buttonHover != null && game.settings.isSfxOn) {
                        game.assetLoader.buttonHover.play(game.settings.musicVolume);
                    }

                    stack.clearActions();
                    stack.addAction(Actions.scaleTo(1.05f, 1.05f, 0.1f, Interpolation.fade));
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                if (pointer == -1 && isHovered) {
                    isHovered = false;

                    stack.clearActions();
                    stack.addAction(Actions.scaleTo(1f, 1f, 0.1f, Interpolation.fade));
                }
            }
        });

        return card;
    }

    private Texture getTextureForAchievement(Achievement ach, boolean isUnlocked) {
        if (!isUnlocked) {
            return game.assetLoader.achievementSecretTex;
        }

        return switch (ach) {
            case COMPLETION -> game.assetLoader.achievementCompleteTex;
            case SPEEDRUN -> game.assetLoader.achievementSpeedrunTex;
            case TRUE_HUNTER -> game.assetLoader.achievementHunterTex;
            case DEFEAT_FALSE_KNIGHT -> game.assetLoader.achievementFalseKnightTex;
            case SHADOW_MASTER -> game.assetLoader.achievementEndingATex;
            default -> game.assetLoader.achievementSecretTex;
        };
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
        stage.dispose();
    }
}
