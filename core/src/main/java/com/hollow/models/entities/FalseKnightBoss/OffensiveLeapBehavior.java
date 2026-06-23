package com.hollow.models.entities.FalseKnightBoss;

import com.badlogic.gdx.math.Vector2;

public class OffensiveLeapBehavior implements BossBehavior {
    private float timer = 0f;
    private boolean hasJumped = false;
    private final float MIN_LEAP_TIME = 0.2f;

    @Override
    public void enter(FalseKnight boss) {
        boss.currentState = FalseKnight.state.OFFENSIVE_LEAP;
        timer = 0f;
        hasJumped = false;
        boss.velocity.setZero();
    }

    @Override
    public void update(FalseKnight boss, float delta, Vector2 playerPos) {
        timer += delta;

        float anticTime = boss.jumpAnticAnim.getAnimationDuration() / boss.animationSpeedMultiplier;

        if (timer >= anticTime && !hasJumped) {
            hasJumped = true;
            boss.isGrounded = false;
            int direction = boss.isFacingRight ? 1 : -1;
            boss.velocity.y = boss.leapVelocityY;
            boss.velocity.x = boss.leapVelocityX * direction;
        }

        if (hasJumped && boss.isGrounded) {
            boss.changeBehavior(new IdleBehavior());
        }
    }

    @Override
    public void exit(FalseKnight boss) {
        boss.velocity.x = 0;
    }
}
