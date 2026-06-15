package com.hollow.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.hollow.models.entities.Knight.Knight;

public class KnightAnimationLoader {

    public Animation<TextureRegion> idleAnim;

    public static void loadAllAnimations(Knight knight) {
        knight.airborneAnim = loadAnimation("animation/Airborne.png", 12, 0.1f);
        knight.dashAnim = loadAnimation("animation/Dash.png", 12, 0.05f);
        knight.deathAnim = loadAnimation("animation/Death.png", 18, 0.1f);
        knight.doubleJumpAnim = loadAnimation("animation/Double Jump.png", 8, 0.08f);
        knight.downSlashAnim = loadAnimation("animation/DownSlash.png", 5, 0.05f);
        knight.focusAnim = loadAnimation("animation/Focus.png", 4, 0.1f);
//        knight.hurtAnim = loadAnimation("animation/Idle Hurt.png", 12, 0.1f);
        knight.landingAnim = loadAnimation("animation/Landing.png", 4, 0.08f);
        knight.lookDownAnim = loadAnimation("animation/LookDown.png", 6, 0.1f);
        knight.lookUpAnim = loadAnimation("animation/LookUp.png", 6, 0.1f);
        knight.runAnim = loadAnimation("animation/Run.png", 13, 0.2f);
        knight.slashAnim = loadAnimation("animation/Slash.png", 5, 0.1f);
        knight.slashAltAnim = loadAnimation("animation/SlashAlt.png", 5, 0.1f);
        knight.upSlashAnim = loadAnimation("animation/UpSlash.png", 5, 0.1f);
        knight.wallSlideAnim = loadAnimation("animation/Wall Slide.png", 4, 0.1f);
        knight.wallJumpAnim = loadAnimation("animation/Walljump.png", 9, 0.1f);
        knight.idleAnim = loadAnimation("animation/Idle Hurt.png", 12, 0.1f);
    }

    private static Animation<TextureRegion> loadAnimation(String fileName, int frameCount, float frameDuration) {
        Texture texture = new Texture(Gdx.files.internal(fileName));

        int frameWidth = texture.getWidth() / frameCount;
        int frameHeight = texture.getHeight();

        TextureRegion[][] tmp = TextureRegion.split(texture, frameWidth, frameHeight);
        Array<TextureRegion> frames = new Array<>(frameCount);

        for (int i = 0; i < frameCount; i++) {
            frames.add(tmp[0][i]);
        }

        return new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
    }
}
