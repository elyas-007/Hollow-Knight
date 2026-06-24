package com.hollow.views.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.hollow.HollowKnight;
import com.hollow.controllers.CharmSelectionController;
import com.hollow.models.GameData;
import com.hollow.models.entities.Knight.Charm;

public class InventoryUI {
    public Stage stage;
    private HollowKnight game;
    private GameData data;

    private Table equippedCharmsTable;
    private Table notchesTable;
    private Table selectorTable;
    private Table rootTable;

    private Label charmNameLabel;
    private Label charmDescLabel;
    private Image charmIconRight;

    private final int MAX_NOTCHES = 3;
    private Container<Image>[] equippedSlots;

    private Array<Stack> charmSlots = new Array<>();
    private Array<Charm> charmList = new Array<>();
    private CharmSelectionController controller;

    public InventoryUI(HollowKnight game, GameData data) {
        this.game = game;
        this.data = data;
        stage = new Stage(new FitViewport(1920, 1080));
        setupUI();
    }

    @SuppressWarnings("unchecked")
    private void setupUI() {
        rootTable = new Table();
        rootTable.setFillParent(true);

        Table contentTable = new Table();
        buildMainLayout(contentTable);

        Table leftTable = new Table();

        LabelStyle style = new LabelStyle(game.assetLoader.font, Color.WHITE);
        Label equippedLabel = new Label("Equipped", style);
        leftTable.add(equippedLabel).left().padBottom(10).row();

        equippedCharmsTable = new Table();
        equippedSlots = new Container[MAX_NOTCHES];

        for (int i = 0; i < MAX_NOTCHES; i++) {
            equippedSlots[i] = new Container<Image>();
            equippedSlots[i].prefSize(70, 70);
            equippedCharmsTable.add(equippedSlots[i]).padRight(15);
        }
        leftTable.add(equippedCharmsTable).left().padBottom(20).row();

        Label notchLabel = new Label("Notches", style);
        leftTable.add(notchLabel).left().padBottom(10).row();

        notchesTable = new Table();
        leftTable.add(notchesTable).left().padBottom(30).row();

        Image divider = new Image(game.assetLoader.inventory_divider);
        leftTable.add(divider).expandX().fill().padBottom(30).row();

        Table charmsGrid = new Table();
        int cols = 4;
        int currentCol = 0;

        charmSlots.clear();
        charmList.clear();

        for (final Charm charm : Charm.values()) {
            Stack slotStack = new Stack();
            Image bg = new Image(game.assetLoader.charm_place);
            bg.setTouchable(Touchable.disabled);
            slotStack.add(bg);

            charmList.add(charm);
            charmSlots.add(slotStack);

            if (data.unlockedCharms.contains(charm, true)) {
                final Image charmImg = new Image(game.assetLoader.charmTextures.get(charm));
                charmImg.setTouchable(Touchable.enabled);
                slotStack.add(charmImg);
            }


            charmsGrid.add(slotStack).size(80, 80).pad(10);
            currentCol++;
            if (currentCol >= cols) {
                charmsGrid.row();
                currentCol = 0;
            }
        }
        leftTable.add(charmsGrid).left();

        Table rightTable = new Table();
        charmNameLabel = new Label("", style);
        charmNameLabel.setAlignment(Align.center);

        charmIconRight = new Image();

        charmDescLabel = new Label("", new LabelStyle(game.assetLoader.subFont, Color.LIGHT_GRAY));
        charmDescLabel.setWrap(true);
        charmDescLabel.setAlignment(Align.center);

        rightTable.add(charmNameLabel).padBottom(20).row();
        rightTable.add(charmIconRight).size(150, 150).padBottom(30).row();
        rightTable.add(charmDescLabel).width(500).height(200);

        contentTable.add(leftTable).expand().center().padRight(50);
        contentTable.add(rightTable).width(600).center();

        stage.addActor(rootTable);

        createSelector();
        updateEquippedAndNotches();

        controller = new CharmSelectionController(game, stage, charmSlots, selectorTable, cols, this);
    }

    private void buildMainLayout(Table contentTable) {
        Image tl = new Image(game.assetLoader.overScreen_Top_Left);
        Image top = new Image(game.assetLoader.inventory_top);
        Label titleL = new Label("CHARMS", new LabelStyle(game.assetLoader.font, Color.WHITE));
        Image tr = new Image(game.assetLoader.overScreen_Top_Right);

        Table title = new Table();
        title.add(titleL).padBottom(5).padTop(-10).row();
        title.add(top);
        rootTable.add(tl).top().left();
        rootTable.add(title).top().center().expandX();
        rootTable.add(tr).top().right();
        rootTable.row();

        rootTable.add(contentTable).colspan(3).expand().center().fill();
        rootTable.row();

        Image bl = new Image(game.assetLoader.overScreen_Bottom_Left);
        Image bottom = new Image(game.assetLoader.inventory_bottom);
        Image br = new Image(game.assetLoader.overScreen_Bottom_Right);

        rootTable.add(bl).bottom().left();
        rootTable.add(bottom).bottom().center().expandX();
        rootTable.add(br).bottom().right();
    }

