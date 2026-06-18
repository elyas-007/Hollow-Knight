package com.hollow.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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

    public NinePatch saveBackground_greenPath;
    public NinePatch saveBackground_forgotten;
    public Texture under_SelectProfile;
    public Texture geoHud;
    public Texture healthFrame;
    public Texture mask;
    public Texture profile_fleur;
    public Texture profilePointer;
    public Skin sliderSkin;
    public Skin scrollerSkin;

    public Texture pauseTop;
    public Texture pauseBottom;



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





        saveBackground_greenPath = new NinePatch(new Texture("ui/startGameMenu/Area_Green_Path.png"), 10, 10, 10, 10);
        saveBackground_forgotten = new NinePatch(new Texture("ui/startGameMenu/Area_Forgotten Crossroads.png"), 10, 10, 10, 10);
        under_SelectProfile = new Texture("ui/startGameMenu/Warning_Fleur0008.png");;
        geoHud = new Texture("ui/startGameMenu/select_game_HUD_coin_v020004.png");
        healthFrame = new Texture("ui/startGameMenu/select_game_HUD_0002_health_frame.png");
        mask = new Texture("ui/startGameMenu/select_game_HUD_0001_health.png");
        profile_fleur = new Texture("ui/startGameMenu/profile_fleur0012.png");
        profilePointer = new Texture("ui/startGameMenu/main_menu_pointer_anim0010.png");


        pauseTop = new Texture("ui/pauseMenu/pause_top_fleur0006.png");
        pauseBottom = new Texture("ui/pauseMenu/bottom_fleur0007.png");

        scrollerSkin = new Skin(Gdx.files.internal("ui/VerticalScroller/VerticalScroller.json"));
        sliderSkin = new Skin(Gdx.files.internal("ui/Slider/slider.json"));

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
