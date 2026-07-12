package com.hollow.models.entities.knight;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.hollow.controllers.manager.AudioManager;
import com.hollow.models.Effect;
import com.hollow.models.data.GameData;
import com.hollow.views.screen.GameScreen;

public class Knight {
    public static final float G = -30f;
    public static final float MAX_FALL_SPEED = -20f;
    public static final float WALL_SLIDE_SPEED = -4f;
    public static final float JUMP_SPEED = 20f;
    public static final float MOVE_SPEED = 8f;
    public static final float WALL_JUMP_X = 6f;
    public static final float WALL_JUMP_Y = 13f;
    public static final float DASH_SPEED = 20f;
    public static final float DASH_DURATION = 0.18f;
    public static final float DASH_COOLDOWN = 0.6f;
    public static final float FORCE_NAIL_X = 8f;
    public static final float FORCE_NAIL_Y = 6f;
    public static final float INVINCIBLE_DURATION = 1.2f;
    public static final float HEAL_DURATION = 1.4f;
    public static final float HIT_WIDTH = 1.0f;
    public static final float HIT_HEIGHT = 1.2f;
    public static final float LOOK_DELAY = 0.6f;

    public com.hollow.models.entities.knight.KnightState state = KnightState.IDLE;
    public KnightState preState;
    public float stateTimer = 0f;
    public float stateLockTimer = 0f;

    public final Vector2 position = new Vector2();
    public final Vector2 velocity = new Vector2();
    public final Rectangle hitbox = new Rectangle();

    private float lookTimer = 0f;
    private int currentMasks = 5;
    private int maxMasks = 5;
    private int currentSoul = 0;
    private final int maxSoul = 99;
    private float invincibleTimer = 0f;
    private final Vector2 lastPosition = new Vector2();

    private boolean isFacingRight;
    private boolean isGrounded;
    private boolean canDoubleJump;
    private boolean canDash;
    private boolean isDashing;
    private float dashDuration = 0f;
    private float dashCooldown = 0f;
    private float dashDirection = 1f;
    public boolean castWraiths = false;

    private float moveDirection = 0f;
    private int touchingWallSide = 0;
    private boolean healing = false;
    private float healTimer = 0f;

    private int lookDirection = 0;
    private boolean altSlash = false;

    private final GameData data;
    public Array<Effect> activeEffects = new Array<>();


    public Animation<TextureRegion> dashEffectAnim, slashEffectAnim, upSlashEffectAnim, downSlashEffectAnim;
    public Animation<TextureRegion> idleAnim, idleHurtAnim, runStartAnim, runAnim, airborneAnim;
    public Animation<TextureRegion> doubleJumpAnim, landingAnim, dashAnim, wallSlideAnim, wallJumpAnim;
    public Animation<TextureRegion> slashAnim, slashAltAnim, upSlashAnim, downSlashAnim;
    public Animation<TextureRegion> focusAnim, focusStartAnim, focusGetAnim, focusEndAnim;
    public Animation<TextureRegion> castAnim, lookUpAnim, lookDownAnim, hurtAnim, deathAnim;
    public Animation<TextureRegion> soulScreamAnim, shadowScreamAnim, soulBallAnim, shadowBallAnim, blast;

    public boolean castProjectile = false;
    private boolean hasCastFired = false;
    public boolean noclipMode = false;
    public boolean godMode = false;
    public boolean isGrassTerrain = false;
    public boolean isEndingMode = false;

    private final AudioManager audioManager;

    public Knight(float startX, float startY, GameData data, AudioManager audioManager) {
        position.set(startX, startY);
        lastPosition.set(startX, startY);
        hitbox.set(startX, startY, HIT_WIDTH, HIT_HEIGHT);
        this.data = data;
        this.audioManager = audioManager;
    }


    public void update(float delta) {
        updateState(delta);
        updateTimers(delta);
        updateDash(delta);
        applyG(delta);
        updatePosition(delta);
        updateHitbox();
        updateHealing(delta);
        playSounds();
        updateEffects(delta);
    }

