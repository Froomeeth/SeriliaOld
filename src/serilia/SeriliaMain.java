package serilia;

import arc.struct.Seq;
import serilia.content.*;
import mindustry.Vars;
import mindustry.mod.*;
import rhino.*;

public class SeriliaMain extends Mod{

    public SeriliaMain(){}


    @Override
    public void loadContent(){
        SeResources.load();
        SeUnits.load();
        SeBlocks.load();
        SeriliaTechTree.load();
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
