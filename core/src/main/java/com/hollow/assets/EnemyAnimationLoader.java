package com.hollow.assets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.hollow.models.entities.Enemy.*;

import static com.hollow.assets.KnightAnimationLoader.loadAnimation;

public class EnemyAnimationLoader {
    public static void loadTiktikAnimations(Tiktik tiktik) {

        tiktik.walkAnim = loadAnimation("animation/enemy/tiktik/Walk.png", 4, 0.1f, Animation.PlayMode.LOOP);

        tiktik.deathAirAnim = loadAnimation("animation/enemy/tiktik/Death Air.png", 4, 0.1f, Animation.PlayMode.NORMAL);

        tiktik.deathLandAnim = loadAnimation("animation/enemy/tiktik/Death Land.png", 3, 0.1f, Animation.PlayMode.NORMAL);
        tiktik.turnAnim = loadAnimation("animation/enemy/tiktik/Turn.png", 2, 0.1f, Animation.PlayMode.NORMAL);
        TextureRegion[] landFrames = tiktik.deathLandAnim.getKeyFrames();
        tiktik.corpseFrame = landFrames[landFrames.length - 1];
    }


    public static void loadCrawlidAnimations(Crawlid crawlid) {
        crawlid.walkAnim = loadAnimation("animation/enemy/crawlid/Walk.png", 4, 0.15f, Animation.PlayMode.LOOP);
        crawlid.turnAnim = loadAnimation("animation/enemy/crawlid/Turn.png", 2, 0.1f, Animation.PlayMode.NORMAL);
        crawlid.deathAirAnim = loadAnimation("animation/enemy/crawlid/Death Air.png", 3, 0.1f, Animation.PlayMode.NORMAL);
        crawlid.deathLandAnim = loadAnimation("animation/enemy/crawlid/Death Land.png", 2, 0.1f, Animation.PlayMode.NORMAL);
        crawlid.corpseFrame = crawlid.deathLandAnim.getKeyFrames()[1];
    }

    public static void loadHuskHornheadAnimations(HuskHornhead husk) {
        husk.idleAnim = loadAnimation("animation/enemy/husk_hornhead/Idle.png", 6, 0.15f, Animation.PlayMode.LOOP);
        husk.walkAnim = loadAnimation("animation/enemy/husk_hornhead/Walk.png", 7, 0.12f, Animation.PlayMode.LOOP);
        husk.turnAnim = loadAnimation("animation/enemy/husk_hornhead/Turn.png", 2, 0.08f, Animation.PlayMode.NORMAL);
        husk.attackAnticipateAnim = loadAnimation("animation/enemy/husk_hornhead/Attack Anticipate.png", 5, 0.1f, Animation.PlayMode.NORMAL);
        husk.attackLungeAnim = loadAnimation("animation/enemy/husk_hornhead/Attack Lunge.png", 12, 0.05f, Animation.PlayMode.NORMAL);
        husk.deathLandAnim = loadAnimation("animation/enemy/husk_hornhead/Death Land.png", 8, 0.1f, Animation.PlayMode.NORMAL);

        husk.corpseFrame = husk.deathLandAnim.getKeyFrames()[7];
    }

    public static void loadMosquitoAnimations(Mosquito mosquito) {
        mosquito.idleAnim = loadAnimation("animation/enemy/mosquito/Idle.png", 8, 0.1f, Animation.PlayMode.LOOP);
        mosquito.attackAnticipateAnim = loadAnimation("animation/enemy/mosquito/Attack Anticipate.png", 6, 0.1f, Animation.PlayMode.NORMAL);
        mosquito.attackLungeAnim = loadAnimation("animation/enemy/mosquito/Attack.png", 3, 0.05f, Animation.PlayMode.LOOP);
        mosquito.turnAnim = loadAnimation("animation/enemy/mosquito/Turn.png", 2, 0.05f, Animation.PlayMode.NORMAL);
        mosquito.deathAirAnim = loadAnimation("animation/enemy/mosquito/Death Air.png", 3, 0.1f, Animation.PlayMode.NORMAL);
        mosquito.deathLandAnim = loadAnimation("animation/enemy/mosquito/Death Land.png", 2, 0.1f, Animation.PlayMode.NORMAL);
        mosquito.corpseFrame = mosquito.deathLandAnim.getKeyFrames()[1];
    }

    public static void loadMosscreepAnimations(Mosscreep mosscreep) {
        mosscreep.walkAnim = loadAnimation("animation/enemy/mosscreep/Walk.png", 3, 0.15f, Animation.PlayMode.LOOP);
        mosscreep.turnAnim = loadAnimation("animation/enemy/mosscreep/Turn.png", 3, 0.1f, Animation.PlayMode.NORMAL);
        mosscreep.deathAirAnim = loadAnimation("animation/enemy/mosscreep/Death Air.png", 4, 0.1f, Animation.PlayMode.NORMAL);
        mosscreep.deathLandAnim = loadAnimation("animation/enemy/mosscreep/Death Land.png", 2, 0.1f, Animation.PlayMode.NORMAL);

        mosscreep.corpseFrame = mosscreep.deathLandAnim.getKeyFrames()[1];
    }
}
