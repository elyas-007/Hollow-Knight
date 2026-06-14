package com.hollow.models.entities.Knight;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.hollow.models.enums.KnightState;
import com.hollow.utils.Constants;

public class Knight {
    // constants
    public static final float G = -30f;
    public static final float JUMP_SPEED = 16f;
    public static final float MOVE_SPEED = 6f;
    public static final float DASH_SPEED = 20f;
    public static final float DASH_DURATION = 0.18f;
    public static final float DASH_COOLDOWN = 0.6f;
    public static final float FORCE_NAIL_X = 8f;
    public static final float FORCE_NAIL_Y = 6f;
    public static final float INVINCIBLE_DURATION = 1.2f;
    public static final float HIT_WIDTH = 0.8f;
    public static final float HIT_HEIGHT = 1.2f;
    public static KnightState state = KnightState.IDLE;
    public static KnightState preState;
    public float stateTimer = 0f;
    public final Vector2 position = new Vector2();
    public final Vector2 velocity = new Vector2();
    public final Rectangle hitbox = new Rectangle();


    private int currentMasks = 5;
    private int maxMasks = 5;
    private int currentSoul = 0; // 0 - 99
    private final int maxSoul = 99;
    private float invincibleTimer = 0f;
    private Vector2 lastPosition = new Vector2();

    private boolean isFacingRight;
    private boolean isGrounded;
    private boolean canDoubleJump;
    private boolean canDash;
    private boolean isDashing;
    private float dashDuration = 0f;
    private float dashCooldown = 0f;
    private float dashDirection = 1f;  //left -> -1 right -> 1



    //Animations

    public Animation<TextureRegion> idleAnimation;
    public Animation<TextureRegion> attackingAnimation;
    public Animation<TextureRegion> runningAnimation;
    public Animation<TextureRegion> hurtAnimation;
    public Animation<TextureRegion> dashAnimation;
    public Animation<TextureRegion> fallAnimation;
    public Animation<TextureRegion> jumpAnimation;






    public Knight(float startX, float startY) {
        position.set(startX, startY);
        lastPosition.set(startX, startY);
        hitbox.set(startX, startY, HIT_WIDTH, HIT_HEIGHT);
    }


    public void update(float delta) {
        // update
        updateTimers(delta);
        updateDash(delta);
        updateHitbox(delta);
        updatePosition(delta);
        applyG(delta);
        updateState();
    }

    private void updateTimers(float delta) {
        if (invincibleTimer > 0)
            invincibleTimer -= delta;

        if (dashCooldown > 0) {
            dashCooldown -= delta;
            if (dashCooldown <= 0)
                canDash = true;
        }
    }

    private void updatePosition(float delta) {
        position.x += velocity.x * delta;
        position.y += velocity.y  * delta;
    }

    private void updateHitbox(float delta) {
        hitbox.setPosition(position.x, position.y);
    }

    private void updateDash(float delta) {
        if (isDashing) {
            dashDuration -= delta;
            velocity.set(DASH_SPEED * dashDirection, 0f);
            if (dashDuration <= 0) {
                isDashing = false;
                velocity.x = MOVE_SPEED * dashDirection * 0.3f;
            }
        }
    }

    private void applyG(float delta) {
        if (!isDashing) {
            velocity.y += G * delta;
            if (velocity.y < -20f)
                velocity.y = -20f;
        }
    }

    private void updateState() {
        preState = state;

        if (state == KnightState.DEAD || state == KnightState.HURT)
            return;

        if (isDashing) {
            state = KnightState.DASHING;
        } else if (!isGrounded) {
            state = velocity.y > 0 ? KnightState.JUMPING : KnightState.FALLING;
        } else if (velocity.x != 0f) {
            state = KnightState.RUNNING;
        } else {
            state = KnightState.IDLE;
        }

        if (state != preState) stateTimer = 0f;
    }

    public void updateAnimations(float delta) {
        stateTimer += delta;
    }

    public TextureRegion getCurrentFrame() {
        Animation<TextureRegion> frame = getCurrentAnimation();
        if (frame == null)
            return null;

        boolean loop = (state != KnightState.ATTACKING && state != KnightState.HURT);
        return frame.getKeyFrame(stateTimer, loop);
    }

