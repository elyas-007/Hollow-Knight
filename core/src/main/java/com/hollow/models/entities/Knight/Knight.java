package com.hollow.models.entities.Knight;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.hollow.models.Effect;
import com.hollow.models.GameData;
import com.hollow.models.enums.KnightState;

public class Knight {
    // constants
    public static final float G = -30f;
    public static final float MAX_FALL_SPEED = -20f;
    public static final float WALL_SLIDE_SPEED = -4f;
    public static final float JUMP_SPEED = 20f;
    public static final float MOVE_SPEED = 8f;
    public static final float WALL_JUMP_X   = 6f;
    public static final float WALL_JUMP_Y   = 13f;
    public static final float DASH_SPEED = 20f;
    public static final float DASH_DURATION = 0.18f;
    public static final float DASH_COOLDOWN = 0.6f;
    public static final float FORCE_NAIL_X = 8f;
    public static final float FORCE_NAIL_Y = 6f;
    public static final float INVINCIBLE_DURATION = 1.2f;
    public static final float HEAL_DURATION = 1.4f;
    public static final float HIT_WIDTH = 0.8f;
    public static final float HIT_HEIGHT = 1.2f;
    public KnightState state = KnightState.IDLE;
    public KnightState preState;
    public float stateTimer = 0f;
    public float stateLockTimer = 0f;

    public final Vector2 position = new Vector2();
    public final Vector2 velocity = new Vector2();
    public final Rectangle hitbox = new Rectangle();

    private float lookTimer = 0f;
    public static final float LOOK_DELAY = 0.6f;

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

    private float moveDirection = 0f; // -1, 0, +1
    private int touchingWallSide = 0; //+1 -> right -1 -> left 0 -> nothing
    private boolean healing = false;
    private float healTimer = 0f;

    private int lookDirection = 0; // +1 -> up -1 -> down 0-> nothing
    private boolean altSlash = false;

    private GameData data;
    //Animations

    public Array<Effect> activeEffects = new Array<>();
    public Animation<TextureRegion> dashEffectAnim, slashEffectAnim, upSlashEffectAnim, downSlashEffectAnim;

    public Animation<TextureRegion> idleAnim;
    public Animation<TextureRegion> runAnim;
    public Animation<TextureRegion> airborneAnim;
    public Animation<TextureRegion> doubleJumpAnim;
    public Animation<TextureRegion> landingAnim;
    public Animation<TextureRegion> dashAnim;
    public Animation<TextureRegion> wallSlideAnim;
    public Animation<TextureRegion> wallJumpAnim;
    public Animation<TextureRegion> slashAnim;
    public Animation<TextureRegion> slashAltAnim;
    public Animation<TextureRegion> upSlashAnim;
    public Animation<TextureRegion> downSlashAnim;
    public Animation<TextureRegion> focusAnim;
    public Animation<TextureRegion> lookUpAnim;
    public Animation<TextureRegion> lookDownAnim;
    public Animation<TextureRegion> hurtAnim;
    public Animation<TextureRegion> deathAnim;

    public Animation<TextureRegion> soulBallAnim;
    public Animation<TextureRegion> shadowBallAnim;






    public Knight(float startX, float startY, GameData data) {
        position.set(startX, startY);
        lastPosition.set(startX, startY);
        hitbox.set(startX, startY, HIT_WIDTH, HIT_HEIGHT);
        this.data = data;
    }


