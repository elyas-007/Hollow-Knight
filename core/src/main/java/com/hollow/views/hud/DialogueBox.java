package com.hollow.views.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hollow.HollowKnight;

public class DialogueBox {
    private HollowKnight game;
    private Stage stage;
    private Label textLabel;


    private Table rootTable;


    private Array<String> dialogueLines;
    private int currentLineIndex = 0;

    private String targetText = "";
    private float charTimer = 0f;
    private int charIndex = 0;
    private final float TYPE_SPEED = 0.05f;

    public boolean isVisible = false;
    private boolean isTyping = false;

    private Label promptLabel;
    private Table promptTable;

    public DialogueBox(HollowKnight game) {
        this.game = game;
        stage = new Stage(new FitViewport(game.SCREEN_WIDTH, game.SCREEN_HEIGHT));

        LabelStyle style = new LabelStyle(game.assetLoader.font, Color.WHITE);

        textLabel = new Label("", style);
        textLabel.setWrap(true);
        textLabel.setAlignment(Align.center);

        Image topImage = new Image(game.assetLoader.topOrnament);
        Image bottomImage = new Image(game.assetLoader.bottomOrnament);
        topImage.setScaling(Scaling.fit);
        bottomImage.setScaling(Scaling.fit);

        rootTable = new Table();
        rootTable.bottom();
        rootTable.setScale(0.5f);
        rootTable.setFillParent(true);
        rootTable.padBottom(50f);

        rootTable.add(topImage).center().width(400).height(150).padBottom(5f).row();
        rootTable.add(textLabel).width(400).height(100).center().padBottom(5f).row();
        rootTable.add(bottomImage).center();

        LabelStyle promptStyle = new LabelStyle(game.assetLoader.font, Color.WHITE);
        promptLabel = new Label("Press 'E' ro Interact", promptStyle);
        promptLabel.setAlignment(Align.center);

        promptTable = new Table();
        promptTable.bottom();
        promptTable.setFillParent(true);
        promptTable.padBottom(40f);

        promptTable.add(promptLabel).center();
        promptTable.setVisible(false);

        stage.addActor(promptTable);
        stage.addActor(rootTable);
        rootTable.setVisible(false);
        dialogueLines = new Array<>();
    }

    public void startDialogue(String[] lines) {
        dialogueLines.clear();
        dialogueLines.addAll(lines);
        currentLineIndex = 0;
        isVisible = true;
        rootTable.setVisible(true);
        showLine();
    }

    public void setPromptVisible(boolean visible) {
        if (promptTable != null) {
            promptTable.setVisible(visible);
        }
    }

    private void showLine() {
        if (currentLineIndex < dialogueLines.size) {
            targetText = dialogueLines.get(currentLineIndex);
            textLabel.setText("");
            charIndex = 0;
            charTimer = 0f;
            isTyping = true;
        } else {
            isVisible = false;
            rootTable.setVisible(false);
        }
    }

    public void update(float delta) {
        if (!isVisible)
            return;

        if (isTyping) {
            charTimer += delta;
            if (charTimer >= TYPE_SPEED) {
                charTimer = 0f;
                charIndex++;

                if (charIndex >= targetText.length()){
                    charIndex = targetText.length();
                    isTyping = false;
                }

                textLabel.setText(targetText.substring(0, charIndex));

                //TODO: play sound
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_ENTER)) {
            if (isTyping) {
                textLabel.setText(targetText);
                isTyping = false;
            } else {
                currentLineIndex++;
                showLine();
            }
        }
    }

    public void draw() {
        stage.act();
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
    }
}
