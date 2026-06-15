package com.hollow.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.hollow.HollowKnight;
import com.hollow.models.entities.Knight.Knight;

public class Game {
    private HollowKnight game;
    private final Knight knight;
    private final Array<SolidBlock> groundRects;
    private final Array<SolidBlock> spikeRects;

    private int keyLeft;
    private int keyRight;
    private int keyUp;
    private int keyDown;
    private int keyJump;
    private int keyDash;
    private int keyAttack;
    private int keyFocus;

    public Game(HollowKnight game, Knight knight, Array<SolidBlock> groundRects, Array<SolidBlock> spikeRects) {
        this.game = game;
        this.knight = knight;
        this.groundRects = groundRects;
        this.spikeRects = spikeRects;
        keyLeft = game.settings.keyLeft;
        keyRight = game.settings.keyRight;
        keyUp = game.settings.keyUp;
        keyDown = game.settings.keyDown;
        keyJump = game.settings.keyJump;
        keyDash = game.settings.keyDash;
        keyAttack =  game.settings.keyAttack;
        keyFocus=  game.settings.keyFocus;
        keyUp = game.settings.keyUp;
        keyDown = game.settings.keyDown;
    }

    private boolean jumpPressed = false;

    private float respawnTimer = 0f;
    private static final float RESPAWN_DELAY = 0.4f;
    private boolean pendingRespawn = false;


    public void update(float delta) {
//        if (knight.isDead()) {
//            handleDeath();
//            return;
//        }
        handleInput(delta);
        knight.update(delta);
        resolveGroundCollision();
    }

    private void handleInput(float delta) {
        if (pendingRespawn)
            return;

        boolean isMovingLeft = Gdx.input.isKeyPressed(keyLeft);
        boolean isMovingRight = Gdx.input.isKeyPressed(keyRight);

        if (isMovingLeft && !isMovingRight) {
            knight.movingHorizontally(-1f);
        } else if (isMovingRight && !isMovingLeft) {
            knight.movingHorizontally(1f);
        } else {
            knight.stopMovingHorizontally();
        }

        if (Gdx.input.isKeyPressed(keyJump)) {
            knight.jumping();
            jumpPressed = true;
        }

        if (jumpPressed && !Gdx.input.isKeyPressed(keyJump)) {
            knight.littleJumping();
            jumpPressed = false;
        }

        if (Gdx.input.isKeyPressed(keyAttack)) {
//            knight.attacking();
        }

        if (Gdx.input.isKeyPressed(keyDash)) {
            knight.dashing();
        }

        if (Gdx.input.isKeyPressed(keyFocus)) {
            knight.heal();
        }

        //TODO : looking up or down
    }

    private void resolveGroundCollision() {
        Rectangle knightBox = knight.getHitbox();
        boolean wasOnGround = false;

        for (SolidBlock ground : groundRects) {
            if (!knightBox.overlaps(ground.bounds)) continue;

            float overlapLeft   = (knightBox.x + knightBox.width) - ground.bounds.x;
            float overlapRight  = (ground.bounds.x + ground.bounds.width) - knightBox.x;
            float overlapBottom = (knightBox.y + knightBox.height) - ground.bounds.y;
            float overlapTop    = (ground.bounds.y + ground.bounds.height) - knightBox.y;

            float minOverlap = Math.min(Math.min(overlapLeft, overlapRight),
                Math.min(overlapBottom, overlapTop));

            if (minOverlap == overlapTop) {
                knight.landing(ground.bounds.y + ground.bounds.height);
                wasOnGround = true;
            } else if (minOverlap == overlapBottom) {
                knight.hitCeiling(ground.bounds.y);
            } else if (minOverlap == overlapLeft) {
                knight.hitWall(ground.bounds.x, 1);
            } else {
                knight.hitWall(ground.bounds.x + ground.bounds.width, -1);
            }
            knightBox.setPosition(knight.getX(), knight.getY());
        }
        if (!wasOnGround) {
            knight.setAirborne();
        }
    }



    private void handleDeath() {
        //TODO : death Animation and setScreen
    }


}
