package serilia;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.FrameBuffer;
import arc.struct.Seq;
import mindustry.graphics.Layer;
import serilia.content.*;
import mindustry.Vars;
import mindustry.mod.*;
import rhino.*;
import serilia.vfx.Parallax;
import serilia.vfx.SeGraphics;

public class SeriliaMain extends Mod{
    public static Parallax parallax = new Parallax();
    public static FrameBuffer buffer = Vars.renderer.effectBuffer;

    public SeriliaMain(){}

    @Override
    public void loadContent(){
        SeResources.load();
        SeUnits.load();
        SeBlocks.load();
        SeriliaSystem.load();
        SeriliaTechTree.load();

        Vars.renderer.addEnvRenderer(1024 * 2, () ->
                Draw.drawRange(Layer.legUnit, 1f, () -> buffer.begin(Color.clear), () -> {
                    buffer.end();
                    buffer.blit(new SeGraphics.ChromaticAberrationShader());
                })
        );
    }

    public static NativeJavaPackage p = null;

    @Override
    public void init() {
        super.init();
        ImporterTopLevel scope = (ImporterTopLevel) Vars.mods.getScripts().scope;

        Seq<String> packages = Seq.with(
                "serilia",
                "serilia.types",
                "serilia.content"
        );

        packages.each(name -> {
            p = new NativeJavaPackage(name, Vars.mods.mainLoader());
            p.setParentScope(scope);
            scope.importPackage(p);
        });
    }
}
