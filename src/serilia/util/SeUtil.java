package serilia.util;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Vec2;

public class SeUtil{
    public static Color[] spectrum = {Color.red, Color.coral, Color.yellow, Color.lime, Color.green, Color.teal, Color.blue, Color.purple, Color.magenta};


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


    /*public int checkTiling4(Building build){
        int tiling = 0;

        for(int i = 0; i < 4; i++){
            Building b = build.nearby(Geometry.d4(i + 2 % 4).x * build.block.size + 1, Geometry.d4(i + 2 % 4).y * build.block.size + 1);
            if(b != null && ((BitmaskTiler)build).blends(b.block)){
                tiling |= (1 << i);
            }
        }

        return tiling;
    }*/
}