    private void playSounds() {
        if (state == KnightState.RUNNING && isGrounded) audioManager.playFootsteps(isGrassTerrain);
        else audioManager.stopFootsteps();

        if (state == KnightState.AIRBORNE && velocity.y < -6f) audioManager.playFallingSound();
        else audioManager.stopFallingSound();

        if (state == KnightState.WALL_SLIDE) audioManager.playWallSlideLoop();
        else audioManager.stopWallSlideLoop();

        if (state == KnightState.FOCUSING_START || state == KnightState.FOCUSING) {
            audioManager.playFocusChargeLoop();
        } else {
            audioManager.stopFocusChargeLoop();
        }
    }

    private void updateEffects(float delta) {
        for (int i = activeEffects.size - 1; i >= 0; i--) {
            Effect effect = activeEffects.get(i);
            effect.update(delta);
            if (effect.isFinished()) activeEffects.removeIndex(i);
        }
    }

    private void updateTimers(float delta) {
        if (invincibleTimer > 0)
            invincibleTimer -= delta;

        if (dashCooldown > 0) {
            dashCooldown -= delta;
            if (dashCooldown <= 0 && isGrounded)
                canDash = true;
        }
    }

    private void updateHealing(float delta) {
        if (!healing) return;
        healTimer += delta;

        boolean hasQuickFocus = data.getActiveSlot().getEquippedCharms()
            .contains(Charm.QUICK_FOCUS, true);
        float currentHealDuration = hasQuickFocus ? HEAL_DURATION * 0.6f : HEAL_DURATION;

        if (healTimer >= currentHealDuration) {
            currentSoul = Math.max(0, currentSoul - 33);
            currentMasks = Math.min(maxMasks, currentMasks + 1);

            state = KnightState.FOCUSING_GET;
            stateTimer = 0f;
            stateLockTimer = animDuration(focusGetAnim);
            healTimer = 0f;
            audioManager.playSound(audioManager.audioLoader.knightFocusHeal);

            if (currentSoul < 33 || currentMasks >= maxMasks) healing = false;
        }
    }

    private void updatePosition(float delta) {
        position.x += velocity.x * delta;
        position.y += velocity.y  * delta;
    }

    private void updateHitbox() {
        hitbox.setPosition(position.x, position.y);
    }

    private void updateDash(float delta) {
        if (isDashing) {
            dashDuration -= delta;
            boolean hasSharpShadow = data.getActiveSlot().getEquippedCharms()
                .contains(Charm.SHARP_SHADOW, true);
            float currentDashSpeed = hasSharpShadow ? DASH_SPEED * 1.2f : DASH_SPEED;
            velocity.set(currentDashSpeed * dashDirection, 0f);

            if (dashDuration <= 0) {
                isDashing = false;
                velocity.x = MOVE_SPEED * dashDirection * 0.3f;
            }
        }
    }

    private void applyG(float delta) {
        if (!isDashing && state != KnightState.CASTING && state != KnightState.DEAD) {
            velocity.y += G * delta;
            if (state == KnightState.WALL_SLIDE && velocity.y < WALL_SLIDE_SPEED) {
                velocity.y = WALL_SLIDE_SPEED;
            } else if (velocity.y < MAX_FALL_SPEED) {
                velocity.y = MAX_FALL_SPEED;
            }
        }
    }

    private void updateState(float delta) {
        preState = state;
        if (checkDeathState()) return;
        if (checkHealingState(delta)) return;
        if (checkLockedState(delta)) return;

        determineMovementState(delta);
        if (state != preState) stateTimer = 0f;
    }

    private boolean checkDeathState() {
        if (currentMasks <= 0) {
            if (state != KnightState.DEAD) {
                state = KnightState.DEAD;
                stateTimer = 0f;
                stateLockTimer = animDuration(deathAnim);
            }
            return true;
        }
        return false;
    }

