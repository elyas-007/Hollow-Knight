package com.hollow.models;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.hollow.HollowKnight;
import com.hollow.assets.AudioLoader;

public class AudioManager {
    private static AudioManager instance;
    private static HollowKnight game;
    public final AudioLoader audioLoader;

    private Music currentMusic;
    private Music prevMusic;

    private Music currentMainLayer;
    private Music currentBassLayer;
    private Music currentAtmos;

    private Music prevMainLayer;
    private Music prevBassLayer;
    private Music prevAtmos;

    private boolean isInCombat = false;
    private float currentBassVolume = 0f;

    private boolean isFading = false;
    private float fadeTimer = 0f;
    private final float FADE_DURATION = 1.5f;

    private long footstepSoundId = -1;
    private Sound currentFootstepSound = null;

    private long fallingSoundId = -1;
    private boolean isFallingSoundPlaying = false;

    private long charmLoopId = -1;
    private boolean isCharmLoopPlaying = false;

    private long wallSlideSoundId = -1;
    private boolean isWallSlidePlaying = false;

    private long zoteIdleSoundId = -1;
    private boolean isZoteIdlePlaying = false;

    private long zoteAttackSoundId = -1;
    private boolean isZoteAttackPlaying = false;

    private long focusChargeSoundId = -1;
    private boolean isFocusChargePlaying = false;

    private int lastZoteTalkIndex = -1;

