package com.hollow.models.entities.zote;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

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

    private final String[] introDialogue = {
        "Just what do you think you're doing?! You dare to come between me and my prey?",
        "Is it a habit of yours to scurry about, getting in the way and causing bother?",
        "I am Zote the Mighty, a knight of great renown. Cross me again, and you'll find out why they call my weapon 'Life Ender'.",
        "Now leave me be. I have important work to do."
    };

    private final String[] precepts = {
        "Precept One: 'Always Win Your Battles'. Losing a battle earns you nothing and teaches you nothing. Win your battles, or don't engage in them at all!",
        "Precept Two: 'Never Let Them Laugh at You'. Fools laugh at everything, even at their superiors. But beware, laughter isn't harmless!",
        "Precept Three: 'Always Be Rested'. Fighting and adventuring take their toll on your body. When you rest, your body strengthens and repairs itself.",
        "Precept Four: 'Forget Your Past'. The past is painful, and thinking about your past can only bring you misery. Think about something else instead.",
        "Precept Five: 'Strength Beats Strength'. Is your opponent strong? No matter! Simply overcome their strength with even more strength.",
        "Precept Six: 'Choose Your Own Fate'. Our elders teach that our fate is chosen for us before we are even born. I disagree.",
        "Precept Seven: 'Mourn Not the Dead'. When we die, do things get better for us or worse? There's no way to tell, so we shouldn't bother mourning.",
        "Precept Eight: 'Travel Alone'. You can rely on nobody, and nobody will always be loyal. Therefore, nobody should be your constant companion.",
        "Precept Nine: 'Keep Your Home Tidy'. Your home is where you keep your most prized possession - yourself. Therefore, you should make an effort to keep it nice and clean.",
        "Precept Ten: 'Keep Your Weapon Sharp'. I make sure that my weapon, 'Life Ender', is kept well-sharpened at all times. This makes it much easier to cut things."
    };


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
        if (currentState == newState)
            return;

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
            return introDialogue;
        } else {
            int rand = MathUtils.random(0, precepts.length - 1);
            return new String[]{precepts[rand]};
        }
    }
}
