package com.hollow.models.entities.Enemy;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Crystallized extends Enemy {
    public Animation<TextureRegion> idleAnim;
    public Animation<TextureRegion> runAnim;
    public Animation<TextureRegion> shootAnim;
    public Animation<TextureRegion> evadeAnim;

    private float enrageTimer = 0f;
    private static final float ENRAGE_DURATION = 2.5f;
    private static final float ENRAGE_SPEED = 6.0f;
    private static final float LASER_RANGE = 20f;
    private static final float HEAR_RANGE = 6f;

    public boolean laserFired = false;

    public Crystallized(float startX, float startY) {
        super(startX, startY);
        health = 5;
        hitbox.width = 1.4f;
        hitbox.height = 1.6f;
        state = EnemyState.IDLE;
        velocity.x = 0;
        this.name = "crystallized";
    }

    @Override
    public void update(float delta) {
        stateTime += delta;

        if (state == EnemyState.CORPSE)
            return;

        velocity.y += G * delta;
        position.y += velocity.y * delta;

        boolean playerToRight = targetX > position.x;
        float disX = Math.abs(targetX - position.x);
        float disY = Math.abs(targetY - position.y);

        switch (state) {
            case IDLE -> {
                velocity.x = 0f;

                if (playerToRight == isFacingRight && disX <= LASER_RANGE && disY < 3f) {
                    state = EnemyState.ATTACK_ANTICIPATE;
                    stateTime = 0f;
                    laserFired = false;
                }

                else if (playerToRight != isFacingRight && disX <= HEAR_RANGE && disY < 3f) {
                    state = EnemyState.TURNING;
                    stateTime = 0f;
                }
            }

            case ATTACK_ANTICIPATE -> {
                velocity.x = 0f;
                float animDuration = shootAnim != null ? shootAnim.getAnimationDuration() : 0.4f;

                if (stateTime >= animDuration) {
                    state = EnemyState.ATTACK_LUNGE;
                    stateTime = 0f;
                    enrageTimer = ENRAGE_DURATION;

                    isFacingRight = playerToRight;
                    velocity.x = isFacingRight ? ENRAGE_SPEED : -ENRAGE_SPEED;
                }
            }

            case ATTACK_LUNGE -> {
                position.x += velocity.x * delta;
                enrageTimer -= delta;

                if (enrageTimer <= 0) {
                    state = EnemyState.IDLE;
                    stateTime = 0f;
                    velocity.x = 0f;
                }
            }

            case EVADING -> {
                velocity.x = isFacingRight ? -SPEED * 2.5f : SPEED * 2.5f;
                position.x += velocity.x * delta;

                float evadeDuration = evadeAnim != null ? evadeAnim.getAnimationDuration() : 0.3f;
                if (stateTime >= evadeDuration) {
                    state = EnemyState.IDLE;
                    stateTime = 0f;
                    velocity.x = 0f;
                }
            }

            case TURNING -> {
                velocity.x = 0f;
                if (turnAnim != null && turnAnim.isAnimationFinished(stateTime)) {
                    isFacingRight = !isFacingRight;
                    state = EnemyState.IDLE;
                    stateTime = 0f;
                }
            }

            case DYING_LAND -> {
                velocity.x = 0;
                if (deathLandAnim != null && deathLandAnim.isAnimationFinished(stateTime)) {
                    state = EnemyState.CORPSE;
                }
            }

            case DYING_AIR -> {
                position.x += velocity.x * delta;
            }
        }

        hitbox.setPosition(position.x, position.y);
    }

    @Override
    public void takeDamage(int amount, boolean hitFromRight) {
        if (state == EnemyState.DYING_AIR || state == EnemyState.DYING_LAND || state == EnemyState.CORPSE)
            return;

        health -= amount;
        if (health <= 0) {
            state = EnemyState.DYING_AIR;
            stateTime = 0f;
            velocity.x = hitFromRight ? -3f : 3f;
            velocity.y = 12f;
            isFacingRight = !hitFromRight;
        } else {
            state = EnemyState.EVADING;
            stateTime = 0f;
            isFacingRight = hitFromRight;
        }

    }

    @Override
    public void turnAround() {
        if (state == EnemyState.ATTACK_LUNGE || state == EnemyState.EVADING) {
            state = EnemyState.TURNING;
            stateTime = 0f;
        }
    }

    @Override
    public TextureRegion getCurrentFrame() {
        if (state == EnemyState.CORPSE) return corpseFrame;
        if (state == EnemyState.DYING_LAND && deathLandAnim != null) return deathLandAnim.getKeyFrame(stateTime, false);
        if (state == EnemyState.DYING_AIR && deathAirAnim != null) return deathAirAnim.getKeyFrame(stateTime, false);
        if (state == EnemyState.TURNING && turnAnim != null) return turnAnim.getKeyFrame(stateTime, false);

        if (state == EnemyState.EVADING && evadeAnim != null) return evadeAnim.getKeyFrame(stateTime, false);
        if (state == EnemyState.ATTACK_ANTICIPATE && shootAnim != null) return shootAnim.getKeyFrame(stateTime, false);
        if (state == EnemyState.ATTACK_LUNGE && runAnim != null) return runAnim.getKeyFrame(stateTime, true);
        if (state == EnemyState.IDLE && idleAnim != null) return idleAnim.getKeyFrame(stateTime, true);

        return null;
    }
}
