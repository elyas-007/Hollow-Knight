package com.hollow.models;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class AmbientObject {
    public float baseX, baseY;
    public float x, y;
    public float width, height;
    public Animation<TextureRegion> animation;
    public float stateTime;

    private float targetX, targetY;
    private float moveTimer = 0f;
    private float changeInterval = 0f;
    private float rangeX, rangeY;
    private float speed;

    public boolean isFacingRight;
    public boolean isMoving;

    public AmbientObject(float x, float y, float width, float height, Animation<TextureRegion> animation, boolean isMoving) {
        this.baseX = x;
        this.baseY = y;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.animation = animation;
        this.isMoving = isMoving;

        this.stateTime = MathUtils.random(0f, 10f);

        if (isMoving) {
            this.rangeX = MathUtils.random(2.0f, 4.0f);
            this.rangeY = MathUtils.random(1.5f, 3.0f);
            this.speed = MathUtils.random(1.5f, 4.0f);

            setNewTarget();
        }
    }

    private void setNewTarget() {
        targetX = baseX + MathUtils.random(-rangeX, rangeX);
        targetY = baseY + MathUtils.random(-rangeY, rangeY);

        changeInterval = MathUtils.random(0.2f, 1.2f);
        moveTimer = 0f;
    }

    public void update(float delta) {
        stateTime += delta;

        if (isMoving) {
            moveTimer += delta;

            if (moveTimer >= changeInterval) {
                setNewTarget();
            }
            x = MathUtils.lerp(x, targetX, speed * delta);
            y = MathUtils.lerp(y, targetY, speed * delta);

            float flutterY = MathUtils.sin(stateTime * 25f) * 0.04f;
            y += flutterY;

            isFacingRight = targetX > x;
        }
    }

    public TextureRegion getCurrentFrame() {
        if (animation != null) {
            return animation.getKeyFrame(stateTime, true);
        }
        return null;
    }
}
