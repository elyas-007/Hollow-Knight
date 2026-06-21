package com.hollow.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.hollow.models.entities.Knight.Knight;

public class KnightAnimationLoader {

    public static Animation<TextureRegion> dashEffectAnim;
    public static Animation<TextureRegion> slashEffectAnim;
    public static Animation<TextureRegion> upSlashEffectAnim;
    public static Animation<TextureRegion> downSlashEffectAnim;

    public static void loadAllAnimations(Knight knight) {
        knight.airborneAnim = loadAnimation("animation/knight/Airborne.png", 12, 0.1f, Animation.PlayMode.LOOP);
        knight.dashAnim = loadAnimation("animation/knight/Dash.png", 12, 0.05f, Animation.PlayMode.LOOP);
        knight.deathAnim = loadAnimation("animation/knight/Death.png", 18, 0.1f, Animation.PlayMode.LOOP);
        knight.doubleJumpAnim = loadAnimation("animation/knight/Double Jump.png", 8, 0.08f, Animation.PlayMode.LOOP);
        knight.downSlashAnim = loadAnimation("animation/knight/DownSlash.png", 5, 0.05f, Animation.PlayMode.LOOP);
        knight.focusAnim = loadAnimation("animation/knight/Focus.png", 4, 0.1f, Animation.PlayMode.LOOP);
        knight.landingAnim = loadAnimation("animation/knight/Landing.png", 4, 0.08f, Animation.PlayMode.LOOP);
        knight.lookDownAnim = loadAnimation("animation/knight/LookDown.png", 6, 0.1f, Animation.PlayMode.LOOP);
        knight.lookUpAnim = loadAnimation("animation/knight/LookUp.png", 6, 0.1f, Animation.PlayMode.LOOP);
        knight.runAnim = loadAnimation("animation/knight/Run.png", 13, 0.05f, Animation.PlayMode.LOOP);
        knight.slashAnim = loadAnimation("animation/knight/Slash.png", 5, 0.1f, Animation.PlayMode.LOOP);
        knight.slashAltAnim = loadAnimation("animation/knight/SlashAlt.png", 5, 0.1f, Animation.PlayMode.LOOP);
        knight.upSlashAnim = loadAnimation("animation/knight/UpSlash.png", 5, 0.1f, Animation.PlayMode.LOOP);
        knight.wallSlideAnim = loadAnimation("animation/knight/Wall Slide.png", 4, 0.1f, Animation.PlayMode.LOOP);
        knight.wallJumpAnim = loadAnimation("animation/knight/Walljump.png", 9, 0.1f, Animation.PlayMode.LOOP);
        knight.idleAnim = loadAnimation("animation/knight/Idle Hurt.png", 12, 0.1f, Animation.PlayMode.LOOP);


        knight.dashEffectAnim = loadAnimation("effect/Dash Effect.png", 8, 0.05f, Animation.PlayMode.NORMAL);
        knight.slashEffectAnim = loadAnimation("effect/SlashEffect.png", 5, 0.05f, Animation.PlayMode.NORMAL);
        knight.upSlashEffectAnim = loadAnimation("effect/UpSlashEffect.png", 5, 0.05f, Animation.PlayMode.NORMAL);
        knight.downSlashEffectAnim = loadAnimation("effect/DownSlashEffect.png", 5, 0.05f, Animation.PlayMode.NORMAL);
    }

    public static Animation<TextureRegion> loadAnimation(String fileName, int frameCount, float frameDuration, Animation.PlayMode mode) {
        Texture texture = new Texture(Gdx.files.internal(fileName));
        int frameWidth = texture.getWidth() / frameCount;
        int frameHeight = texture.getHeight();

        TextureRegion[][] tmp = TextureRegion.split(texture, frameWidth, frameHeight);

        Animation<TextureRegion> animation = new Animation<>(frameDuration, tmp[0]);
        animation.setPlayMode(mode);
        return animation;
    }
}