    private boolean checkHealingState(float delta) {
        if (healing) {
            if (stateLockTimer > 0) {
                stateLockTimer -= delta;
            } else {
                if (state == KnightState.FOCUSING_START || state == KnightState.FOCUSING_GET) {
                    state = KnightState.FOCUSING;
                    stateTimer = 0f;
                } else if (state != KnightState.FOCUSING) {
                    state = KnightState.FOCUSING;
                }
            }
            if (state != preState) stateTimer = 0f;
            return true;
        }
        return false;
    }

    private boolean checkLockedState(float delta) {
        if (stateLockTimer > 0) {
            stateLockTimer -= delta;

            if (state == KnightState.CASTING || state == KnightState.UP_CASTING) {
                if (!hasCastFired && stateTimer >= animDuration(castAnim) * 0.4f) {
                    if (state == KnightState.CASTING) castProjectile = true;
                    else castWraiths = true;
                    hasCastFired = true;
                }
                if (stateTimer >= animDuration(castAnim)) stateLockTimer = 0f;
                else return true;
            } else if ((state == KnightState.WALL_JUMP || state == KnightState.LANDING)
                && (moveDirection != 0 || isDashing)) {
                stateLockTimer = 0f;
            } else {
                if (state != preState) stateTimer = 0f;
                return true;
            }
        }
        return false;
    }

    private void determineMovementState(float delta) {
        if (isDashing) {
            state = KnightState.DASHING;
            lookTimer = 0f;
        } else if (!isGrounded && touchingWallSide != 0 && velocity.y < 0 && wallSideAllowed()) {
            state = KnightState.WALL_SLIDE;
            lookTimer = 0f;
        } else if (!isGrounded) {
            state = KnightState.AIRBORNE;
            lookTimer = 0f;
        } else if (velocity.x != 0) {
            if (state != KnightState.RUN_START && state != KnightState.RUNNING) {
                state = KnightState.RUN_START;
                stateTimer = 0f;
                stateLockTimer = animDuration(runStartAnim);
            } else if (state == KnightState.RUN_START && stateLockTimer <= 0) {
                state = KnightState.RUNNING;
            }
            lookTimer = 0f;
        } else {
            if (lookDirection != 0) {
                lookTimer += delta;
                if (lookTimer >= LOOK_DELAY) {
                    state = (lookDirection > 0) ? KnightState.LOOK_UP : KnightState.LOOK_DOWN;
                } else {
                    state = KnightState.IDLE;
                }
            } else {
                lookTimer = 0f;
                state = KnightState.IDLE;
            }
        }
    }

    private boolean wallSideAllowed() {
        return (touchingWallSide > 0 && moveDirection > 0) || (touchingWallSide < 0 && moveDirection < 0);
    }

    private boolean isLocked() {
        return stateLockTimer > 0 || state == KnightState.DEAD || healing;
    }

    private boolean movementLocked() {
        return state == KnightState.HURT    || state == KnightState.DEAD
            || state == KnightState.FOCUSING || state == KnightState.FOCUSING_START
            || state == KnightState.FOCUSING_GET || state == KnightState.FOCUSING_END
            || state == KnightState.WALL_JUMP || state == KnightState.CASTING;
    }

    public void updateAnimations(float delta) {
        stateTimer += delta;
    }

    private float animDuration(Animation<TextureRegion> anim) {
        return anim != null ? anim.getAnimationDuration() : 0.25f;
    }

    public TextureRegion getCurrentFrame() {
        Animation<TextureRegion> anim = getAnimationForState();
        if (anim == null) return null;
        return anim.getKeyFrame(stateTimer, isLoopingState(state));
    }

