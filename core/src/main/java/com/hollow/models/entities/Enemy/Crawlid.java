package com.hollow.models.entities.Enemy;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class Crawlid extends Enemy {

    public Crawlid(float startX, float startY) {
        super(startX, startY);
        health = 2;
        this.name = "crawlid";
    }

    @Override
    public void update(float delta) {
        stateTime += delta;

        if (state == EnemyState.CORPSE) {
            return;
        }

        velocity.y += G * delta;
        position.y += velocity.y * delta;

        if (state == EnemyState.TURNING) {
            velocity.x = 0;
            if (turnAnim != null && turnAnim.isAnimationFinished(stateTime)) {
                isFacingRight = !isFacingRight;
                velocity.x = isFacingRight ? SPEED : -SPEED;
                state = EnemyState.WALKING;
                stateTime = 0f;
            }
        } else if (state == EnemyState.DYING_LAND) {
            velocity.x = 0;
            if (deathLandAnim != null && deathLandAnim.isAnimationFinished(stateTime)) {
                state = EnemyState.CORPSE;
            }
        } else {
            position.x += velocity.x * delta;
        }

        hitbox.setPosition(position.x, position.y);
    }

    @Override
    public void turnAround() {
        if (state != EnemyState.WALKING) return;

        state = EnemyState.TURNING;
        stateTime = 0f;
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
        }
    }

    @Override
    public TextureRegion getCurrentFrame() {
        if (state == EnemyState.CORPSE) return corpseFrame;
        if (state == EnemyState.DYING_LAND && deathLandAnim != null) return deathLandAnim.getKeyFrame(stateTime, false);
        if (state == EnemyState.DYING_AIR && deathAirAnim != null) return deathAirAnim.getKeyFrame(stateTime, false);
        if (state == EnemyState.TURNING && turnAnim != null) return turnAnim.getKeyFrame(stateTime, false);
        if (state == EnemyState.WALKING && walkAnim != null) return walkAnim.getKeyFrame(stateTime, true);
        return null;
    }
}
