package com.hollow.assets;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.hollow.HollowKnight;
import com.hollow.models.AmbientObject;
import com.hollow.models.BreakableWall;
import com.hollow.models.SolidBlock;
import com.hollow.models.TransitionZone;
import com.hollow.models.entities.Enemy.*;

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

    public Array<Crawlid> getCrawLidSpawn(TiledMap map, float unitScale) {
        Array<Crawlid> crawlids = new Array<>();

        MapLayer layer = map.getLayers().get("enemies");

        if (layer == null) return crawlids;

        for (MapObject object : layer.getObjects()) {
            if (object.getName() != null && object.getName().equalsIgnoreCase("Crawlid")) {
                float x = object.getProperties().get("x", Float.class) * unitScale;
                float y = object.getProperties().get("y", Float.class) * unitScale;

                crawlids.add(new Crawlid(x, y));
            }
        }
        return crawlids;
    }

    public Array<HuskHornhead> getHuskHornHead(TiledMap map, float unitScale) {
        Array<HuskHornhead> huskHornheads = new Array<>();

        MapLayer layer = map.getLayers().get("enemies");

        if (layer == null) return huskHornheads;

        for (MapObject object : layer.getObjects()) {
            if (object.getName() != null && object.getName().equalsIgnoreCase("HuskHornhead")) {
                float x = object.getProperties().get("x", Float.class) * unitScale;
                float y = object.getProperties().get("y", Float.class) * unitScale;

                huskHornheads.add(new HuskHornhead(x, y));
            }
        }
        return huskHornheads;
    }

    public Array<Mosquito> getMosquito(TiledMap map, float unitScale) {
        Array<Mosquito> mosquitos = new Array<>();

        MapLayer layer = map.getLayers().get("enemies");

        if (layer == null)
            return mosquitos;

        for (MapObject object : layer.getObjects()) {
            if (object.getName() != null && object.getName().equalsIgnoreCase("mosquito")) {
                float x = object.getProperties().get("x", Float.class) * unitScale;
                float y = object.getProperties().get("y", Float.class) * unitScale;

                mosquitos.add(new Mosquito(x, y));
            }
        }
        return mosquitos;
    }

    public Array<Mosscreep> getMosscreep(TiledMap map, float unitScale) {
        Array<Mosscreep> mosscreeps = new Array<>();

        MapLayer layer = map.getLayers().get("enemies");

        if (layer == null)
            return mosscreeps;

        for (MapObject object : layer.getObjects()) {
            if (object.getName() != null && object.getName().equalsIgnoreCase("mosscreep")) {
                float x = object.getProperties().get("x", Float.class) * unitScale;
                float y = object.getProperties().get("y", Float.class) * unitScale;

                mosscreeps.add(new Mosscreep(x, y));
            }
        }
        return mosscreeps;
    }

    public Array<Crystallized> getCrystallized(TiledMap map, float unitScale) {
        Array<Crystallized> crystallizeds = new Array<>();

        MapLayer layer = map.getLayers().get("enemies");

        if (layer == null)
            return crystallizeds;

        for (MapObject object : layer.getObjects()) {
            if (object.getName() != null && object.getName().equalsIgnoreCase("crystallized")) {
                float x = object.getProperties().get("x", Float.class) * unitScale;
                float y = object.getProperties().get("y", Float.class) * unitScale;

                crystallizeds.add(new Crystallized(x, y));
            }
        }
        return crystallizeds;
    }

    public Array<BreakableWall> getBreakableWall(TiledMap map, float unitScale) {
        Array<BreakableWall> breakableWalls = new Array<>();

        MapLayer layer = map.getLayers().get("breakable_wall");

        if (layer == null)
            return breakableWalls;

        for (MapObject object : layer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                breakableWalls.add(
                    new BreakableWall(rect.x * unitScale, rect.y * unitScale,
                                        rect.width * unitScale, rect.height * unitScale)
                );
            }
        }

        return breakableWalls;
    }

    public Vector2 getVoidHeartPos(TiledMap map, float unitScale) {
        MapLayer layer = map.getLayers().get("void-heart");

        if (layer == null)
            return null;

        for (MapObject object : layer.getObjects()) {
            if (object.getName() != null && object.getName().equalsIgnoreCase("void-heart")) {
                float x = object.getProperties().get("x", Float.class) * unitScale;
                float y = object.getProperties().get("y", Float.class) * unitScale;

                return new Vector2(x, y);
            }
        }
        return null;
    }

    public Array<AmbientObject> getAmbientObjects(TiledMap map, float unitScale, AssetLoader assetLoader) {
        Array<AmbientObject> ambientObjects = new Array<>();
        MapLayer layer = map.getLayers().get("ambient_decorations");

        if (layer == null) return ambientObjects;

        for (MapObject object : layer.getObjects()) {
            float x = object.getProperties().get("x", Float.class) * unitScale;
            float y = object.getProperties().get("y", Float.class) * unitScale;
            float width = object.getProperties().get("width", Float.class) * unitScale;
            float height = object.getProperties().get("height", Float.class) * unitScale;

            String type = object.getProperties().get("type", String.class);

            if ("butterfly".equals(type)) {
                ambientObjects.add(new AmbientObject(x, y, width, height, assetLoader.butterflyAnim, true));
            }
        }
        return ambientObjects;
    }

}
