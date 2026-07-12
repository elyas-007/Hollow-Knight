package com.hollow.models.entities.zote;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.hollow.controllers.manager.LanguageManager;

public class Zote {
    public enum State { IDLE, TALKING, ANGRY, FALLING, GETTING_UP, ROLLING, TURNING, SLEEPING }

    public State currentState;

    public Vector2 position;
    public Rectangle hitbox;
    public Rectangle interactionBox;

    public boolean isFacingRight;
    public boolean hasFinishedIntro = false;
    public boolean pendingDialogue = false;

    public Animation<TextureRegion> idleAnim;
    public Animation<TextureRegion> talkAnim;
    public Animation<TextureRegion> attackAnim;
    public Animation<TextureRegion> fallAnim;
    public Animation<TextureRegion> getUpAnim;
    public Animation<TextureRegion> rollAnim;
    public Animation<TextureRegion> turnAnim;

    private float stateTime = 0f;
    public float angryTimer = 0f;

    public final float ANGRY_DURATION = 3.0f;

    public Zote(float x, float y) {
        position = new Vector2(x, y);

        currentState = hasFinishedIntro ? State.IDLE : State.SLEEPING;

        hitbox = new Rectangle(x, y, 1f, 1.5f);
        interactionBox = new Rectangle(x - 1.5f, y, 4f, 1.5f);
    }

    public void update(float delta) {
        hitbox.setPosition(position.x, position.y);
        interactionBox.setPosition(position.x - 1.5f, position.y);
    }

    public void changeState(State newState) {
        if (currentState == newState) return;

        currentState = newState;
        stateTime = 0f;
    }

    public TextureRegion getCurrentFrame(float delta) {
        stateTime += delta;

        switch (currentState) {
            case SLEEPING -> {
                if (getUpAnim != null) return getUpAnim.getKeyFrame(0, false);
            }
            case TALKING -> {
                if (talkAnim != null) return talkAnim.getKeyFrame(stateTime, true);
            }
            case ANGRY -> {
                if (attackAnim != null) return attackAnim.getKeyFrame(stateTime, true);
            }
            case ROLLING -> {
                if (rollAnim != null) return rollAnim.getKeyFrame(stateTime, true);
            }
            case FALLING -> {
                if (fallAnim != null) {
                    if (fallAnim.isAnimationFinished(stateTime)) changeState(State.GETTING_UP);
                    return fallAnim.getKeyFrame(stateTime, false);
                }
            }
            case GETTING_UP -> {
                if (getUpAnim != null) {
                    if (getUpAnim.isAnimationFinished(stateTime)) changeState(State.IDLE);
                    return getUpAnim.getKeyFrame(stateTime, false);
                }
            }
            case TURNING -> {
                if (turnAnim != null) {
                    if (turnAnim.isAnimationFinished(stateTime)) changeState(State.IDLE);
                    return turnAnim.getKeyFrame(stateTime, false);
                }
            }
            default -> {
                if (idleAnim != null)
                    return idleAnim.getKeyFrame(stateTime, true);
            }
        }
        return null;
    }

    public String[] getDialogue() {
        if (!hasFinishedIntro) {
            hasFinishedIntro = true;
            return new String[]{
                LanguageManager.getInstance().get("zoteIntro1"),
                LanguageManager.getInstance().get("zoteIntro2"),
                LanguageManager.getInstance().get("zoteIntro3"),
                LanguageManager.getInstance().get("zoteIntro4")
            };
        } else {
            int rand = MathUtils.random(1, 10);
            return new String[]{ LanguageManager.getInstance().get("zotePrecept" + rand) };
        }
    }
}
