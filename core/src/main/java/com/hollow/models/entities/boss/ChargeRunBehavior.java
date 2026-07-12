package com.hollow.models.entities.boss;

import com.badlogic.gdx.math.Vector2;

public class ChargeRunBehavior implements BossBehavior {
    private float timer = 0f;
    private final float CHARGE_DURATION = 1.2f;

    @Override
    public void enter(FalseKnight boss) {
        boss.currentState = FalseKnight.State.CHARGE_RUN;
        timer = 0f;
        int direction = boss.isFacingRight ? 1 : -1;
        boss.velocity.x = boss.chargeSpeed * direction;
    }

    @Override
    public void update(FalseKnight boss, float delta, Vector2 playerPos) {
        timer += delta;

        if (timer >= CHARGE_DURATION) {
            boss.changeBehavior(new IdleBehavior());
            return;
        }

        if (timer > 0.1f && boss.velocity.x == 0) {
            boss.changeBehavior(new IdleBehavior());
        }
    }

    @Override
    public void exit(FalseKnight boss) {
        boss.velocity.x = 0f;
    }
}
