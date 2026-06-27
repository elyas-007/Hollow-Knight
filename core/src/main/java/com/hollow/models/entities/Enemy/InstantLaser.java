package com.hollow.models.entities.Enemy;

import com.badlogic.gdx.math.Rectangle;

public class InstantLaser {
    public Rectangle hitbox;
    public boolean isFacingRight;
    public float lifeTime = 0.6f;
    public float stateTime = 0f;

    public InstantLaser(float x, float y, float width, float height, boolean isFacingRight) {
        this.hitbox = new Rectangle(x, y, width, height);
        this.isFacingRight = isFacingRight;
    }

    public void update(float delta) {
        stateTime += delta;
        lifeTime -= delta;
    }

    public boolean isFinished() {
        return lifeTime <= 0;
    }
}
