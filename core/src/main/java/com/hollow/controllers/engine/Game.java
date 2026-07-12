package com.hollow.controllers.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.hollow.HollowKnight;
import com.hollow.controllers.manager.AchievementManager;
import com.hollow.controllers.manager.SaveManager;
import com.hollow.models.*;
import com.hollow.models.achievement.Achievement;
import com.hollow.models.data.GameData;
import com.hollow.models.entities.boss.FalseKnight;
import com.hollow.models.entities.boss.IdleBehavior;
import com.hollow.models.entities.boss.Shockwave;
import com.hollow.models.entities.enemy.Crystallized;
import com.hollow.models.entities.knight.*;
import com.hollow.models.entities.enemy.InstantLaser;
import com.hollow.models.entities.zote.Zote;
import com.hollow.models.entities.enemy.Enemy;
import com.hollow.views.screen.GameScreen;

public class Game {
    public Array<Debris> activeDebris = new Array<>();
    public Array<Projectile> activeProjectiles = new Array<>();
    public Array<Effect> activeEffects = new Array<>();
    public Array<WraithEffect> activeWraiths = new Array<>();
    public Array<InstantLaser> activeInstantLasers = new Array<>();
    public boolean hasShadowCharm;
    public boolean instaKillMode = false;
    public boolean inCombat = false;
    public FalseKnight boss;
    public Vector2 voidHeartPos;
    public float voidHeartStateTime = 0f;

    private final HollowKnight game;
    private final Knight knight;
    private final Array<SolidBlock> groundRects;
    private final Array<SolidBlock> spikeRects;
    private final Array<BreakableWall> breakableWalls;
    private final GameData data;
    private final Array<com.hollow.models.entities.enemy.Enemy> enemies;
    private final TransitionZone transitionZones;
    private final GameScreen screen;
    private final CombatEngine combatEngine;

    private SolidBlock leftDoor;
    private SolidBlock rightDoor;
    private static final float COMBAT_RADIUS = 15f;
    private float respawnTimer = 0f;
    private static final float RESPAWN_DELAY = 0.4f;
    private boolean pendingRespawn = false;

    public Knight getKnight() { return knight; }
    public GameData getData() { return data; }
    public Array<SolidBlock> getGroundRects() { return groundRects; }
    public Array<SolidBlock> getSpikeRects() { return spikeRects; }
    public Array<BreakableWall> getBreakableWalls() { return breakableWalls; }
    public Array<com.hollow.models.entities.enemy.Enemy> getEnemies() { return enemies; }

    public Game(HollowKnight game, Knight knight, Array<SolidBlock> groundRects,
                Array<SolidBlock> spikeRects, Array<Enemy> enemies,
                TransitionZone transitionZones, GameScreen screen,
                GameData data, FalseKnight boss, Array<BreakableWall> breakableWalls) {
        this.game = game;
        this.knight = knight;
        this.groundRects = groundRects;
        this.spikeRects = spikeRects;
        this.breakableWalls = breakableWalls;
        this.enemies = enemies;
        this.screen = screen;
        this.transitionZones = transitionZones;
        this.data = data;
        this.boss = boss;
        this.combatEngine = new CombatEngine(this, game, screen);
        this.hasShadowCharm = data.getActiveSlot().getEquippedCharms().contains(Charm.VOID_HEART, true);
    }

    public void update(float delta) {
        updateCombatState();
        if (data != null && !knight.isDead() && !pendingRespawn && !screen.isPaused) {
            float time = data.getActiveSlot().getPlayTime();
            data.getActiveSlot().setPlayTime(time + delta);
        }

        if (knight.isDead()) {
            knight.update(delta);
            resolveGroundCollision();
            if (knight.isDeathAnimationFinished()) handleDeath();
            return;
        }

        if (pendingRespawn) {
            respawnTimer += delta;
            if (respawnTimer >= RESPAWN_DELAY) pendingRespawn = false;
            knight.updateAnimations(delta);
            return;
        }

        handleInput();
        knight.update(delta);
        if (!knight.noclipMode) {
            resolveGroundCollision();
            resolveSpikeCollision();
        }
        updateEnemies(delta);
        checkAndLockArena();
        updateCombatMechanics(delta);
        if (boss != null) updateBoss(delta);
        updateEnvironment(delta);
//        checkBossDefeated();
    }

