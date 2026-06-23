package com.hollow.assets;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.hollow.models.SolidBlock;
import com.hollow.models.TransitionZone;
import com.hollow.models.entities.Enemy.Tiktik;

public class TiledMapHelper {
    private TiledMap tiledMap;

    public TiledMap loadMap(String path) {
        tiledMap = new TmxMapLoader().load(path);
        return tiledMap;
    }

    public Array<SolidBlock> getSolidRectangles() {
        Array<SolidBlock> solidBlocks = new Array<>();

        MapLayer layer = tiledMap.getLayers().get("logic");

        for (MapObject object : layer.getObjects()) {

            if (object instanceof RectangleMapObject) {

                Rectangle rect = ((RectangleMapObject) object).getRectangle();

                boolean isDeadly = false;
                if (object.getProperties().containsKey("deadly")) {
                    isDeadly = object.getProperties().get("deadly", Boolean.class);
                }

                solidBlocks.add(new SolidBlock(rect.x * (1f / 64f), rect.y * (1f / 64f),
                    rect.width * (1f / 64f), rect.height * (1f / 64f), isDeadly));
            }
        }

        return solidBlocks;
    }

    public Array<Tiktik> getTiktikSpawns(TiledMap map, float unitScale) {
        Array<Tiktik> tiktiks = new Array<>();

        MapLayer layer = map.getLayers().get("enemies");
        if (layer == null) return tiktiks;

        for (MapObject object : layer.getObjects()) {
            if (object.getName() != null && object.getName().equalsIgnoreCase("Tiktik")) {
                float x = object.getProperties().get("x", Float.class) * unitScale;
                float y = object.getProperties().get("y", Float.class) * unitScale;

                tiktiks.add(new Tiktik(x, y));
            }
        }
        return tiktiks;
    }

    public TransitionZone getTransitionZone(TiledMap map, float unitScale) {
        MapLayer layer = map.getLayers().get("transition");

        if (layer == null) return null;

        for (MapObject object : layer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();

                String targetMap = object.getProperties().get("targetMap", String.class);

                if (targetMap != null) {
                    return new TransitionZone(
                        rect.x * unitScale,
                        rect.y * unitScale,
                        rect.width * unitScale,
                        rect.height * unitScale,
                        targetMap
                    );
                }
            }
        }
        return null;
    }

    public Rectangle getBossRoom(TiledMap map, float unitScale) {
        MapLayer layer = map.getLayers().get("boss_room");

        if (layer == null) return null;

        for (MapObject object : layer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();

                return new Rectangle(rect.x * unitScale, rect.y * unitScale, rect.width * unitScale, rect.height * unitScale);
            }
        }
        return null;
    }

}
