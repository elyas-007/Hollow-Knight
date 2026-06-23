package com.hollow.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.hollow.models.entities.FalseKnightBoss.FalseKnight;
import com.hollow.assets.KnightAnimationLoader.*;
import static com.hollow.assets.KnightAnimationLoader.loadAnimation;

public class BossAnimationLoader {
    private static final float BASE_FRAME_DURATION = 0.08f;

    public static void loadAllAnimations(FalseKnight boss) {
        boss.idleAnim = loadAnimation("animation/boss/Idle.png", 5, BASE_FRAME_DURATION, Animation.PlayMode.LOOP);
        boss.runAnticAnim = loadAnimation("animation/boss/Run Antic.png", 2, BASE_FRAME_DURATION, Animation.PlayMode.NORMAL);
        boss.runAnim = loadAnimation("animation/boss/Run.png", 5, BASE_FRAME_DURATION, Animation.PlayMode.LOOP);
        boss.maceSlamAnticAnim = loadAnimation("animation/boss/Attack Antic.png", 6, BASE_FRAME_DURATION, Animation.PlayMode.NORMAL);
        boss.maceSlamAnim = loadAnimation("animation/boss/Attack.png", 3, BASE_FRAME_DURATION, Animation.PlayMode.NORMAL);
        boss.maceSlamRecoverAnim = loadAnimation("animation/boss/Attack Recover.png", 5, BASE_FRAME_DURATION, Animation.PlayMode.NORMAL);
        boss.jumpAnticAnim = loadAnimation("animation/boss/Jump Antic.png", 3, BASE_FRAME_DURATION, Animation.PlayMode.NORMAL);
        boss.jumpAnim = loadAnimation("animation/boss/Jump.png", 4, BASE_FRAME_DURATION, Animation.PlayMode.LOOP);
        boss.jumpAttackAnim = loadAnimation("animation/boss/Jump Attack.png", 8, BASE_FRAME_DURATION, Animation.PlayMode.NORMAL);
        boss.landAnim = loadAnimation("animation/boss/Land.png", 5, BASE_FRAME_DURATION, Animation.PlayMode.NORMAL);
        boss.stunAnim = loadAnimation("animation/boss/Body.png", 5, 0.1f, Animation.PlayMode.LOOP);
        boss.stunRecoverAnim = loadAnimation("animation/boss/Stun Recover.png", 6, BASE_FRAME_DURATION, Animation.PlayMode.NORMAL);
        boss.deathFallAnim = loadAnimation("animation/boss/DeathFall.png", 3, BASE_FRAME_DURATION, Animation.PlayMode.NORMAL);
        boss.deathHitAnim = loadAnimation("animation/boss/DeathHit.png", 3, BASE_FRAME_DURATION, Animation.PlayMode.NORMAL);
        boss.deathLandAnim = loadAnimation("animation/boss/DeathLand.png", 11, BASE_FRAME_DURATION, Animation.PlayMode.NORMAL);
        boss.turnAnim = loadAnimation("animation/boss/Turn.png", 2, BASE_FRAME_DURATION * 0.5f, Animation.PlayMode.NORMAL);
        boss.shockwaveAnim = loadAnimation("animation/boss/Shockwave.png", 8,  BASE_FRAME_DURATION * 0.5f, Animation.PlayMode.LOOP);
    }
}
