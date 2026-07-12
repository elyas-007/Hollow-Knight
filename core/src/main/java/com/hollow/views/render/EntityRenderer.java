package com.hollow.views.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.hollow.HollowKnight;
import com.hollow.models.Debris;
import com.hollow.models.Effect;
import com.hollow.controllers.engine.Game;
import com.hollow.models.entities.enemy.*;
import com.hollow.models.entities.knight.Charm;
import com.hollow.models.entities.knight.Knight;
import com.hollow.models.entities.knight.Projectile;
import com.hollow.models.entities.knight.WraithEffect;
import com.hollow.models.entities.zote.Zote;
import com.hollow.models.entities.knight.KnightState;

public class EntityRenderer {
    private final HollowKnight game;
    private static final float UNIT_SCALE = 1f / 64f;

    public EntityRenderer(HollowKnight game) {
        this.game = game;
    }

    public void renderKnight(Knight knight) {
        TextureRegion frame = knight.getCurrentFrame();
        if (frame == null) return;

        boolean facingLeft = !knight.isFacingRight();
        float spriteW = knight.getWidth() * 2.9f;
        float spriteH = knight.getHeight() * 2.5f;
        float drawX = knight.getX() - ((spriteW - knight.getWidth()) / 2f);
        float drawY = knight.getY() - 0.15f;

        if (knight.getState() == KnightState.WALL_SLIDE) {
            drawX += facingLeft ? -0.4f : 0.4f;
        }

        Color originalColor = game.batch.getColor().cpy();
        if (knight.isInvincible()) {
            float alpha = 0.5f + 0.5f * (float) Math.sin(knight.stateTimer * 25f);
            game.batch.setColor(1f, 1f, 1f, alpha);
        }

        if (!facingLeft) {
            game.batch.draw(frame, drawX + spriteW, drawY, -spriteW, spriteH);
        } else {
            game.batch.draw(frame, drawX, drawY, spriteW, spriteH);
        }
        game.batch.setColor(originalColor);
    }

    public void renderEnemies(Game controller) {
        if (controller.getEnemies() == null) return;

        for (Enemy enemy : controller.getEnemies()) {
            TextureRegion frame = enemy.getCurrentFrame();
            if (frame == null) continue;

            float w = enemy.hitbox.width;
            float h = enemy.hitbox.height;
            float spriteW = 1.4f, spriteH = 0.9f, offsetY = 0.12f;

            switch (enemy) {
                case Crawlid crawlid -> {
                    spriteW = w * 2.2f;
                    spriteH = h * 2.8f;
                    offsetY = 0.15f;
                }
                case HuskHornhead huskHornhead -> {
                    spriteW = w * 2.2f;
                    spriteH = h * 2.2f;
                    offsetY = 0.3f;
                }
                case Mosquito mosquito -> {
                    spriteW = w * 1.8f;
                    spriteH = h * 1.5f;
                    offsetY = 0.6f;
                }
                case Mosscreep mosscreep -> {
                    spriteW = w * 1.6f;
                    spriteH = h * 1.6f;
                    offsetY = 0.15f;
                }
                case Crystallized crystallized -> {
                    spriteW = w * 1.8f;
                    spriteH = h * 1.6f;
                    offsetY = 0.15f;
                }
                default -> {
                }
            }

            float drawX = enemy.position.x - ((spriteW - w) / 2f);
            float drawY = enemy.position.y - offsetY;

            if (enemy.isFacingRight) game.batch.draw(frame, drawX + spriteW, drawY, -spriteW, spriteH);
            else game.batch.draw(frame, drawX, drawY, spriteW, spriteH);
        }
    }

    public void renderZote(Zote zote) {
        if (zote == null) return;
        TextureRegion frame = zote.getCurrentFrame(Gdx.graphics.getDeltaTime());
        if (frame == null) return;

        float spriteW = zote.hitbox.width * 2.5f;
        float spriteH = zote.hitbox.height * 2.5f;
        float drawX = zote.position.x - ((spriteW - zote.hitbox.width) / 2f);
        float drawY = zote.position.y - 0.4f;

        if (zote.isFacingRight) game.batch.draw(frame, drawX, drawY, spriteW, spriteH);
        else game.batch.draw(frame, drawX + spriteW, drawY, -spriteW, spriteH);
    }

    public void renderEffects(Knight knight) {
        for (Effect effect : knight.activeEffects) {
            TextureRegion frame = effect.getCurrentFrame();
            if (frame == null) continue;

            float drawX = knight.getX() + effect.offsetX;
            float drawY = knight.getY() + effect.offsetY;
            if (effect.isFacingRight) game.batch.draw(frame, drawX, drawY, effect.width, effect.height);
            else game.batch.draw(frame, drawX + effect.width, drawY, -effect.width, effect.height);
        }
    }

