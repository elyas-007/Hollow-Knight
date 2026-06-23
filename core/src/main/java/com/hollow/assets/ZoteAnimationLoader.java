package com.hollow.assets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.hollow.models.entities.zote.Zote;
import com.hollow.assets.KnightAnimationLoader.*;

import static com.hollow.assets.KnightAnimationLoader.loadAnimation;

public class ZoteAnimationLoader {

    public static void loadZoteAnimations(Zote zote) {
        zote.idleAnim = loadAnimation("animation/zote/Idle.png", 5,0.15f, Animation.PlayMode.LOOP);
        zote.talkAnim = loadAnimation("animation/zote/Talk.png", 5, 0.15f, Animation.PlayMode.LOOP);
        zote.attackAnim = loadAnimation("animation/zote/Attack.png", 4, 0.1f, Animation.PlayMode.LOOP);
        zote.rollAnim = loadAnimation("animation/zote/Roll.png", 3, 0.1f, Animation.PlayMode.LOOP);

        zote.fallAnim = loadAnimation("animation/zote/Fall.png", 5, 0.1f, Animation.PlayMode.NORMAL);
        zote.getUpAnim = loadAnimation("animation/zote/Get Up.png", 4, 0.1f, Animation.PlayMode.NORMAL);
        zote.turnAnim = loadAnimation("animation/zote/Turn.png", 2, 0.1f, Animation.PlayMode.NORMAL);
    }
}
