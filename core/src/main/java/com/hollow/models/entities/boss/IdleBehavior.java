package com.hollow.models.entities.boss;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class IdleBehavior implements BossBehavior {
    private float timer = 0f;

    @Override
    public void enter(FalseKnight boss) {
        boss.currentState = FalseKnight.State.IDLE;
        timer = 0f;
        boss.velocity.setZero();
    }

    @Override
    public void update(FalseKnight boss, float delta, Vector2 playerPos) {
        if (!boss.bossFightStarted) {
            boss.velocity.x = 0;
            return;
        }

        timer += delta;
        boss.isFacingRight = playerPos.x > boss.position.x;

        if (timer >= boss.actionCooldown) {
            decideNextMove(boss, playerPos);
        }
    }

    @Override
    public void exit(FalseKnight boss) {}

    private void decideNextMove(FalseKnight boss, Vector2 playerPos) {
        float dis = Math.abs(playerPos.x - boss.position.x);
        FalseKnight.State nextState;

        if (dis <= 3f) {
            nextState = MathUtils.randomBoolean(0.8f) ? FalseKnight.State.MACE_SLAM : FalseKnight.State.DEFENSIVE_LEAP;
        } else if (dis >= 7f) {
            nextState = MathUtils.randomBoolean(0.5f) ? FalseKnight.State.CHARGE_RUN : FalseKnight.State.OFFENSIVE_LEAP;
        } else {
            int rand = MathUtils.random(1, 3);
            if (rand == 1) nextState = FalseKnight.State.MACE_SLAM;
            else if (rand == 2) nextState = FalseKnight.State.OFFENSIVE_LEAP;
            else nextState = FalseKnight.State.CHARGE_RUN;
        }

        if (boss.isPhaseTwo && MathUtils.randomBoolean(0.3f)) {
            nextState = FalseKnight.State.POWER_SLAM;
        }

        if (nextState == boss.lastState) {
            if (nextState == FalseKnight.State.MACE_SLAM) {
                nextState = FalseKnight.State.OFFENSIVE_LEAP;
            } else {
                nextState = FalseKnight.State.MACE_SLAM;
            }
        }

        boss.lastState = nextState;

        switch (nextState) {
            case MACE_SLAM -> boss.changeBehavior(new MaceSlamBehavior());
            case CHARGE_RUN -> boss.changeBehavior(new ChargeRunBehavior());
            case OFFENSIVE_LEAP -> boss.changeBehavior(new OffensiveLeapBehavior());
            case DEFENSIVE_LEAP -> boss.changeBehavior(new DefensiveLeapBehavior());
            case POWER_SLAM -> boss.changeBehavior(new PowerSlamBehavior());
            default -> {}
        }
    }
}
