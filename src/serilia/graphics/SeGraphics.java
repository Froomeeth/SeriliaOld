package serilia.graphics;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import arc.math.Mathf;
import arc.scene.ui.layout.Scl;
import arc.util.Time;
import mindustry.Vars;
import mindustry.graphics.Shaders;

/**Miscellaneous drawing, shader definitions.*/
public class SeGraphics {

    //draw
    public static void drawHalfSpin(TextureRegion region, float x, float y, float r){
        float a = Draw.getColor().a;
        r = Mathf.mod(r, 180f);
        Draw.rect(region, x, y, r);
        Draw.alpha(r / 180f*a);
        Draw.rect(region, x, y, r - 180f);
        Draw.alpha(a);
    }

    //shaders
    public FrameBuffer buffer = Vars.renderer.effectBuffer;

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