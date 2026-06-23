package com.hollow.views.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.hollow.HollowKnight;
import com.hollow.models.entities.FalseKnightBoss.FalseKnight;

public class BossRenderer {
    private final HollowKnight game;

    private static final float SCALE_MULTIPLIER = 2.5f;

    public BossRenderer(HollowKnight game) {
        this.game = game;
    }

    public void render(FalseKnight boss, float delta) {
        if (boss == null)
            return;

        TextureRegion frame = boss.getCurrentFrame(delta);
        if (frame == null)
            return;

        SpriteBatch batch = game.batch;

        boolean facingLeft = !boss.isFacingRight;

        float x = boss.position.x;
        float y = boss.position.y;
        float hitboxW = boss.hitbox.width;
        float hitboxH = boss.hitbox.height;

        float spriteW = hitboxW * SCALE_MULTIPLIER;
        float spriteH = hitboxH * SCALE_MULTIPLIER;

        float offsetX = (spriteW - hitboxW) / 2f;
        float offsetY = 0.9f;

        float drawX = x - offsetX;
        float drawY = y - offsetY;

        if (!facingLeft) {
            batch.draw(frame, drawX + spriteW, drawY, -spriteW, spriteH);
        } else {
            batch.draw(frame, drawX, drawY, spriteW, spriteH);
        }

        if (boss.shockwaveAnim != null) {
            for (com.hollow.models.entities.FalseKnightBoss.Shockwave wave : boss.activeShockwaves) {
                TextureRegion waveFrame = boss.shockwaveAnim.getKeyFrame(wave.stateTime);

                float waveDrawW = wave.hitbox.width * 2.5f;
                float waveDrawH = wave.hitbox.height * 2.5f;

                if (wave.isFacingRight) {
                    batch.draw(waveFrame, wave.position.x, wave.position.y, waveDrawW, waveDrawH);
                } else {
                    batch.draw(waveFrame, wave.position.x + waveDrawW, wave.position.y, -waveDrawW, waveDrawH);
                }
            }
        }
    }
}