    private Animation<TextureRegion> getAnimationForState() {
        return switch (state) {
            case RUN_START -> runStartAnim;
            case RUNNING -> runAnim;
            case AIRBORNE -> airborneAnim;
            case DOUBLE_JUMPING -> doubleJumpAnim;
            case LANDING -> landingAnim;
            case DASHING -> dashAnim;
            case WALL_SLIDE -> wallSlideAnim;
            case WALL_JUMP -> wallJumpAnim;
            case SLASH -> slashAnim;
            case SLASH_ALT -> slashAltAnim;
            case UP_SLASH -> upSlashAnim;
            case DOWN_SLASH -> downSlashAnim;
            case FOCUSING_START -> focusStartAnim;
            case FOCUSING -> focusAnim;
            case FOCUSING_GET -> focusGetAnim;
            case FOCUSING_END -> focusEndAnim;
            case CASTING -> castAnim;
            case DEAD -> deathAnim;
            case LOOK_UP -> lookUpAnim;
            case LOOK_DOWN -> lookDownAnim;
            case UP_CASTING -> lookUpAnim;
            case IDLE -> (currentMasks == 1) ? idleHurtAnim : idleAnim;
            default -> idleAnim;
        };
    }

    private boolean isLoopingState(KnightState s) {
        return switch (s) {
            case IDLE, RUNNING , DASHING, WALL_SLIDE, FOCUSING, LOOK_UP, LOOK_DOWN -> true;
            default -> false;
        };
    }

    public void movingHorizontally(float direction) {
        if (isDashing || movementLocked()) {
            moveDirection = 0f;
            return;
        }
        float speed = isEndingMode ? MOVE_SPEED * 0.35f : MOVE_SPEED;
        velocity.x = direction * speed;
        isFacingRight = direction > 0;
        moveDirection = direction;
    }

    public void stopMovingHorizontally() {
        moveDirection = 0f;
        if (!isDashing && !movementLocked()) velocity.x = 0f;
    }

    public void jumping() {
        if (movementLocked()) return;

        if (state == KnightState.WALL_SLIDE) {
            wallJump();
        } else if (isGrounded) {
            velocity.y = JUMP_SPEED;
            isGrounded = false;
            canDoubleJump = true;
            audioManager.playSound(audioManager.audioLoader.knightJump);
        } else if (canDoubleJump) {
            velocity.y = JUMP_SPEED * 0.8f;
            canDoubleJump = false;
            state = KnightState.DOUBLE_JUMPING;
            stateTimer = 0f;
            stateLockTimer = animDuration(doubleJumpAnim);
            audioManager.playSound(audioManager.audioLoader.knightWings);
        }
    }

    public void wallJump() {
        if (state != KnightState.WALL_SLIDE) return;

        velocity.x = -touchingWallSide * WALL_JUMP_X;
        velocity.y = WALL_JUMP_Y;
        isFacingRight = touchingWallSide < 0;
        isGrounded = false;
        canDoubleJump = true;
        canDash = true;
        touchingWallSide = 0;

        state = KnightState.WALL_JUMP;
        stateTimer = 0f;
        stateLockTimer = animDuration(wallJumpAnim);
        audioManager.playSound(audioManager.audioLoader.knightWallJump);
    }

    public void littleJumping() {
        if (velocity.y > 0) velocity.y *= 0.4f;
    }

    public void dashing() {
        if (!canDash || isDashing || isLocked()) return;
        isDashing = true;
        canDash = false;
        dashDuration = DASH_DURATION;

        dashCooldown = data.getActiveSlot().getEquippedCharms().contains( // dash master
            Charm.DASH_MASTER, true) ? DASH_COOLDOWN * 0.5f : DASH_COOLDOWN;
        dashDirection = (isFacingRight) ? 1f : -1f;

        float offsetX = isFacingRight ? -1.5f : -0.5f;
        activeEffects.add(new Effect(dashEffectAnim, offsetX, 0f, 2.5f, 1.5f, isFacingRight));

        boolean hasSharpShadow = data.getActiveSlot().getEquippedCharms()
            .contains(Charm.SHARP_SHADOW, true);
        if (hasSharpShadow) audioManager.playSound(audioManager.audioLoader.knightShadeDash);
        else audioManager.playSound(audioManager.audioLoader.knightDash);
    }

