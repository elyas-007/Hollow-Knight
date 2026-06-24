package com.hollow.models.entities.Enemy;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class HuskHornhead extends Enemy {

    public Animation<TextureRegion> idleAnim;
    public Animation<TextureRegion> attackAnticipateAnim;
    public Animation<TextureRegion> attackLungeAnim;


    private float cooldown = 0f;


    public HuskHornhead(float startX, float startY) {
        super(startX, startY);
        health = 3;
        hitbox.width = 1.4f;
        hitbox.height = 1.2f;
    }

    @Override
    public void update(float delta) {
        stateTime += delta;

        if (cooldown > 0) cooldown -= delta;

        if (state == EnemyState.CORPSE)
            return;

        velocity.y += G * delta;
        position.y += velocity.y * delta;

        switch (state) {
            case IDLE -> {
                velocity.x = 0f;
                if (stateTime > 1.5f) {
                    state = EnemyState.WALKING;
                    stateTime = 0f;
                    velocity.x = isFacingRight ? SPEED : -SPEED;
                }
            }

            case WALKING -> {
                position.x += velocity.x * delta;

                if (cooldown <= 0 && Math.abs(position.x - targetX) < 5f) {
                    boolean playerToRight = targetX > position.x;
                    if (playerToRight == isFacingRight) {
                        state = EnemyState.ATTACK_ANTICIPATE;
                        stateTime = 0f;
                        velocity.x = 0f;
                    }
                }
            }

            case ATTACK_ANTICIPATE -> {
                velocity.x = 0;
                if (attackAnticipateAnim != null && attackAnticipateAnim.isAnimationFinished(stateTime)) {
                    state = EnemyState.ATTACK_LUNGE;
                    stateTime = 0f;
                    velocity.x = isFacingRight ? SPEED * 4.5f : -SPEED * 4.5f;
                }
            }

            case ATTACK_LUNGE -> {
                position.x += velocity.x * delta;
                if (attackLungeAnim != null && attackLungeAnim.isAnimationFinished(stateTime)) {
                    state = EnemyState.IDLE;
                    stateTime = 0f;
                    cooldown = 2.0f;
                }
            }

            case TURNING -> {
                velocity.x = 0f;
                if (turnAnim != null && turnAnim.isAnimationFinished(stateTime)) {
                    isFacingRight = !isFacingRight;
                    state = EnemyState.WALKING;
                    stateTime = 0f;
                    velocity.x = isFacingRight ? SPEED : -SPEED;
                }
            }

            case DYING_LAND ->  {
                velocity.x = 0;
                if (deathLandAnim != null && deathLandAnim.isAnimationFinished(stateTime)) {
                    state = EnemyState.CORPSE;
                }
            }

            case DYING_AIR -> {
                position.x += velocity.x * delta;
            }
        }
        hitbox.setPosition(position.x, position.y);
    }

    @Override
    public void takeDamage(int amount, boolean hitFromRight) {
        if (state == EnemyState.DYING_AIR || state == EnemyState.DYING_LAND || state == EnemyState.CORPSE) return;

        health -= amount;
        if (health <= 0) {
            state = EnemyState.DYING_AIR;
            stateTime = 0f;
            velocity.x = hitFromRight ? -3f : 3f;
            velocity.y = 12f;
            isFacingRight = !hitFromRight;
        }
    }

    @Override
    public void turnAround() {
        if (state == EnemyState.WALKING || state == EnemyState.ATTACK_LUNGE) {
            state = EnemyState.TURNING;
            stateTime = 0f;
        }
    }

    @Override
    public TextureRegion getCurrentFrame() {
        if (state == EnemyState.CORPSE) return corpseFrame;
        if (state == EnemyState.DYING_LAND && deathLandAnim != null) return deathLandAnim.getKeyFrame(stateTime, false);
        if (state == EnemyState.DYING_AIR && deathLandAnim != null) return deathLandAnim.getKeyFrames()[0];
        if (state == EnemyState.TURNING && turnAnim != null) return turnAnim.getKeyFrame(stateTime, false);
        if (state == EnemyState.ATTACK_ANTICIPATE && attackAnticipateAnim != null) return attackAnticipateAnim.getKeyFrame(stateTime, false);
        if (state == EnemyState.ATTACK_LUNGE && attackLungeAnim != null) return attackLungeAnim.getKeyFrame(stateTime, true);
        if (state == EnemyState.IDLE && idleAnim != null) return idleAnim.getKeyFrame(stateTime, true);
        if (state == EnemyState.WALKING && walkAnim != null) return walkAnim.getKeyFrame(stateTime, true);

        return null;
    }
}
