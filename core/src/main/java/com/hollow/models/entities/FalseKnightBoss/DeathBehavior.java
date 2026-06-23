package com.hollow.models.entities.FalseKnightBoss;

import com.badlogic.gdx.math.Vector2;

public class DeathBehavior implements BossBehavior {
    private float timer = 0f;
    private float totalDeathDuration;

    @Override
    public void enter(FalseKnight boss) {
        boss.currentState = FalseKnight.state.DEATH;

        timer = 0f;

        boss.velocity.x = 0;

        totalDeathDuration = boss.deathFallAnim.getAnimationDuration() +
            boss.deathHitAnim.getAnimationDuration() +
            boss.deathLandAnim.getAnimationDuration();
    }

    @Override
    public void update(FalseKnight boss, float delta, Vector2 playerPos) {
        timer += delta;

        if (timer >= totalDeathDuration) {

        }
    }

    @Override
    public void exit(FalseKnight boss) {

    }
}