    public void update(float delta) {
        // update
        updateState(delta);
        updateTimers(delta);
        updateDash(delta);
        applyG(delta);
        updatePosition(delta);
        updateHitbox();
        updateHealing(delta);

        for (int i = activeEffects.size - 1; i >= 0; i--) {
            Effect effect = activeEffects.get(i);
            effect.update(delta);
            if (effect.isFinished()) {
                activeEffects.removeIndex(i);
            }
        }
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

    private void updateHealing(float delta) {
        if (!healing) return;
        healTimer += delta;

        float currentHealDuration = data.equippedCharms.contains( // quick focus
            Charm.QUICK_FOCUS, true) ? HEAL_DURATION * 0.6f
                                                    : HEAL_DURATION;

        if (healTimer >= currentHealDuration) {
            currentSoul = Math.max(0, currentSoul - 33);
            currentMasks = Math.min(maxMasks, currentMasks + 1);
            healing = false;
            healTimer = 0f;
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
            float currentDashSpeed = data.equippedCharms.contains( //sharp shadow
                Charm.SHARP_SHADOW, true) ?
                DASH_SPEED * 1.2f : DASH_SPEED;
            velocity.set(currentDashSpeed * dashDirection, 0f);
            if (dashDuration <= 0) {
                isDashing = false;
                velocity.x = MOVE_SPEED * dashDirection * 0.3f;
            }
        }
    }

    private void applyG(float delta) {
        if (!isDashing) {
            velocity.y += G * delta;

            if (state == KnightState.WALL_SLIDE) {
                if (velocity.y < WALL_SLIDE_SPEED) {
                    velocity.y = WALL_SLIDE_SPEED;
                }
            } else {
                if (velocity.y < MAX_FALL_SPEED) {
                    velocity.y = MAX_FALL_SPEED;
                }
            }
        }
    }

    private void updateState(float delta) {
        preState = state;

        if (currentMasks <= 0) {
            if (state != KnightState.DEAD) {
                state = KnightState.DEAD;
                stateTimer = 0f;
                stateLockTimer = animDuration(deathAnim);
            }
            return;
        }

        if (healing) {
            state = KnightState.FOCUSING;
            return;
        }

        if (stateLockTimer > 0) {
            if (state == KnightState.WALL_JUMP && (moveDirection != 0 || isDashing)) {
                stateLockTimer = 0f;
            } else {
                stateLockTimer -= delta;
                return;
            }
        }

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
            state = KnightState.RUNNING;
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

        if (state != preState) stateTimer = 0f;
    }

    private boolean wallSideAllowed() {
        return (touchingWallSide > 0 && moveDirection > 0) ||
            (touchingWallSide < 0 && moveDirection < 0);
    }

    private boolean isLocked() {
        return stateLockTimer > 0 || state == KnightState.DEAD || healing;
    }

    private boolean movementLocked() {
        return state == KnightState.HURT    || state == KnightState.DEAD
            || state == KnightState.LANDING || state == KnightState.FOCUSING
            || state == KnightState.WALL_JUMP;
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
            case FOCUSING -> focusAnim;
            case LOOK_UP -> lookUpAnim;
            case LOOK_DOWN -> lookDownAnim;
            case HURT -> hurtAnim;
            case DEAD -> deathAnim;
            default -> idleAnim;
        };
    }

    private boolean isLoopingState(KnightState s) {
        return switch (s) {
            case IDLE, RUNNING , DASHING, WALL_SLIDE, FOCUSING, LOOK_UP, LOOK_DOWN -> true;
            default ->
                false;
        };
    }

    public void movingHorizontally(float direction) {
        if (isDashing || movementLocked()) {
            moveDirection = 0f;
            return;
        }
        velocity.x = direction * MOVE_SPEED;
        isFacingRight = direction > 0;
        moveDirection = direction;
    }

    public void stopMovingHorizontally() {
        moveDirection = 0f;
        if (!isDashing && !movementLocked()) velocity.x = 0f;
    }

    public void jumping() {
        if (movementLocked())
            return;

        if (state == KnightState.WALL_SLIDE) {
            wallJump();
        } else if (isGrounded) {
            velocity.y = JUMP_SPEED;
            isGrounded = false;
            canDoubleJump = true;
        } else if (canDoubleJump) {
            velocity.y = JUMP_SPEED * 0.8f;
            canDoubleJump = false;
            state = KnightState.DOUBLE_JUMPING;
            stateTimer = 0f;
            stateLockTimer = animDuration(doubleJumpAnim);
        }
    }

    public void wallJump() {
        if (state != KnightState.WALL_SLIDE)
            return;

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
    }

    public void littleJumping() {
        if (velocity.y > 0)
            velocity.y *= 0.4f;
    }

