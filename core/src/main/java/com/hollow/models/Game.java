package com.hollow.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.hollow.HollowKnight;
import com.hollow.assets.TiledMapHelper;
import com.hollow.models.entities.Enemy.Tiktik;
import com.hollow.models.entities.FalseKnightBoss.FalseKnight;
import com.hollow.models.entities.FalseKnightBoss.IdleBehavior;
import com.hollow.models.entities.FalseKnightBoss.Shockwave;
import com.hollow.models.entities.Knight.Knight;
import com.hollow.views.screens.GameScreen;

public class Game {
    private HollowKnight game;
    private final Knight knight;
    public FalseKnight boss;
    private final Array<SolidBlock> groundRects;
    private final Array<SolidBlock> spikeRects;
    private GameData data;

    private boolean wasOutsideArena = false;

    private Array<Tiktik> tiktiks;

    private int keyLeft;
    private int keyRight;
    private int keyUp;
    private int keyDown;
    private int keyJump;
    private int keyDash;
    private int keyAttack;
    private int keyFocus;

    private float startX;
    private float startY;

    private TransitionZone transitionZones;
    private GameScreen screen;

    private SolidBlock leftDoor;
    private SolidBlock rightDoor;

    public Game(HollowKnight game, Knight knight, Array<SolidBlock> groundRects, Array<SolidBlock> spikeRects, Array<Tiktik> tiktiks, TransitionZone transitionZones, GameScreen screen, GameData data, FalseKnight boss) {
        this.game = game;
        this.knight = knight;
        this.groundRects = groundRects;
        this.spikeRects = spikeRects;
        keyLeft = game.settings.keyLeft;
        keyRight = game.settings.keyRight;
        keyUp = game.settings.keyUp;
        keyDown = game.settings.keyDown;
        keyJump = game.settings.keyJump;
        keyDash = game.settings.keyDash;
        keyAttack =  game.settings.keyAttack;
        keyFocus=  game.settings.keyFocus;
        keyUp = game.settings.keyUp;
        keyDown = game.settings.keyDown;

        this.tiktiks = tiktiks;

        this.startX = knight.getX();
        this.startY = knight.getY();

        this.screen = screen;
        this.transitionZones = transitionZones;

        this.data = data;
        this.boss = boss;
    }

    private boolean jumpPressed = false;

    private float respawnTimer = 0f;
    private static final float RESPAWN_DELAY = 0.4f;
    private boolean pendingRespawn = false;


    public void update(float delta) {
        if (knight.isDead()) {
            knight.update(delta);
            resolveGroundCollision();

            if (knight.isDeathAnimationFinished()) {
                handleDeath();
            }
            return;
        }

        if (pendingRespawn) {
            respawnTimer += delta;
            if (respawnTimer >= RESPAWN_DELAY) {
                pendingRespawn = false;
            }
            knight.updateAnimations(delta);
            return;
        }

        handleInput(delta);
        knight.update(delta);
        resolveGroundCollision();
        resolveSpikeCollision();
        updateEnemies(delta);

        checkAndLockArena();

        if (boss != null) {
            if (boss.currentState == FalseKnight.state.DEATH) {
                if (!data.falseKnightDefeated) {
                    data.falseKnightDefeated = true;
                    data.falseKnightDeathX = boss.position.x;
                    data.falseKnightDeathY = boss.position.y;
                    SaveManager.save(data);
                }

                if (screen.bossFightActive) {
                    screen.bossFightActive = false;
                    if (leftDoor != null) groundRects.removeValue(leftDoor, true);
                    if (rightDoor != null) groundRects.removeValue(rightDoor, true);
                }
            } else {
                boss.update(delta, new Vector2(knight.getX(), knight.getY()));
                resolveBossCollision();

                if (knight.getHitbox().overlaps(boss.hitbox) && !knight.isInvincible()) {
                    boolean hitFromRight = knight.getX() < boss.position.x;
                    knight.takeDamage(1, hitFromRight);
                }

                for (int i = boss.activeShockwaves.size - 1; i >= 0; i--) {
                    Shockwave wave = boss.activeShockwaves.get(i);

                    wave.update(delta);
                    if (wave.isDestroyed) {
                        boss.activeShockwaves.removeIndex(i);
                        continue;
                    }

                    if (knight.getHitbox().overlaps(wave.hitbox) && !knight.isInvincible()) {
                        boolean hitFromRight = knight.getX() < wave.position.x;
                        knight.takeDamage(2, hitFromRight);
                    }
                }
            }
        }
        checkMapTransitions();
    }

