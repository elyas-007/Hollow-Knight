package com.hollow.models.entities.enemy;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.hollow.controllers.manager.AudioManager;

public class Mosquito extends Enemy {

    public Animation<TextureRegion> idleAnim;
    public Animation<TextureRegion> attackAnticipateAnim;
    public Animation<TextureRegion> attackLungeAnim;

    private float cooldown;
    private Vector2 lungeDir = new Vector2();
    private final Vector2 tempDir = new Vector2();

    private static final float FLY_SPEED = 2f;
    private static final float LUNGE_SPEED = 12f;

    public AudioManager audioManager;
    private long flyLoopId = -1;


    public Mosquito(float startX, float startY) {
        super(startX, startY);
        health = 1;
        hitbox.width = 1f;
        hitbox.height = 1f;
        state = EnemyState.IDLE;
        this.name = "mosquito";
    }


    @Override
    public void update(float delta) {
        stateTime += delta;
        if (cooldown > 0) cooldown -= delta;
        if (state == EnemyState.CORPSE) return;

        if (state == EnemyState.DYING_AIR || state == EnemyState.DYING_LAND) {
            velocity.y += G * delta;
        }


        handleAudio();
        handleState(delta);

        position.x += velocity.x * delta;
        position.y += velocity.y * delta;

        hitbox.setPosition(position.x, position.y);
    }

    private void handleAudio() {
        if (state != EnemyState.DYING_AIR && state != EnemyState.DYING_LAND) {
            if (flyLoopId == -1 && audioManager != null && audioManager.audioLoader.mosqFly != null) {
                flyLoopId = audioManager.audioLoader.mosqFly.loop(0.2f);
            }
        } else {
            if (flyLoopId != -1 && audioManager != null && audioManager.audioLoader.mosqFly != null) {
                audioManager.audioLoader.mosqFly.stop(flyLoopId);
                flyLoopId = -1;
            }
        }
    }

    private void handleState(float delta) {
        switch (state) {
            case IDLE -> {
                float distanceToPlayer = position.dst(targetX, targetY);

                if (distanceToPlayer > 15f) {
                    velocity.setZero();
                    return;
                }

                tempDir.set(targetX - position.x, targetY - position.y).nor();

                velocity.x = tempDir.x * FLY_SPEED;
                velocity.y = tempDir.y * FLY_SPEED;
                isFacingRight = (targetX - position.x) > 0;

                if (cooldown <= 0 && position.dst(targetX, targetY) < 6f) {
                    state = EnemyState.ATTACK_ANTICIPATE;
                    stateTime = 0f;
                    velocity.setZero();
                    if (audioManager != null) audioManager.playSound(audioManager.audioLoader.mosqPrepare);
                }
            }
            case ATTACK_ANTICIPATE -> {
                velocity.setZero();
                if (attackAnticipateAnim != null && attackAnticipateAnim.isAnimationFinished(stateTime)) {
                    state = EnemyState.ATTACK_LUNGE;
                    stateTime = 0f;
                    lungeDir.set(targetX - position.x, targetY - position.y).nor();
                    velocity.x = lungeDir.x * LUNGE_SPEED;
                    velocity.y = lungeDir.y * LUNGE_SPEED;
                    if (audioManager != null) audioManager.playSound(audioManager.audioLoader.mosqCharge);
                }
            }
            case ATTACK_LUNGE -> {
                if (stateTime > 2.0f) {
                    state = EnemyState.IDLE;
                    stateTime = 0f;
                    cooldown = 2.0f;
                }
            }
            case TURNING -> {
                isFacingRight = !isFacingRight;
                state = EnemyState.IDLE;
                stateTime = 0f;
            }
            case DYING_AIR -> {
            }
            case DYING_LAND -> {
                velocity.x = 0;
                if (deathLandAnim != null && deathLandAnim.isAnimationFinished(stateTime)) {
                    state = EnemyState.CORPSE;
                }
            }
        }
    }


    @Override
    public void takeDamage(int amount, boolean hitFromRight) {
        if (state == EnemyState.DYING_AIR || state == EnemyState.DYING_LAND || state == EnemyState.CORPSE) return;

        health -= amount;
        if (health <= 0) {
            state = EnemyState.DYING_AIR;
            stateTime = 0f;
            velocity.x = hitFromRight ? -3f : 3f;
            velocity.y = 8f;
            isFacingRight = !hitFromRight;

            if (flyLoopId != -1 && audioManager != null) {
                audioManager.audioLoader.mosqFly.stop(flyLoopId);
                flyLoopId = -1;
            }
        }
    }

    @Override
    public void turnAround() {
        if (state == EnemyState.IDLE) {
            state = EnemyState.TURNING;
            stateTime = 0f;
        } else if (state == EnemyState.ATTACK_LUNGE) {
            state = EnemyState.IDLE;
            stateTime = 0f;
            cooldown = 2.0f;
            velocity.setZero();
            if (audioManager != null) audioManager.playSound(audioManager.audioLoader.mosqWallHit);
        }
    }

    @Override
    public TextureRegion getCurrentFrame() {
        if (state == EnemyState.CORPSE) return corpseFrame;
        if (state == EnemyState.DYING_LAND && deathLandAnim != null)
            return deathLandAnim.getKeyFrame(stateTime, false);
        if (state == EnemyState.DYING_AIR && deathAirAnim != null)
            return deathAirAnim.getKeyFrame(stateTime, false);
        if (state == EnemyState.TURNING && turnAnim != null)
            return turnAnim.getKeyFrame(stateTime, false);
        if (state == EnemyState.ATTACK_ANTICIPATE && attackAnticipateAnim != null)
            return attackAnticipateAnim.getKeyFrame(stateTime, false);
        if (state == EnemyState.ATTACK_LUNGE && attackLungeAnim != null)
            return attackLungeAnim.getKeyFrame(stateTime, true);
        if (state == EnemyState.IDLE && idleAnim != null)
            return idleAnim.getKeyFrame(stateTime, true);
        return null;
    }
}