    public void attacking(int direction) {
        if (isLocked()) return;
        Animation<TextureRegion> anim;

        if (direction > 0) {
            state = KnightState.UP_SLASH;
            anim = upSlashAnim;
            activeEffects.add(new Effect(upSlashEffectAnim, -0.6f, 1.0f, 2f, 2f, isFacingRight));
        } else if (direction < 0 && !isGrounded) {
            state = KnightState.DOWN_SLASH;
            anim = downSlashAnim;
            activeEffects.add(new Effect(downSlashEffectAnim, -0.6f, -1.2f, 2f, 2f, isFacingRight));
        } else {
            altSlash = !altSlash;
            state = altSlash ? KnightState.SLASH_ALT : KnightState.SLASH;
            anim = altSlash ? slashAltAnim : slashAnim;
            float offsetX = isFacingRight ? 0.5f : -1.5f;
            activeEffects.add(new Effect(slashEffectAnim, offsetX, 0f, 2.5f, 2f, isFacingRight));
        }

        stateTimer = 0f;
        boolean hasQuickSlash = data.getActiveSlot().getEquippedCharms()
            .contains(Charm.QUICK_SLASH, true);
        float speedMultiplier = hasQuickSlash ? 0.6f : 1f;
        stateLockTimer = animDuration(anim) * speedMultiplier;
    }

    public void landing(float top) {
        boolean wasAirborne = !isGrounded && velocity.y < -3f;
        boolean isHardLanding = !isGrounded && velocity.y <= -18f;

        position.y = top;
        velocity.y = 0;
        isGrounded = true;
        if (dashCooldown <= 0) canDash = true;
        canDoubleJump = false;
        touchingWallSide = 0;
        lastPosition.set(position);

        if (wasAirborne) {
            if (state == KnightState.SLASH || state == KnightState.SLASH_ALT ||
                state == KnightState.UP_SLASH || state == KnightState.DOWN_SLASH) {
                stateLockTimer = 0f;
            }

            if (stateLockTimer <= 0 && state != KnightState.HURT && state != KnightState.DEAD) {
                state = KnightState.LANDING;
                stateTimer = 0f;
                stateLockTimer = animDuration(landingAnim);
            }
        }
        if (isHardLanding) {
            audioManager.playSound(audioManager.audioLoader.knightHardLand);
        }
    }

    public void hitCeiling(float top) {
        position.y = top - HIT_HEIGHT;
        velocity.y = 0f;
    }

    public void hitWall(float wallX, int side) {
        position.x = (side > 0) ? (wallX - HIT_WIDTH) : wallX;
        velocity.x = 0f;
        touchingWallSide = side;
    }

    public void setAirborne() {
        isGrounded = false;
    }

    public void takeDamage(int damage, boolean fromRight) {
        if (godMode || invincibleTimer > 0 || state == KnightState.DEAD) return;

        currentMasks -= damage;
        GameScreen.triggerShake(0.2f, 0.1f);
        invincibleTimer = INVINCIBLE_DURATION;
        healing = false;
        isDashing = false;
        touchingWallSide = 0;

        if (currentMasks > 0) {
            velocity.x = fromRight ? -FORCE_NAIL_X : FORCE_NAIL_X;
            velocity.y = FORCE_NAIL_Y;
            isGrounded = false;
        } else {
            velocity.setZero();
        }

        if (currentMasks <= 0) {
            currentMasks = 0;
            state = KnightState.DEAD;
            stateTimer = 0f;
            stateLockTimer = animDuration(deathAnim);
            audioManager.playSound(audioManager.audioLoader.knightDeath);
        } else {
            state = KnightState.HURT;
            stateTimer = 0f;
            stateLockTimer = animDuration(hurtAnim);
            if (damage != 0) audioManager.playSound(audioManager.audioLoader.knightDamage);
        }
    }

