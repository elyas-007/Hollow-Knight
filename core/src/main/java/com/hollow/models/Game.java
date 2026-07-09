package com.hollow.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.hollow.HollowKnight;
import com.hollow.assets.TiledMapHelper;
import com.hollow.models.entities.Enemy.*;
import com.hollow.models.entities.FalseKnightBoss.FalseKnight;
import com.hollow.models.entities.FalseKnightBoss.IdleBehavior;
import com.hollow.models.entities.FalseKnightBoss.Shockwave;
import com.hollow.models.entities.Knight.Charm;
import com.hollow.models.entities.Knight.Knight;
import com.hollow.models.entities.Knight.Projectile;
import com.hollow.models.entities.Knight.WraithEffect;
import com.hollow.models.entities.zote.Zote;
import com.hollow.models.enums.KnightState;
import com.hollow.views.screens.GameScreen;

public class Game {
    private HollowKnight game;
    private final Knight knight;
    public FalseKnight boss;
    private final Array<SolidBlock> groundRects;
    private final Array<SolidBlock> spikeRects;
    private final Array<BreakableWall> breakableWalls;
    public Array<Debris> activeDebris = new Array<>();
    private GameData data;

    private boolean wasOutsideArena = false;

    public Vector2 voidHeartPos;
    public float voidHeartStateTime = 0f;

    private Array<Enemy> enemies;

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

    public Array<Projectile> activeProjectiles = new Array<>();
    public Array<Effect> activeEffects = new Array<>();
    public Array<WraithEffect> activeWraiths = new Array<>();
    public Array<InstantLaser> activeInstantLasers = new Array<>();
    public boolean hasShadowCharm;

    public boolean instaKillMode = false;

    public boolean inCombat = false;
    private final float COMBAT_RADIUS = 15f;

    public Game(HollowKnight game, Knight knight, Array<SolidBlock> groundRects,
                Array<SolidBlock> spikeRects, Array<Enemy> enemies,
                TransitionZone transitionZones, GameScreen screen,
                GameData data, FalseKnight boss, Array<BreakableWall> breakableWalls) {
        this.game = game;
        this.knight = knight;
        this.groundRects = groundRects;
        this.spikeRects = spikeRects;
        this.breakableWalls = breakableWalls;
        keyLeft = game.data.getSettings().getKeyLeft();
        keyRight = game.data.getSettings().getKeyRight();
        keyUp = game.data.getSettings().getKeyUp();
        keyDown = game.data.getSettings().getKeyDown();
        keyAttack = game.data.getSettings().getKeyAttack();
        keyDash = game.data.getSettings().getKeyDash();
        keyJump = game.data.getSettings().getKeyJump();
        keyFocus = game.data.getSettings().getKeyFocus();

        this.enemies = enemies;

        this.startX = knight.getX();
        this.startY = knight.getY();

        this.screen = screen;
        this.transitionZones = transitionZones;

        this.data = data;
        this.boss = boss;

        this.hasShadowCharm = data.getActiveSlot().getEquippedCharms().contains( // void heart
            Charm.VOID_HEART, true);
    }

    private boolean jumpPressed = false;

    private float respawnTimer = 0f;
    private static final float RESPAWN_DELAY = 0.4f;
    private boolean pendingRespawn = false;