    private Animation<TextureRegion> getCurrentAnimation() {
        return switch (state) {
            case JUMPING -> jumpAnimation;
            case FALLING -> fallAnimation;
            case RUNNING -> runningAnimation;
            case DASHING -> dashAnimation;
            case HURT -> hurtAnimation;
            case ATTACKING -> attackingAnimation;
            default -> idleAnimation;
        };
    }

    public void movingHorizontally(float direction) {
        if (isDashing) return;
        velocity.x += direction * MOVE_SPEED;
        isFacingRight = direction > 0;
    }

    public void stopMovingHorizontally() {
        if (!isDashing) velocity.x = 0f;
    }

    public void jumping() {
        if (isGrounded) {
            velocity.y = JUMP_SPEED;
            isGrounded = false;
            canDoubleJump = true;
        } else if (canDoubleJump) {
            velocity.y = JUMP_SPEED * 0.8f;
            canDoubleJump = false;
        }
    }

    public void littleJumping() {
        if (velocity.y > 0)
            velocity.y *= 0.4f;
    }

    public void dashing() {
        if (!canDash || isDashing) return;
        isDashing = true;
        canDash = false;
        dashDuration = DASH_DURATION;
        dashCooldown = DASH_COOLDOWN;
        dashDirection = (isFacingRight) ? 1f : -1f;
    }

    public void attacking() {
        state = KnightState.ATTACKING;
        stateTimer = 0f;
    }

    public void landing(float top) {
        position.y = top;
        velocity.y = 0f;
        isGrounded = true;
        canDash = true;
        canDoubleJump = true;
        lastPosition.set(position.x, position.y);
    }

    public void hitCeiling(float top) {
        position.y = top - HIT_HEIGHT;
        velocity.y = 0f;
    }

    public void hitWall(float wallX, boolean fromLeft) {
        if (fromLeft) position.x = wallX;
        else position.x = wallX - HIT_WIDTH;
        velocity.x = 0f;
    }

    public void setOntAir() {
        isGrounded = false;
    }

    public void takeDamage(float damage, boolean fromRight) {
        if (invincibleTimer > 0 || state == KnightState.DEAD) return;

        currentMasks -= damage;
        invincibleTimer = INVINCIBLE_DURATION;
        state = KnightState.HURT;
        stateTimer = 0f;

        velocity.x = fromRight ? -FORCE_NAIL_X : FORCE_NAIL_X;
        velocity.y = FORCE_NAIL_Y;
        isGrounded = false;

        if (currentMasks <= 0) {
            currentMasks = 0;
            state = KnightState.DEAD;
        }
    }

    public void hitSpike() {
        takeDamage(1, false);
    }

    public void reSpawn() {
        position.set(lastPosition.x, lastPosition.y);
        velocity.set(0, 0);
        isGrounded = false;
        state = KnightState.FALLING;
        stateTimer = 0f;
    }

    public void gainSoul() {
        currentSoul = Math.min(maxSoul,  currentSoul + 33);
    }

    public boolean heal() {
        if (currentSoul >= 33 && currentMasks < maxMasks) {
            currentSoul -= 33;
            currentMasks = Math.min(maxMasks, currentMasks + 1);
            return true;
        }
        return false;
    }


    public float getX()          { return position.x; }
    public float getY()          { return position.y; }
    public float getWidth()      { return HIT_WIDTH; }
    public float getHeight()     { return HIT_HEIGHT; }
    public Vector2 getPosition() { return position; }
    public Vector2 getVelocity() { return velocity; }
    public Rectangle getHitbox() { return hitbox; }

    public boolean isOnGround()   { return isGrounded; }
    public boolean isFacingRight() { return isFacingRight; }
    public boolean isDashing()    { return isDashing; }
    public boolean isInvincible() { return invincibleTimer > 0; }
    public boolean isDead()       { return state == KnightState.DEAD; }

    public int getCurrentMasks()     { return currentMasks; }
    public int getMaxMasks()  { return maxMasks; }
    public int getSoul()   { return currentSoul; }
    public KnightState getState(){ return state; }

    public void setOnGround(boolean val) { this.isGrounded = val; }
    public void setVelocityY(float vy)   { this.velocity.y = vy; }
    public void setMaxHp(int val)        { this.maxMasks = val; currentMasks = Math.min(currentMasks, maxMasks); }
}
