package com.hollow.models.entities.Knight;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Projectile {
    public Vector2 position;
    public Vector2 velocity;
    public Rectangle hitbox;
    public boolean isFacingRight;
    public boolean isShadow;
    public boolean isDestroyed = false;
    public float stateTime = 0f;
    private float lifeTime = 1.5f;

    private final float SPEED = 20f;

    public Projectile(float x, float y, boolean isFacingRight, boolean isShadow) {
        this.position = new Vector2(x, y);
        this.isFacingRight = isFacingRight;
        this.isShadow = isShadow;

        this.velocity = new Vector2(isFacingRight ? SPEED : -SPEED, 0);
        this.hitbox = new Rectangle(x,y, 1.5f, 1f);
    }

    public void update(float delta) {
        position.x += velocity.x * delta;
        hitbox.setPosition(position.x, position.y);

        stateTime += delta;
        lifeTime -= delta;

        if (lifeTime <= 0) {
            isDestroyed = true;
        }
    }
}