    private AudioManager() {
        audioLoader = new AudioLoader();
        audioLoader.load();
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            throw new RuntimeException("AudioLoader is not initialized! Call init() first.");
        }
        return instance;
    }

    public static void init(HollowKnight hollowGame) {
        if (instance == null) {
            game = hollowGame;
            instance = new AudioManager();
        }
    }

    private boolean isSfxOn() { return game.data.getSettings().isSfxOn(); }
    private boolean isMusicOn() { return game.data.getSettings().isMusicOn(); }
    private float getSfxVol() { return game.data.getSettings().getSfxVolume(); }
    private float getMusicVol() { return game.data.getSettings().getMusicVolume(); }

    public void playSound(Sound sound) {
        if (isSfxOn() && sound != null) {
            sound.play(getSfxVol());
        }
    }

    public void playMusic(Music music, boolean loop, boolean fade) {
        if (music == currentMusic) return;

        if (fade) {
            prevMusic = currentMusic;
            prevAtmos = currentAtmos;
            prevMainLayer = currentMainLayer;
            prevBassLayer = currentBassLayer;

            currentAtmos = null;
            currentMainLayer = null;
            currentBassLayer = null;

            currentMusic = music;
            isFading = true;
            fadeTimer = 0f;

            if (currentMusic != null && isMusicOn()) {
                currentMusic.setLooping(loop);
                currentMusic.setVolume(0f);
                currentMusic.play();
            }
        } else {
            stopMusic();
            currentMusic = music;
            if (currentMusic != null && isMusicOn()) {
                currentMusic.setLooping(loop);
                currentMusic.setVolume(getMusicVol());
                currentMusic.play();
            }
        }
    }

    public void playDynamicMusic(Music atmos, Music main, Music bass, boolean fade) {
        if (main == currentMainLayer) return;

        if (fade) {
            prevMusic = currentMusic;
            prevAtmos = currentAtmos;
            prevMainLayer = currentMainLayer;
            prevBassLayer = currentBassLayer;

            currentMusic = null;
            currentAtmos = atmos;
            currentMainLayer = main;
            currentBassLayer = bass;

            isFading = true;
            fadeTimer = 0f;

            if (isMusicOn()) {
                safePlayInit(currentAtmos, 0f);
                safePlayInit(currentMainLayer, 0f);
                safePlayInit(currentBassLayer, 0f);
            }
        } else {
            stopMusic();
            currentAtmos = atmos;
            currentMainLayer = main;
            currentBassLayer = bass;

            if (isMusicOn()) {
                safePlayInit(currentAtmos, getMusicVol() * 0.8f);
                safePlayInit(currentMainLayer, getMusicVol());
                safePlayInit(currentBassLayer, 0f);
            }
            currentBassVolume = 0f;
        }
    }

    private void safePlayInit(Music music, float initialVolume) {
        if (music != null) {
            music.setLooping(true);
            music.setVolume(initialVolume);
            music.play();
        }
    }

    public void update(float delta) {
        float targetBassVolume = isInCombat ? getMusicVol() : 0f;
        float fadeSpeed = 1.0f;

        if (currentBassVolume != targetBassVolume) {
            if (currentBassVolume < targetBassVolume) {
                currentBassVolume = Math.min(currentBassVolume + (delta * fadeSpeed), targetBassVolume);
            } else {
                currentBassVolume = Math.max(currentBassVolume - (delta * fadeSpeed), targetBassVolume);
            }
        }

        if (isFading) {
            fadeTimer += delta;
            float progress = Math.min(fadeTimer / FADE_DURATION, 1f);
            float inv = 1f - progress;
            float mVol = getMusicVol();

            safeSetVolume(prevMusic, mVol * inv);
            safeSetVolume(prevAtmos, mVol * 0.8f * inv);
            safeSetVolume(prevMainLayer, mVol * inv);
            safeSetVolume(prevBassLayer, currentBassVolume * inv);

            safeSetVolume(currentMusic, mVol * progress);
            safeSetVolume(currentAtmos, mVol * 0.8f * progress);
            safeSetVolume(currentMainLayer, mVol * progress);
            safeSetVolume(currentBassLayer, currentBassVolume * progress);

            if (progress >= 1f) {
                isFading = false;
                safeStop(prevMusic, prevAtmos, prevMainLayer, prevBassLayer);
                prevMusic = prevAtmos = prevMainLayer = prevBassLayer = null;
            }
        } else {
            safeSetVolume(currentBassLayer, currentBassVolume);
        }
    }

    public void stopMusic() {
        isFading = false;
        safeStop(currentMusic, prevMusic, currentAtmos, currentMainLayer, currentBassLayer, prevAtmos, prevMainLayer, prevBassLayer);
        currentMusic = currentAtmos = currentMainLayer = currentBassLayer = null;
        prevMusic = prevAtmos = prevMainLayer = prevBassLayer = null;
    }

    public void pauseMusic() {
        safePause(currentMusic, prevMusic, currentAtmos, currentMainLayer, currentBassLayer, prevAtmos, prevMainLayer, prevBassLayer);
    }

    public void resumeMusic() {
        if (!isMusicOn()) return;

        safePlay(currentMusic, currentAtmos, currentMainLayer, currentBassLayer);
        if (isFading) safePlay(prevMusic, prevAtmos, prevMainLayer, prevBassLayer);
    }

    private void safeStop(Music... musics) {
        for (Music m : musics) if (m != null) m.stop();
    }

    private void safePause(Music... musics) {
        for (Music m : musics) if (m != null && m.isPlaying()) m.pause();
    }

    private void safePlay(Music... musics) {
        for (Music m : musics) if (m != null) m.play();
    }

    private void safeSetVolume(Music music, float volume) {
        if (music != null) music.setVolume(volume);
    }

    public void setCombatState(boolean inCombat) {
        this.isInCombat = inCombat;
    }

    public void updateMusicVolume() {
        if (!isFading) {
            float vol = getMusicVol();
            safeSetVolume(currentMusic, vol);
            safeSetVolume(currentAtmos, vol);
            safeSetVolume(currentMainLayer, vol);

            currentBassVolume = Math.min(currentBassVolume, vol);
            safeSetVolume(currentBassLayer, currentBassVolume);
        }
    }

    public void playFootsteps(boolean isGrass) {
        if (!isSfxOn()) return;

        Sound targetSound = isGrass ? audioLoader.knight_runGrass : audioLoader.knight_runStone;

        if (currentFootstepSound != null && currentFootstepSound != targetSound) {
            stopFootsteps();
        }

        if (footstepSoundId == -1) {
            currentFootstepSound = targetSound;
            if (currentFootstepSound != null) {
                footstepSoundId = currentFootstepSound.loop(0.8f);
            }
        }
    }

    public void stopFootsteps() {
        if (currentFootstepSound != null && footstepSoundId != -1) {
            currentFootstepSound.stop(footstepSoundId);
        }
        footstepSoundId = -1;
        currentFootstepSound = null;
    }

    public void playFallingSound() {
        if (!isSfxOn() || isFallingSoundPlaying) return;
        if (audioLoader.Knight_falling != null) {
            fallingSoundId = audioLoader.Knight_falling.loop(0.8f);
            isFallingSoundPlaying = true;
        }
    }

    public void stopFallingSound() {
        if (isFallingSoundPlaying && audioLoader.Knight_falling != null && fallingSoundId != -1) {
            audioLoader.Knight_falling.stop(fallingSoundId);
        }
        fallingSoundId = -1;
        isFallingSoundPlaying = false;
    }

    public void playCharmProximityLoop() {
        if (!isSfxOn() || isCharmLoopPlaying) return;
        if (audioLoader.knight_pickUpSpell != null) {
            charmLoopId = audioLoader.knight_pickUpSpell.loop(0.6f);
            isCharmLoopPlaying = true;
        }
    }

    public void stopCharmProximityLoop() {
        if (isCharmLoopPlaying && audioLoader.knight_pickUpSpell != null && charmLoopId != -1) {
            audioLoader.knight_pickUpSpell.stop(charmLoopId);
        }
        charmLoopId = -1;
        isCharmLoopPlaying = false;
    }

    public void playWallSlideLoop() {
        if (!isSfxOn() || isWallSlidePlaying) return;
        if (audioLoader.knight_slidingWall != null) {
            wallSlideSoundId = audioLoader.knight_slidingWall.loop(0.7f);
            isWallSlidePlaying = true;
        }
    }

    public void stopWallSlideLoop() {
        if (isWallSlidePlaying && audioLoader.knight_slidingWall != null && wallSlideSoundId != -1) {
            audioLoader.knight_slidingWall.stop(wallSlideSoundId);
        }
        wallSlideSoundId = -1;
        isWallSlidePlaying = false;
    }

    public void playZoteDialogue() {
        if (!isSfxOn()) return;
        if (audioLoader.zoteTalk != null && audioLoader.zoteTalk.length > 0) {
            int index;
            do {
                index = MathUtils.random(0, audioLoader.zoteTalk.length - 1);
            } while (index == lastZoteTalkIndex && audioLoader.zoteTalk.length > 1);

            lastZoteTalkIndex = index;
            if (audioLoader.zoteTalk[index] != null) audioLoader.zoteTalk[index].play(0.9f);
        }
    }

    public void stopZoteIdleLoop() {
        if (isZoteIdlePlaying && audioLoader.zoteIdleLoop != null && zoteIdleSoundId != -1) {
            audioLoader.zoteIdleLoop.stop(zoteIdleSoundId);
        }
        zoteIdleSoundId = -1;
        isZoteIdlePlaying = false;
    }

    public void playZoteAttackLoop() {
        if (!isSfxOn() || isZoteAttackPlaying) return;
        if (audioLoader.zoteAttackLoop != null) {
            zoteAttackSoundId = audioLoader.zoteAttackLoop.loop(0.8f);
            isZoteAttackPlaying = true;
        }
    }

    public void stopZoteAttackLoop() {
        if (isZoteAttackPlaying && audioLoader.zoteAttackLoop != null && zoteAttackSoundId != -1) {
            audioLoader.zoteAttackLoop.stop(zoteAttackSoundId);
        }
        zoteAttackSoundId = -1;
        isZoteAttackPlaying = false;
    }

    public void playFocusChargeLoop() {
        if (!isSfxOn() || isFocusChargePlaying) return;
        if (audioLoader.knight_focusCharge != null) {
            focusChargeSoundId = audioLoader.knight_focusCharge.loop(0.9f);
            isFocusChargePlaying = true;
        }
    }

    public void stopFocusChargeLoop() {
        if (isFocusChargePlaying && audioLoader.knight_focusCharge != null && focusChargeSoundId != -1) {
            audioLoader.knight_focusCharge.stop(focusChargeSoundId);
        }
        focusChargeSoundId = -1;
        isFocusChargePlaying = false;
    }

    public void playFalseKnightShout() {
        if (!isSfxOn()) return;
        if (audioLoader.fk_shouts != null && audioLoader.fk_shouts.length > 0) {
            int index = MathUtils.random(0, audioLoader.fk_shouts.length - 1);
            if (audioLoader.fk_shouts[index] != null) audioLoader.fk_shouts[index].play(0.9f);
        }
    }

    public void playWallHitSound() {
        if (!isSfxOn()) return;
        Sound[] hits = {audioLoader.wall_hit_1, audioLoader.wall_hit_2};
        int index = MathUtils.random(0, 1);
        if (hits[index] != null) hits[index].play(0.8f);
    }

    public void playWallDeathSound() {
        if (!isSfxOn()) return;
        if (audioLoader.wall_death != null) audioLoader.wall_death.play(0.9f);
    }

    public void stopAllSfxLoops() {
        stopFootsteps();
        stopFallingSound();
        stopWallSlideLoop();
        stopCharmProximityLoop();
        stopZoteIdleLoop();
        stopZoteAttackLoop();
        stopFocusChargeLoop();
    }

    public void dispose() {
        stopAllSfxLoops();
        audioLoader.dispose();
    }
}
