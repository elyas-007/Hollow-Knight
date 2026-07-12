package com.hollow.models.entities.boss;

import com.badlogic.gdx.math.Vector2;
import com.hollow.views.screen.GameScreen;

public class PowerSlamBehavior implements BossBehavior {
    private float timer = 0f;
    private boolean isJumping = true;
    private boolean hasStruck = false;

    private static final float SLAM_LEAP_VELOCITY_Y = 15f;
    private static final float SLAM_LEAP_VELOCITY_X = 4f;
    private static final float RECOVERY_TIME = 1.0f;

    @Override
    public void enter(FalseKnight boss) {
        boss.currentState = FalseKnight.State.POWER_SLAM;
        timer = 0f;
        isJumping = true;
        hasStruck = false;
        boss.isGrounded = false;

        boss.audioManager.playFalseKnightShout();
        boss.audioManager.playSound(boss.audioManager.audioLoader.fkJump);

        int direction = boss.isFacingRight ? 1 : -1;
        boss.velocity.x = SLAM_LEAP_VELOCITY_X * direction;
        boss.velocity.y = SLAM_LEAP_VELOCITY_Y;
    }

    @Override
    public void update(FalseKnight boss, float delta, Vector2 playerPos) {
        timer += delta;

        if (isJumping) {
            if (timer > 0.2f && boss.isGrounded) {
                isJumping = false;
                boss.velocity.x = 0;
                timer = 0f;
            }
        } else {
            if (!hasStruck) {
                hasStruck = true;
                performPowerStrike(boss, playerPos);
            }

            if (timer >= RECOVERY_TIME) {
                boss.changeBehavior(new IdleBehavior());
            }
        }
    }

    @Override
    public void exit(FalseKnight boss) {
        boss.velocity.x = 0;
        isJumping = false;
        hasStruck = false;
    }

    private void performPowerStrike(FalseKnight boss, Vector2 playerPos) {
        GameScreen.triggerShake(0.5f, 0.6f);
        boss.spawnShockwave(true);
        boss.spawnShockwave(false);
    }
}
