package com.hollow.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.hollow.HollowKnight;
import com.hollow.models.entities.Enemy.Tiktik;
import com.hollow.models.entities.Knight.Knight;

public class Game {
    private HollowKnight game;
    private final Knight knight;
    private final Array<SolidBlock> groundRects;
    private final Array<SolidBlock> spikeRects;

    private Array<Tiktik> tiktiks;

    private int keyLeft;
    private int keyRight;
    private int keyUp;
    private int keyDown;
    private int keyJump;
    private int keyDash;
    private int keyAttack;
    private int keyFocus;

    public Game(HollowKnight game, Knight knight, Array<SolidBlock> groundRects, Array<SolidBlock> spikeRects, Array<Tiktik> tiktiks) {
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

        this.tiktiks = tiktiks;
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
        updateEnemies(delta);
    }

    private void updateEnemies(float delta) {
        for (int i = tiktiks.size - 1; i >= 0; i--) {
            Tiktik enemy = tiktiks.get(i);

            enemy.update(delta);
            resolveEnemyCollisions(enemy);

            if (enemy.state == Tiktik.EnemyState.WALKING) {
                if (knight.getHitbox().overlaps(enemy.hitbox) && !knight.isInvincible()) {
                    boolean hitFromRight = knight.getX() < enemy.position.x;
                    knight.takeDamage(1, hitFromRight);
                }
            }
        }
    }

    private void resolveEnemyCollisions(Tiktik enemy) {
        boolean wasOnGround = false;

        for (SolidBlock ground : groundRects) {
            if (!enemy.hitbox.overlaps(ground.bounds)) continue;

            float overlapLeft   = (enemy.hitbox.x + enemy.hitbox.width) - ground.bounds.x;
            float overlapRight  = (ground.bounds.x + ground.bounds.width) - enemy.hitbox.x;
            float overlapBottom = (enemy.hitbox.y + enemy.hitbox.height) - ground.bounds.y;
            float overlapTop    = (ground.bounds.y + ground.bounds.height) - enemy.hitbox.y;

            float minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapBottom, overlapTop));

            if (minOverlap == overlapTop) {
                enemy.position.y = ground.bounds.y + ground.bounds.height;
                enemy.velocity.y = 0;
                wasOnGround = true;
            } else if (minOverlap == overlapLeft || minOverlap == overlapRight) {
                if (enemy.state == Tiktik.EnemyState.WALKING) {
                    enemy.turnAround();
                }

                if (minOverlap == overlapLeft) {
                    enemy.position.x = ground.bounds.x - enemy.hitbox.width;
                } else {
                    enemy.position.x = ground.bounds.x + ground.bounds.width;
                }
            }
            enemy.hitbox.setPosition(enemy.position.x, enemy.position.y);
        }
    }

    public Array<Tiktik> getTiktiks() {
        return tiktiks;
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

        if (Gdx.input.isKeyJustPressed(keyJump)) {
            knight.jumping();
        }

        if (!Gdx.input.isKeyPressed(keyJump) && knight.getVelocity().y > 0) {
            knight.littleJumping();
        }

        if (Gdx.input.isKeyJustPressed(keyAttack)) {
            int dir = 0;
            if (Gdx.input.isKeyPressed(keyUp)) dir = 1;
            else if (Gdx.input.isKeyPressed(keyDown) && !knight.isOnGround()) dir = -1;
            knight.attacking(dir);
        }

        if (Gdx.input.isKeyPressed(keyDash)) {
            knight.dashing();
        }

        if (Gdx.input.isKeyPressed(keyFocus)) {
            knight.startFocusing();
        }

        if (Gdx.input.isKeyPressed(keyUp) && knight.isOnGround() && knight.getVelocity().x == 0) {
            knight.setLookDirection(1);
        } else if (Gdx.input.isKeyPressed(keyDown) && knight.isOnGround() && knight.getVelocity().x == 0) {
            knight.setLookDirection(-1);
        } else {
            knight.setLookDirection(0);
        }

        //TODO : looking up or down
    }

    private void resolveGroundCollision() {
        Rectangle knightBox = knight.getHitbox();
        boolean wasOnGround = false;
        boolean touchingWall = false;

        float delta = Gdx.graphics.getDeltaTime();
        float prevY = knight.getY() - (knight.getVelocity().y * delta);

        for (SolidBlock ground : groundRects) {
            if (!knightBox.overlaps(ground.bounds)) continue;

            float overlapLeft   = (knightBox.x + knightBox.width) - ground.bounds.x;
            float overlapRight  = (ground.bounds.x + ground.bounds.width) - knightBox.x;
            float overlapBottom = (knightBox.y + knightBox.height) - ground.bounds.y;
            float overlapTop    = (ground.bounds.y + ground.bounds.height) - knightBox.y;

            if (prevY < (ground.bounds.y + ground.bounds.height) - 0.2f) {
                overlapTop = Float.MAX_VALUE;
            }

            if (prevY + knightBox.height > ground.bounds.y + 0.2f) {
                overlapBottom = Float.MAX_VALUE;
            }

            float minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapBottom, overlapTop));

            if (minOverlap == Float.MAX_VALUE) continue;

            if (minOverlap == overlapTop) {
                knight.landing(ground.bounds.y + ground.bounds.height);
                wasOnGround = true;
            } else if (minOverlap == overlapBottom) {
                knight.hitCeiling(ground.bounds.y);
            } else if (minOverlap == overlapLeft) {
                knight.hitWall(ground.bounds.x, 1);
                touchingWall = true;
            } else if (minOverlap == overlapRight) {
                knight.hitWall(ground.bounds.x + ground.bounds.width, -1);
                touchingWall = true;
            }
            knightBox.setPosition(knight.getX(), knight.getY());
        }

        if (!wasOnGround) {
            knight.setAirborne();
        }

        if (!touchingWall) {
            knight.resetWallTouch();
        }
    }



    private void handleDeath() {
        //TODO : death Animation and setScreen
    }


}
