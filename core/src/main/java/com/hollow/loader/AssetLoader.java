package com.hollow.loader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.hollow.HollowKnight;
import com.hollow.models.entities.knight.Charm;

import java.util.HashMap;

public class AssetLoader {
    private static HollowKnight game;
    private static AssetLoader instance;

    public Texture background, infection, blue, vheart_beam, hollowKnightLogo, titleBottom;
    public Texture settingBottom, brightness, changeBgIcon, under_SelectProfile, geoHud;
    public Texture healthFrame, mask, profile_fleur, profilePointer;
    public Texture pauseTop, pauseBottom, orbMaskTexture, topOrnament, bottomOrnament;
    public Texture emptyNotch, fullNotch, charm_place, inventory_divider, inventory_top;
    public Texture inventory_bottom, top_menu, achievementCompleteTex, achievementEndingATex;
    public Texture achievementFalseKnightTex, achievementHunterTex, achievementSecretTex;
    public Texture achievementSpeedrunTex, achievementFrameTex, butterflyTexture;

    public TextureRegion pointerR, pointerL, healthFrameHud, fullMask, emptyMask;
    public TextureRegion overScreen_Top_Left, overScreen_Top_Right;
    public TextureRegion overScreen_Bottom_Left, overScreen_Bottom_Right;
    public TextureRegion charm_selector_Top_Left, charm_selector_Top_Right;
    public TextureRegion charm_selector_Bottom_Left, charm_selector_Bottom_Right;
    public TextureRegion crystalLaserTex, rockTexture;

    public NinePatch saveBackground_greenPath, saveBackground_forgotten;
    public Skin sliderSkin;
    public Animation<TextureRegion> maskShatterAnim, maskRefillAnim, maskShineAnim;
    public Animation<TextureRegion> soulIdleAnim, butterflyAnim;
    public ShaderProgram liquidShader;
    private TextureAtlas soulOrbAtlas;
    public HashMap<Charm, Texture> charmTextures;
    public BitmapFont font, subFont;

    private AssetLoader() {}

    public static AssetLoader  getInstance() {
        if (instance == null) {
            throw new RuntimeException("AssetLoader instance is null! Please init() first!");
        }
        return instance;
    }

    public static void init(HollowKnight hollowGame) {
        if (instance == null) {
            game = hollowGame;
        }
        instance = new AssetLoader();
    }

    public void loadAssets() {
        loadMenuAssets();
        loadEnvironmentAssets();
        loadFonts();
        loadSelectGameAssets();
        loadHudAssets();
        loadInventoryAssets();
        loadAchievementAssets();
        loadOtherAssets();
    }

    private void loadMenuAssets() {
        Texture pointTexture = new Texture("ui/mainMenu/main_menu_pointer_anim0008.png");
        pointerR = new TextureRegion(pointTexture);
        pointerL = new TextureRegion(pointTexture);
        pointerL.flip(true, false);
        hollowKnightLogo = new Texture("ui/mainMenu/logo.png");
        titleBottom = new Texture("ui/mainMenu/titleBottom.png");
        settingBottom = new Texture("ui/mainMenu/settingBottom.png");
        brightness = new Texture("ui/mainMenu/brightness_image.png");
        changeBgIcon = new Texture("ui/mainMenu/distant villager - _0002_s_royal_02_skull.png");

        pauseTop = new Texture("ui/pauseMenu/pause_top_fleur0006.png");
        pauseBottom = new Texture("ui/pauseMenu/bottom_fleur0007.png");
    }

    private void loadEnvironmentAssets() {
        background = new Texture("background/background.png");
        vheart_beam = new Texture("background/vheart_beam.png");
        infection = new Texture("background/infection.png");
        blue = new Texture("background/blue.png");
    }

