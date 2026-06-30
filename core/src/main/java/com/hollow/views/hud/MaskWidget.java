package com.hollow.views.hud;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MaskWidget extends Image {
    public enum MaskState {FULL, SHATTERING, EMPTY, REFILLING, SHINING}


    private MaskState state = MaskState.FULL;
    private float stateTime = 0f;

    private float shineTimer = 0f;
    private float timeToNextShine = MathUtils.random(3f, 8f);

    private final TextureRegion fullMask;
    private final TextureRegion emptyMask;
    private final Animation<TextureRegion> shatterAnim;
    private final Animation<TextureRegion> refillAnim;
    private final Animation<TextureRegion> shineAnim;

    public MaskWidget(TextureRegion fullMask, TextureRegion emptyMask,
                      Animation<TextureRegion> shatterAnim,
                      Animation<TextureRegion> refillAnim,
                      Animation<TextureRegion> shineAnim) {
        super(fullMask);
        this.fullMask = fullMask;
        this.emptyMask = emptyMask;
        this.shatterAnim = shatterAnim;
        this.refillAnim = refillAnim;
        this.shineAnim = shineAnim;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (state == MaskState.SHATTERING) {
            stateTime += delta;
            setDrawable(new TextureRegionDrawable(shatterAnim.getKeyFrame(stateTime, false)));

            if (shatterAnim.isAnimationFinished(stateTime)) {
                state = MaskState.EMPTY;
                setDrawable(new TextureRegionDrawable(emptyMask));
            }
        } else if (state == MaskState.REFILLING) {
            stateTime += delta;
            setDrawable(new TextureRegionDrawable(refillAnim.getKeyFrame(stateTime, false)));

            if (refillAnim.isAnimationFinished(stateTime)) {
                state = MaskState.FULL;
                setDrawable(new TextureRegionDrawable(fullMask));
                resetShineTimer();
            }
        } else if (state == MaskState.SHINING) {
            stateTime += delta;
            setDrawable(new TextureRegionDrawable(shineAnim.getKeyFrame(stateTime, false)));

            if (shineAnim.isAnimationFinished(stateTime)) {
                state = MaskState.FULL;
                setDrawable(new TextureRegionDrawable(fullMask));
                resetShineTimer();
            }
        } else if (state == MaskState.FULL) {
            shineTimer += delta;
            if (shineTimer >= timeToNextShine) {
                state = MaskState.SHINING;
                stateTime = 0f;
            }
        }
    }

    public void shatter() {
        if (state == MaskState.FULL || state == MaskState.SHINING) {
            state = MaskState.SHATTERING;
            stateTime = 0f;
        }
    }

    public void fill() {
        if (state != MaskState.FULL && state != MaskState.REFILLING && state != MaskState.SHINING) {
            state = MaskState.REFILLING;
            stateTime = 0f;
        }
    }

    public MaskState getState() {
        return state;
    }

    private void resetShineTimer() {
        shineTimer = 0f;
        timeToNextShine = MathUtils.random(4f, 10f);
    }

}
