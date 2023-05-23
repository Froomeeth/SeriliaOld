package serilia.entities.comp;

import ent.anno.Annotations;
import serilia.gen.entities.LiquidGraphUpdaterc;

@Annotations.EntityDef(value = LiquidGraphUpdaterc.class, serialize = false, genIO = false)
@Annotations.EntityComponent
public abstract class LiquidGraphUpdaterComp{
    public transient LiquidGraphUpdaterComp graph;

    public void update(){
        graph.update();
    }
}
