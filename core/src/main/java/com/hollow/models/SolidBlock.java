package com.hollow.models;

import com.badlogic.gdx.math.Rectangle;

public class SolidBlock {
    public Rectangle bounds;
    public boolean isDeadly;

    public SolidBlock(float x, float y, float width, float height, boolean isDeadly) {
        this.bounds = new Rectangle(x, y, width, height);
        this.isDeadly = isDeadly;
    }

    public SolidBlock() {
        this.isDeadly = false;
    }
}
