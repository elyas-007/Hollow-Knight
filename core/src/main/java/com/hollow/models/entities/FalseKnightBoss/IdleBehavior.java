package com.hollow.models.entities.FalseKnightBoss;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class IdleBehavior implements BossBehavior {
    private float timer = 0f;

    @Override
    public void enter(FalseKnight boss) {
        boss.currentState = FalseKnight.state.IDLE;
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
    public void exit(FalseKnight boss) {

    }

    private void decideNextMove(FalseKnight boss, Vector2 playerPos) {
        float dis = Math.abs(playerPos.x - boss.position.x);
        FalseKnight.state nextState;

        if (dis <= 3f) {
            nextState = MathUtils.randomBoolean(0.8f) ? FalseKnight.state.MACE_SLAM : FalseKnight.state.DEFENSIVE_LEAP;
        } else if (dis >= 7f) {
            nextState = MathUtils.randomBoolean(0.5f) ? FalseKnight.state.CHARGE_RUN : FalseKnight.state.OFFENSIVE_LEAP;
        } else {
            int rand = MathUtils.random(1, 3);
            if (rand == 1) nextState = FalseKnight.state.MACE_SLAM;
            else if (rand == 2) nextState = FalseKnight.state.OFFENSIVE_LEAP;
            else nextState = FalseKnight.state.CHARGE_RUN;
        }

        if (boss.isPhaseTwo && MathUtils.randomBoolean(0.3f)) {
            nextState = FalseKnight.state.POWER_SLAM;
        }

        if (nextState == boss.lastState) {
            if (nextState == FalseKnight.state.MACE_SLAM) {
                nextState = FalseKnight.state.OFFENSIVE_LEAP;
            } else {
                nextState = FalseKnight.state.MACE_SLAM;
            }
        }

        boss.lastState = nextState;

        switch (nextState) {
            case MACE_SLAM:
                boss.changeBehavior(new MaceSlamBehavior());
                break;
            case CHARGE_RUN:
                boss.changeBehavior(new ChargeRunBehavior());
                break;
            case OFFENSIVE_LEAP:
                boss.changeBehavior(new OffensiveLeapBehavior());
                break;
            case DEFENSIVE_LEAP:
                boss.changeBehavior(new DefensiveLeapBehavior());
                break;
            case POWER_SLAM:
                boss.changeBehavior(new PowerSlamBehavior());
                break;
            default:
                break;
        }
    }
}
