package com.hollow.models.data;

import com.badlogic.gdx.Input;
import com.hollow.models.language.Language;

public class SettingData {
    private float musicVolume = 1f;
    private float sfxVolume = 0.5f;
    private float brightness = 0.6f;
    private boolean isMusicOn = true;
    private boolean isSfxOn = true;
    private Language lang = Language.EN;
    private int keyLeft = Input.Keys.LEFT;
    private int keyRight = Input.Keys.RIGHT;
    private int keyUp = Input.Keys.UP;
    private int keyDown = Input.Keys.DOWN;
    private int keyJump = Input.Keys.Z;
    private int keyDash = Input.Keys.C;
    private int keyAttack =  Input.Keys.X;
    private int keyFocus=  Input.Keys.A;

    public void resetAudio() {
        musicVolume = 1f;
        sfxVolume = 0.5f;
        isMusicOn = true;
        isSfxOn = true;
    }

    public void resetKey() {
        keyLeft = Input.Keys.LEFT;
        keyRight = Input.Keys.RIGHT;
        keyUp = Input.Keys.UP;
        keyDown = Input.Keys.DOWN;
        keyJump = Input.Keys.Z;
        keyDash = Input.Keys.C;
        keyAttack =  Input.Keys.X;
        keyFocus=  Input.Keys.A;
    }

    public void resetBrightness() {
        brightness = 0.6f;
    }
    public float getMusicVolume() {return musicVolume;}
    public void setMusicVolume(float musicVolume) {this.musicVolume = musicVolume;}
    public float getSfxVolume() {return sfxVolume;}
    public void setSfxVolume(float sfxVolume) {this.sfxVolume = sfxVolume;}
    public float getBrightness() {return brightness;}
    public void setBrightness(float brightness) {this.brightness = brightness;}
    public boolean isMusicOn() {return isMusicOn;}
    public void setMusicOn(boolean musicOn) {isMusicOn = musicOn;}
    public boolean isSfxOn() {return isSfxOn;}
    public void setSfxOn(boolean sfxOn) {isSfxOn = sfxOn;}
    public Language getLang() {return lang;}
    public void setLang(Language lang) {this.lang = lang;}
    public int getKeyLeft() {return keyLeft;}
    public void setKeyLeft(int keyLeft) {this.keyLeft = keyLeft;}
    public int getKeyRight() {return keyRight;}
    public void setKeyRight(int keyRight) {this.keyRight = keyRight;}
    public int getKeyUp() {return keyUp;}
    public void setKeyUp(int keyUp) {this.keyUp = keyUp;}
    public int getKeyDown() {return keyDown;}
    public void setKeyDown(int keyDown) {this.keyDown = keyDown;}
    public int getKeyJump() {return keyJump;}
    public void setKeyJump(int keyJump) {this.keyJump = keyJump;}
    public int getKeyDash() {return keyDash;}
    public void setKeyDash(int keyDash) {this.keyDash = keyDash;}
    public int getKeyAttack() {return keyAttack;}
    public void setKeyAttack(int keyAttack) {this.keyAttack = keyAttack;}
    public int getKeyFocus() {return keyFocus;}
    public void setKeyFocus(int keyFocus) {this.keyFocus = keyFocus;}
}
