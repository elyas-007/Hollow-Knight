package com.hollow.views.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hollow.HollowKnight;
import com.hollow.models.Achievement;
import com.hollow.models.AchievementManager;
import com.hollow.models.AchievementObserver;
import com.hollow.models.entities.Knight.Knight;

public class GameHud implements Disposable, AchievementObserver {
    public Stage stage;
    private Viewport viewport;


    private Array<MaskWidget> maskWidgets;
    private SoulVessel soulVessel;

    private Table popupTable;
    private Label achievementTitleLabel;
    private Label achievementDescLabel;

    private Label cheatLabel;
    private Label itemPopupLabel;

    private int lastMasks;

    public GameHud(HollowKnight game, Knight knight) {
        viewport = new FitViewport(game.SCREEN_WIDTH, game.SCREEN_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, game.batch);


        Table root = new Table();
        root.setFillParent(true);
        root.top().left().padTop(20).padLeft(20);
        stage.addActor(root);

        soulVessel = new SoulVessel(
            game.assetLoader.healthFrameHud,
            game.assetLoader.soulIdleAnim,
            game.assetLoader.orbMaskTexture,
            game.assetLoader.liquidShader
        );

        maskWidgets = new Array<>();
        Table maskTable = new Table();
        lastMasks = knight.getCurrentMasks();

        for (int i = 0; i < knight.getMaxMasks(); i++) {
            MaskWidget mask = new MaskWidget(
                game.assetLoader.fullMask,
                game.assetLoader.emptyMask,
                game.assetLoader.maskShatterAnim,
                game.assetLoader.maskRefillAnim,
                game.assetLoader.maskShineAnim
            );
            maskWidgets.add(mask);
            maskTable.add(mask).size(70, 70).padRight(3);
        }

        Table rightStats = new Table();
        rightStats.add(maskTable).left().padTop(20).padBottom(2).row();

        root.add(soulVessel).size(180, 180).padRight(-45);
        root.add(rightStats).left().top().padTop(35);

        setupAchievementPopup(game);
        setupCheatPopup(game);
        AchievementManager.getInstance().addObserver(this);

        itemPopupLabel = new Label("", new LabelStyle(game.assetLoader.font, Color.CYAN));
        itemPopupLabel.setAlignment(Align.center);
        itemPopupLabel.setPosition(viewport.getWorldWidth() / 2f, viewport.getWorldHeight() / 2f);
        itemPopupLabel.getColor().a = 0f;
        stage.addActor(itemPopupLabel);
    }

    private void setupCheatPopup(HollowKnight game) {
        LabelStyle style = new LabelStyle(game.assetLoader.font, Color.WHITE);
        cheatLabel = new Label("", style);
        cheatLabel.setAlignment(Align.right);

        cheatLabel.setPosition(viewport.getWorldWidth() + 400f, viewport.getWorldHeight() - 40f, Align.topRight);

        stage.addActor(cheatLabel);
    }

    public void showCheatPopup(String cheatName, boolean isEnabled) {
        cheatLabel.clearActions();

        String status = isEnabled ? "Enabled" : "Disabled";
        cheatLabel.setText(cheatName + ": " + status);

        cheatLabel.setColor(isEnabled ? Color.GREEN : Color.RED);

        cheatLabel.addAction(Actions.sequence(
            Actions.moveToAligned(viewport.getWorldWidth() - 30f, viewport.getWorldHeight() - 40f, Align.topRight, 0.3f, Interpolation.exp10Out),
            Actions.delay(2f),
            Actions.moveToAligned(viewport.getWorldWidth() + 400, viewport.getWorldHeight() - 40f, Align.topRight, 0.3f, Interpolation.exp10In)
        ));
    }

    public void showItemPopup(String text) {
        itemPopupLabel.setText(text);
        itemPopupLabel.clearActions();
        itemPopupLabel.getColor().a = 1f;
        itemPopupLabel.setPosition(viewport.getWorldWidth() / 2f, viewport.getWorldHeight() / 2f - 50f);

        itemPopupLabel.addAction(Actions.sequence(
            Actions.moveBy(0, 80f, 1.5f, Interpolation.exp10Out),
            Actions.delay(1.5f),
            Actions.fadeOut(1f)
        ));
    }

    public void setupAchievementPopup(HollowKnight game) {
        LabelStyle titleStyle = new LabelStyle(game.assetLoader.font, Color.YELLOW);
        LabelStyle descStyle = new LabelStyle(game.assetLoader.font, Color.WHITE);

        achievementTitleLabel = new Label("", titleStyle);
        achievementTitleLabel.setAlignment(Align.center);

        achievementDescLabel = new Label("", descStyle);
        achievementDescLabel.setAlignment(Align.center);
        achievementDescLabel.setFontScale(0.8f);

        popupTable = new Table();
        popupTable.setTransform(true);
        popupTable.setOrigin(Align.center);

        popupTable.add(new Label("Achievement Unlocked!", new LabelStyle(game.assetLoader.font, Color.GOLD))).padBottom(5).row();
        popupTable.add(achievementTitleLabel).padBottom(5).row();
        popupTable.add(achievementDescLabel).row();

        popupTable.setPosition(viewport.getWorldWidth() / 2f, -150f, Align.center);

        stage.addActor(popupTable);
    }

    public void update(Knight knight, float delta) {
        stage.act(delta);

        soulVessel.setSoul(knight.getSoul());

        //update geo


        int currentMask = knight.getCurrentMasks();

        while (currentMask < lastMasks) {
            for (int i = maskWidgets.size - 1; i >= 0; i--) {
                MaskWidget.MaskState s = maskWidgets.get(i).getState();
                if (s == MaskWidget.MaskState.FULL || s == MaskWidget.MaskState.SHINING) {
                    maskWidgets.get(i).shatter();
                    break;
                }
            }
            lastMasks--;
        }

        while (currentMask > lastMasks) {
            for (int i = 0; i < maskWidgets.size; i++) {
                MaskWidget.MaskState s = maskWidgets.get(i).getState();
                if (s != MaskWidget.MaskState.FULL && s != MaskWidget.MaskState.SHINING && s != MaskWidget.MaskState.REFILLING) {
                    maskWidgets.get(i).fill();
                    break;
                }
            }
            lastMasks++;
        }
    }

    public void draw() {
        stage.draw();
    }
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        AchievementManager.getInstance().removeObserver(this);
        stage.dispose();
    }

    @Override
    public void onAchievementsUnlocked(Achievement a) {
        achievementTitleLabel.setText(a.title);
        achievementDescLabel.setText(a.dec);

        popupTable.clearActions();
        popupTable.setPosition(viewport.getWorldWidth() / 2f, -150f, Align.center);

        popupTable.addAction(Actions.sequence(
            Actions.moveToAligned(viewport.getWorldWidth() / 2f, 100f, Align.center, 0.6f, Interpolation.exp10Out),
            Actions.delay(3.5f),
            Actions.moveToAligned(viewport.getWorldWidth() / 2f, -150f, Align.center, 0.6f, Interpolation.exp10In)
        ));
    }
}
