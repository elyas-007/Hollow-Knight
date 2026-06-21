package com.hollow.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.hollow.models.entities.Enemy.Tiktik;

public class EnemyAnimationLoader {
    public static void loadTiktikAnimations(Tiktik tiktik) {

        tiktik.walkAnim = loadAnimation("animation/enemy/tiktik/Walk.png", 4, 0.1f, Animation.PlayMode.LOOP);

        tiktik.deathAirAnim = loadAnimation("animation/enemy/tiktik/Death Air.png", 4, 0.1f, Animation.PlayMode.NORMAL);

        tiktik.deathLandAnim = loadAnimation("animation/enemy/tiktik/Death Land.png", 3, 0.1f, Animation.PlayMode.NORMAL);

        TextureRegion[] landFrames = tiktik.deathLandAnim.getKeyFrames();
        tiktik.corpseFrame = landFrames[landFrames.length - 1];
    }

    private static Animation<TextureRegion> loadAnimation(String fileName, int frameCount, float frameDuration, Animation.PlayMode playMode) {
        Texture texture = new Texture(Gdx.files.internal(fileName));
        int frameWidth = texture.getWidth() / frameCount;
        int frameHeight = texture.getHeight();

        TextureRegion[][] tmp = TextureRegion.split(texture, frameWidth, frameHeight);

        Animation<TextureRegion> animation = new Animation<>(frameDuration, tmp[0]);
        animation.setPlayMode(playMode);

        return animation;
    }
}
