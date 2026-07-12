package com.hollow.controllers.engine;

import com.badlogic.gdx.math.Rectangle;
import com.hollow.HollowKnight;
import com.hollow.controllers.manager.AchievementManager;
import com.hollow.controllers.manager.SaveManager;
import com.hollow.models.*;
import com.hollow.models.achievement.Achievement;
import com.hollow.models.entities.enemy.Enemy;
import com.hollow.models.entities.enemy.InstantLaser;
import com.hollow.models.entities.boss.FalseKnight;
import com.hollow.models.entities.knight.Charm;
import com.hollow.models.entities.knight.Projectile;
import com.hollow.models.entities.knight.WraithEffect;
import com.hollow.models.entities.zote.Zote;
import com.hollow.views.screen.GameScreen;
import com.hollow.controllers.engine.Game;

public class CombatEngine {
    private final Game core;
    private final HollowKnight game;
    private final GameScreen screen;

    public CombatEngine(Game core, HollowKnight game, GameScreen screen) {
        this.core = core;
        this.game = game;
        this.screen = screen;
    }

    public void castProjectile() {
        GameScreen.triggerShake(0.12f, 0.5f);
        core.hasShadowCharm = core.getData().getActiveSlot().getEquippedCharms().contains(Charm.VOID_HEART, true);
        game.audioManager.playSound(game.audioManager.audioLoader.knightFireball);

        float spawnX = core.getKnight().isFacingRight() ?
            (core.getKnight().getX() + core.getKnight().getWidth()) : (core.getKnight().getX() - 1.5f);
        float spawnY = core.getKnight().getY() + 0.2f;
        core.activeProjectiles.add(new Projectile(spawnX, spawnY, core.getKnight().isFacingRight(), core.hasShadowCharm));

        float blastW = 5.0f, blastH = 5.0f, offset = 1f;
        float effectX = core.getKnight().isFacingRight() ?
            core.getKnight().getX() + (core.getKnight().getWidth() / 2f) - offset :
            core.getKnight().getX() - blastW + (core.getKnight().getWidth() / 2f) + offset;
        float effectY = core.getKnight().getY() - (blastH / 2f) + (core.getKnight().getHeight() / 2f);

        core.activeEffects.add(new Effect(core.getKnight().blast, effectX, effectY, blastW, blastH,
            core.getKnight().isFacingRight()));

        if (core.hasShadowCharm) AchievementManager.getInstance().unlockAchievement(Achievement.SHADOW_MASTER);
    }

    public void castWraiths() {
        GameScreen.triggerShake(0.3f, 0.4f);
        core.hasShadowCharm = core.getData().getActiveSlot().getEquippedCharms().contains(Charm.VOID_HEART, true);
        game.audioManager.playSound(game.audioManager.audioLoader.knightFireball);

        float effectW = 7.0f, effectH = 8.0f;
        float effectX = core.getKnight().getX() + (core.getKnight().getWidth() / 2f) - (effectW / 2f);
        float effectY = core.getKnight().getY() + core.getKnight().getHeight() - 0.5f;

        core.activeWraiths.add(new WraithEffect(effectX, effectY, effectW, effectH, core.hasShadowCharm, 0.7f));
    }

    public void updateWraiths(float delta) {
        for (int i = core.activeWraiths.size - 1; i >= 0; i--) {
            WraithEffect w = core.activeWraiths.get(i);
            w.stateTime += delta;

            int expectedTicks = (int) (w.stateTime / w.tickInterval);
            if (expectedTicks > w.ticksDone && w.ticksDone < 3) {
                w.ticksDone++;
                applyWraithDamage(w);
            }

            if (w.stateTime >= w.maxTime) core.activeWraiths.removeIndex(i);
        }
    }

    private void applyWraithDamage(WraithEffect w) {
        int damage = w.isShadow ? 33 : 22;
        if (core.instaKillMode) damage = 9999;

        for (Enemy enemy : core.getEnemies()) {
            if (enemy.state != Enemy.EnemyState.CORPSE && w.hitbox.overlaps(enemy.hitbox)) {
                enemy.takeDamage(damage, enemy.position.x > w.x + w.width / 2f);
                game.audioManager.playSound(game.audioManager.audioLoader.enemyHit);
                checkEnemyKill(enemy);
            }
        }

        if (core.boss != null && core.boss.currentState != FalseKnight.State.DEATH) {
            Rectangle targetBox = (core.boss.currentState == FalseKnight.State.STUNNED) ?
                core.boss.vulnerabilityBox : core.boss.hitbox;
            if (w.hitbox.overlaps(targetBox)) {
                core.boss.takeDamage(damage);
                game.audioManager.playSound(game.audioManager.audioLoader.fkArmourHit);
            }
        }
    }

    public void updateProjectiles(float delta) {
        for (int i = core.activeProjectiles.size - 1; i >= 0; i--) {
            Projectile p = core.activeProjectiles.get(i);
            p.update(delta);

            checkProjectileGroundCollision(p);
            if (!p.isDestroyed) checkProjectileEnemyCollision(p);
            if (!p.isDestroyed) checkProjectileBossCollision(p);
            if (!p.isDestroyed) checkProjectileWallCollision(p);

            if (p.isDestroyed) core.activeProjectiles.removeIndex(i);
        }
    }

