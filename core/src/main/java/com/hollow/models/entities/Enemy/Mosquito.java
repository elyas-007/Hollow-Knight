package com.hollow.models.entities.Enemy;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Mosquito extends Enemy {

    public Animation<TextureRegion> idleAnim;
    public Animation<TextureRegion> attackAnticipateAnim;
    public Animation<TextureRegion> attackLungeAnim;

    private float cooldown;
    private Vector2 lungeDir = new Vector2();
    private static final float FLY_SPEED = 2f;
    private static final float LUNGE_SPEED = 12f;


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

        if (state == EnemyState.CORPSE)
            return;

        if (state == EnemyState.DYING_AIR || state == EnemyState.DYING_LAND) {
            velocity.y += G * delta;
        }

        position.y += velocity.y * delta;

        switch (state) {
            case IDLE -> {
                float dirX = targetX - position.x;
                float dirY = targetY - position.y;
                Vector2 dir = new Vector2(dirX, dirY).nor();

                velocity.x = dir.x * FLY_SPEED;
                velocity.y = dir.y * FLY_SPEED;
                position.x += velocity.x * delta;

                isFacingRight = dirX > 0;

                if (cooldown <= 0 && position.dst(targetX, targetY) < 6f) {
                    state = EnemyState.ATTACK_ANTICIPATE;
                    stateTime = 0f;
                    velocity.setZero();
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
                }
            }

            case ATTACK_LUNGE -> {
                position.x += velocity.x * delta;
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
                position.x += velocity.x * delta;
            }

            case DYING_LAND -> {
                velocity.x = 0;
                if (deathLandAnim != null && deathLandAnim.isAnimationFinished(stateTime)) {
                    state = EnemyState.CORPSE;
                }
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
            velocity.y = 8f;
            isFacingRight = !hitFromRight;
        }
    }

    @Override
    public void turnAround() {
        if (state == EnemyState.IDLE) {
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
        if (state == EnemyState.ATTACK_ANTICIPATE && attackAnticipateAnim != null) return attackAnticipateAnim.getKeyFrame(stateTime, false);
        if (state == EnemyState.ATTACK_LUNGE && attackLungeAnim != null) return attackLungeAnim.getKeyFrame(stateTime, true);
        if (state == EnemyState.IDLE && idleAnim != null) return idleAnim.getKeyFrame(stateTime, true);
        return null;
    }
}
