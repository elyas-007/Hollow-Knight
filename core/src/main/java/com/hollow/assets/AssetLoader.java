package com.hollow.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.hollow.HollowKnight;

public class AssetLoader {
    public HollowKnight game;

    // Texture
    public Texture background;
    public TextureRegion pointerR;
    public TextureRegion pointerL;
    public Texture hollowKnightLogo;
    public Texture titleBottom;
    public Texture settingBottom;
    // Audio
    public  Music titleTheme;
    public Sound buttonHover;
    public Sound buttonClick;

    public BitmapFont font;
    public BitmapFont subFont;

    public AssetLoader(HollowKnight hollowKnight) {
        game = hollowKnight;
    }

    public void loadMainMenu() {
        background = new Texture("ui/mainMenu/background.png");
        Texture pointTexture = new Texture("ui/mainMenu/main_menu_pointer_anim0008.png");
        pointerR = new TextureRegion(pointTexture);
        pointerL = new TextureRegion(pointTexture);
        pointerL.flip(true, false);

        hollowKnightLogo = new Texture("ui/mainMenu/logo.png");

        titleBottom = new Texture("ui/mainMenu/titleBottom.png");
        settingBottom = new Texture("ui/mainMenu/settingBottom.png");

        titleTheme = Gdx.audio.newMusic(Gdx.files.internal("audio/bgm/Title.wav"));
        buttonHover = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/button-hover.wav"));
        buttonClick = Gdx.audio.newSound(Gdx.files.internal("audio/sound-effect/button-click.wav"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/primary_font.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();

        parameter.size = 24;
        font = generator.generateFont(parameter);


        parameter.size = 16;
        subFont = generator.generateFont(parameter);

        generator.dispose();
    }

    public void dispose() {
        if (background != null) background.dispose();
        if (hollowKnightLogo != null) hollowKnightLogo.dispose();
        if (pointerR != null) pointerR.getTexture().dispose();
        if (pointerL != null) pointerL.getTexture().dispose();
        if (font != null) font.dispose();
        if (subFont != null) subFont.dispose();
        if (titleTheme != null) titleTheme.dispose();
        if (buttonHover != null) buttonHover.dispose();
        if (buttonClick != null) buttonClick.dispose();
    }



}