    private void checkProjectileGroundCollision(Projectile p) {
        for (SolidBlock ground : core.getGroundRects()) {
            if (!ground.isDeadly && p.hitbox.overlaps(ground.bounds)) {
                p.isDestroyed = true;
                break;
            }
        }
    }

    private void checkProjectileEnemyCollision(Projectile p) {
        for (Enemy enemy : core.getEnemies()) {
            if (enemy.state != Enemy.EnemyState.CORPSE && p.hitbox.overlaps(enemy.hitbox)) {
                int spellDmg = p.isShadow ? 2 : 1;
                if (core.hasShadowCharm) spellDmg = (int)(spellDmg * 1.5f);
                if (core.instaKillMode) spellDmg = 9999;

                enemy.takeDamage(spellDmg, p.isFacingRight);
                game.audioManager.playSound(game.audioManager.audioLoader.enemyHit);
                checkEnemyKill(enemy);
                p.isDestroyed = true;
                break;
            }
        }
    }

    private void checkProjectileBossCollision(Projectile p) {
        if (core.boss != null && core.boss.currentState != FalseKnight.State.DEATH) {
            Rectangle targetBox = (core.boss.currentState == FalseKnight.State.STUNNED) ?
                core.boss.vulnerabilityBox : core.boss.hitbox;
            if (p.hitbox.overlaps(targetBox)) {
                int damage = p.isShadow ? 33 : 22;
                if (core.instaKillMode) damage = 9999;

                if (core.boss.currentState == FalseKnight.State.STUNNED) {
                    game.audioManager.playSound(game.audioManager.audioLoader.fkHeadHit);
                } else {
                    game.audioManager.playSound(game.audioManager.audioLoader.fkArmourHit);
                }

                core.boss.takeDamage(damage);
                p.isDestroyed = true;
            }
        }
    }

    private void checkProjectileWallCollision(Projectile p) {
        for (BreakableWall w : core.getBreakableWalls()) {
            if (!w.isDestroyed() && p.hitbox.overlaps(w.bounds)) {
                w.takeDamage(1);
                GameScreen.triggerShake(0.1f, 0.05f);
                spawnDebris(w, 10);

                if (w.getHp() <= 0) {
                    game.audioManager.playWallDeathSound();
                    w.setDestroyed(true);
                    core.getGroundRects().removeValue(w, true);
                    core.getBreakableWalls().removeValue(w, true);
                    screen.removeWallTiles(w.bounds);
                } else {
                    game.audioManager.playWallHitSound();
                }
                p.isDestroyed = true;
                break;
            }
        }
    }

    private void spawnDebris(BreakableWall w, int count) {
        for (int j = 0; j < count; j++) {
            float spawnX = w.bounds.x + (w.bounds.width / 2f);
            float spawnY = w.bounds.y + (w.bounds.height / 2f);
            core.activeDebris.add(new Debris(game.assetLoader.rockTexture, spawnX, spawnY));
        }
    }

    public void updateLasers(float delta) {
        for (int i = core.activeInstantLasers.size - 1; i >= 0; i--) {
            InstantLaser laser = core.activeInstantLasers.get(i);
            laser.update(delta);

            if (laser.hitbox.overlaps(core.getKnight().getHitbox()) && !core.getKnight().isInvincible()) {
                boolean hitRight = core.getKnight().getX() < (laser.isFacingRight ? laser.hitbox.x : laser.hitbox.x + laser.hitbox.width);
                core.getKnight().takeDamage(1, hitRight);
            }

            if (laser.isFinished()) core.activeInstantLasers.removeIndex(i);
        }
    }

    public void performAttack(int dir) {
        Rectangle attackBox = new Rectangle();
        float kx = core.getKnight().getX(), ky = core.getKnight().getY();
        float kw = core.getKnight().getWidth(), kh = core.getKnight().getHeight();
        float range = 1.8f;

        if (dir > 0) attackBox.set(kx - 0.5f, ky + kh, kw + 1f, range);
        else if (dir < 0) attackBox.set(kx - 0.5f, ky - range, kw + 1f, range);
        else {
            if (core.getKnight().isFacingRight()) attackBox.set(kx + kw, ky, range, kh);
            else attackBox.set(kx - range, ky, range, kh);
        }

        boolean hitSomething = false;
        int nailDamage = core.getData().getActiveSlot().getEquippedCharms().contains(Charm.UNBREAKABLE_STRENGTH, true) ? 2 : 1;
        if (core.instaKillMode) nailDamage = 9999;
        int soulAmount = core.getData().getActiveSlot().getEquippedCharms().contains(Charm.SOUL_CATCHER, true) ? 22 : 11;
        boolean heavyBlow = core.getData().getActiveSlot().getEquippedCharms().contains(Charm.HEAVY_BLOW, true);

        hitSomething |= checkMeleeEnemyHit(attackBox, nailDamage, soulAmount, heavyBlow);
        hitSomething |= checkMeleeWallHit(attackBox);
        if (dir < 0) hitSomething |= checkMeleeSpikeHit(attackBox);
        hitSomething |= checkMeleeZoteHit(attackBox);
        hitSomething |= checkMeleeBossHit(attackBox, dir);

        if (hitSomething) {
            if (dir < 0) {
                core.getKnight().setVelocityY(30f);
                core.getKnight().setOnGround(false);
                core.getKnight().resetWallTouch();
                core.getKnight().resetDash();
                core.getKnight().resetDoubleJump();
            } else if (dir == 0) {
                core.getKnight().getVelocity().x = core.getKnight().isFacingRight() ? -4f : 4f;
            }
        } else {
            game.audioManager.playSound(game.audioManager.audioLoader.knightSlash);
        }
    }