    private void checkMapTransitions() {
        Rectangle knightBox = knight.getHitbox();
        if (knightBox.overlaps(transitionZones.bounds)) {
            knight.stopMovingHorizontally();
            screen.startTransition(transitionZones.targetMap);
        }
    }

    private void updateEnemies(float delta) {
        for (int i = tiktiks.size - 1; i >= 0; i--) {
            Tiktik enemy = tiktiks.get(i);

            enemy.update(delta);
            resolveEnemyCollisions(enemy);

            if (enemy.state == Tiktik.EnemyState.WALKING) {
                if (knight.getHitbox().overlaps(enemy.hitbox) && !knight.isInvincible()) {
                    boolean hitFromRight = knight.getX() < enemy.position.x;
                    knight.takeDamage(1, hitFromRight);
                }
            }
        }
    }

    private void resolveEnemyCollisions(Tiktik enemy) {
        boolean wasOnGround = false;

        for (SolidBlock ground : groundRects) {
            if (!enemy.hitbox.overlaps(ground.bounds)) continue;

            float overlapLeft   = (enemy.hitbox.x + enemy.hitbox.width) - ground.bounds.x;
            float overlapRight  = (ground.bounds.x + ground.bounds.width) - enemy.hitbox.x;
            float overlapBottom = (enemy.hitbox.y + enemy.hitbox.height) - ground.bounds.y;
            float overlapTop    = (ground.bounds.y + ground.bounds.height) - enemy.hitbox.y;

            float minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapBottom, overlapTop));

