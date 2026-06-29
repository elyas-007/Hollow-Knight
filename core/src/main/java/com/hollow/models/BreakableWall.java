package com.hollow.models;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class BreakableWall extends SolidBlock {
    private int hp;
    private boolean isDestroyed = false;

    private float shakeTimer = 0f;
    private float shakeIntensity = 0f;
    private final Rectangle originalBounds;


    public BreakableWall(float x, float y, float width, float height) {
        this.hp = 3;
        this.originalBounds = new Rectangle(x, y, width, height);
        this.bounds = new Rectangle(x, y, width, height);
    }

    public void update(float delta) {
        if (shakeTimer > 0) {
            shakeTimer -= delta;

            float randomX = MathUtils.random(-shakeIntensity, shakeIntensity);
            float randomY = MathUtils.random(-shakeIntensity, shakeIntensity);

            this.bounds.setPosition(originalBounds.x + randomX, originalBounds.y + randomY);

            if (shakeTimer <= 0) {
                this.bounds.setPosition(originalBounds.x, originalBounds.y);
            }
        }
    }


    public void takeDamage(int damage) {
        if (isDestroyed) return;
        hp -= damage;

        triggerShake(0.1f, 0.05f);
    }

    public void triggerShake(float duration, float intensity) {
        this.shakeTimer = duration;
        this.shakeIntensity = intensity;
    }

    public int getHp() {
        return hp;
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public void setDestroyed(boolean destroyed) {
        isDestroyed = destroyed;
    }
}