    private void updateCombatMechanics(float delta) {
        if (knight.castProjectile) {
            knight.castProjectile = false;
            combatEngine.castProjectile();
        }
        if (knight.castWraiths) {
            knight.castWraiths = false;
            combatEngine.castWraiths();
        }
        combatEngine.updateWraiths(delta);
        combatEngine.updateProjectiles(delta);
        combatEngine.updateLasers(delta);
    }

    private void updateEnvironment(float delta) {
        for (int i = activeDebris.size - 1; i >= 0; i--) {
            Debris d = activeDebris.get(i);
            d.update(delta);
            if (d.isDead()) activeDebris.removeIndex(i);
        }
        updateVoidHeart(delta);
        updateZoteLogic(delta);
        checkMapTransitions();
        checkSpeedrunAchievement();
    }

    private void checkSpeedrunAchievement() {
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
                updateCrystallizedEnemy(crystalEnemy);
            }

            if (enemy.state != Enemy.EnemyState.CORPSE && enemy.state != Enemy.EnemyState.DYING_AIR
                && enemy.state != Enemy.EnemyState.DYING_LAND) {
                if (knight.getHitbox().overlaps(enemy.hitbox) && !knight.isInvincible() && !knight.noclipMode) {
                    if (knight.isDashing() && data.getActiveSlot().getEquippedCharms().contains(Charm.SHARP_SHADOW, true)) {
                        boolean hitFromRight = knight.getX() > enemy.position.x;
                        enemy.takeDamage(1, hitFromRight);
                        game.audioManager.playSound(game.audioManager.audioLoader.enemyHit);
                        combatEngine.checkEnemyKill(enemy);
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

    private void updateCrystallizedEnemy(Crystallized crystalEnemy) {
        if (crystalEnemy.state == Enemy.EnemyState.ATTACK_ANTICIPATE && !crystalEnemy.laserFired) {
            if (crystalEnemy.stateTime > 0.3f) {
                crystalEnemy.laserFired = true;
                float startX = crystalEnemy.isFacingRight ? crystalEnemy.position.x + crystalEnemy.hitbox.width : crystalEnemy.position.x;
                float startY = crystalEnemy.position.y + 0.6f;
                float maxDist = 40f, actualW = maxDist;

                for (SolidBlock ground : groundRects) {
                    if (!ground.isDeadly) {
                        boolean yOverlap = (ground.bounds.y < startY + 1.0f) && (ground.bounds.y + ground.bounds.height > startY);
                        if (yOverlap) {
                            if (crystalEnemy.isFacingRight && ground.bounds.x > startX) {
                                float dist = ground.bounds.x - startX;
                                if (dist < actualW) actualW = dist;
                            } else if (!crystalEnemy.isFacingRight && ground.bounds.x + ground.bounds.width < startX) {
                                float dist = startX - (ground.bounds.x + ground.bounds.width);
                                if (dist < actualW) actualW = dist;
                            }
                        }
                    }
                }
                float finalX = crystalEnemy.isFacingRight ? startX : startX - actualW;
                activeInstantLasers.add(new InstantLaser(finalX, startY, actualW, 1.0f, crystalEnemy.isFacingRight));
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
            } else if (minOverlap == overlapBottom) {
                enemy.position.y = ground.bounds.y - enemy.hitbox.height;
                if (enemy.velocity.y > 0) enemy.velocity.y = 0;
            } else if (minOverlap == overlapLeft || minOverlap == overlapRight) {
                enemy.turnAround();
                if (minOverlap == overlapLeft) enemy.position.x = ground.bounds.x - enemy.hitbox.width;
                else enemy.position.x = ground.bounds.x + ground.bounds.width;
            }
            enemy.hitbox.setPosition(enemy.position.x, enemy.position.y);
        }
    }

    private void handleInput() {
        if (pendingRespawn) return;

        boolean ctrlPressed = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT);
        if (ctrlPressed) handleCheats();

        if (knight.noclipMode) {
            handleNoclipInput();
            return;
        }

        if (screen.isEndingSequence) {
            handleEndingSequenceInput();
            return;
        }

        if (screen.zote != null && handleZoteInteraction()) return;

        handlePlayerActions();
    }

    private void handleCheats() {
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

    private void handleNoclipInput() {
        float noclipSpeed = 25f;
        knight.getVelocity().setZero();
        if (Gdx.input.isKeyPressed(data.getSettings().getKeyLeft())) knight.getVelocity().x = -noclipSpeed;
        if (Gdx.input.isKeyPressed(data.getSettings().getKeyRight())) knight.getVelocity().x = noclipSpeed;
        if (Gdx.input.isKeyPressed(data.getSettings().getKeyUp())) knight.getVelocity().y = noclipSpeed;
        if (Gdx.input.isKeyPressed(data.getSettings().getKeyDown())) knight.getVelocity().y = -noclipSpeed;
    }

    private void handleEndingSequenceInput() {
        boolean isMovingLeft = Gdx.input.isKeyPressed(data.getSettings().getKeyLeft());
        boolean isMovingRight = Gdx.input.isKeyPressed(data.getSettings().getKeyRight());

        if (isMovingLeft && !isMovingRight) knight.movingHorizontally(-1f);
        else if (isMovingRight && !isMovingLeft) knight.movingHorizontally(1f);
        else knight.stopMovingHorizontally();

        if (screen.endingStatueRect != null && knight.getX() > (screen.endingStatueRect.x - 20f)) {
            AchievementManager.getInstance().unlockAchievement(Achievement.COMPLETION);
        }

        if (Gdx.input.isKeyJustPressed(data.getSettings().getKeyJump())) knight.jumping();
        if (!Gdx.input.isKeyPressed(data.getSettings().getKeyJump()) && knight.getVelocity().y > 0) knight.littleJumping();

        if (screen.endingStatueRect != null && knight.getHitbox().overlaps(screen.endingStatueRect)) {
            if (!screen.endingUI.isVisible) screen.dialogueBox.setPromptVisible(true);
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
    }

    private boolean handleZoteInteraction() {
        boolean inRange = knight.getHitbox().overlaps(screen.zote.interactionBox);
        boolean isInteractiveState = (screen.zote.currentState == Zote.State.IDLE || screen.zote.currentState == Zote.State.SLEEPING);
        boolean canShowPrompt = inRange && isInteractiveState && !screen.dialogueBox.isVisible && !screen.zote.pendingDialogue;

        screen.dialogueBox.setPromptVisible(canShowPrompt);

        if (screen.zote.pendingDialogue && screen.zote.currentState == Zote.State.IDLE) {
            screen.zote.pendingDialogue = false;
            screen.dialogueBox.startDialogue(screen.zote.getDialogue());
        }

        if (screen.zote.currentState == Zote.State.GETTING_UP) {
            knight.stopMovingHorizontally();
            return true;
        }

        if (screen.dialogueBox.isVisible) {
            knight.stopMovingHorizontally();
            screen.zote.currentState = Zote.State.TALKING;
            return true;
        } else if (screen.zote.currentState == Zote.State.TALKING) {
            screen.zote.currentState = Zote.State.IDLE;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.E) && knight.isOnGround()) {
            if (knight.getHitbox().overlaps(screen.zote.interactionBox)) {
                screen.zote.isFacingRight = knight.getX() < screen.zote.position.x;
                if (screen.zote.currentState == Zote.State.SLEEPING) {
                    screen.zote.changeState(Zote.State.GETTING_UP);
                    screen.zote.pendingDialogue = true;
                    game.audioManager.playSound(game.audioManager.audioLoader.zoteGetUp);
                } else if (screen.zote.currentState == Zote.State.IDLE) {
                    screen.dialogueBox.startDialogue(screen.zote.getDialogue());
                }
                knight.stopMovingHorizontally();
                knight.setLookDirection(0);
                return true;
            }
        }
        return false;
    }

    private void handlePlayerActions() {
        int keyLeft = data.getSettings().getKeyLeft();
        int keyRight = data.getSettings().getKeyRight();
        int keyUp = data.getSettings().getKeyUp();
        int keyDown = data.getSettings().getKeyDown();

        boolean isMovingLeft = Gdx.input.isKeyPressed(keyLeft);
        boolean isMovingRight = Gdx.input.isKeyPressed(keyRight);

        if (isMovingLeft && !isMovingRight) knight.movingHorizontally(-1f);
        else if (isMovingRight && !isMovingLeft) knight.movingHorizontally(1f);
        else knight.stopMovingHorizontally();

        if (Gdx.input.isKeyJustPressed(data.getSettings().getKeyJump())) knight.jumping();
        if (!Gdx.input.isKeyPressed(data.getSettings().getKeyJump()) && knight.getVelocity().y > 0) knight.littleJumping();

        if (Gdx.input.isKeyJustPressed(data.getSettings().getKeyAttack())) {
            int dir = 0;
            if (Gdx.input.isKeyPressed(keyUp)) dir = 1;
            else if (Gdx.input.isKeyPressed(keyDown) && !knight.isOnGround()) dir = -1;
            knight.attacking(dir);
            combatEngine.performAttack(dir);
        }

        if (Gdx.input.isKeyPressed(data.getSettings().getKeyDash())) knight.dashing();
        if (Gdx.input.isKeyPressed(data.getSettings().getKeyFocus())) knight.startFocusing();
        else knight.stopFocusing();

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            if (knight.getState() != KnightState.CASTING && knight.getState() != KnightState.UP_CASTING
                && !knight.isBusy() && knight.getSoul() >= 33) {
                knight.consumeSoul(33);
                if (Gdx.input.isKeyPressed(keyUp)) knight.startUpCasting();
                else knight.startCasting();
            }
        }

        if (Gdx.input.isKeyPressed(keyUp) && knight.isOnGround() && knight.getVelocity().x == 0) knight.setLookDirection(1);
        else if (Gdx.input.isKeyPressed(keyDown) && knight.isOnGround() && knight.getVelocity().x == 0) knight.setLookDirection(-1);
        else knight.setLookDirection(0);
    }

    private void resolveGroundCollision() {
        Rectangle knightBox = knight.getHitbox();
        boolean wasOnGround = false, touchingWall = false;
        float prevY = knight.getY() - (knight.getVelocity().y * Gdx.graphics.getDeltaTime());

        for (SolidBlock ground : groundRects) {
            if (ground.isDeadly || !knightBox.overlaps(ground.bounds)) continue;

            float overlapLeft   = (knightBox.x + knightBox.width) - ground.bounds.x;
            float overlapRight  = (ground.bounds.x + ground.bounds.width) - knightBox.x;
            float overlapBottom = (knightBox.y + knightBox.height) - ground.bounds.y;
            float overlapTop    = (ground.bounds.y + ground.bounds.height) - knightBox.y;

            if (prevY < (ground.bounds.y + ground.bounds.height) - 0.2f) overlapTop = Float.MAX_VALUE;
            if (prevY + knightBox.height > ground.bounds.y + 0.2f) overlapBottom = Float.MAX_VALUE;

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

        if (!wasOnGround) knight.setAirborne();
        if (!touchingWall) knight.resetWallTouch();
    }

    private void resolveSpikeCollision() {
        if (knight.isInvincible() || knight.isDead() || pendingRespawn || knight.godMode) return;
        for (SolidBlock spike : spikeRects) {
            if (spike.isDeadly && knight.hitbox.overlaps(spike.bounds)) {
                knight.hitSpike();
                if (!knight.isDead()) knight.reSpawn();
                pendingRespawn = true;
                respawnTimer = 0f;
                break;
            }
        }
    }

    private void handleDeath() {
        data.getActiveSlot().setDeathCount(data.getActiveSlot().getDeathCount() + 1);
        SaveManager.save(data);
        Vector2 initialPos = screen.helper.findSpawnPoint(screen.map, GameScreen.UNIT_SCALE);
        knight.fullRespawn(initialPos.x, initialPos.y);
        resetBossFight();
    }

    private void checkAndLockArena() {
        if (boss == null || boss.bossFightStarted || boss.currentState == FalseKnight.State.DEATH) return;

        float minX = screen.arenaMinX, maxX = screen.arenaMaxX, y = screen.arenaY, height = screen.arenaHeight;

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

        if (boss != null && boss.currentState != FalseKnight.State.DEATH) {
            boss.bossFightStarted = false;
            boss.currentHp = boss.maxHp;
            boss.isPhaseTwo = false;
            boss.isGrounded = false;
            boss.reset();
            boss.velocity.setZero();

            if (boss.activeShockwaves != null) boss.activeShockwaves.clear();

            Vector2 bossSpawn = screen.helper.findBossSpawnPoint(screen.map, GameScreen.UNIT_SCALE);
            boss.position.set(bossSpawn.x, bossSpawn.y);
            boss.changeBehavior(new IdleBehavior());
        }
    }

    private void updateVoidHeart(float delta) {
        if (!data.getActiveSlot().getUnlockedCharms().contains(Charm.VOID_HEART, true) && voidHeartPos != null) {
            voidHeartStateTime += delta;
            Rectangle charmHitbox = new Rectangle(voidHeartPos.x, voidHeartPos.y, 1f, 1f);

            if (knight.getPosition().dst(voidHeartPos) < 6f && !screen.isPaused) game.audioManager.playCharmProximityLoop();
            else game.audioManager.stopCharmProximityLoop();

            if (knight.getHitbox().overlaps(charmHitbox)) {
                screen.dialogueBox.setPromptVisible(true);

                if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                    data.getActiveSlot().getUnlockedCharms().add(Charm.VOID_HEART);
                    SaveManager.save(data);
                    screen.hud.showItemPopup("Void Heart Unlocked!");

                    game.audioManager.stopCharmProximityLoop();
                    game.audioManager.playSound(game.audioManager.audioLoader.knightPickUpSpellFinal);

                    if (screen.inventoryUI != null) screen.inventoryUI.refreshUnlockedCharms();
                }
            }
        } else {
            game.audioManager.stopCharmProximityLoop();
        }
    }

    private void updateZoteLogic(float delta) {
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
    }

    private void updateCombatState() {
        if (knight.isDead() || pendingRespawn) {
            inCombat = false;
            return;
        }

        boolean enemyNearby = false;
        for (Enemy enemy : enemies) {
            if (enemy.state != Enemy.EnemyState.CORPSE && enemy.state != Enemy.EnemyState.DYING_AIR
                && enemy.state != Enemy.EnemyState.DYING_LAND) {
                if (knight.getPosition().dst(enemy.position) <= COMBAT_RADIUS) {
                    enemyNearby = true;
                    break;
                }
            }
        }

        if (screen.zote != null && screen.zote.currentState == Zote.State.ANGRY) enemyNearby = true;
        inCombat = enemyNearby;
    }

    private void updateBoss(float delta) {
        if (boss.currentState == FalseKnight.State.DEATH) {
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

    private void resolveBossCollision() {
        if (boss == null || boss.currentState == FalseKnight.State.DEATH) return;

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

}
