package com.hollow.models;

import com.badlogic.gdx.math.Rectangle;

public class TransitionZone {
    public Rectangle bounds;
    public String targetMap;
    public float spawnX;
    public float spawnY;

    public TransitionZone(float x, float y, float width, float height, String targetMap) {
        this.bounds = new Rectangle(x, y, width, height);
        this.targetMap = targetMap;
    }
}