            if (minOverlap == overlapTop) {
                enemy.position.y = ground.bounds.y + ground.bounds.height;
                enemy.velocity.y = 0;
                wasOnGround = true;
            } else if (minOverlap == overlapLeft || minOverlap == overlapRight) {
                if (enemy.state == Tiktik.EnemyState.WALKING) {
                    enemy.turnAround();
                }

                if (minOverlap == overlapLeft) {
                    enemy.position.x = ground.bounds.x - enemy.hitbox.width;
                } else {
                    enemy.position.x = ground.bounds.x + ground.bounds.width;
                }
            }
            enemy.hitbox.setPosition(enemy.position.x, enemy.position.y);
        }
    }

    public Array<Tiktik> getTiktiks() {
        return tiktiks;
    }

    private void handleInput(float delta) {
        if (pendingRespawn)
            return;

        boolean isMovingLeft = Gdx.input.isKeyPressed(keyLeft);
        boolean isMovingRight = Gdx.input.isKeyPressed(keyRight);

        if (isMovingLeft && !isMovingRight) {
            knight.movingHorizontally(-1f);
        } else if (isMovingRight && !isMovingLeft) {
            knight.movingHorizontally(1f);
        } else {
            knight.stopMovingHorizontally();
        }

        if (Gdx.input.isKeyJustPressed(keyJump)) {
            knight.jumping();
        }

        if (!Gdx.input.isKeyPressed(keyJump) && knight.getVelocity().y > 0) {
            knight.littleJumping();
        }

        if (Gdx.input.isKeyJustPressed(keyAttack)) {
            int dir = 0;
            if (Gdx.input.isKeyPressed(keyUp)) dir = 1;
            else if (Gdx.input.isKeyPressed(keyDown) && !knight.isOnGround()) dir = -1;
            knight.attacking(dir);
            performAttack(dir);
        }

        if (Gdx.input.isKeyPressed(keyDash)) {
            knight.dashing();
        }

        if (Gdx.input.isKeyPressed(keyFocus)) {
            knight.startFocusing();
        } else {
            knight.stopFocusing();
        }

        if (Gdx.input.isKeyPressed(keyUp) && knight.isOnGround() && knight.getVelocity().x == 0) {
            knight.setLookDirection(1);
        } else if (Gdx.input.isKeyPressed(keyDown) && knight.isOnGround() && knight.getVelocity().x == 0) {
            knight.setLookDirection(-1);
        } else {
            knight.setLookDirection(0);
        }

        //TODO : looking up or down
    }

    private void resolveGroundCollision() {
        Rectangle knightBox = knight.getHitbox();
        boolean wasOnGround = false;
        boolean touchingWall = false;

        float delta = Gdx.graphics.getDeltaTime();
        float prevY = knight.getY() - (knight.getVelocity().y * delta);

        for (SolidBlock ground : groundRects) {
            if (ground.isDeadly) continue;
            if (!knightBox.overlaps(ground.bounds)) continue;

            float overlapLeft   = (knightBox.x + knightBox.width) - ground.bounds.x;
            float overlapRight  = (ground.bounds.x + ground.bounds.width) - knightBox.x;
            float overlapBottom = (knightBox.y + knightBox.height) - ground.bounds.y;
            float overlapTop    = (ground.bounds.y + ground.bounds.height) - knightBox.y;

            if (prevY < (ground.bounds.y + ground.bounds.height) - 0.2f) {
                overlapTop = Float.MAX_VALUE;
            }

            if (prevY + knightBox.height > ground.bounds.y + 0.2f) {
                overlapBottom = Float.MAX_VALUE;
            }

            float minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapBottom, overlapTop));

            if (minOverlap == Float.MAX_VALUE) continue;

            if (minOverlap == overlapTop) {
                knight.landing(ground.bounds.y + ground.bounds.height);
                wasOnGround = true;
            } else if (minOverlap == overlapBottom) {
                knight.hitCeiling(ground.bounds.y);
            } else if (minOverlap == overlapLeft) {
                knight.hitWall(ground.bounds.x, 1);
                touchingWall = true;
            } else if (minOverlap == overlapRight) {
                knight.hitWall(ground.bounds.x + ground.bounds.width, -1);
                touchingWall = true;
            }
            knightBox.setPosition(knight.getX(), knight.getY());
        }

        if (!wasOnGround) {
            knight.setAirborne();
        }

        if (!touchingWall) {
            knight.resetWallTouch();
        }
    }

    private void performAttack(int dir) {
        Rectangle attackBox = new Rectangle();
        float kx = knight.getX();
        float ky = knight.getY();
        float kw = knight.getWidth();
        float kh = knight.getHeight();

        float range = 1.8f;

        if (dir > 0) {
            attackBox.set(kx - 0.5f, ky + kh, kw + 1f, range);
        } else if (dir < 0) {
            attackBox.set(kx - 0.5f, ky - range, kw + 1f, range);
        } else {
            if (knight.isFacingRight()) {
                attackBox.set(kx + kw, ky, range, kh);
            } else {
                attackBox.set(kx - range, ky, range, kh);
            }
        }

        boolean hitSomething = false;

        for (Tiktik enemy : tiktiks) {
            if (enemy.state == Tiktik.EnemyState.WALKING) {
                if (attackBox.overlaps(enemy.hitbox)) {
                    boolean hitFromRight = knight.getX() > enemy.position.x;
                    enemy.takeDamage(1, hitFromRight);
                    knight.gainSoul(11);
                    hitSomething = true;
                }
            }
        }

        if (dir < 0) {
            for (SolidBlock spike : spikeRects) {
                if (spike.isDeadly) {
                    if (attackBox.overlaps(spike.bounds)) {
                        hitSomething = true;
                        break;
                    }
                }
            }
        }

        if (hitSomething) {
            if (dir < 0) {
                knight.setVelocityY(15f);
                knight.setOnGround(false);
                knight.resetWallTouch();
            } else if (dir == 0) {
                float recoil = knight.isFacingRight() ? -4f : 4f;
                knight.getVelocity().x = recoil;
            }
        }

        if (boss != null && boss.currentState != FalseKnight.state.DEATH) {
            Rectangle targetBox = (boss.currentState == FalseKnight.state.STUNNED) ? boss.vulnerabilityBox : boss.hitbox;

            if (attackBox.overlaps(targetBox)) {
                boss.takeDamage(10);
                knight.gainSoul(11);
                hitSomething = true;

                if (dir < 0) {
                    knight.setVelocityY(15f);
                    knight.setOnGround(false);
                    knight.resetWallTouch();
                } else if (dir == 0) {
                    float recoil = knight.isFacingRight() ? -4f : 4f;
                    knight.getVelocity().x = recoil;
                }
            }
        }
    }


    private void resolveSpikeCollision() {
        if (knight.isInvincible() || knight.isDead() || pendingRespawn) {
            return;
        }

        Rectangle knightBox = knight.hitbox;

        for (SolidBlock spike : spikeRects) {
            if (spike.isDeadly) {
                if (knightBox.overlaps(spike.bounds)) {
                    knight.hitSpike();
                    knight.reSpawn();

                    pendingRespawn = true;
                    respawnTimer = 0f;
                    break;
                }
            }
        }
    }

    private void resolveBossCollision() {
        if (boss == null || boss.currentState == FalseKnight.state.DEATH) return;

        Rectangle bossBox = boss.hitbox;
        boolean wasOnGround = false;

        float delta = Gdx.graphics.getDeltaTime();
        float prevY = boss.position.y - (boss.velocity.y * delta);

        for (SolidBlock ground : groundRects) {
            if (ground.isDeadly) continue;
            if (!bossBox.overlaps(ground.bounds)) continue;

            float overlapLeft   = (bossBox.x + bossBox.width) - ground.bounds.x;
            float overlapRight  = (ground.bounds.x + ground.bounds.width) - bossBox.x;
            float overlapBottom = (bossBox.y + bossBox.height) - ground.bounds.y;
            float overlapTop    = (ground.bounds.y + ground.bounds.height) - bossBox.y;

            if (prevY < (ground.bounds.y + ground.bounds.height) - 0.2f) {
                overlapTop = Float.MAX_VALUE;
            }

            if (prevY + bossBox.height > ground.bounds.y + 0.2f) {
                overlapBottom = Float.MAX_VALUE;
            }

            float minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapBottom, overlapTop));

            if (minOverlap == Float.MAX_VALUE) continue;

            if (minOverlap == overlapTop) {
                boss.landing(ground.bounds.y + ground.bounds.height);
                wasOnGround = true;
            } else if (minOverlap == overlapBottom) {
                boss.position.y = ground.bounds.y - bossBox.height;
                boss.velocity.y = 0;
            } else if (minOverlap == overlapLeft) {
                boss.hitWall(ground.bounds.x, 1);
            } else if (minOverlap == overlapRight) {
                boss.hitWall(ground.bounds.x + ground.bounds.width, -1);
            }

            bossBox.setPosition(boss.position.x, boss.position.y);
        }

        if (!wasOnGround) {
            boss.setAirborne();
        }
    }




    private void handleDeath() {
        Vector2 initialPos = screen.findSpawnPoint();
        knight.fullRespawn(initialPos.x, initialPos.y);

        resetBossFight();
    }


    private void checkAndLockArena() {
        if (boss == null || boss.bossFightStarted || boss.currentState == FalseKnight.state.DEATH) return;

        float minX = screen.arenaMinX;
        float maxX = screen.arenaMaxX;
        float y = screen.arenaY;
        float height = screen.arenaHeight;

        if (knight.getX() < minX || knight.getX() > maxX) {
            wasOutsideArena = true;
        }

        if (wasOutsideArena && knight.getX() > minX + 2f && knight.getX() < maxX - 2f) {
            boss.bossFightStarted = true;
            screen.bossFightActive = true;

            leftDoor = new SolidBlock();
            leftDoor.bounds = new Rectangle(minX, y, 1f, height);
            leftDoor.isDeadly = false;

            rightDoor = new SolidBlock();
            rightDoor.bounds = new Rectangle(maxX - 1f, y, 1f, height);
            rightDoor.isDeadly = false;

            groundRects.add(leftDoor);
            groundRects.add(rightDoor);
        }
    }

    private void resetBossFight() {
        if (leftDoor != null) groundRects.removeValue(leftDoor, true);
        if (rightDoor != null) groundRects.removeValue(rightDoor, true);

        screen.bossFightActive = false;
        wasOutsideArena = false;

        if (boss != null && boss.currentState != FalseKnight.state.DEATH) {
            boss.bossFightStarted = false;
            boss.currentHp = boss.maxHp;
            boss.isPhaseTwo = false;
            boss.isGrounded = false;
            boss.velocity.setZero();

            if (boss.activeShockwaves != null) {
                boss.activeShockwaves.clear();
            }

            Vector2 bossSpawn = screen.findBossSpawnPoint();
            boss.position.set(bossSpawn.x, bossSpawn.y);
            boss.changeBehavior(new IdleBehavior());
        }
    }
}
