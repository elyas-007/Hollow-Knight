package com.hollow.views.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.hollow.models.entities.Knight.Knight;

public class SoulVessel extends Actor {
    private final TextureRegion frame;
    private final Animation<TextureRegion> liquidAnim;
    private final Texture orbMaskTexture;
    private final ShaderProgram shader;

    private float targetSoul = 0f;
    private float displaySoul = 0f;
    private final float maxSoul = 99f;
    private float currentGlow = 0f;

    private float stateTime = 0f;

    public SoulVessel(TextureRegion frame, Animation<TextureRegion> liquidAnim, Texture orbMaskTexture, ShaderProgram shader) {
        this.frame = frame;
        this.liquidAnim = liquidAnim;
        this.orbMaskTexture = orbMaskTexture;
        this.shader = shader;

        setSize(130, 130);
    }

    public void setSoul(int currentSoul) {
        this.targetSoul = currentSoul;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        displaySoul = MathUtils.lerp(displaySoul, targetSoul, 5 * delta);
        stateTime += delta;

        float percent = displaySoul / maxSoul;
        if (percent >= 0.99f) {
            currentGlow = MathUtils.lerp(currentGlow, 1f, 10 * delta);
        } else {
            currentGlow = MathUtils.lerp(currentGlow, 0f, 15 * delta);
        }
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        float percent = displaySoul / maxSoul;
        percent = MathUtils.clamp(percent, 0f, 1f);
        batch.draw(frame, getX(), getY(), getWidth(), getHeight());

        batch.flush();
        batch.setShader(shader);

        orbMaskTexture.bind(1);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

        TextureRegion currentFrame = liquidAnim.getKeyFrame(stateTime, true);

        shader.setUniformi("u_mask", 1);
        shader.setUniformf("u_soulPercentage", percent);
        shader.setUniformf("u_glowWeight", currentGlow);
        shader.setUniformf("u_minU", currentFrame.getU());
        shader.setUniformf("u_maxU", currentFrame.getU2());
        shader.setUniformf("u_minV", currentFrame.getV());
        shader.setUniformf("u_maxV", currentFrame.getV2());

        batch.draw(currentFrame, getX(), getY(), getWidth(), getHeight());

        batch.flush();
        batch.setShader(null);
    }
}