    public void dashing() {
        if (!canDash || isDashing || isLocked())
            return;

        isDashing = true;
        canDash = false;
        dashDuration = DASH_DURATION;
        dashCooldown = data.equippedCharms.contains( // dash master
            Charm.DASH_MASTER, true) ?
            DASH_COOLDOWN * 0.5f : DASH_COOLDOWN;
        dashDirection = (isFacingRight) ? 1f : -1f;

        float offsetX = isFacingRight ? -1.5f : -0.5f;
        activeEffects.add(new Effect(dashEffectAnim, offsetX, 0f, 2.5f, 1.5f, isFacingRight));
    }

    public void attacking(int direction) {
        if (isLocked())
            return;
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
        float speedMultiplier = data.equippedCharms.contains( //quick slash
            Charm.QUICK_SLASH, true) ? 0.6f : 1f;
        stateLockTimer = animDuration(anim) * speedMultiplier;
    }

    public void landing(float top) {
        boolean wasAirborne = !isGrounded;

        position.y = top;
        velocity.y = 0;
        isGrounded = true;
        canDash = true;
        canDoubleJump = false;
        touchingWallSide = 0;
        lastPosition.set(position);

        if (wasAirborne && stateLockTimer <= 0
            && state != KnightState.HURT && state != KnightState.DEAD) {
            state = KnightState.LANDING;
            stateTimer = 0f;
            stateLockTimer = animDuration(landingAnim);
        }
    }

    public void hitCeiling(float top) {
        position.y = top - HIT_HEIGHT;
        velocity.y = 0f;
    }

    public void hitWall(float wallX, int side) {
        if (side > 0) {
            position.x = wallX - HIT_WIDTH;
        } else {
            position.x = wallX;
        }
        velocity.x = 0f;
        touchingWallSide = side;
    }

    public void setAirborne() {
        isGrounded = false;
    }

    public void takeDamage(int damage, boolean fromRight) {
        if (invincibleTimer > 0 || state == KnightState.DEAD) return;

        currentMasks -= damage;
        invincibleTimer = INVINCIBLE_DURATION;
        healing = false;
        isDashing = false;
        touchingWallSide = 0;
//        stateTimer = 0f;

        velocity.x = fromRight ? -FORCE_NAIL_X : FORCE_NAIL_X;
        velocity.y = FORCE_NAIL_Y;
        isGrounded = false;

        if (currentMasks <= 0) {
            currentMasks = 0;
            state = KnightState.DEAD;
            stateTimer = 0f;
            stateLockTimer = animDuration(deathAnim);
        } else {
            state = KnightState.HURT;
            stateTimer = 0f;
            stateLockTimer = animDuration(hurtAnim);
        }
    }

    public void stopFocusing() {
        if (healing) {
            healing = false;
            healTimer = 0f;
        }
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

    public void gainSoul(int amount) {
        currentSoul = Math.min(99, currentSoul + amount);
    }

    public void startFocusing() {
        if (healing)
            return;

        if (isLocked() || !isGrounded || velocity.x != 0)
            return;

        if (currentSoul < 33 || currentMasks >= maxMasks)
            return;

        healing = true;
        healTimer = 0f;
        state = KnightState.FOCUSING;
        stateTimer = 0f;
    }

    public boolean heal() {
        if (currentSoul >= 33 && currentMasks < maxMasks) {
            currentSoul -= 33;
            currentMasks = Math.min(maxMasks, currentMasks + 1);
            return true;
        }
        return false;
    }

    public void resetWallTouch() {
        touchingWallSide = 0;
    }

    public void setLookDirection(int dir) {
        this.lookDirection = dir;
    }

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

    public void fullRespawn(float startX, float startY) {
        position.set(startX, startY);
        lastPosition.set(startX, startY);
        velocity.set(0, 0);
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
    public boolean isBusy() {return isLocked();}

    public int getCurrentMasks()     { return currentMasks; }
    public int getMaxMasks()  { return maxMasks; }
    public int getSoul()   { return currentSoul; }
    public KnightState getState(){ return state; }

    public void setOnGround(boolean val) { this.isGrounded = val; }
    public void setVelocityY(float vy)   { this.velocity.y = vy; }
    public void setMaxHp(int val)        { this.maxMasks = val; currentMasks = Math.min(currentMasks, maxMasks); }
}
