package com.hollow.models.entities.Knight;

import com.badlogic.gdx.math.Vector2;
import com.hollow.models.enums.KnightState;
import com.hollow.utils.Constants;

public class Knight {
    private Vector2 position = new Vector2();
    private Vector2 velocity = new Vector2();
    private float hitX;
    private float hitY;
    private KnightState state =  KnightState.IDLE;
    private final float moveSpeed;
    private final float jumpForce;
    private final float gravity;
    private final float dashSpeed;
    private final float dashDuration;
    private float dashTimer;

    private int currentMasks;
    private final int maxMasks;
    private int currentSoul;
    private final int maxSoul;

    private boolean isFacingRight;
    private boolean isGrounded;
    private boolean canDoubleJump;
    private boolean canDash;
    private boolean isDashing;
    private boolean isInvincible;

    public Knight(float startX, float startY, float moveSpeed, float jumpForce, float gravity) {
        position.x = startX;
        position.y = startY;
        this.moveSpeed = Constants.Knight.MOVE_SPEED;
        this.jumpForce = Constants.Knight.JUMP_FORCE;
        this.gravity = Constants.Knight.GRAVITY;
        this.maxMasks = Constants.Knight.MAX_MASK;
        this.currentMasks = maxMasks;
        this.maxSoul = Constants.Knight.MAX_SOUL;
        this.currentSoul = 0;
        this.dashSpeed = Constants.Knight.DASH_SPEED_MULTIPLIER * moveSpeed;
        this.dashDuration = Constants.Knight.DASH_DURATION;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public float getHitX() {
        return hitX;
    }

    public void setHitX(float hitX) {
        this.hitX = hitX;
    }

    public float getHitY() {
        return hitY;
    }

    public void setHitY(float hitY) {
        this.hitY = hitY;
    }

    public KnightState getState() {
        return state;
    }

    public void setState(KnightState state) {
        this.state = state;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public float getJumpForce() {
        return jumpForce;
    }

    public float getGravity() {
        return gravity;
    }

    public float getDashSpeed() {
        return dashSpeed;
    }

    public float getDashDuration() {
        return dashDuration;
    }

    public float getDashTimer() {
        return dashTimer;
    }

    public void setDashTimer(float dashTimer) {
        this.dashTimer = dashTimer;
    }

    public int getCurrentMasks() {
        return currentMasks;
    }

    public void setCurrentMasks(int currentMasks) {
        this.currentMasks = currentMasks;
    }

    public int getMaxMasks() {
        return maxMasks;
    }

    public int getCurrentSoul() {
        return currentSoul;
    }

    public void setCurrentSoul(int currentSoul) {
        this.currentSoul = currentSoul;
    }

    public int getMaxSoul() {
        return maxSoul;
    }

    public boolean isFacingRight() {
        return isFacingRight;
    }

    public void setFacingRight(boolean facingRight) {
        isFacingRight = facingRight;
    }

    public boolean isGrounded() {
        return isGrounded;
    }

    public void setGrounded(boolean grounded) {
        isGrounded = grounded;
    }

    public boolean isCanDoubleJump() {
        return canDoubleJump;
    }

    public void setCanDoubleJump(boolean canDoubleJump) {
        this.canDoubleJump = canDoubleJump;
    }

    public boolean isCanDash() {
        return canDash;
    }

    public void setCanDash(boolean canDash) {
        this.canDash = canDash;
    }

    public boolean isDashing() {
        return isDashing;
    }

    public void setDashing(boolean dashing) {
        isDashing = dashing;
    }

    public boolean isInvincible() {
        return isInvincible;
    }

    public void setInvincible(boolean invincible) {
        isInvincible = invincible;
    }
}
