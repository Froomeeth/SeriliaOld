package serilia.entities.comp;

import ent.anno.Annotations;
import serilia.gen.entities.LiquidGraphUpdaterc;
import serilia.world.blocks.liquid.LiquidGraph;

@Annotations.EntityDef(value = LiquidGraphUpdaterc.class, serialize = false, genIO = false)
@Annotations.EntityComponent
public abstract class LiquidGraphUpdaterComp{
    public transient LiquidGraph graph;

    @Annotations.Replace(42)
    public void update(){
        graph.update();
    }
}
