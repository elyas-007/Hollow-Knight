package com.hollow.models.entities.FalseKnightBoss;

import com.badlogic.gdx.math.Vector2;

public class DefensiveLeapBehavior implements BossBehavior {
    private float timer = 0f;
    private final float MIN_LEAP_TIME = 0.2f;

    @Override
    public void enter(FalseKnight boss) {
        boss.currentState = FalseKnight.state.DEFENSIVE_LEAP;
        timer = 0f;
        boss.isGrounded = false;

        int direction = boss.isFacingRight ? -1 : 1;

        boss.velocity.y = boss.leapVelocityY * 0.8f;
        boss.velocity.x = boss.leapVelocityX * direction;
    }

    @Override
    public void update(FalseKnight boss, float delta, Vector2 playerPosition) {
        timer += delta;

        if (timer > MIN_LEAP_TIME && boss.isGrounded) {
            boss.changeBehavior(new IdleBehavior());
        }
    }

    @Override
    public void exit(FalseKnight boss) {
        boss.velocity.x = 0;
    }
}
