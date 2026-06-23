package com.hollow.models.entities.FalseKnightBoss;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class FalseKnight {
    public boolean bossFightStarted = false;

    private BossBehavior currentBehavior;

    public enum state {
        IDLE,
        MACE_SLAM,
        CHARGE_RUN,
        OFFENSIVE_LEAP,
        DEFENSIVE_LEAP,
        POWER_SLAM,
        STUNNED,
        DEATH
    }
    public state currentState;
    public state lastState;

    public Vector2 position;
    public Vector2 velocity;
    public Rectangle hitbox;
    public Rectangle vulnerabilityBox;

    public int maxHp = 100;
    public int currentHp;
    public boolean isPhaseTwo = false;
    public boolean isFacingRight = false;

    public float moveSpeed = 4f;
    public float chargeSpeed = 8f;
    public float leapVelocityX = 6f;
    public float leapVelocityY = 12f;
    public float actionCooldown = 2f;
    public float stunDuration = 5f;

    private float cooldownTimer = 0f;

    private final float CLOSE_RANGE = 3f;
    private final float MID_RANGE = 7f;

    public static final float G = -30f;
    public static final float MAX_FALL_SPEED = -20f;
    public boolean isGrounded = false;

    public Animation<TextureRegion> idleAnim;
    public Animation<TextureRegion> runAnticAnim;
    public Animation<TextureRegion> runAnim;
    public Animation<TextureRegion> maceSlamAnticAnim;
    public Animation<TextureRegion> maceSlamAnim;
    public Animation<TextureRegion> maceSlamRecoverAnim;
    public Animation<TextureRegion> jumpAnticAnim;
    public Animation<TextureRegion> jumpAnim;
    public Animation<TextureRegion> jumpAttackAnim;
    public Animation<TextureRegion> landAnim;
    public Animation<TextureRegion> stunAnim;
    public Animation<TextureRegion> stunRecoverAnim;
    public Animation<TextureRegion> deathFallAnim;
    public Animation<TextureRegion> deathHitAnim;
    public Animation<TextureRegion> deathLandAnim;
    public Animation<TextureRegion> turnAnim;

    public Animation<TextureRegion> shockwaveAnim;
    public Array<Shockwave> activeShockwaves = new Array<>();

    private float animationStateTime = 0f;
    public float animationSpeedMultiplier = 1.0f;

    public FalseKnight(float x, float y) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        this.hitbox = new Rectangle(x, y, 3f, 4f);
        this.vulnerabilityBox = new Rectangle(x + 0.75f, y + 1.25f, 1.5f, 1.5f);

        this.currentHp = maxHp;
        this.lastState = state.IDLE;
    }

    public void changeBehavior(BossBehavior newBehavior) {
        if (currentBehavior != null) {
            currentBehavior.exit(this);
        }
        currentBehavior = newBehavior;
        animationStateTime = 0f;
        currentBehavior.enter(this);
    }

    public void update(float delta, Vector2 playerPosition) {
        if (currentBehavior != null) {
            currentBehavior.update(this, delta, playerPosition);
        }

        applyG(delta);
        updatePosition(delta);
        updateHitbox();
    }

    private void applyG(float delta) {
        if (currentState != state.DEATH) {
            velocity.y += G * delta;
            if (velocity.y < MAX_FALL_SPEED) {
                velocity.y = MAX_FALL_SPEED;
            }
        }
    }

    private void updatePosition(float delta) {
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;
    }

    private void updateHitbox() {
        hitbox.setPosition(position.x, position.y);
        float weakPointX = isFacingRight ? (position.x + 1.5f) : (position.x - 0.5f);

        vulnerabilityBox.setPosition(weakPointX, position.y);
    }

    public void hitWall(float wallX, int side) {
        if (side > 0) {
            position.x = wallX - hitbox.width;
        } else {
            position.x = wallX;
        }
        velocity.x = 0f;
    }

    public void setAirborne() {
        isGrounded = false;
    }

    public void landing(float top) {
        position.y = top;
        velocity.y = 0;
        isGrounded = true;
    }

    public void takeDamage(int damage) {
        currentHp -= damage;

        if (currentHp <= 0) {
            currentHp = 0;
            changeBehavior(new DeathBehavior());
            return;
        }

        if (currentHp <= maxHp / 2 && !isPhaseTwo) {
            isPhaseTwo = true;
            changeBehavior(new StunnedBehavior());
        }
    }

    public void spawnShockwave(boolean facingRight) {
        float spawnX = facingRight ? (position.x + hitbox.width) : (position.x - 1.5f);
        activeShockwaves.add(new Shockwave(spawnX, position.y, facingRight));
    }

    public TextureRegion getCurrentFrame(float delta) {
        animationStateTime += (delta * animationSpeedMultiplier);

        switch (currentState) {
            case IDLE -> {
                return idleAnim.getKeyFrame(animationStateTime, true);
            }

            case CHARGE_RUN -> {
                if (!runAnticAnim.isAnimationFinished(animationStateTime)) {
                    return runAnticAnim.getKeyFrame(animationStateTime);
                }
                float runTime = animationStateTime - runAnticAnim.getAnimationDuration();
                return runAnim.getKeyFrame(runTime, true);
            }

            case MACE_SLAM -> {
                if (!maceSlamAnticAnim.isAnimationFinished(animationStateTime)) {
                    return maceSlamAnticAnim.getKeyFrame(animationStateTime);
                }
                float afterAntic = animationStateTime - maceSlamAnticAnim.getAnimationDuration();
                if (!maceSlamAnim.isAnimationFinished(afterAntic)) {
                    return maceSlamAnim.getKeyFrame(afterAntic);
                }
                float afterSlam = afterAntic - maceSlamAnim.getAnimationDuration();
                return maceSlamRecoverAnim.getKeyFrame(afterSlam);
            }

            case OFFENSIVE_LEAP, DEFENSIVE_LEAP -> {
                if (!jumpAnticAnim.isAnimationFinished(animationStateTime)) {
                    return jumpAnticAnim.getKeyFrame(animationStateTime);
                }
                float airTime = animationStateTime - jumpAnticAnim.getAnimationDuration();
                return jumpAnim.getKeyFrame(airTime, true);
            }

            case POWER_SLAM -> {
                if (!jumpAttackAnim.isAnimationFinished(animationStateTime)) {
                    return jumpAttackAnim.getKeyFrame(animationStateTime);
                }
                float recoverTime = animationStateTime - jumpAttackAnim.getAnimationDuration();
                return maceSlamRecoverAnim.getKeyFrame(recoverTime);
            }

            case STUNNED -> {
                return stunAnim.getKeyFrame(animationStateTime, true);
            }

            case DEATH -> {
                if (!deathFallAnim.isAnimationFinished(animationStateTime)) {
                    return deathFallAnim.getKeyFrame(animationStateTime);
                }
                float afterFall = animationStateTime - deathFallAnim.getAnimationDuration();
                if (!deathHitAnim.isAnimationFinished(afterFall)) {
                    return deathHitAnim.getKeyFrame(afterFall);
                }
                float afterHit = afterFall - deathHitAnim.getAnimationDuration();
                return deathLandAnim.getKeyFrame(afterHit);
            }

            default -> {
                return null;
            }
        }
    }

    public void setupAsCorpse() {
        this.currentState = state.DEATH;
        this.bossFightStarted = true;
        this.velocity.setZero();

        this.animationStateTime = 100f;
    }
}
