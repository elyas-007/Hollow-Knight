package com.hollow.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioLoader {
    public Music titleTheme;
    public Sound buttonHover;
    public Sound buttonClick;

    public Music greenpathAtmos;
    public Music greenpathMain;
    public Music greenpathBass;

    public Music crossroadsMain;
    public Music crossroadsBass;

    public Music bossFight;

    public Sound knight_damage;
    public Sound knight_dash;
    public Sound knight_death;
    public Sound Knight_falling;
    public Sound knight_fireball;
    public Sound knight_jump;
    public Sound knight_slidingWall;
    public Sound knight_runStone;
    public Sound knight_runGrass;
    public Sound knight_pickUpSpell;
    public Sound Knight_pickUpSpellFinal;
    public Sound knight_wings;
    public Sound knight_wallJump;
    public Sound knight_slash;
    public Sound knight_focusCharge;
    public Sound knight_focusHeal;
    public Sound knight_hardLand;
    public Sound knight_shadeDash;

    public Sound enemy_hit;
    public Sound enemy_death;
    public Sound mosq_charge;
    public Sound mosq_fly;
    public Sound mosq_wallHit;
    public Sound mosq_prepare;

    public Sound[] zoteTalk;
    public Sound zoteGetUp;
    public Sound zoteRoar;
    public Sound zoteAttackLoop;
    public Sound zoteIdleLoop;
    public Sound zotePirouette;
    public Sound zoteFloorFall;

    public Sound fk_jump;
    public Sound fk_strike;
    public Sound fk_swing;
    public Sound fk_armourHit;
    public Sound fk_headHit;
    public Sound fk_stun;
    public Sound fk_death;
    public Sound[] fk_shouts;

    public Sound wall_hit_1;
    public Sound wall_hit_2;
    public Sound wall_death;
    public Sound hit_metal;

    public Sound[] soulPickups = new Sound[7];



    public void load() {
        titleTheme = Gdx.audio.newMusic(Gdx.files.internal("audio/bgm/Title.wav"));
        buttonHover = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/button-hover.wav"));
        buttonClick = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/button-click.wav"));

        greenpathAtmos = Gdx.audio.newMusic(Gdx.files.internal("audio/bgm/greenpathAtmos.wav"));
        greenpathMain = Gdx.audio.newMusic(Gdx.files.internal("audio/bgm/greenpathMain.wav"));
        greenpathBass = Gdx.audio.newMusic(Gdx.files.internal("audio/bgm/greenpathBass.wav"));

        crossroadsMain = Gdx.audio.newMusic(Gdx.files.internal("audio/bgm/crossroadsMain.wav"));
        crossroadsBass = Gdx.audio.newMusic(Gdx.files.internal("audio/bgm/crossroadsBass.wav"));
        bossFight = Gdx.audio.newMusic(Gdx.files.internal("audio/bgm/bossFight.wav"));


        knight_damage = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/knight_damage.wav"));
        knight_dash = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/knight_dash.wav"));
        knight_death = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/knight_death.wav"));
        Knight_falling = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/Knight_falling.wav"));
        knight_fireball = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/knight_fireball.wav"));
        knight_jump = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/knight_jump.wav"));
        knight_slidingWall = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/knight_slidingWall.wav"));
        knight_runStone = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/knight_runStone.wav"));
        knight_runGrass = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/knight_runGrass.wav"));
        knight_pickUpSpell = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/knight_pickUpSpell.wav"));
        Knight_pickUpSpellFinal = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/Knight_pickUpSpellFinal.wav"));
        knight_wings = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/knight_wings.wav"));
        knight_wallJump = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/knight_wallJump.wav"));
        knight_slash = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/hero_evade.wav"));
        knight_focusCharge = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/focus_health_charging.wav"));
        knight_focusHeal = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/focus_health_heal.wav"));
        knight_hardLand = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/hero_land_hard.wav"));
        knight_shadeDash = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/knight/hero_shade_dash_2.wav"));

        enemy_hit = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/enemy/enemy_damage.wav"));
        enemy_death = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/enemy/enemy_death_sword.wav"));
        mosq_charge = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/enemy/mosquito_charge_charge.wav"));
        mosq_fly = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/enemy/mosquito_fly_loop.wav"));
        mosq_wallHit = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/enemy/mosquito_wall_hit.wav"));
        mosq_prepare = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/enemy/mosquito_charge_prepare.wav"));


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

        fk_jump = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/boss/false_knight_jump.wav"));
        fk_strike = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/boss/false_knight_strike_ground.wav"));
        fk_swing = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/boss/false_knight_swing.wav"));
        fk_armourHit = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/boss/false_knight_damage_armour.wav"));
        fk_headHit = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/boss/false_knight_head_damage_2.wav"));
        fk_stun = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/boss/boss_stun.wav"));
        fk_death = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/boss/boss_explode.wav"));

        fk_shouts = new Sound[3];
        fk_shouts[0] = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/boss/False_Knight_Attack_New_01.wav"));
        fk_shouts[1] = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/boss/False_Knight_Attack_New_02.wav"));
        fk_shouts[2] = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/boss/False_Knight_Attack_New_03.wav"));

        wall_hit_1 = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/breakable_wall_hit_1.wav"));
        wall_hit_2 = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/breakable_wall_hit_2.wav"));
        wall_death = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/breakable_wall_death.wav"));
        hit_metal = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/sword_hit_reject.wav"));

        for (int i = 0; i < 7; i++) {
            soulPickups[i] = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/soul_pickup_" + (i + 1) + ".wav"));
        }
    }

    public void dispose() {
        if (titleTheme != null) titleTheme.dispose();
        if (buttonHover != null) buttonHover.dispose();
        if (buttonClick != null) buttonClick.dispose();

        if (greenpathAtmos != null) greenpathAtmos.dispose();
        if (greenpathMain != null) greenpathMain.dispose();
        if (greenpathBass != null) greenpathBass.dispose();

        if (crossroadsMain != null) crossroadsMain.dispose();
        if (crossroadsBass != null) crossroadsBass.dispose();
        if (bossFight != null) bossFight.dispose();

        if (knight_damage != null) knight_damage.dispose();
        if (knight_dash != null) knight_dash.dispose();
        if (knight_death != null) knight_death.dispose();
        if (Knight_falling != null) Knight_falling.dispose();
        if (knight_fireball != null) knight_fireball.dispose();
        if (knight_jump != null) knight_jump.dispose();
        if (knight_slidingWall != null) knight_slidingWall.dispose();
        if (knight_runStone != null) knight_runStone.dispose();
        if (knight_runGrass != null) knight_runGrass.dispose();
        if (knight_pickUpSpell != null) knight_pickUpSpell.dispose();
        if (Knight_pickUpSpellFinal != null) Knight_pickUpSpellFinal.dispose();
        if (knight_wings != null) knight_wings.dispose();
        if (knight_wallJump != null) knight_wallJump.dispose();
        if (knight_slash != null) knight_slash.dispose();
        if (knight_focusCharge != null) knight_focusCharge.dispose();
        if (knight_focusHeal != null) knight_focusHeal.dispose();
        if (knight_hardLand != null) knight_hardLand.dispose();
        if (knight_shadeDash != null) knight_shadeDash.dispose();

        if (zoteTalk != null) {
            for (Sound s : zoteTalk) {
                if (s != null) s.dispose();
            }
        }
        if (zoteGetUp != null) zoteGetUp.dispose();
        if (zoteRoar != null) zoteRoar.dispose();
        if (zoteAttackLoop != null) zoteAttackLoop.dispose();
        if (zoteIdleLoop != null) zoteIdleLoop.dispose();
        if (zotePirouette != null) zotePirouette.dispose();
        if (zoteFloorFall != null) zoteFloorFall.dispose();

        if (fk_death != null) fk_death.dispose();
        if (fk_armourHit != null) fk_armourHit.dispose();
        if (fk_headHit != null) fk_headHit.dispose();
        if (fk_jump != null) fk_jump.dispose();
        if (fk_strike != null) fk_strike.dispose();
        if (fk_stun != null) fk_stun.dispose();
        if (fk_swing != null) fk_swing.dispose();
        for (Sound s : fk_shouts)  {
            if (s != null) s.dispose();
        }

        if (mosq_charge != null) mosq_charge.dispose();
        if (mosq_fly != null) mosq_fly.dispose();
        if (mosq_wallHit != null) mosq_wallHit.dispose();
        if (mosq_prepare != null) mosq_prepare.dispose();

        if (wall_hit_1 != null) wall_hit_1.dispose();
        if (wall_hit_2 != null) wall_hit_2.dispose();
        if (wall_death != null) wall_death.dispose();
    }
}
