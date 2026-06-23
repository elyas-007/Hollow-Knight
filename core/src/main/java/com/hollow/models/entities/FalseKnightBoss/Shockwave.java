package com.hollow.models.entities.FalseKnightBoss;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Shockwave {
    public Vector2 position;
    public Rectangle hitbox;
    public float velocityX;
    public boolean isFacingRight;
    public float stateTime = 0f;
    public boolean isDestroyed = false;

    private final float LIFE_TIME = 1.5f;


    public Shockwave(float x, float y, boolean isFacingRight) {
        this.position = new Vector2(x, y);
        this.isFacingRight = isFacingRight;

        this.velocityX = isFacingRight ? 14f : -14f;

        this.hitbox = new Rectangle(x, y, 1.5f, 1f);
    }

    public void update(float delta) {
        position.x += velocityX * delta;
        hitbox.setPosition(position.x, position.y);

        stateTime += delta;
        if (stateTime > LIFE_TIME) {
            isDestroyed = true;
        }
    }
}