    public void update(float delta) {
        updateCombatState();
        if (data != null && !knight.isDead() && !pendingRespawn && !screen.isPaused) {
            float time = data.getActiveSlot().getPlayTime();
            data.getActiveSlot().setPlayTime(time + delta);
        }

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
        if (!knight.noclipMode) {
            resolveGroundCollision();
            resolveSpikeCollision();
        }
        updateEnemies(delta);

        checkAndLockArena();
        if (knight.castProjectile) {
            knight.castProjectile = false;

            GameScreen.triggerShake(0.12f, 0.5f);

            hasShadowCharm = data.getActiveSlot().getEquippedCharms().contains(Charm.VOID_HEART, true);

            game.audioManager.playSound(game.audioManager.audioLoader.knight_fireball);

            float spawnX = knight.isFacingRight() ? (knight.getX() + knight.getWidth()) : (knight.getX() - 1.5f);
            float spawnY = knight.getY() + 0.2f;
            activeProjectiles.add(new Projectile(spawnX, spawnY, knight.isFacingRight(), hasShadowCharm));

            float blastWidth = 5.0f;
            float blastHeight = 5.0f;

            float offset = 1f;

            float effectX = knight.isFacingRight() ?
                knight.getX() + (knight.getWidth() / 2f) - offset :
                knight.getX() - blastWidth + (knight.getWidth() / 2f) + offset;

            float effectY = knight.getY() - (blastHeight / 2f) + (knight.getHeight() / 2f);

            activeEffects.add(new Effect(
                knight.blast,
                effectX,
                effectY,
                blastWidth,
                blastHeight,
                knight.isFacingRight()
            ));

            if (hasShadowCharm) {
                AchievementManager.getInstance().unlockAchievement(Achievement.SHADOW_MASTER);
            }
        }

        if (knight.castWraiths) {
            knight.castWraiths = false;
            GameScreen.triggerShake(0.3f, 0.4f);

            hasShadowCharm = data.getActiveSlot().getEquippedCharms().contains(Charm.VOID_HEART, true);
            game.audioManager.playSound(game.audioManager.audioLoader.knight_fireball);

            float effectWidth = 7.0f;
            float effectHeight = 8.0f;
            float effectX = knight.getX() + (knight.getWidth() / 2f) - (effectWidth / 2f);
            float effectY = knight.getY() + knight.getHeight() - 0.5f;

            activeWraiths.add(new WraithEffect(effectX, effectY, effectWidth, effectHeight, hasShadowCharm, 0.7f));
        }

        for (int i = activeWraiths.size - 1; i >= 0; i--) {
            WraithEffect w = activeWraiths.get(i);
            w.stateTime += delta;

            int expectedTicks = (int) (w.stateTime / w.tickInterval);
            if (expectedTicks > w.ticksDone && w.ticksDone < 3) {
                w.ticksDone++;

                for (Enemy enemy : enemies) {
                    if (enemy.state != Enemy.EnemyState.CORPSE && w.hitbox.overlaps(enemy.hitbox)) {
                        int damage = w.isShadow ? 15 : 8;
                        if (instaKillMode) damage = 9999;
                        enemy.takeDamage(damage, enemy.position.x > w.x + w.width / 2f);
                        game.audioManager.playSound(game.audioManager.audioLoader.enemy_hit);
                        checkEnemyKill(enemy);
                    }
                }

                if (boss != null && boss.currentState != FalseKnight.state.DEATH) {
                    Rectangle targetBox = (boss.currentState == FalseKnight.state.STUNNED) ? boss.vulnerabilityBox : boss.hitbox;
                    if (w.hitbox.overlaps(targetBox)) {
                        int damage = w.isShadow ? 15 : 8;
                        if (instaKillMode) damage = 9999;
                        boss.takeDamage(damage);
                        game.audioManager.playSound(game.audioManager.audioLoader.fk_armourHit);
                    }
                }
            }

            if (w.stateTime >= w.maxTime) {
                activeWraiths.removeIndex(i);
            }
        }

        for (int i = activeProjectiles.size - 1; i >= 0; i--) {
            Projectile p = activeProjectiles.get(i);
            p.update(delta);

            for (SolidBlock ground : groundRects) {
                if (!ground.isDeadly && p.hitbox.overlaps(ground.bounds)) {
                    p.isDestroyed = true;
                    break;
                }
            }

            if (!p.isDestroyed) {
                for (Enemy enemy : enemies) {
                    if (enemy.state != Enemy.EnemyState.CORPSE && p.hitbox.overlaps(enemy.hitbox)) {
                        int spellDamage = p.isShadow ? 2 : 1; // void hurt
                        if (hasShadowCharm) spellDamage = (int)(spellDamage * 1.5f);
                        if (instaKillMode) spellDamage = 9999;
                        enemy.takeDamage(spellDamage, p.isFacingRight);
                        game.audioManager.playSound(game.audioManager.audioLoader.enemy_hit);
                        checkEnemyKill(enemy);
                        break;
                    }
                }
            }

            if (!p.isDestroyed && boss != null && boss.currentState != FalseKnight.state.DEATH) {
                Rectangle targetBox = (boss.currentState == FalseKnight.state.STUNNED) ? boss.vulnerabilityBox : boss.hitbox;
                if (p.hitbox.overlaps(targetBox)) {
                    int damage = p.isShadow ? 15 : 10;
                    if (instaKillMode) damage = 9999;

                    if (boss.currentState == FalseKnight.state.STUNNED) {
                        game.audioManager.playSound(game.audioManager.audioLoader.fk_headHit);
                    } else {
                        game.audioManager.playSound(game.audioManager.audioLoader.fk_armourHit);
                    }

                    boss.takeDamage(damage);
                }
            }

            for (BreakableWall w : breakableWalls) {
                if (!p.isDestroyed && !w.isDestroyed()) {
                    if (p.hitbox.overlaps(w.bounds)) {
                        w.takeDamage(1);
                        GameScreen.triggerShake(0.1f, 0.05f);

                        int debrisCount = 10;
                        for (int j = 0; j < debrisCount; j++) {
                            float spawnX = w.bounds.x + (w.bounds.width / 2f);
                            float spawnY = w.bounds.y + (w.bounds.height / 2f);

                            activeDebris.add(new Debris(game.assetLoader.rockTexture, spawnX, spawnY));
                        }
                        if (w.getHp() <= 0) {
                            game.audioManager.playWallDeathSound();
                            w.setDestroyed(true);
                            groundRects.removeValue(w, true);
                            breakableWalls.removeValue(w, true);

                            screen.removeWallTiles(w.bounds);
                        } else {
                            game.audioManager.playWallHitSound();
                        }
                        p.isDestroyed = true;
                    }
                }
            }


            if (p.isDestroyed) {
                activeProjectiles.removeIndex(i);
            }
        }

        for (int i = activeInstantLasers.size - 1; i >= 0; i--) {
            InstantLaser laser = activeInstantLasers.get(i);
            laser.update(delta);

            if (laser.hitbox.overlaps(knight.getHitbox()) && !knight.isInvincible()) {
                boolean hitFromRight = knight.getX() < (laser.isFacingRight ? laser.hitbox.x : laser.hitbox.x + laser.hitbox.width);
                knight.takeDamage(1, hitFromRight);
            }

            if (laser.isFinished()) {
                activeInstantLasers.removeIndex(i);
            }
        }

        if (boss != null) {
            if (boss.currentState == FalseKnight.state.DEATH) {
                if (!data.getActiveSlot().isFalseKnightDefeated()) {
                    data.getActiveSlot().setFalseKnightDefeated(true);
                    data.getActiveSlot().setFalseKnightDeathX(boss.position.x);
                    data.getActiveSlot().setFalseKnightDeathY(boss.position.y);
                    AchievementManager.getInstance().unlockAchievement(Achievement.DEFEAT_FALSE_KNIGHT);
                    SaveManager.save(data);

                    if (!screen.isEndingSequence) {
                        screen.startEndingSequence();
                    }
                }

                if (screen.bossFightActive) {
                    screen.bossFightActive = false;
                    screen.stopBossMusic();
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

        for (int i = activeDebris.size - 1; i >= 0; i--) {
            Debris d = activeDebris.get(i);
            d.update(delta);
            if (d.isDead()) {
                activeDebris.removeIndex(i);
            }
        }

        if (!data.getActiveSlot().getUnlockedCharms().contains(Charm.VOID_HEART, true) && voidHeartPos != null) {
            voidHeartStateTime += delta;
            Rectangle charmHitbox = new Rectangle(voidHeartPos.x, voidHeartPos.y, 1f, 1f);

            float distance = knight.getPosition().dst(voidHeartPos);

            if (distance < 6f && !screen.isPaused) {
                game.audioManager.playCharmProximityLoop();
            } else {
                game.audioManager.stopCharmProximityLoop();
            }

            if (knight.getHitbox().overlaps(charmHitbox)) {
                screen.dialogueBox.setPromptVisible(true);

                if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                    data.getActiveSlot().getUnlockedCharms().add(Charm.VOID_HEART);
                    SaveManager.save(data);
                    screen.hud.showItemPopup("Void Heart Unlocked!");

                    game.audioManager.stopCharmProximityLoop();
                    game.audioManager.playSound(game.audioManager.audioLoader.Knight_pickUpSpellFinal);

                    if (screen.inventoryUI != null) {
                        screen.inventoryUI.refreshUnlockedCharms();
                    }
                }
            }
        } else {
            game.audioManager.stopCharmProximityLoop();
        }

        if (screen.zote != null) {
            if (screen.zote.currentState == Zote.State.ANGRY) {
                game.audioManager.playZoteAttackLoop();
                screen.zote.angryTimer -= delta;

                if (screen.zote.angryTimer <= 0) {
                    screen.zote.changeState(Zote.State.IDLE);
                } else {
                    float zoteSpeed = 4f;

                    if (knight.getX() > screen.zote.position.x) {
                        screen.zote.position.x += zoteSpeed * delta;
                        screen.zote.isFacingRight = true;
                    } else {
                        screen.zote.position.x -= zoteSpeed * delta;
                        screen.zote.isFacingRight = false;
                    }

                    if (knight.getHitbox().overlaps(screen.zote.hitbox) && !knight.isInvincible()) {
                        boolean hitFromRight = knight.getX() < screen.zote.position.x;
                        knight.takeDamage(0, hitFromRight);
                    }
                }
            } else {
                game.audioManager.stopZoteAttackLoop();
            }
        }

        checkMapTransitions();

        if (screen.speedrunRect != null && knight.getHitbox().overlaps(screen.speedrunRect)) {
            if (data.getActiveSlot().getPlayTime() <= 300f) {
                AchievementManager.getInstance().unlockAchievement(Achievement.SPEEDRUN);
            }
        }
    }

    private void checkMapTransitions() {
        Rectangle knightBox = knight.getHitbox();
        if (knightBox.overlaps(transitionZones.bounds)) {
            knight.stopMovingHorizontally();
            screen.startTransition(transitionZones.targetMap);
        }
    }

    private void updateEnemies(float delta) {
        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            enemy.targetX = knight.getX();
            enemy.targetY = knight.getY();

            enemy.update(delta);
            resolveEnemyCollisions(enemy);

            if (enemy instanceof Crystallized crystalEnemy) {
                if (crystalEnemy.state == Enemy.EnemyState.ATTACK_ANTICIPATE && !crystalEnemy.laserFired) {
                    if (crystalEnemy.stateTime > 0.3f) {
                        crystalEnemy.laserFired = true;

                        float startX = crystalEnemy.isFacingRight ? crystalEnemy.position.x + crystalEnemy.hitbox.width : crystalEnemy.position.x;
                        float startY = crystalEnemy.position.y + 0.6f;
                        float laserHeight = 1.0f;
                        float maxDistance = 40f;
                        float actualWidth = maxDistance;

                        for (SolidBlock ground : groundRects) {
                            if (!ground.isDeadly) {
                                boolean yOverlap = (ground.bounds.y < startY + laserHeight) && (ground.bounds.y + ground.bounds.height > startY);

                                if (yOverlap) {
                                    if (crystalEnemy.isFacingRight && ground.bounds.x > startX) {
                                        float dist = ground.bounds.x - startX;
                                        if (dist < actualWidth) actualWidth = dist;
                                    } else if (!crystalEnemy.isFacingRight && ground.bounds.x + ground.bounds.width < startX) {
                                        float dist = startX - (ground.bounds.x + ground.bounds.width);
                                        if (dist < actualWidth) actualWidth = dist;
                                    }
                                }
                            }
                        }
                        float finalX = crystalEnemy.isFacingRight ? startX : startX - actualWidth;
                        activeInstantLasers.add(new InstantLaser(finalX, startY, actualWidth, laserHeight, crystalEnemy.isFacingRight));
                    }
                }
            }

            if (enemy.state != Enemy.EnemyState.CORPSE && enemy.state != Enemy.EnemyState.DYING_AIR && enemy.state != Enemy.EnemyState.DYING_LAND) {
                if (knight.getHitbox().overlaps(enemy.hitbox) && !knight.isInvincible() && !knight.noclipMode) {

                    if (knight.isDashing() && data.getActiveSlot().getEquippedCharms().contains(Charm.SHARP_SHADOW, true)) {
                        boolean hitFromRight = knight.getX() > enemy.position.x;
                        enemy.takeDamage(1, hitFromRight);
                        game.audioManager.playSound(game.audioManager.audioLoader.enemy_hit);
                        checkEnemyKill(enemy);
                        int soulAmount = data.getActiveSlot().getEquippedCharms().contains(Charm.SOUL_CATCHER, true) ? 22 : 11;
                        knight.gainSoul(soulAmount);
                    } else {
                        boolean hitFromRight = knight.getX() < enemy.position.x;
                        knight.takeDamage(1, hitFromRight);
                    }
                }
            }
        }
    }

    private void resolveEnemyCollisions(Enemy enemy) {
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

                if (enemy.state == Enemy.EnemyState.DYING_AIR) {
                    enemy.state = Enemy.EnemyState.DYING_LAND;
                    enemy.stateTime = 0f;
                    enemy.velocity.x = 0f;
                }

                wasOnGround = true;
            } else if (minOverlap == overlapLeft || minOverlap == overlapRight) {
                enemy.turnAround();

                if (minOverlap == overlapLeft) {
                    enemy.position.x = ground.bounds.x - enemy.hitbox.width;
                } else {
                    enemy.position.x = ground.bounds.x + ground.bounds.width;
                }
            }
            enemy.hitbox.setPosition(enemy.position.x, enemy.position.y);
        }
    }

    public Array<Enemy> getEnemies() {
        return enemies;
    }

    private void handleInput(float delta) {
        if (pendingRespawn)
            return;

        keyLeft = game.data.getSettings().getKeyLeft();
        keyRight = game.data.getSettings().getKeyRight();
        keyUp = game.data.getSettings().getKeyUp();
        keyDown = game.data.getSettings().getKeyDown();
        keyAttack = game.data.getSettings().getKeyAttack();
        keyDash = game.data.getSettings().getKeyDash();
        keyJump = game.data.getSettings().getKeyJump();
        keyFocus = game.data.getSettings().getKeyFocus();

        boolean ctrlPressed = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT);

        if (ctrlPressed) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
                knight.getPosition().set(screen.arenaMaxX + 2f, screen.arenaY + 2f);
                knight.getVelocity().setZero();
                screen.hud.showCheatPopup("Boss Teleport", true);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.F2)) {
                knight.noclipMode = !knight.noclipMode;
                screen.hud.showCheatPopup("Noclip", knight.noclipMode);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
                knight.emergencyHealCheat();
                screen.hud.showCheatPopup("Emergency Heal", true);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.F4)) {
                knight.refillSoulCheat();
                screen.hud.showCheatPopup("Refill Soul", true);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
                knight.godMode = !knight.godMode;
                screen.hud.showCheatPopup("God Mode", knight.godMode);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.F6)) {
                instaKillMode = !instaKillMode;
                screen.hud.showCheatPopup("Insta-Kill", instaKillMode);
            }
        }

        if (knight.noclipMode) {
            float noclipSpeed = 25f;
            knight.getVelocity().setZero();
            if (Gdx.input.isKeyPressed(keyLeft)) knight.getVelocity().x = -noclipSpeed;
            if (Gdx.input.isKeyPressed(keyRight)) knight.getVelocity().x = noclipSpeed;
            if (Gdx.input.isKeyPressed(keyUp)) knight.getVelocity().y = noclipSpeed;
            if (Gdx.input.isKeyPressed(keyDown)) knight.getVelocity().y = -noclipSpeed;
            return;
        }

        if (screen.isEndingSequence) {
            boolean isMovingLeft = Gdx.input.isKeyPressed(keyLeft);
            boolean isMovingRight = Gdx.input.isKeyPressed(keyRight);

            if (isMovingLeft && !isMovingRight) {
                knight.movingHorizontally(-1f);
            } else if (isMovingRight && !isMovingLeft) {
                knight.movingHorizontally(1f);
            } else {
                knight.stopMovingHorizontally();
            }

            if (screen.endingStatueRect != null && knight.getX() > (screen.endingStatueRect.x - 20f)) {
                AchievementManager.getInstance().unlockAchievement(Achievement.COMPLETION);
            }

            if (Gdx.input.isKeyJustPressed(keyJump)) {
                knight.jumping();
            }

            if (!Gdx.input.isKeyPressed(keyJump) && knight.getVelocity().y > 0) {
                knight.littleJumping();
            }

            if (screen.endingStatueRect != null && knight.getHitbox().overlaps(screen.endingStatueRect)) {
                if (!screen.endingUI.isVisible) {
                    screen.dialogueBox.setPromptVisible(true);
                }

                if (Gdx.input.isKeyJustPressed(Input.Keys.E) && knight.isOnGround() && !screen.endingUI.isVisible) {
                    knight.stopMovingHorizontally();
                    screen.dialogueBox.setPromptVisible(false);

                    screen.endingUI.showUI(screen.multiplexer);
                }
            } else {
                if (screen.zote == null || !knight.getHitbox().overlaps(screen.zote.interactionBox)) {
                    screen.dialogueBox.setPromptVisible(false);
                }
            }
            return;
        }

        if (screen.zote != null) {
            boolean inRange = knight.getHitbox().overlaps(screen.zote.interactionBox);
            boolean isInteractiveState = (screen.zote.currentState == Zote.State.IDLE || screen.zote.currentState == Zote.State.SLEEPING);
            boolean canShowPrompt = inRange && isInteractiveState && !screen.dialogueBox.isVisible && !screen.zote.pendingDialogue;

            screen.dialogueBox.setPromptVisible(canShowPrompt);

            if (screen.zote.pendingDialogue && screen.zote.currentState == Zote.State.IDLE) {
                screen.zote.pendingDialogue = false;
                screen.dialogueBox.startDialogue(screen.zote.getDialogue());
//                game.audioManager.playZoteDialogue();
            }

            if (screen.zote.currentState == Zote.State.GETTING_UP) {
                knight.stopMovingHorizontally();
//                knight.state = KnightState.IDLE;
                return;
            }

            if (screen.dialogueBox.isVisible) {
                knight.stopMovingHorizontally();
//                knight.state = KnightState.IDLE;
                screen.zote.currentState = Zote.State.TALKING;
                return;
            } else if (screen.zote.currentState == Zote.State.TALKING) {
                screen.zote.currentState = Zote.State.IDLE;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.E) && knight.isOnGround()) {
                if (knight.getHitbox().overlaps(screen.zote.interactionBox)) {

                    screen.zote.isFacingRight = knight.getX() > screen.zote.position.x;

                    if (screen.zote.currentState == Zote.State.SLEEPING) {
                        screen.zote.changeState(Zote.State.GETTING_UP);
                        screen.zote.pendingDialogue = true;
                        game.audioManager.playSound(game.audioManager.audioLoader.zoteGetUp);
                    } else if (screen.zote.currentState == Zote.State.IDLE) {
                        screen.dialogueBox.startDialogue(screen.zote.getDialogue());
//                        game.audioManager.playZoteDialogue();
                    }

                    knight.stopMovingHorizontally();
                    knight.setLookDirection(0);
                    return;
                }
            }
        }

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

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            if (knight.getState() != KnightState.CASTING && knight.getState() != KnightState.UP_CASTING && !knight.isBusy() && knight.getSoul() >= 33) {
                knight.consumeSoul(33);

                if (Gdx.input.isKeyPressed(keyUp)) {
                    knight.startUpCasting();
                } else {
                    knight.startCasting();
                }
            }
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
        int nailDamage = data.getActiveSlot().getEquippedCharms().contains(Charm.UNBREAKABLE_STRENGTH, true) ? 2 : 1; // unbreakable_strength
        if (instaKillMode) nailDamage = 9999;
        int soulAmount = data.getActiveSlot().getEquippedCharms().contains(Charm.SOUL_CATCHER, true) ? 22 : 11; // soul catcher
        boolean hasHeavyBlow = data.getActiveSlot().getEquippedCharms().contains(Charm.HEAVY_BLOW, true); // heavy blow

        for (Enemy enemy : enemies) {
            if (enemy.state != Enemy.EnemyState.CORPSE && enemy.state != Enemy.EnemyState.DYING_AIR && enemy.state != Enemy.EnemyState.DYING_LAND) {
                if (attackBox.overlaps(enemy.hitbox)) {
                    boolean hitFromRight = knight.getX() > enemy.position.x;
                    enemy.takeDamage(nailDamage, hitFromRight);
                    game.audioManager.playSound(game.audioManager.audioLoader.enemy_hit);
                    checkEnemyKill(enemy);

                    if (hasHeavyBlow) {
                        enemy.velocity.x = hitFromRight ? -12f : 12f;
                    }

                    knight.gainSoul(soulAmount);
                    hitSomething = true;
                }
            }
        }

        for (BreakableWall w : breakableWalls) {
            if (!w.isDestroyed()) {
                if (attackBox.overlaps(w.bounds)) {
                    w.takeDamage(1);
                    GameScreen.triggerShake(0.1f, 0.05f);
                    hitSomething = true;

                    int debrisCount = 8;
                    for (int i = 0; i < debrisCount; i++) {
                        float spawnX = w.bounds.x + (w.bounds.width / 2f);
                        float spawnY = w.bounds.y + (w.bounds.height / 2f);

                        activeDebris.add(new Debris(game.assetLoader.rockTexture, spawnX, spawnY));
                    }
                    if (w.getHp() <= 0) {
                        game.audioManager.playWallDeathSound();
                        w.setDestroyed(true);
                        groundRects.removeValue(w, true);
                        breakableWalls.removeValue(w, true);

                        screen.removeWallTiles(w.bounds);
                    } else {
                        game.audioManager.playWallHitSound();
                    }
                }
            }
        }

        if (dir < 0) {
            for (SolidBlock spike : spikeRects) {
                if (spike.isDeadly) {
                    if (attackBox.overlaps(spike.bounds)) {
                        hitSomething = true;
                        game.audioManager.playSound(game.audioManager.audioLoader.hit_metal);
                        break;
                    }
                }
            }
        }

        if (screen.zote != null) {
            if ((screen.zote.currentState == Zote.State.IDLE || screen.zote.currentState == Zote.State.TALKING)
                && attackBox.overlaps(screen.zote.hitbox)) {

                screen.zote.changeState(Zote.State.ANGRY);
                screen.zote.angryTimer = screen.zote.ANGRY_DURATION;
                hitSomething = true;

                game.audioManager.playSound(game.audioManager.audioLoader.zoteRoar);
            }
        }

        if (boss != null && boss.currentState != FalseKnight.state.DEATH) {
            Rectangle targetBox = (boss.currentState == FalseKnight.state.STUNNED) ? boss.vulnerabilityBox : boss.hitbox;

            if (attackBox.overlaps(targetBox)) {
                int nail = 10;
                if (instaKillMode)
                    nail = 9999;

                if (boss.currentState == FalseKnight.state.STUNNED) {
                    game.audioManager.playSound(game.audioManager.audioLoader.fk_headHit);
                } else {
                    game.audioManager.playSound(game.audioManager.audioLoader.fk_armourHit);
                }
                boss.takeDamage(nail);
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

        if (hitSomething) {
            if (dir < 0) {
                knight.setVelocityY(30f);
                knight.setOnGround(false);
                knight.resetWallTouch();

                knight.resetDash();
                knight.resetDoubleJump();

            } else if (dir == 0) {
                knight.getVelocity().x = knight.isFacingRight() ? -4f : 4f;
            }
        } else {
            game.audioManager.playSound(game.audioManager.audioLoader.knight_slash);
        }
    }


    private void resolveSpikeCollision() {
        if (knight.isInvincible() || knight.isDead() || pendingRespawn || knight.godMode) {
            return;
        }

        Rectangle knightBox = knight.hitbox;

        for (SolidBlock spike : spikeRects) {
            if (spike.isDeadly) {
                if (knightBox.overlaps(spike.bounds)) {
                    knight.hitSpike();
                    if (!knight.isDead())
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

        data.getActiveSlot().setDeathCount(data.getActiveSlot().getDeathCount() + 1);
        SaveManager.save(data);

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


        if (((knight.getX() > minX + 5f) && (knight.getX() < maxX - 5f)) && ((knight.getY() < y + height) && (knight.getY() > y))) {
            boss.bossFightStarted = true;
            screen.bossFightActive = true;
            screen.playBossMusic();

            leftDoor = new SolidBlock();
            leftDoor.bounds = new Rectangle(minX - 1f, y, 1f, height);
            leftDoor.isDeadly = false;

            rightDoor = new SolidBlock();
            rightDoor.bounds = new Rectangle(maxX + 0.5f, y, 1f, height);
            rightDoor.isDeadly = false;

            groundRects.add(leftDoor);
            groundRects.add(rightDoor);
        }
    }

    private void resetBossFight() {
        if (leftDoor != null) groundRects.removeValue(leftDoor, true);
        if (rightDoor != null) groundRects.removeValue(rightDoor, true);

        if (screen.bossFightActive) {
            screen.bossFightActive = false;
            screen.stopBossMusic();
        }

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

    private void checkEnemyKill(Enemy enemy) {
        if (enemy.health <= 0) {
            game.audioManager.playSound(game.audioManager.audioLoader.enemy_death);
            data.registerEnemyKill(enemy.name);
            SaveManager.save(data);
            if (data.getTotalEnemyKilled() >= 6) {
                AchievementManager.getInstance().unlockAchievement(Achievement.TRUE_HUNTER);
                SaveManager.save(data);
            }
        }
    }

    private void updateCombatState() {
        if (knight.isDead() || pendingRespawn) {
            inCombat = false;
            return;
        }

        boolean enemyNearby = false;

        for (Enemy enemy : enemies) {
            if (enemy.state != Enemy.EnemyState.CORPSE &&
                enemy.state != Enemy.EnemyState.DYING_AIR &&
                enemy.state != Enemy.EnemyState.DYING_LAND) {

                float distance = knight.getPosition().dst(enemy.position);

                if (distance <= COMBAT_RADIUS) {
                    enemyNearby = true;
                    break;
                }
            }
        }

        if (screen.zote != null && screen.zote.currentState == Zote.State.ANGRY) {
            enemyNearby = true;
        }

        inCombat = enemyNearby;
    }
}
