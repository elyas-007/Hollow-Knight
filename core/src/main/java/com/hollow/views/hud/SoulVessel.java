package com.hollow.views.hud;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.hollow.models.entities.Knight.Knight;

public class SoulVessel extends Actor {
    private final TextureRegion frame;
    private final TextureRegion liquid;

    private float targetSoul = 0f;
    private float displaySoul = 0f;
    private final float maxSoul = 99f;

    public SoulVessel(TextureRegion frame, TextureRegion liquid) {
        this.frame = frame;
        this.liquid = liquid;

        setSize(130, 130);
    }

    public void setSoul(int currentSoul) {
        this.targetSoul = currentSoul;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        displaySoul = MathUtils.lerp(displaySoul, targetSoul, 5 * delta);
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        float percent = displaySoul / maxSoul;

        if (percent < 0) percent = 0;
        if (percent > 1) percent = 1;


        if (percent > 0) {
            TextureRegion temp = new TextureRegion(liquid);
            int cropHeight = (int) (liquid.getRegionHeight() * percent);

            temp.setRegionY(liquid.getRegionY() + liquid.getRegionHeight() - cropHeight);
            temp.setRegionHeight(cropHeight);

            batch.draw(temp, getX(), getY(), getWidth(), getHeight() * percent);
        }

        batch.draw(frame, getX(), getY(), getWidth(), getHeight());
    }
}