    public void stopFocusing() {
        if (healing || state == KnightState.FOCUSING || state == KnightState.FOCUSING_START || state == KnightState.FOCUSING_GET) {
            healing = false;
            healTimer = 0f;
            state = KnightState.FOCUSING_END;
            stateTimer = 0f;
            stateLockTimer = animDuration(focusEndAnim);
        }
    }

    public void startCasting() {
        if (isLocked()) return;
        state = KnightState.CASTING;
        stateTimer = 0f;
        stateLockTimer = animDuration(castAnim);
        castProjectile = false;
        hasCastFired = false;
        velocity.setZero();
    }

    public void hitSpike() {
        takeDamage(1, false);
    }

    public void reSpawn() {
        position.set(lastPosition.x, lastPosition.y + 0.2f);
        velocity.set(0, 0);
        isGrounded = false;
        touchingWallSide = 0;
        healing = false;
    }

    public void resetDash() { this.canDash = true; }
    public void resetDoubleJump() { this.canDoubleJump = true; }

    public void gainSoul(int amount) {
        if (currentSoul >= maxSoul) return;
        currentSoul = Math.min(maxSoul, currentSoul + amount);
        audioManager.playSoulPickupSound(currentSoul);
    }

    public void startFocusing() {
        if (healing || isLocked() || !isGrounded || velocity.x != 0) return;
        if (currentSoul < 33 || currentMasks >= maxMasks) return;

        healing = true;
        healTimer = 0f;
        state = KnightState.FOCUSING_START;
        stateTimer = 0f;
        stateLockTimer = animDuration(focusStartAnim);
    }

    public void resetWallTouch() { touchingWallSide = 0; }
    public void setLookDirection(int dir) { this.lookDirection = dir; }

    public boolean isDeathAnimationFinished() {
        return state == KnightState.DEAD && deathAnim != null && deathAnim.isAnimationFinished(stateTimer);
    }

    public boolean consumeSoul(int amount) {
        if (currentSoul >= amount) {
            currentSoul -= amount;
            return true;
        }
        return false;
    }

    public void startUpCasting() {
        if (isLocked()) return;
        state = KnightState.UP_CASTING;
        stateTimer = 0f;
        stateLockTimer = animDuration(castAnim);
        castWraiths = false;
        hasCastFired = false;

        velocity.x = 0;
        if (!isGrounded) velocity.y = Math.max(0, velocity.y);
    }

    public void fullRespawn(float startX, float startY) {
        position.set(startX, startY);
        lastPosition.set(startX, startY);
        velocity.setZero();
        isGrounded = false;
        touchingWallSide = 0;
        healing = false;

        currentMasks = maxMasks;
        currentSoul = 0;

        state = KnightState.AIRBORNE;
        stateTimer = 0f;
        stateLockTimer = 0f;
        activeEffects.clear();
    }

    public void refillSoulCheat() { gainSoul(99); }
    public void emergencyHealCheat() { if (currentMasks < maxMasks) currentMasks++; }

    public float getX() { return position.x; }
    public float getY() { return position.y; }
    public float getWidth() { return HIT_WIDTH; }
    public float getHeight() { return HIT_HEIGHT; }
    public Vector2 getPosition() { return position; }
    public Vector2 getVelocity() { return velocity; }
    public Rectangle getHitbox() { return hitbox; }

    public boolean isOnGround() { return isGrounded; }
    public boolean isFacingRight() { return isFacingRight; }
    public boolean isDashing() { return isDashing; }
    public boolean isInvincible() { return invincibleTimer > 0; }
    public boolean isDead() { return state == KnightState.DEAD; }
    public boolean isBusy() {return isLocked();}

    public int getCurrentMasks() { return currentMasks; }
    public int getMaxMasks() { return maxMasks; }
    public int getSoul() { return currentSoul; }
    public KnightState getState() { return state; }

    public void setOnGround(boolean val) { this.isGrounded = val; }
    public void setVelocityY(float vy)   { this.velocity.y = vy; }
}
