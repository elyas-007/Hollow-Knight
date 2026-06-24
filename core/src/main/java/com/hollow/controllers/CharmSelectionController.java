package com.hollow.controllers;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.hollow.HollowKnight;
import com.hollow.views.hud.InventoryUI;

public class CharmSelectionController {
    private final HollowKnight  game;
    private final Stage stage;
    private final Array<Stack> charmSlots;
    private final Table selectorTable;
    private final InventoryUI inventoryUI;

    private int selectedItem = 0;
    private final int cols;
    private float stateTime = 0f;
    private final Vector2 vec = new Vector2();

    public CharmSelectionController(HollowKnight game, Stage stage, Array<Stack> charmSlots,
                                    Table selectorTable, int cols, InventoryUI inventoryUI) {
        this.game = game;
        this.stage = stage;
        this.charmSlots = charmSlots;
        this.selectorTable = selectorTable;
        this.cols = cols;
        this.inventoryUI = inventoryUI;

        setupInputHandling();
        setupMouseHoverListeners();

        if (charmSlots.size > 0) {
            updateSelectionState(0, false);
        }
    }

    private void setupInputHandling() {
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                int total = charmSlots.size;
                if (total == 0) return false;

                int col = selectedItem % cols;
                int numRows = (int) Math.ceil((double) total / cols);
                int newIndex = selectedItem;

                if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
                    newIndex -= cols;
                    if (newIndex < 0) newIndex += (numRows * cols);
                    if (newIndex >= total) newIndex = total - 1;
                } else if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
                    newIndex += cols;
                    if (newIndex >= total) newIndex = newIndex % cols;
                } else if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A) {
                    if (col > 0) newIndex -= 1;
                    else {
                        newIndex += (cols - 1);
                        if (newIndex >= total) newIndex = total - 1;
                    }
                } else if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) {
                    if (col < cols - 1 && newIndex + 1 < total) newIndex += 1;
                    else newIndex -= col;
                } else if (keycode == Input.Keys.ENTER || keycode == Input.Keys.NUMPAD_ENTER) {
                    triggerSelection();
                    return true;
                }

                if (newIndex != selectedItem) {
                    updateSelectionState(newIndex, game.settings.isSfxOn);
                    return true;
                }
                return false;
            }
        });
    }

    private void setupMouseHoverListeners() {
        for (int i = 0; i < charmSlots.size; i++) {
            final int dex = i;
            charmSlots.get(i).addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    if (pointer == -1 && selectedItem != dex) {
                        updateSelectionState(dex, game.settings.isSfxOn);
                    }
                }

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (selectedItem != dex) {
                        updateSelectionState(dex, false);
                    }
                    triggerSelection();
                }
            });
        }
    }

    private void updateSelectionState(int newIndex, boolean playSound) {
        this.selectedItem = newIndex;

        inventoryUI.highlightSlot(selectedItem);

        if (playSound && game.assetLoader.buttonHover != null && game.settings.isSfxOn) {
            game.assetLoader.buttonHover.stop();
            game.assetLoader.buttonHover.play();
        }

    }

    public void update(float delta) {
        if (charmSlots.size == 0)
            return;

        stateTime += delta;

        float offset = (float) Math.sin(stateTime * 8f) * 4f;

        Stack activeSlot = charmSlots.get(selectedItem);
        vec.set(0, 0);
        activeSlot.localToStageCoordinates(vec);

        selectorTable.setPosition(vec.x - 5 - offset, vec.y - 5 - offset);
        selectorTable.setSize(activeSlot.getWidth() + 10 + (offset * 2), activeSlot.getHeight() + 10 + (offset * 2));
        selectorTable.setVisible(true);
    }

    public void triggerSelection() {
        inventoryUI.toggleEquipSlot(selectedItem);
    }

    public int getSelectedItem() {return  selectedItem;}
}