    private void createSelector() {
        selectorTable = new Table();
        selectorTable.setTouchable(Touchable.disabled);
        selectorTable.add(new Image(game.assetLoader.charm_selector_Top_Left)).left().top();
        selectorTable.add().expandX();
        selectorTable.add(new Image(game.assetLoader.charm_selector_Top_Right)).right().top();
        selectorTable.row();
        selectorTable.add().expandY().colspan(3);
        selectorTable.row();
        selectorTable.add(new Image(game.assetLoader.charm_selector_Bottom_Left)).left().bottom();
        selectorTable.add().expandX();
        selectorTable.add(new Image(game.assetLoader.charm_selector_Bottom_Right)).right().bottom();

        selectorTable.setVisible(false);
        stage.addActor(selectorTable);
    }

    public void highlightSlot(int index) {
        if (index < 0 || index >= charmList.size)
            return;

        Charm charm = charmList.get(index);

        if (data.unlockedCharms.contains(charm, true)) {
            charmNameLabel.setText(charm.getTitle());
            charmDescLabel.setText(charm.getDescription());
            charmIconRight.setDrawable(new Image(game.assetLoader.charmTextures.get(charm)).getDrawable());
        } else {
            charmNameLabel.setText("???");
            charmDescLabel.setText("");
            charmIconRight.setDrawable(null);
        }
    }

    public void toggleEquipSlot(int index) {
        if (index < 0 || index >= charmList.size) return;

        Charm charm = charmList.get(index);
        if (!data.unlockedCharms.contains(charm, true)) return;

        Stack slotStack = charmSlots.get(index);

        if (data.equippedCharms.contains(charm, true)) {
            data.equippedCharms.removeValue(charm, true);
            updateEquippedAndNotches();
            if (game.settings.isSfxOn && game.assetLoader.buttonClick != null) {
                game.assetLoader.buttonClick.stop();
                game.assetLoader.buttonClick.play();
            }
        } else if (data.equippedCharms.size < MAX_NOTCHES) {
            animateEquip(charm, slotStack);
            if (game.settings.isSfxOn && game.assetLoader.buttonClick != null) {
                game.assetLoader.buttonClick.stop();
                game.assetLoader.buttonClick.play();
            }
        }
    }

    private void animateEquip(final Charm charm, Actor sourceSlot) {
        final int targetIndex = data.equippedCharms.size;
        data.equippedCharms.add(charm);

        updateEquippedAndNotches();

        final Actor targetIcon = equippedSlots[targetIndex].getActor();
        if (targetIcon != null) {
            targetIcon.setVisible(false);
        }

        Vector2 startPos = sourceSlot.localToStageCoordinates(new Vector2(0, 0));
        Vector2 endPos = equippedSlots[targetIndex].localToStageCoordinates(new Vector2(0, 0));

        final Image flyingIcon = new Image(game.assetLoader.charmTextures.get(charm));
        flyingIcon.setPosition(startPos.x, startPos.y);
        flyingIcon.setSize(sourceSlot.getWidth(), sourceSlot.getHeight()); // اندازه اولیه 80x80
        stage.addActor(flyingIcon);

        flyingIcon.addAction(Actions.sequence(
            Actions.parallel(
                Actions.moveTo(endPos.x, endPos.y, 0.35f, Interpolation.pow2Out),
                Actions.sizeTo(70, 70, 0.35f, Interpolation.pow2Out)
            ),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    if (targetIcon != null) targetIcon.setVisible(true);
                    flyingIcon.remove();
                }
            })
        ));
    }

    private void updateEquippedAndNotches() {
        for (int i = 0; i < MAX_NOTCHES; i++) {
            if (i < data.equippedCharms.size) {
                final Charm c = data.equippedCharms.get(i);
                Image img = new Image(game.assetLoader.charmTextures.get(c));
                img.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        data.equippedCharms.removeValue(c, true);
                        updateEquippedAndNotches();
                        if (game.settings.isSfxOn && game.assetLoader.buttonClick != null) {
                            game.assetLoader.buttonClick.stop();
                            game.assetLoader.buttonClick.play();
                        }
                    }
                });
                equippedSlots[i].setActor(img);
            } else {
                equippedSlots[i].setActor(null);
            }
        }

        notchesTable.clearChildren();
        for (int i = 0; i < MAX_NOTCHES; i++) {
            Image notch;
            if (i < data.equippedCharms.size) {
                notch = new Image(game.assetLoader.fullNotch);
            } else {
                notch = new Image(game.assetLoader.emptyNotch);
            }
            notchesTable.add(notch).size(35, 35).padRight(10);
        }
    }

    public void act(float delta) {
        stage.act(delta);
        if (controller != null)
            controller.update(delta);
    }

    public void draw() {
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
    }
}
