package com.hollow.models.entities.Enemy;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Enemy {
    public String name;
    public Vector2 position = new Vector2();
    public Vector2 velocity = new Vector2();
    public Rectangle hitbox = new Rectangle();

    public enum EnemyState {WALKING, DYING_AIR, TURNING, DYING_LAND, CORPSE, IDLE, ATTACK_ANTICIPATE, ATTACK_LUNGE, EVADING}

    public EnemyState state = EnemyState.WALKING;

    public static final float SPEED = 1.5f;
    public static final float G = -30f;
    public static final float HIT_WIDTH = 1f;
    public static final float HIT_HEIGHT = 0.5f;

    public float targetX;
    public float targetY;

    public boolean isFacingRight = true;
    public int health;
    public float stateTime;

    public Animation<TextureRegion> walkAnim;
    public Animation<TextureRegion> turnAnim;
    public Animation<TextureRegion> deathAirAnim;
    public Animation<TextureRegion> deathLandAnim;
    public TextureRegion corpseFrame;

    public Enemy(float startX, float startY) {
        position.set(startX, startY);
        hitbox.set(startX, startY, HIT_WIDTH, HIT_HEIGHT);
        velocity.x = SPEED;
    }

    public abstract void update(float delta);
    public abstract void takeDamage(int amount, boolean hitFromRight);
    public abstract void turnAround();
    public abstract TextureRegion getCurrentFrame();
}
