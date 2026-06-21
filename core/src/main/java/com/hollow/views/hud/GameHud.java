package com.hollow.views.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hollow.HollowKnight;
import com.hollow.models.entities.Knight.Knight;

public class GameHud implements Disposable {
    public Stage stage;
    private Viewport viewport;


    private Array<MaskWidget> maskWidgets;
    private SoulVessel soulVessel;
    private Label geoLabel;

    private int lastMasks;
    private int lastGeo;

    public GameHud(HollowKnight game, Knight knight) {
        viewport = new FitViewport(1280, 720, new OrthographicCamera());
        stage = new Stage(viewport, game.batch);


        Table root = new Table();
        root.setFillParent(true);
        root.top().left().padTop(20).padLeft(20);
        stage.addActor(root);

        soulVessel = new SoulVessel(game.assetLoader.healthFrameHud, game.assetLoader.soul); // bug

        maskWidgets = new Array<>();
        Table maskTable = new Table();
        lastMasks = knight.getCurrentMasks();

        for (int i = 0; i < knight.getMaxMasks(); i++) {
            MaskWidget mask = new MaskWidget(
                game.assetLoader.fullMask,
                game.assetLoader.emptyMask,
                game.assetLoader.maskShatterAnim
            );
            maskWidgets.add(mask);
            maskTable.add(mask).size(55, 55).padRight(5);
        }

        lastGeo = 0;
        Label.LabelStyle labelStyle = new Label.LabelStyle(game.assetLoader.font, Color.WHITE);
        geoLabel = new Label(String.valueOf(lastGeo), labelStyle);

        Table geoTable = new Table();
        geoTable.add(new Image(game.assetLoader.geoHudGame)).size(40,40).padRight(8);
        geoTable.add(geoLabel).left();

        Table rightStats = new Table();
        rightStats.add(maskTable).left().padBottom(2).row();
        rightStats.add(geoTable).left().padLeft(25);

        root.add(soulVessel).size(160, 160).padRight(-10);
        root.add(rightStats).left().top().padTop(45);
    }

    public void update(Knight knight, float delta) {
        stage.act(delta);

        soulVessel.setSoul(knight.getSoul());

        //update geo


        int currentMask = knight.getCurrentMasks();

        while (currentMask < lastMasks) {
            for (int i = maskWidgets.size - 1; i >= 0; i--) {
                if (maskWidgets.get(i).getState() == MaskWidget.MaskState.FULL) {
                    maskWidgets.get(i).shatter();
                    break;
                }
            }
            lastMasks--;
        }

        while (currentMask > lastMasks) {
            for (int i = 0; i < maskWidgets.size; i++) {
                if (maskWidgets.get(i).getState() != MaskWidget.MaskState.FULL) {
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
        stage.dispose();
    }
}
