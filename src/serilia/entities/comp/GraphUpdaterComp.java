package serilia.entities.comp;

import ent.anno.Annotations;
import serilia.gen.entities.GraphUpdaterc;
import serilia.types.Graph;

@Annotations.EntityDef(value = GraphUpdaterc.class, serialize = false, genIO = false)
@Annotations.EntityComponent
public abstract class GraphUpdaterComp{
    public transient Graph graph;

    @Annotations.Replace(42)
    public void update(){
        graph.update();
    }
}
