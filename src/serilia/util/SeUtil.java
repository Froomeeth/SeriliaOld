package serilia.util;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import mindustry.content.Fx;
import mindustry.core.UI;
import mindustry.gen.Building;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.ShieldWall;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.power.PowerGraph;
import serilia.world.blocks.power.LaserNode;

public class SeUtil{
    public static Color[] spectrum = {Color.red, Color.coral, Color.yellow, Color.lime, Color.green, Color.teal, Color.blue, Color.purple, Color.magenta};

    //vec
    public static Vec2 vecSetLine(Vec2 vec, Vec2 pos, float rotation, float length){
        vec.setLength(length).setAngle(rotation).add(pos);
        return vec;
    }
    public static Vec2 vecSetLine(Vec2 vec, float x, float y, float rotation, float length){
        vec.setLength(length).setAngle(rotation).add(x, y);
        return vec;
    }


    public static void quadHelper(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4){
        Fill.quad(x1, y1, x2, y2, x3, y3, x4, y4);
        debugDots(new float[]{x1, y1, x2, y2, x3, y3, x4, y4});
    }
    public static void quadHelper(TextureRegion region, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4){
        Fill.quad(region, x1, y1, x2, y2, x3, y3, x4, y4);
        debugDots(new float[]{x1, y1, x2, y2, x3, y3, x4, y4});
    }
    public static void quadHelper(TextureRegion region, Vec2 v1, Vec2 v2, Vec2 v3, Vec2 v4){
        quadHelper(region, v1.x, v1.y, v2.x, v2.y, v3.x, v3.y, v4.x, v4.y);
    }
    public static void quadHelper(Vec2 v1, Vec2 v2, Vec2 v3, Vec2 v4){
        quadHelper(v1.x, v1.y, v2.x, v2.y, v3.x, v3.y, v4.x, v4.y);
    }


    public static void debugDots(Vec2[] points){
        for(int i = 0; i < points.length; i++){
            Draw.color(spectrum[i], 0.5f);
            Fill.poly(points[i].x, points[i].y, 12, 2f);
        }
        Draw.color();
    }
    public static void debugDots(float[] points){
        for(int i = 0; i < points.length; i += 2){
            Draw.color(spectrum[i / 2], 0.5f);
            Fill.poly(points[i], points[i + 1], 12, 2f);
        }
        Draw.color();
    }


    //bittiler stuff
    public static TextureRegion[][] splitLayers(String name, int size, int layerCount){
        TextureRegion[][] layers = new TextureRegion[layerCount][];

        for(int i = 0; i < layerCount; i++){
            layers[i] = split(name, size, i);
        }
        return layers;
    }
    public static TextureRegion[] split(String name, int size, int layer){
        TextureRegion tex = Core.atlas.find(name);
        int margin = 2;
        int countX = tex.width / size; //fuck you only square
        TextureRegion[] tiles = new TextureRegion[countX];

        for(int step = 0; step < countX; step++){
            tiles[step] = new TextureRegion(tex, 1 + (step * (margin + size)), 1 + (layer * (margin + size)), size, size);
        }
        return tiles;
    }


    //w a l l
    public static void generateWalls(Seq<?> fromWalls, Seq<Integer> genSizes){
        fromWalls.each(wall -> {
            generateWalls((Block)wall, genSizes);
        });
    }

    public static void generateWalls(Block thewall, Seq<Integer> genSizes){
        genSizes.each(si -> {
            int s = si;
            if(thewall instanceof Wall wall){
                Wall w = wall instanceof ShieldWall ? new ShieldWall(wall.name.substring(8) + "-" + s) : new Wall(wall.name.substring(8) + "-" + s);

                ItemStack[] reqs = new ItemStack[wall.requirements.length];
                for(int i = 0; i < reqs.length; i++){
                    int quantity = Mathf.round(((float) wall.requirements[i].amount / (wall.size * wall.size) * s * s));
                    reqs[i] = new ItemStack(wall.requirements[i].item, UI.roundAmount(quantity));
                }
                w.requirements(Category.defense, wall.buildVisibility, reqs);
                w.size = s;
                w.buildVisibility = wall.buildVisibility;
                w.scaledHealth = wall.scaledHealth;
                w.lightningChance = wall.lightningChance;
                w.lightningDamage = wall.lightningDamage;
                w.lightningColor = wall.lightningColor;
                w.lightningSound = wall.lightningSound;
                w.lightningLength = wall.lightningLength;
                w.flashHit = wall.flashHit;
                w.flashColor = wall.flashColor;
                w.deflectSound = wall.deflectSound;
                w.chanceDeflect = wall.chanceDeflect;
                w.conductivePower = wall.conductivePower;
                w.variants = wall.variants;
                w.insulated = wall.insulated;
                w.envRequired = wall.envRequired;
                w.envEnabled = wall.envEnabled;
                w.envDisabled = wall.envDisabled;
                w.placeEffect = wall.placeEffect;
                w.placeSound = wall.placeSound;
                w.breakEffect = wall.breakEffect;
                w.breakSound = wall.breakSound;
                w.destroySound = wall.destroySound;
                if(wall instanceof ShieldWall){
                    ShieldWall sw = (ShieldWall) w, shWall = (ShieldWall) wall;
                    sw.shieldHealth = shWall.shieldHealth / (wall.size * wall.size) * s * s;
                    sw.breakCooldown = shWall.breakCooldown;
                    sw.regenSpeed = shWall.regenSpeed / (wall.size * wall.size) * s * s;
                    ;
                    sw.glowColor = shWall.glowColor;
                    sw.glowMag = shWall.glowMag;
                    sw.glowScl = shWall.glowScl;
                }
            }
        });
    }


    public static PowerGraph getGraphWithin(Building caster, int range, int dir){
        return getGraphWithin(caster, range, dir, -1);
    }

    public static PowerGraph getGraphWithin(Building caster, int range, int dir, int rot){
        for(int r = 1; r <= range; r++){
            //get block on current side at increasing distance "r"
            Building the = caster.nearby(Geometry.d4(dir).x * r, Geometry.d4(dir).y * r);

            Fx.electrified.at(caster.x + (Geometry.d4(dir).x * r * 8), caster.y + (Geometry.d4(dir).y * r * 8));

            if(the instanceof LaserNode.LaserNodeBuild && (rot < 0 || the.rotation == rot)){ //todo lasernode
                //found, break current loop and return it
                Fx.landShock.at(the);
                return the.power.graph;
            }
        }
        //did not find
        return null;
    }
}