    private void loadFonts() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
            Gdx.files.internal("font/TrajanPro-Bold.otf"));
        FreeTypeFontGenerator generatorSub = new FreeTypeFontGenerator(
            Gdx.files.internal("font/primary_font.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();

        String extendedChars = "áéíóúÁÉÍÓÚàèìòùÀÈÌÒÙâêîôûÂÊÎÔÛäëïöüÿÄËÏÖÜŸçÇñÑæÆœŒß¿¡«»‹›“”‘’";
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS + extendedChars;

        parameter.size = 30;
        font = generator.generateFont(parameter);

        parameter.size = 24;
        subFont = generatorSub.generateFont(parameter);

        generator.dispose();
        generatorSub.dispose();
    }

    private void loadSelectGameAssets() {
        saveBackground_greenPath = new NinePatch(
            new Texture("ui/startGameMenu/Area_Green_Path.png"), 10, 10, 10, 10);
        saveBackground_forgotten = new NinePatch(
            new Texture("ui/startGameMenu/Area_Forgotten Crossroads.png"), 10, 10, 10, 10);
        under_SelectProfile = new Texture("ui/startGameMenu/Warning_Fleur0008.png");
        geoHud = new Texture("ui/startGameMenu/select_game_HUD_coin_v020004.png");
        healthFrame = new Texture("ui/startGameMenu/select_game_HUD_0002_health_frame.png");
        mask = new Texture("ui/startGameMenu/select_game_HUD_0001_health.png");
        profile_fleur = new Texture("ui/startGameMenu/profile_fleur0012.png");
        profilePointer = new Texture("ui/startGameMenu/main_menu_pointer_anim0010.png");
        sliderSkin = new Skin(Gdx.files.internal("ui/slider/slider.json"));
    }

    private void loadHudAssets() {
        healthFrameHud = new TextureRegion(new Texture("ui/hud/HUD Cln_167.png"));
        fullMask = new TextureRegion(new Texture("ui/hud/FilledHealth.png"));
        emptyMask = new TextureRegion(new Texture("ui/hud/EmptyHealth.png"));
        maskShatterAnim = KnightAnimationLoader.loadAnimation(
            "ui/hud/BreakHealth.png", 6, 0.1f, Animation.PlayMode.NORMAL);
        maskRefillAnim = KnightAnimationLoader.loadAnimation(
            "ui/hud/HealthRefill.png", 5, 0.1f, Animation.PlayMode.NORMAL);
        maskShineAnim = KnightAnimationLoader.loadAnimation(
            "ui/hud/FilledHealthShine.png", 5, 0.1f, Animation.PlayMode.NORMAL);
        orbMaskTexture = new Texture("ui/hud/SoulOrb_Full.png");

        String vertCode = Gdx.files.internal("ui/hud/soul_orb.vert").readString();
        String fragCode = Gdx.files.internal("ui/hud/soul_orb.frag").readString();
        ShaderProgram.pedantic = false;
        liquidShader = new ShaderProgram(vertCode, fragCode);

        soulOrbAtlas = new TextureAtlas(Gdx.files.internal("ui/hud/Soulorb.atlas"));
        Array<TextureAtlas.AtlasRegion> idleFrames = new Array<>();
        for (int i = 0; i <= 5; i++) {
            idleFrames.add(soulOrbAtlas.findRegion("HUD_Soulorb_fills_soul_idle000" + i));
        }
        soulIdleAnim = new Animation<>(0.1f, idleFrames, Animation.PlayMode.LOOP);

        topOrnament = new Texture("ui/hud/gg_board_UI_top_0004.png");
        bottomOrnament = new Texture("ui/hud/gg_board_UI_bottom_0003.png");
        top_menu = new Texture("ui/hud/prompt_divider.png");
    }

    private void loadInventoryAssets() {
        emptyNotch = new Texture("ui/inventory_and_charms/charm_cost.png");
        fullNotch = new Texture("ui/inventory_and_charms/charm_UI__0000_charm_cost_02_lit.png");
        charm_place = new Texture("ui/inventory_and_charms/charm_backboard.png");

        Texture corner = new Texture("ui/inventory_and_charms/overscan_corner_fleur009.png");
        overScreen_Top_Left = new TextureRegion(corner);
        overScreen_Top_Right = new TextureRegion(corner);
        overScreen_Bottom_Left = new TextureRegion(corner);
        overScreen_Bottom_Right = new TextureRegion(corner);
        overScreen_Top_Right.flip(true, false);
        overScreen_Bottom_Left.flip(false, true);
        overScreen_Bottom_Right.flip(true, true);

        Texture charm_selector = new Texture("ui/inventory_and_charms/Inv_0014_selection_cursor.png");
        charm_selector_Top_Left = new TextureRegion(charm_selector);
        charm_selector_Top_Right = new TextureRegion(charm_selector);
        charm_selector_Bottom_Left = new TextureRegion(charm_selector);
        charm_selector_Bottom_Right = new TextureRegion(charm_selector);
        charm_selector_Top_Right.flip(true, false);
        charm_selector_Bottom_Right.flip(true, true);
        charm_selector_Bottom_Left.flip(false, true);

        inventory_divider = new Texture("ui/inventory_and_charms/Inv_0017_divider.png");
        inventory_top = new Texture("ui/inventory_and_charms/game_over_fleur.png");
        inventory_bottom = new Texture("ui/inventory_and_charms/bottom_fleur0003.png");

        charmTextures = new HashMap<>();
        charmTextures.put(Charm.SOUL_CATCHER, new Texture(
            "ui/inventory_and_charms/Soul Catcher - _0001_charm_more_soul.png"));
        charmTextures.put(Charm.DASH_MASTER, new Texture(
            "ui/inventory_and_charms/Dashmaster - _0011_charm_generic_03.png"));
        charmTextures.put(Charm.UNBREAKABLE_STRENGTH, new Texture(
            "ui/inventory_and_charms/Unbreakable Strength_0002_charm_glass_attack_up_full.png"));
        charmTextures.put(Charm.QUICK_SLASH, new Texture(
            "ui/inventory_and_charms/Quick Slash - _0003_charm_nail_slash_speed_up.png"));
        charmTextures.put(Charm.QUICK_FOCUS, new Texture(
            "ui/inventory_and_charms/Quick Focus - _0005_charm_fast_focus.png"));
        charmTextures.put(Charm.HEAVY_BLOW, new Texture(
            "ui/inventory_and_charms/Heavy Blow - _0008_charm_nail_damage_up.png"));
        charmTextures.put(Charm.SHARP_SHADOW, new Texture(
            "ui/inventory_and_charms/Sharp Shadow - charm_shade_impact.png"));
        charmTextures.put(Charm.VOID_HEART, new Texture(
            "ui/inventory_and_charms/Void Heart - charm_black.png"));
    }

    private void loadAchievementAssets() {
        achievementCompleteTex = new Texture("ui/achievement/achievement__0000_100_complete.png");
        achievementEndingATex = new Texture("ui/achievement/achievement__0006_ending_A.png");
        achievementFalseKnightTex = new Texture("ui/achievement/achievement_false_knight #50302521.png");
        achievementHunterTex = new Texture("ui/achievement/achievement_Hunter_Marks.png");
        achievementSecretTex = new Texture("ui/achievement/achievement_secret.png");
        achievementSpeedrunTex = new Texture("ui/achievement/achievement_ultra_fast_finish.png");
        achievementFrameTex = new Texture("ui/achievement/achievement_fleur0005.png");
    }

    private void loadOtherAssets() {
        crystalLaserTex = new TextureRegion(new Texture("animation/enemy/crystallized/CrystalLaser.png"));
        rockTexture = new TextureRegion(new Texture("effect/2163-7@2x.png"));
        butterflyTexture = new Texture(Gdx.files.internal("environment/blue_butterfly.png"));

        int frameWidth = butterflyTexture.getWidth() / 4;
        int frameHeight = butterflyTexture.getHeight() / 3;
        TextureRegion[][] tmp = TextureRegion.split(butterflyTexture, frameWidth, frameHeight);
        Array<TextureRegion> butterflyFrames = new Array<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                butterflyFrames.add(tmp[i][j]);
            }
        }
        butterflyAnim = new Animation<>(0.08f, butterflyFrames, Animation.PlayMode.LOOP);
    }

    public void dispose() {
        if (background != null) background.dispose();
        if (hollowKnightLogo != null) hollowKnightLogo.dispose();
        if (pointerR != null) pointerR.getTexture().dispose();
        if (pointerL != null) pointerL.getTexture().dispose();
        if (font != null) font.dispose();
        if (subFont != null) subFont.dispose();
        if (emptyNotch != null) emptyNotch.dispose();
        if (fullNotch != null) fullNotch.dispose();
        if (charm_place != null) charm_place.dispose();
        if (charmTextures != null) {
            for (Texture t : charmTextures.values()) t.dispose();
        }
        if (soulOrbAtlas != null) soulOrbAtlas.dispose();
        if (orbMaskTexture != null) orbMaskTexture.dispose();
        if (liquidShader != null) liquidShader.dispose();
        if (butterflyTexture != null) butterflyTexture.dispose();
    }
}
