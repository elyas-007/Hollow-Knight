package com.hollow.models.entities.FalseKnightBoss;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.hollow.views.screens.GameScreen;

public class MaceSlamBehavior implements BossBehavior {
    private float timer = 0f;
    private boolean hasStruck = false;

    private final float ANTICIPATION_TIME = 0.6f;
    private final float RECOVERY_TIME = 0.5f;

    @Override
    public void enter(FalseKnight boss) {
        boss.currentState = FalseKnight.state.MACE_SLAM;
        timer = 0f;
        hasStruck = false;
        boss.velocity.x = 0;
    }

    @Override
    public void update(FalseKnight boss, float delta, Vector2 playerPos) {
        timer += delta;

        float anticTime = boss.maceSlamAnticAnim.getAnimationDuration() / boss.animationSpeedMultiplier;
        float strikeTime = boss.maceSlamAnim.getAnimationDuration() / boss.animationSpeedMultiplier;
        float recoverTime = boss.maceSlamRecoverAnim.getAnimationDuration() / boss.animationSpeedMultiplier;

        if (timer >= anticTime && !hasStruck) {
            hasStruck = true;
            performStrike(boss, playerPos);
        }

        if (timer >= anticTime + strikeTime + recoverTime) {
            boss.changeBehavior(new IdleBehavior());
        }
    }

    @Override
    public void exit(FalseKnight boss) {
        hasStruck = false;
    }

    private void performStrike(FalseKnight boss, Vector2 playerPos) {
        float attackRange = 2.5f;
        float attackX = boss.isFacingRight ? (boss.position.x + boss.hitbox.width) :
            (boss.position.x - attackRange);
        Rectangle maceHitBox = new Rectangle(attackX, boss.position.y, attackRange, boss.hitbox.height);

        // perform attack and check
        // update camera for shake

         GameScreen.triggerShake(0.3f, 0.4f);

        boss.spawnShockwave(boss.isFacingRight);
    }
}
