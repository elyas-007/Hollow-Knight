package com.hollow.models.entities.knight;

import com.badlogic.gdx.math.Rectangle;

public class WraithEffect {
    public float x, y, width, height;
    public float stateTime = 0;
    public float maxTime;
    public boolean isShadow;
    public int ticksDone = 0;
    public float tickInterval;
    public Rectangle hitbox;

    public WraithEffect(float x, float y, float width, float height, boolean isShadow, float maxTime) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isShadow = isShadow;
        this.maxTime = maxTime;
        this.tickInterval = maxTime / 3f;
        this.hitbox = new Rectangle(x, y, width, height);
    }
}
