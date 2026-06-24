package com.hollow.views.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.hollow.HollowKnight;
import com.hollow.assets.AssetLoader;

public class MenuBackground {
    private HollowKnight game;
    private Texture background;
    private Texture particleTexture;
    private Texture lightTexture;
    private Array<DustParticle> particles;

    public MenuBackground(AssetLoader assetLoader, HollowKnight game) {
        this.background = assetLoader.background;
        this.game = game;

        Pixmap lightPixmap = new Pixmap(256, 256, Pixmap.Format.RGBA8888);
        for (int x = 0; x < 256; x++) {
            for (int y = 0; y < 256; y++) {
                float dist = Vector2.dst(128, 128, x, y);
                float alpha = 1f - Math.min(dist / 128f, 1f);
                alpha = alpha * alpha * alpha;

                lightPixmap.setColor(new Color(0.85f, 0.9f, 1f, alpha * 0.35f));
                lightPixmap.drawPixel(x, y);
            }
        }
        lightTexture = new Texture(lightPixmap);
        lightPixmap.dispose();

        Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fillCircle(8, 8, 6);
        particleTexture = new Texture(pixmap);
        pixmap.dispose();

        particles = new Array<>();
        for (int i = 0; i < 100; i++) {
            particles.add(new DustParticle());
        }
    }

    public void updateAndDraw(SpriteBatch batch, float delta, float brightness, boolean drawLight) {
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        if (drawLight) {
            float lightWidth = 1500f;
            float lightHeight = 500f;
            float lightX = (1920f - lightWidth) / 2f;
            float lightY = 1080f - 600f;

            float pulse = 0.85f + 0.15f * MathUtils.sin(particles.get(0).stateTime * 1.5f);
            batch.setColor(brightness, brightness, brightness, pulse);

            batch.draw(lightTexture, lightX, lightY, lightWidth / 2f, lightHeight / 2f,
                lightWidth, lightHeight, 1f, 1f, 15f, 0, 0, 256, 256, false, false);
        }


        for (DustParticle p :  particles) {
            p.update(delta);

            float findAlpha = p.alpha * brightness;
            batch.setColor(1f, 1f, 1f, findAlpha);
            batch.draw(particleTexture, p.x, p.y, p.size, p.size);
        }
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.setColor(Color.WHITE);
    }

    public void dispose() {
        if (particleTexture != null) {
            particleTexture.dispose();
        }
    }

    private class DustParticle {
        float x, y;
        float speedY;
        float wobbleSpeed, wobbleAmount;
        float stateTime;
        float size;
        float maxAlpha;
        float alpha;

        public DustParticle() {
            reset(true);
        }

        public void reset(boolean randomY) {
            this.x = MathUtils.random(0, 1920);
            this.y = randomY ? MathUtils.random(-50, 1080) : MathUtils.random(-50, -10);
            this.size = MathUtils.random(4f, 12f);
            this.speedY = MathUtils.random(10f, 40f);
            this.wobbleSpeed = MathUtils.random(0.5f, 2f);
            this.wobbleAmount = MathUtils.random(10f, 30f);
            this.maxAlpha = MathUtils.random(0.1f, 0.6f);
            this.stateTime = MathUtils.random(0f, 10f);
        }

        public void update(float delta) {
            stateTime += delta;
            y += speedY * delta;

            x += MathUtils.sin(stateTime * wobbleSpeed) * wobbleAmount * delta;

            alpha = maxAlpha * (0.5f + 0.5f * MathUtils.sin(stateTime * 1.5f));

            if (y > 1100) {
                reset(false);
            }
        }
    }
}
