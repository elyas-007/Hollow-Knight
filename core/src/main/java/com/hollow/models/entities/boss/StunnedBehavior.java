package com.hollow.models.entities.boss;

import com.badlogic.gdx.math.Vector2;

public class StunnedBehavior implements BossBehavior {
    private float timer = 0f;

    @Override
    public void enter(FalseKnight boss) {
        boss.currentState = FalseKnight.State.STUNNED;
        timer = 0f;
        boss.velocity.setZero();
    }

    @Override
    public void update(FalseKnight boss, float delta, Vector2 playerPos) {
        timer += delta;

        if (timer >= boss.stunDuration) {
            boss.changeBehavior(new IdleBehavior());
        }
    }

    @Override
    public void exit(FalseKnight boss) {
        boss.moveSpeed *= 1.5f;
        boss.chargeSpeed *= 1.2f;
        boss.actionCooldown *= 0.6f;
    }
}
