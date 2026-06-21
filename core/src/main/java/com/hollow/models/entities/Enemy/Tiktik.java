package com.hollow.models.entities.Enemy;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Tiktik {
    public final Vector2 position = new Vector2();
    public final Vector2 velocity = new Vector2();
    public final Rectangle hitbox = new Rectangle();

    public enum EnemyState {WALKING, DYING_AIR, DYING_LAND, CORPSE}

    public EnemyState state = EnemyState.WALKING;

    private static final float SPEED = 1.5f;
    private static final float G = -30f;
    public static final float HIT_WIDTH = 1f;
    public static final float HIT_HEIGHT = 0.5f;

    public boolean isFacingRight = true;
    public int health = 1;
    public float stateTime;

    public Animation<TextureRegion> walkAnim;
    public Animation<TextureRegion> deathAirAnim;
    public Animation<TextureRegion> deathLandAnim;
    public TextureRegion corpseFrame;

    public Tiktik(float startX, float startY) {
        position.set(startX, startY);
        hitbox.set(startX, startY, HIT_WIDTH, HIT_HEIGHT);
        velocity.x = SPEED;
    }

    public void update(float delta) {
        stateTime += delta;

        if (state == EnemyState.CORPSE) {
            return;
        }

        velocity.y += G * delta;
        position.y += velocity.y * delta;

        if (state == EnemyState.DYING_LAND) {
            velocity.x = 0;
            if (deathLandAnim != null && deathLandAnim.isAnimationFinished(stateTime)) {
                state = EnemyState.CORPSE;
            }
        } else {
            position.x += velocity.x * delta;
        }

        hitbox.setPosition(position.x, position.y);
    }

    public void turnAround() {
        if (state != EnemyState.WALKING) return;
        isFacingRight = !isFacingRight;
        velocity.x = isFacingRight ? SPEED : -SPEED;
    }

    public void takeDamage(int amount, boolean hitFromRight) {
        if (state != EnemyState.WALKING) return;
        health -= amount;

        if (health <= 0) {
            state = EnemyState.DYING_AIR;
            stateTime = 0f;

            velocity.x = hitFromRight ? -3f : 3f;
            velocity.y = 12f;
            isFacingRight = !hitFromRight;
        }
    }

    public TextureRegion getCurrentFrame() {
        if (state == EnemyState.CORPSE) return corpseFrame;
        if (state == EnemyState.DYING_LAND && deathLandAnim != null) return deathLandAnim.getKeyFrame(stateTime);
        if (state == EnemyState.DYING_AIR && deathAirAnim != null) return deathAirAnim.getKeyFrame(stateTime);
        if (state == EnemyState.WALKING && walkAnim != null) return walkAnim.getKeyFrame(stateTime);
        return null;
    }
}
