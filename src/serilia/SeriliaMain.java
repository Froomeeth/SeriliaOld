package serilia;

import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.FrameBuffer;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Reflect;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.graphics.Layer;
import mindustry.mod.Mod;
import mindustry.ui.fragments.MenuFragment;
import rhino.ImporterTopLevel;
import rhino.NativeJavaPackage;
import serilia.content.*;
import serilia.gen.entities.EntityRegistry;
import serilia.util.Parallax;
import serilia.graphics.SeGraphics;
import serilia.graphics.SeMenuRenderer;

public class SeriliaMain extends Mod{
    public static Parallax parallax = new Parallax();
    public static FrameBuffer buffer = Vars.renderer.effectBuffer;

    public SeriliaMain(){}

    @Override
    public void loadContent(){
        SeResources.load();
        EntityRegistry.register();
        SeUnits.load();
        AhkarBlocks.load();
        CaliBlocks.load();
        SeSystem.load();
        CaliterraTechTree.load();

        Events.on(EventType.ClientLoadEvent.class, (e) -> {
            Vars.renderer.addEnvRenderer(1024 * 2, () ->
                    Draw.drawRange(Layer.legUnit, 1f, () -> buffer.begin(Color.clear), () -> {
                        buffer.end();
                        buffer.blit(new SeGraphics.ChromaticAberrationShader());
                    })
            );

            //stolen from project oblivion
            try {
                Reflect.set(MenuFragment.class, Vars.ui.menufrag, "renderer", new SeMenuRenderer());
            } catch (Exception exep) {
                Log.err("Failed to replace renderer", exep);
            }
        });
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
