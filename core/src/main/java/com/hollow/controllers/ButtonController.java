package com.hollow.controllers;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.hollow.HollowKnight;

public class ButtonController {
    private final HollowKnight game;
    private final Stage stage;
    private final TextButton[] menuItems;
    private final Image pointerL;
    private final Image pointerR;
    private int selectedItem = 0;
    private final Vector2 vec = new Vector2();
    private float stateTime = 0f;

    public ButtonController(HollowKnight game, Stage stage, TextButton[] menuItems) {
        this.game = game;
        this.stage = stage;
        this.menuItems = menuItems;

        this.pointerL = new Image(game.assetLoader.pointerL);
        this.pointerR = new Image(game.assetLoader.pointerR);

        this.pointerL.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);
        this.pointerR.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);

        stage.addActor(pointerL);
        stage.addActor(pointerR);

        for (TextButton btn : menuItems) {
            btn.setTransform(true);
            btn.setOrigin(Align.center);
        }

        setupInputHandling();
        setupMouseHoverListeners();

        updateSelectionState(0, false);
    }

    private void setupInputHandling() {
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
                    process(-1);
                    return true;
                }

                if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
                    process(1);
                    return true;
                }

                if (keycode == Input.Keys.ENTER || keycode == Input.Keys.NUMPAD_ENTER) {
                    triggerSelection();
                    return true;
                }
                return false;
            }
        });
    }

    private void setupMouseHoverListeners() {
        for (int i = 0; i < menuItems.length; i++) {
            final int index = i;
            menuItems[i].addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    if (pointer == -1 && selectedItem != index)
                        updateSelectionState(index, true);
                }

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    triggerSelection();
                }
            });
        }
    }

    private void process(int k) {
        int newIndex = selectedItem + k;
        if (newIndex < 0) newIndex = menuItems.length - 1;
        else if (newIndex >= menuItems.length) newIndex = 0;

        updateSelectionState(newIndex, true);
    }

    private void updateSelectionState(int newIndex, boolean playSound) {
        this.selectedItem = newIndex;

        if (playSound && game.assetLoader.buttonHover != null) {
            game.assetLoader.buttonHover.stop();
            game.assetLoader.buttonHover.play();
        }

        for (int i = 0; i < menuItems.length; i++) {
            menuItems[i].clearActions();

            if (i == selectedItem) {
                menuItems[i].addAction(Actions.scaleTo(1.15f, 1.15f, 0.1f));
            } else {
                menuItems[i].addAction(Actions.scaleTo(1f, 1f, 0.1f));
            }
        }

        stage.setKeyboardFocus(menuItems[selectedItem]);
    }

    public void update(float delta) {
        if (menuItems == null || menuItems.length == 0) return;

        stateTime += delta;
        float offset = (float) Math.sin(stateTime * 8f) * 6f;

        TextButton activeItem = menuItems[selectedItem];

        vec.set(0, 0);
        activeItem.localToStageCoordinates(vec);

        float currentWidth = activeItem.getWidth() * activeItem.getScaleX();
        float currentHeight = activeItem.getHeight() * activeItem.getScaleY();

        float actualX = vec.x - ((currentWidth - activeItem.getWidth()) / 2f);
        float actualY = vec.y - ((currentHeight - activeItem.getHeight()) / 2f);

        float itemCenterY = actualY + (currentHeight / 2f);

        float leftX = actualX - pointerL.getWidth() - 15f - offset;
        float rightX = actualX + currentWidth + 15f + offset;

        pointerL.setPosition(leftX, itemCenterY - (pointerL.getHeight() / 2f));
        pointerR.setPosition(rightX, itemCenterY - (pointerR.getHeight() / 2f));
    }

    public void triggerSelection() {
        if (game.assetLoader.buttonClick != null) {
            game.assetLoader.buttonClick.stop();
            game.assetLoader.buttonClick.play();
        }

        if (menuItems[selectedItem].getUserObject() != null) {
            ((Runnable) menuItems[selectedItem].getUserObject()).run();
        }
    }

    public int getSelectedIndex() {
        return selectedItem;
    }
}
