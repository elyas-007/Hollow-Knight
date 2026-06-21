package com.hollow.models;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Effect {
    public Animation<TextureRegion> animation;

    public float offsetX;
    public float offsetY;
    public float width;
    public float height;
    public float stateTime;
    public boolean isFacingRight;

    public Effect(Animation<TextureRegion> animation, float offsetX, float offsetY, float width, float height, boolean isFacingRight) {
        this.animation = animation;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.width = width;
        this.height = height;
        this.isFacingRight = isFacingRight;
        this.stateTime = 0f;
    }

    public void update(float delta) {
        stateTime += delta;
    }

    public boolean isFinished() {
        return animation.isAnimationFinished(stateTime);
    }

    public TextureRegion getCurrentFrame() {
        return animation.getKeyFrame(stateTime);
    }
}
