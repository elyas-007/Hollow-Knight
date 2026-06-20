package com.hollow.views.hud;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MaskWidget extends Image {
    public enum MaskState {FULL, SHATTERING, EMPTY}


    private MaskState state = MaskState.FULL;
    private float stateTime = 0f;

    private final TextureRegion fullMask;
    private final TextureRegion emptyMask;
    private final Animation<TextureRegion> shatterAnim;

    public MaskWidget(TextureRegion fullMask, TextureRegion emptyMask, Animation<TextureRegion> shatterAnim) {
        super(fullMask);
        this.fullMask = fullMask;
        this.emptyMask = emptyMask;
        this.shatterAnim = shatterAnim;
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
        }
    }

    public void shatter() {
        if (state == MaskState.FULL) {
            state = MaskState.SHATTERING;
            stateTime = 0f;
        }
    }

    public void fill() {
        state = MaskState.FULL;
        setDrawable(new TextureRegionDrawable(fullMask));
    }

    public MaskState getState() {
        return state;
    }

}
