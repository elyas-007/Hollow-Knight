package com.hollow.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
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
import com.hollow.models.entities.Knight.Charm;

import java.util.HashMap;

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

    public TextureRegion healthFrameHud;
    public TextureRegion geoHudGame;
    public TextureRegion fullMask;
    public TextureRegion emptyMask;
    public Animation<TextureRegion> maskShatterAnim;
    public TextureRegion soul;

    public Texture topOrnament;
    public Texture bottomOrnament;

    public Texture emptyNotch;
    public Texture fullNotch;
    public Texture charm_place;
    public HashMap<Charm, Texture> charmTextures;
    public TextureRegion overScreen_Top_Left;
    public TextureRegion overScreen_Top_Right;
    public TextureRegion overScreen_Bottom_Left;
    public TextureRegion overScreen_Bottom_Right;
    public TextureRegion charm_selector_Top_Left;
    public TextureRegion charm_selector_Top_Right;
    public TextureRegion charm_selector_Bottom_Left;
    public TextureRegion charm_selector_Bottom_Right;
    public Texture inventory_divider;
    public Texture inventory_top;
    public Texture inventory_bottom;

    public Texture top_menu;

    public TextureRegion crystalLaserTex;

    public Texture achievementCompleteTex;
    public Texture achievementEndingATex;
    public Texture achievementFalseKnightTex;
    public Texture achievementHunterTex;
    public Texture achievementSecretTex;
    public Texture achievementSpeedrunTex;
    public Texture achievementFrameTex;



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

        healthFrameHud = new TextureRegion(new Texture("ui/hud/HUD Cln_167.png"));
        geoHudGame = new TextureRegion(new Texture("ui/hud/HUD Cln_089.png"));
        fullMask = new TextureRegion(new Texture("ui/hud/FilledHealthShine_004.png"));
        emptyMask = new TextureRegion(new Texture("ui/hud/EmptyHealth.png"));
        maskShatterAnim = KnightAnimationLoader.loadAnimation("ui/hud/BreakHealth.png", 6, 0.1f, Animation.PlayMode.LOOP);
        soul = new TextureRegion(new Texture("ui/hud/SoulOrb_Full.png"));

        topOrnament = new Texture("ui/hud/gg_board_UI_top_0004.png");
        bottomOrnament = new Texture("ui/hud/gg_board_UI_bottom_0003.png");

        emptyNotch = new Texture("ui/inventory and charms/charm_cost.png");
        fullNotch = new Texture("ui/inventory and charms/charm_UI__0000_charm_cost_02_lit.png");
        charm_place = new Texture("ui/inventory and charms/charm_backboard.png");
        Texture corner = new Texture("ui/inventory and charms/overscan_corner_fleur009.png");
        overScreen_Top_Left = new TextureRegion(corner);
        overScreen_Top_Right = new TextureRegion(corner);
        overScreen_Bottom_Left = new TextureRegion(corner);
        overScreen_Bottom_Right = new TextureRegion(corner);
        overScreen_Top_Right.flip(true, false);
        overScreen_Bottom_Left.flip(false, true);
        overScreen_Bottom_Right.flip(true, true);
        Texture charm_selector = new Texture("ui/inventory and charms/Inv_0014_selection_cursor.png");
        charm_selector_Top_Left = new TextureRegion(charm_selector);
        charm_selector_Top_Right = new TextureRegion(charm_selector);
        charm_selector_Bottom_Left = new TextureRegion(charm_selector);
        charm_selector_Bottom_Right = new TextureRegion(charm_selector);
        charm_selector_Top_Right.flip(true, false);
        charm_selector_Bottom_Right.flip(true, true);
        charm_selector_Bottom_Left.flip(false, true);
        inventory_divider = new Texture("ui/inventory and charms/Inv_0017_divider.png");
        inventory_top =  new Texture("ui/inventory and charms/game_over_fleur.png");
        inventory_bottom = new Texture("ui/inventory and charms/bottom_fleur0003.png");
        charmTextures = new HashMap<>();
        charmTextures.put(Charm.SOUL_CATCHER, new Texture("ui/inventory and charms/Soul Catcher - _0001_charm_more_soul.png"));
        charmTextures.put(Charm.DASH_MASTER, new Texture("ui/inventory and charms/Dashmaster - _0011_charm_generic_03.png"));
        charmTextures.put(Charm.UNBREAKABLE_STRENGTH, new Texture("ui/inventory and charms/Unbreakable Strength_0002_charm_glass_attack_up_full.png"));
        charmTextures.put(Charm.QUICK_SLASH, new Texture("ui/inventory and charms/Quick Slash - _0003_charm_nail_slash_speed_up.png"));
        charmTextures.put(Charm.QUICK_FOCUS, new Texture("ui/inventory and charms/Quick Focus - _0005_charm_fast_focus.png"));
        charmTextures.put(Charm.HEAVY_BLOW, new Texture("ui/inventory and charms/Heavy Blow - _0008_charm_nail_damage_up.png"));
        charmTextures.put(Charm.SHARP_SHADOW, new Texture("ui/inventory and charms/Sharp Shadow - charm_shade_impact.png"));
        charmTextures.put(Charm.VOID_HEART, new Texture("ui/inventory and charms/Void Heart - charm_black.png"));

        top_menu = new Texture("ui/hud/prompt_divider.png");

        crystalLaserTex = new TextureRegion(new Texture("animation/enemy/crystallized/CrystalLaser.png"));

        //achievements

        achievementCompleteTex = new Texture("ui/achievement/achievement__0000_100_complete.png");
        achievementEndingATex = new Texture("ui/achievement/achievement__0006_ending_A.png");
        achievementFalseKnightTex = new Texture("ui/achievement/achievement_false_knight #50302521.png");
        achievementHunterTex = new Texture("ui/achievement/achievement_Hunter_Marks.png");
        achievementSecretTex = new Texture("ui/achievement/achievement_secret.png");
        achievementSpeedrunTex = new Texture("ui/achievement/achievement_ultra_fast_finish.png");
        achievementFrameTex = new Texture("ui/achievement/achievement_fleur0005.png");
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

        if (emptyNotch != null) emptyNotch.dispose();
        if (fullNotch != null) fullNotch.dispose();
        if (charm_place != null) charm_place.dispose();
        if (charmTextures != null) {
            for (Texture t : charmTextures.values()) t.dispose();
        }
    }



}