    private boolean checkMeleeEnemyHit(Rectangle attackBox, int nailDamage, int soulAmount, boolean heavyBlow) {
        boolean hit = false;
        for (Enemy enemy : core.getEnemies()) {
            if (enemy.state != Enemy.EnemyState.CORPSE && enemy.state != Enemy.EnemyState.DYING_AIR
                && enemy.state != Enemy.EnemyState.DYING_LAND) {
                if (attackBox.overlaps(enemy.hitbox)) {
                    boolean hitFromRight = core.getKnight().getX() > enemy.position.x;
                    enemy.takeDamage(nailDamage, hitFromRight);
                    game.audioManager.playSound(game.audioManager.audioLoader.enemyHit);
                    checkEnemyKill(enemy);

                    if (heavyBlow) enemy.velocity.x = hitFromRight ? -12f : 12f;
                    core.getKnight().gainSoul(soulAmount);
                    hit = true;
                }
            }
        }
        return hit;
    }

    private boolean checkMeleeWallHit(Rectangle attackBox) {
        boolean hit = false;
        for (BreakableWall w : core.getBreakableWalls()) {
            if (!w.isDestroyed() && attackBox.overlaps(w.bounds)) {
                w.takeDamage(1);
                GameScreen.triggerShake(0.1f, 0.05f);
                hit = true;
                spawnDebris(w, 8);

                if (w.getHp() <= 0) {
                    game.audioManager.playWallDeathSound();
                    w.setDestroyed(true);
                    core.getGroundRects().removeValue(w, true);
                    core.getBreakableWalls().removeValue(w, true);
                    screen.removeWallTiles(w.bounds);
                } else {
                    game.audioManager.playWallHitSound();
                }
            }
        }
        return hit;
    }

    private boolean checkMeleeSpikeHit(Rectangle attackBox) {
        for (SolidBlock spike : core.getSpikeRects()) {
            if (spike.isDeadly && attackBox.overlaps(spike.bounds)) {
                game.audioManager.playSound(game.audioManager.audioLoader.hitMetal);
                return true;
            }
        }
        return false;
    }

    private boolean checkMeleeZoteHit(Rectangle attackBox) {
        if (screen.zote != null && (screen.zote.currentState == Zote.State.IDLE || screen.zote.currentState == Zote.State.TALKING)) {
            if (attackBox.overlaps(screen.zote.hitbox)) {
                screen.zote.changeState(Zote.State.ANGRY);
                screen.zote.angryTimer = screen.zote.ANGRY_DURATION;
                game.audioManager.playSound(game.audioManager.audioLoader.zoteRoar);
                return true;
            }
        }
        return false;
    }

    private boolean checkMeleeBossHit(Rectangle attackBox, int dir) {
        if (core.boss != null && core.boss.currentState != FalseKnight.State.DEATH) {
            Rectangle targetBox = (core.boss.currentState == FalseKnight.State.STUNNED) ?
                core.boss.vulnerabilityBox : core.boss.hitbox;
            if (attackBox.overlaps(targetBox)) {
                int nail = core.instaKillMode ? 9999 : 11;
                if (core.boss.currentState == FalseKnight.State.STUNNED)
                    game.audioManager.playSound(game.audioManager.audioLoader.fkHeadHit);
                else
                    game.audioManager.playSound(game.audioManager.audioLoader.fkArmourHit);

                core.boss.takeDamage(nail);
                core.getKnight().gainSoul(11);

                if (dir < 0) {
                    core.getKnight().setVelocityY(15f);
                    core.getKnight().setOnGround(false);
                    core.getKnight().resetWallTouch();
                } else if (dir == 0) {
                    core.getKnight().getVelocity().x = core.getKnight().isFacingRight() ? -4f : 4f;
                }
                return true;
            }
        }
        return false;
    }

    public void checkEnemyKill(Enemy enemy) {
        if (enemy.health <= 0) {
            game.audioManager.playSound(game.audioManager.audioLoader.enemyDeath);
            core.getData().registerEnemyKill(enemy.name);
            SaveManager.save(core.getData());
            if (core.getData().getTotalEnemyKilled() >= 6) {
                AchievementManager.getInstance().unlockAchievement(Achievement.TRUE_HUNTER);
                SaveManager.save(core.getData());
            }
        }
    }
}
