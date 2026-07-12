package com.hollow.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioLoader {
    public Music titleTheme, greenpathAtmos, greenpathMain, greenpathBass;
    public Music victoryTheme, crossroadsMain, crossroadsBass, bossFight;

    public Sound buttonHover, buttonClick;

    public Sound knightDamage, knightDash, knightDeath, knightFalling, knightFireball;
    public Sound knightJump, knightSlidingWall, knightRunStone, knightRunGrass;
    public Sound knightPickUpSpell, knightPickUpSpellFinal, knightWings, knightWallJump;
    public Sound knightSlash, knightFocusCharge, knightFocusHeal, knightHardLand, knightShadeDash;

    public Sound enemyHit, enemyDeath;
    public Sound mosqCharge, mosqFly, mosqWallHit, mosqPrepare;

    public Sound[] zoteTalk;
    public Sound zoteGetUp, zoteRoar, zoteAttackLoop, zoteIdleLoop, zotePirouette, zoteFloorFall;

    public Sound fkJump, fkStrike, fkSwing, fkArmourHit, fkHeadHit, fkStun, fkDeath;
    public Sound[] fkShouts;

    public Sound wallHit1, wallHit2, wallDeath, hitMetal;

    public Sound[] soulPickups = new Sound[7];

    public void load() {
        loadMusicAndUI();
        loadKnightSounds();
        loadEnemySounds();
        loadZoteSounds();
        loadBossSounds();
        loadMiscSounds();
    }

    private void loadMusicAndUI() {
        titleTheme = Gdx.audio.newMusic(Gdx.files.internal("audio/bgm/Title.wav"));
        buttonHover = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/button-hover.wav"));
        buttonClick = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/button-click.wav"));

        greenpathAtmos = Gdx.audio.newMusic(Gdx.files.internal("audio/bgm/greenpathAtmos.wav"));
        greenpathMain = Gdx.audio.newMusic(Gdx.files.internal("audio/bgm/greenpathMain.wav"));
        greenpathBass = Gdx.audio.newMusic(Gdx.files.internal("audio/bgm/greenpathBass.wav"));

        victoryTheme = Gdx.audio.newMusic(Gdx.files.internal(
            "audio/bgm/Lana Del Rey - Arcadia.mp3"));

        crossroadsMain = Gdx.audio.newMusic(Gdx.files.internal("audio/bgm/crossroadsMain.wav"));
        crossroadsBass = Gdx.audio.newMusic(Gdx.files.internal("audio/bgm/crossroadsBass.wav"));
        bossFight = Gdx.audio.newMusic(Gdx.files.internal("audio/bgm/bossFight.wav"));
    }

    private void loadKnightSounds() {
        knightDamage = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/knight_damage.wav"));
        knightDash = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/knight_dash.wav"));
        knightDeath = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/knight_death.wav"));
        knightFalling = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/Knight_falling.wav"));
        knightFireball = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/knight_fireball.wav"));
        knightJump = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/knight_jump.wav"));
        knightSlidingWall = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/knight_slidingWall.wav"));
        knightRunStone = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/knight_runStone.wav"));
        knightRunGrass = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/knight_runGrass.wav"));
        knightPickUpSpell = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/knight_pickUpSpell.wav"));
        knightPickUpSpellFinal = Gdx.audio.newSound(Gdx.files.internal(
            "audio/sound-effect/knight/Knight_pickUpSpellFinal.wav"));
        knightWings = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/knight_wings.wav"));
        knightWallJump = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/knight_wallJump.wav"));
        knightSlash = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/hero_evade.wav"));
        knightFocusCharge = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/focus_health_charging.wav"));
        knightFocusHeal = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/focus_health_heal.wav"));
        knightHardLand = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/hero_land_hard.wav"));
        knightShadeDash = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/hero_shade_dash_2.wav"));
    }

    private void loadEnemySounds() {
        enemyHit = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/enemy/enemy_damage.wav"));
        enemyDeath = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/enemy/enemy_death_sword.wav"));
        mosqCharge = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/enemy/mosquito_charge_charge.wav"));
        mosqFly = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/enemy/mosquito_fly_loop.wav"));
        mosqWallHit = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/enemy/mosquito_wall_hit.wav"));
        mosqPrepare = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/enemy/mosquito_charge_prepare.wav"));
    }

    private void loadZoteSounds() {
        zoteTalk = new Sound[5];
        zoteTalk[0] = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/zote/Zote_01.wav"));
        zoteTalk[1] = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/zote/Zote_02.wav"));
        zoteTalk[2] = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/zote/Zote_03.wav"));
        zoteTalk[3] = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/zote/Zote_04.wav"));
        zoteTalk[4] = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/zote/Zote_05.wav"));

        zoteGetUp = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/zote/zote_get_up.wav"));
        zoteRoar = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/zote/Zote_battle_roar.wav"));
        zoteAttackLoop = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/zote/Zote_battle_attack_loop.wav"));
        zoteIdleLoop = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/zote/Zote_final_town_loop.wav"));
        zotePirouette = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/zote/Zote_pirouette_01.wav"));
        zoteFloorFall = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/zote/Zote_floor_fall_01.wav"));
    }

    private void loadBossSounds() {
        fkJump = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/boss/false_knight_jump.wav"));
        fkStrike = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/boss/false_knight_strike_ground.wav"));
        fkSwing = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/boss/false_knight_swing.wav"));
        fkArmourHit = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/boss/false_knight_damage_armour.wav"));
        fkHeadHit = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/boss/false_knight_head_damage_2.wav"));
        fkStun = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/boss/boss_stun.wav"));
        fkDeath = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/boss/boss_explode.wav"));

        fkShouts = new Sound[3];
        fkShouts[0] = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/boss/False_Knight_Attack_New_01.wav"));
        fkShouts[1] = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/boss/False_Knight_Attack_New_02.wav"));
        fkShouts[2] = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/boss/False_Knight_Attack_New_03.wav"));
    }

    private void loadMiscSounds() {
        wallHit1 = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/breakable_wall_hit_1.wav"));
        wallHit2 = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/breakable_wall_hit_2.wav"));
        wallDeath = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/breakable_wall_death.wav"));
        hitMetal = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/sword_hit_reject.wav"));

        for (int i = 0; i < 7; i++) {
            soulPickups[i] = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/soul_pickup_" + (i + 1) + ".wav"));
        }
    }

    public void dispose() {
        disposeMusic();
        disposeKnightSounds();
        disposeEnemyAndZoteSounds();
        disposeBossAndMiscSounds();
    }

    private void disposeMusic() {
        if (titleTheme != null) titleTheme.dispose();
        if (buttonHover != null) buttonHover.dispose();
        if (buttonClick != null) buttonClick.dispose();
        if (greenpathAtmos != null) greenpathAtmos.dispose();
        if (greenpathMain != null) greenpathMain.dispose();
        if (greenpathBass != null) greenpathBass.dispose();
        if (crossroadsMain != null) crossroadsMain.dispose();
        if (crossroadsBass != null) crossroadsBass.dispose();
        if (bossFight != null) bossFight.dispose();
        if (victoryTheme != null) victoryTheme.dispose();
    }

    private void disposeKnightSounds() {
        if (knightDamage != null) knightDamage.dispose();
        if (knightDash != null) knightDash.dispose();
        if (knightDeath != null) knightDeath.dispose();
        if (knightFalling != null) knightFalling.dispose();
        if (knightFireball != null) knightFireball.dispose();
        if (knightJump != null) knightJump.dispose();
        if (knightSlidingWall != null) knightSlidingWall.dispose();
        if (knightRunStone != null) knightRunStone.dispose();
        if (knightRunGrass != null) knightRunGrass.dispose();
        if (knightPickUpSpell != null) knightPickUpSpell.dispose();
        if (knightPickUpSpellFinal != null) knightPickUpSpellFinal.dispose();
        if (knightWings != null) knightWings.dispose();
        if (knightWallJump != null) knightWallJump.dispose();
        if (knightSlash != null) knightSlash.dispose();
        if (knightFocusCharge != null) knightFocusCharge.dispose();
        if (knightFocusHeal != null) knightFocusHeal.dispose();
        if (knightHardLand != null) knightHardLand.dispose();
        if (knightShadeDash != null) knightShadeDash.dispose();
    }

    private void disposeEnemyAndZoteSounds() {
        if (enemyHit != null) enemyHit.dispose();
        if (enemyDeath != null) enemyDeath.dispose();
        if (mosqCharge != null) mosqCharge.dispose();
        if (mosqFly != null) mosqFly.dispose();
        if (mosqWallHit != null) mosqWallHit.dispose();
        if (mosqPrepare != null) mosqPrepare.dispose();

        if (zoteTalk != null) {
            for (Sound s : zoteTalk) if (s != null) s.dispose();
        }
        if (zoteGetUp != null) zoteGetUp.dispose();
        if (zoteRoar != null) zoteRoar.dispose();
        if (zoteAttackLoop != null) zoteAttackLoop.dispose();
        if (zoteIdleLoop != null) zoteIdleLoop.dispose();
        if (zotePirouette != null) zotePirouette.dispose();
        if (zoteFloorFall != null) zoteFloorFall.dispose();
    }

    private void disposeBossAndMiscSounds() {
        if (fkDeath != null) fkDeath.dispose();
        if (fkArmourHit != null) fkArmourHit.dispose();
        if (fkHeadHit != null) fkHeadHit.dispose();
        if (fkJump != null) fkJump.dispose();
        if (fkStrike != null) fkStrike.dispose();
        if (fkStun != null) fkStun.dispose();
        if (fkSwing != null) fkSwing.dispose();
        if (fkShouts != null) {
            for (Sound s : fkShouts) if (s != null) s.dispose();
        }

        if (wallHit1 != null) wallHit1.dispose();
        if (wallHit2 != null) wallHit2.dispose();
        if (wallDeath != null) wallDeath.dispose();
        if (hitMetal != null) hitMetal.dispose();

        if (soulPickups != null) {
            for (Sound s : soulPickups) if (s != null) s.dispose();
        }
    }
}
