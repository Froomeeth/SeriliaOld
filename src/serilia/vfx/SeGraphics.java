package serilia.vfx;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Scl;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.gen.PayloadUnit;
import mindustry.graphics.Shaders;

public class SeGraphics {
    public FrameBuffer buffer = Vars.renderer.effectBuffer;
    public static Color[] spectrum = {Color.red, Color.coral, Color.yellow, Color.lime, Color.green, Color.teal, Color.blue, Color.purple, Color.magenta};

    //draw
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
        for(int i = 0; i < points.length; i++) {
            Draw.color(spectrum[i], 0.5f);
            Fill.poly(points[i].x, points[i].y, 12, 2f);
        }
        Draw.color();
    }
    public static void debugDots(float[] points){
        for(int i = 0; i < points.length; i += 2) {
            Draw.color(spectrum[i/2], 0.5f);
            Fill.poly(points[i], points[i+1], 12, 2f);
        }
        Draw.color();
    }

    //shaders
    public static class ChromaticAberrationShader extends SeLoadShader {
        public ChromaticAberrationShader(){
            super("aberration", "screenspace");
        }

        @Override
        public void apply(){
            setUniformf("u_dp", Scl.scl(1f));
            setUniformf("u_time", Time.time / Scl.scl(1f));
            setUniformf("u_offset",
                    Core.camera.position.x - Core.camera.width / 2,
                    Core.camera.position.y - Core.camera.height / 2);
            setUniformf("u_texsize", Core.camera.width, Core.camera.height);
        }
    }

    public static class SeLoadShader extends Shader{
        public SeLoadShader(String frag, String vert){
            super(Shaders.getShaderFi(vert + ".vert"), Vars.tree.get("shaders/" + frag +".frag"));
        }
    }
}