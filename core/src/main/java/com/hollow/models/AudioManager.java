package com.hollow.models;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.hollow.HollowKnight;
import com.hollow.assets.AudioLoader;

public class AudioManager {
    private final HollowKnight game;
    public AudioLoader audioLoader;

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

    public AudioManager(HollowKnight game) {
        this.game = game;
        this.audioLoader = new AudioLoader();
        this.audioLoader.load();
    }

    public void playSound(Sound sound) {
        if (game.settings.isSfxOn && sound != null) {
            sound.play();
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

            if (currentMusic != null && game.settings.isMusicOn) {
                currentMusic.setLooping(loop);
                currentMusic.setVolume(0f);
                currentMusic.play();
            }
        } else {
            stopMusic();
            currentMusic = music;
            if (currentMusic != null && game.settings.isMusicOn) {
                currentMusic.setLooping(loop);
                currentMusic.setVolume(game.settings.musicVolume);
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

            if (game.settings.isMusicOn) {
                if (currentAtmos != null) { currentAtmos.setLooping(true); currentAtmos.setVolume(0f); currentAtmos.play(); }
                if (currentMainLayer != null) { currentMainLayer.setLooping(true); currentMainLayer.setVolume(0f); currentMainLayer.play(); }
                if (currentBassLayer != null) { currentBassLayer.setLooping(true); currentBassLayer.setVolume(0f); currentBassLayer.play(); }
            }
        } else {
            stopMusic();
            currentAtmos = atmos;
            currentMainLayer = main;
            currentBassLayer = bass;

            if (game.settings.isMusicOn) {
                if (currentAtmos != null) { currentAtmos.setLooping(true); currentAtmos.setVolume(game.settings.musicVolume * 0.8f); currentAtmos.play(); }
                if (currentMainLayer != null) { currentMainLayer.setLooping(true); currentMainLayer.setVolume(game.settings.musicVolume); currentMainLayer.play(); }
                if (currentBassLayer != null) { currentBassLayer.setLooping(true); currentBassLayer.setVolume(0f); currentBassLayer.play(); }
            }
            currentBassVolume = 0f;
        }
    }

    public void update(float delta) {
        float targetBassVolume = isInCombat ? game.settings.musicVolume : 0f;
        float fadeSpeed = 1.0f;

        if (currentBassVolume < targetBassVolume) {
            currentBassVolume += delta * fadeSpeed;
            if (currentBassVolume > targetBassVolume) currentBassVolume = targetBassVolume;
        } else if (currentBassVolume > targetBassVolume) {
            currentBassVolume -= delta * fadeSpeed;
            if (currentBassVolume < targetBassVolume) currentBassVolume = targetBassVolume;
        }

        if (isFading) {
            fadeTimer += delta;
            float progress = Math.min(fadeTimer / FADE_DURATION, 1f);
            float inv = 1f - progress;

            if (prevMusic != null) prevMusic.setVolume(game.settings.musicVolume * inv);
            if (prevAtmos != null) prevAtmos.setVolume(game.settings.musicVolume * 0.8f * inv);
            if (prevMainLayer != null) prevMainLayer.setVolume(game.settings.musicVolume * inv);
            if (prevBassLayer != null) prevBassLayer.setVolume(currentBassVolume * inv);

            if (currentMusic != null) currentMusic.setVolume(game.settings.musicVolume * progress);
            if (currentAtmos != null) currentAtmos.setVolume(game.settings.musicVolume * 0.8f * progress);
            if (currentMainLayer != null) currentMainLayer.setVolume(game.settings.musicVolume * progress);
            if (currentBassLayer != null) currentBassLayer.setVolume(currentBassVolume * progress);

            if (progress >= 1f) {
                isFading = false;
                if (prevMusic != null) { prevMusic.stop(); prevMusic = null; }
                if (prevAtmos != null) { prevAtmos.stop(); prevAtmos = null; }
                if (prevMainLayer != null) { prevMainLayer.stop(); prevMainLayer = null; }
                if (prevBassLayer != null) { prevBassLayer.stop(); prevBassLayer = null; }
            }
        } else {
            if (currentBassLayer != null) {
                currentBassLayer.setVolume(currentBassVolume);
            }
        }
    }

    public void stopMusic() {
        isFading = false;
        if (currentMusic != null) { currentMusic.stop(); currentMusic = null; }
        if (prevMusic != null) { prevMusic.stop(); prevMusic = null; }

        if (currentAtmos != null) { currentAtmos.stop(); currentAtmos = null; }
        if (currentMainLayer != null) { currentMainLayer.stop(); currentMainLayer = null; }
        if (currentBassLayer != null) { currentBassLayer.stop(); currentBassLayer = null; }

        if (prevAtmos != null) { prevAtmos.stop(); prevAtmos = null; }
        if (prevMainLayer != null) { prevMainLayer.stop(); prevMainLayer = null; }
        if (prevBassLayer != null) { prevBassLayer.stop(); prevBassLayer = null; }
    }

    public void pauseMusic() {
        if (currentMusic != null && currentMusic.isPlaying()) currentMusic.pause();
        if (prevMusic != null && prevMusic.isPlaying()) prevMusic.pause();

        if (currentAtmos != null && currentAtmos.isPlaying()) currentAtmos.pause();
        if (currentMainLayer != null && currentMainLayer.isPlaying()) currentMainLayer.pause();
        if (currentBassLayer != null && currentBassLayer.isPlaying()) currentBassLayer.pause();

        if (prevAtmos != null && prevAtmos.isPlaying()) prevAtmos.pause();
        if (prevMainLayer != null && prevMainLayer.isPlaying()) prevMainLayer.pause();
        if (prevBassLayer != null && prevBassLayer.isPlaying()) prevBassLayer.pause();
    }

    public void resumeMusic() {
        if (!game.settings.isMusicOn) return;

        if (currentMusic != null) currentMusic.play();
        if (isFading && prevMusic != null) prevMusic.play();

        if (currentAtmos != null) currentAtmos.play();
        if (currentMainLayer != null) currentMainLayer.play();
        if (currentBassLayer != null) currentBassLayer.play();

        if (isFading) {
            if (prevAtmos != null) prevAtmos.play();
            if (prevMainLayer != null) prevMainLayer.play();
            if (prevBassLayer != null) prevBassLayer.play();
        }
    }

    public void setCombatState(boolean inCombat) {
        this.isInCombat = inCombat;
    }

    public void updateMusicVolume() {
        if (!isFading) {
            if (currentMusic != null) currentMusic.setVolume(game.settings.musicVolume);
            if (currentAtmos != null) currentAtmos.setVolume(game.settings.musicVolume * 0.8f);
            if (currentMainLayer != null) currentMainLayer.setVolume(game.settings.musicVolume);
            if (currentBassLayer != null) {
                currentBassVolume = Math.min(currentBassVolume, game.settings.musicVolume);
                currentBassLayer.setVolume(currentBassVolume);
            }
        }
    }

    public void playFootsteps(boolean isGrass) {
        if (!game.settings.isSfxOn) return;

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
        if (!game.settings.isSfxOn || isFallingSoundPlaying) return;

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
        if (!game.settings.isSfxOn || isCharmLoopPlaying) return;

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
        if (!game.settings.isSfxOn || isWallSlidePlaying) return;

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
        if (!game.settings.isSfxOn) return;
        if (audioLoader.zoteTalk != null && audioLoader.zoteTalk.length > 0) {
            int index;
            do {
                index = MathUtils.random(0, audioLoader.zoteTalk.length - 1);
            } while (index == lastZoteTalkIndex && audioLoader.zoteTalk.length > 1);

            lastZoteTalkIndex = index;

            if (audioLoader.zoteTalk[index] != null) {
                audioLoader.zoteTalk[index].play(0.9f);
            }
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
        if (!game.settings.isSfxOn || isZoteAttackPlaying) return;
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
        if (!game.settings.isSfxOn || isFocusChargePlaying) return;
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
        if (!game.settings.isSfxOn) return;
        if (audioLoader.fk_shouts != null && audioLoader.fk_shouts.length > 0) {
            int index = com.badlogic.gdx.math.MathUtils.random(0, audioLoader.fk_shouts.length - 1);
            if (audioLoader.fk_shouts[index] != null) {
                audioLoader.fk_shouts[index].play(0.9f);
            }
        }
    }

    public void playWallHitSound() {
        if (!game.settings.isSfxOn) return;
        Sound[] hits = {audioLoader.wall_hit_1, audioLoader.wall_hit_2};
        int index = MathUtils.random(0, 1);
        if (hits[index] != null) {
            hits[index].play(0.8f);
        }
    }

    public void playWallDeathSound() {
        if (!game.settings.isSfxOn) return;
        if (audioLoader.wall_death != null) {
            audioLoader.wall_death.play(0.9f);
        }
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