    public void renderWorldEffects(Game controller, float delta) {
        for (int i = controller.activeEffects.size - 1; i >= 0; i--) {
            Effect effect = controller.activeEffects.get(i);
            effect.update(delta);
            if (effect.isFinished()) {
                controller.activeEffects.removeIndex(i);
                continue;
            }
            TextureRegion frame = effect.getCurrentFrame();
            if (frame != null) {
                if (effect.isFacingRight) {
                    game.batch.draw(frame, effect.offsetX, effect.offsetY, effect.width, effect.height);
                } else {
                    game.batch.draw(frame, effect.offsetX + effect.width, effect.offsetY,
                        -effect.width, effect.height);
                }
            }
        }

        for (Debris d : controller.activeDebris) {
            float width = d.texture.getRegionWidth() * UNIT_SCALE * d.scale;
            float height = d.texture.getRegionHeight() * UNIT_SCALE * d.scale;
            float alpha = d.lifeTime / d.maxLifeTime;
            Color c = game.batch.getColor();
            game.batch.setColor(c.r, c.g, c.b, alpha);
            game.batch.draw(d.texture, d.position.x - width / 2f, d.position.y - height / 2f,
                width / 2f, height / 2f, width, height, 1f, 1f, d.rotation);
            game.batch.setColor(c.r, c.g, c.b, 1f);
        }
    }

    public void renderInstantLasers(Game controller) {
        if (controller.activeInstantLasers == null) return;
        TextureRegion laserFrame = game.assetLoader.crystalLaserTex;
        for (InstantLaser laser : controller.activeInstantLasers) {
            if (laser.isFacingRight) {
                game.batch.draw(laserFrame, laser.hitbox.x, laser.hitbox.y,
                    laser.hitbox.width, laser.hitbox.height);
            } else {
                game.batch.draw(laserFrame, laser.hitbox.x + laser.hitbox.width, laser.hitbox.y,
                    -laser.hitbox.width, laser.hitbox.height);
            }
        }
    }

    public void renderWraiths(Game controller, Knight knight) {
        if (controller.activeWraiths == null) return;
        for (WraithEffect w : controller.activeWraiths) {
            TextureRegion frame;
            if (w.isShadow && knight.shadowScreamAnim != null) {
                frame = knight.shadowScreamAnim.getKeyFrame(w.stateTime, false);
            } else if (!w.isShadow && knight.soulScreamAnim != null) {
                frame = knight.soulScreamAnim.getKeyFrame(w.stateTime, false);
            } else continue;

            game.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
            game.batch.draw(frame, w.x, w.y, w.width, w.height);
            game.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }
    }

    public void renderProjectiles(Game controller, Knight knight) {
        if (controller.activeProjectiles == null) return;
        for (Projectile p : controller.activeProjectiles) {
            TextureRegion frame;
            if (p.isShadow && knight.shadowBallAnim != null) {
                frame = knight.shadowBallAnim.getKeyFrame(p.stateTime, true);
            } else if (!p.isShadow && knight.soulBallAnim != null) {
                frame = knight.soulBallAnim.getKeyFrame(p.stateTime, true);
            } else continue;

            float drawW = p.hitbox.width * 2f;
            float drawH = p.hitbox.height * 2f;
            float drawX = p.position.x - (drawW - p.hitbox.width) / 2f;
            float drawY = p.position.y - (drawH - p.hitbox.height) / 2f;

            if (p.isFacingRight) game.batch.draw(frame, drawX, drawY, drawW, drawH);
            else game.batch.draw(frame, drawX + drawW, drawY, -drawW, drawH);
        }
    }

    public void renderCollectibles(Game controller, boolean breakableWallsEmpty) {
        if (!game.data.getActiveSlot().getUnlockedCharms().contains(Charm.VOID_HEART, true) &&
            controller.voidHeartPos != null && breakableWallsEmpty) {
            float x = controller.voidHeartPos.x;
            float y = controller.voidHeartPos.y;
            float pulseAlpha = 0.65f + 0.35f * (float)Math.sin(controller.voidHeartStateTime * 5f);

            game.batch.setColor(1f, 1f, 1f, pulseAlpha);
            game.batch.draw(game.assetLoader.charmTextures.get(Charm.VOID_HEART), x - 0.2f, y - 0.2f, 1.4f, 1.4f);
            game.batch.setColor(1f, 1f, 1f, 1f);
            game.batch.draw(game.assetLoader.charmTextures.get(Charm.VOID_HEART), x, y, 1f, 1f);
        }
    }
}
