package serilia.world.blocks.power;

import arc.math.geom.Geometry;
import mindustry.world.blocks.power.BeamNode;
import mindustry.world.blocks.power.PowerGraph;
import mindustry.world.blocks.power.PowerNode;

import static mindustry.Vars.world;

public class LaserNode extends BeamNode{
    public LaserNode(String name){
        super(name);
        rotate = true;
        range = 7;
    }

    public class LaserNodeBuild extends BeamNodeBuild{
        @Override
        public void updateDirections(){
            int i = rotation;
            var prev = links[i];
            var dir = Geometry.d4[i];
            links[i] = null;
            dests[i] = null;
            int offset = size/2;
            //find first block with power in range
            for(int j = 1 + offset; j <= range + offset; j++){
                var other = world.build(tile.x + j * dir.x, tile.y + j * dir.y);

                //hit insulated wall
                if(other != null && other.isInsulated()){
                    break;
                }

                //power nodes do NOT play nice with beam nodes, do not touch them as that forcefully modifies their links
                if(other != null && other.block.hasPower && other.block.connectedPower && other.team == team && !(other.block instanceof PowerNode)){
                    links[i] = other;
                    dests[i] = world.tile(tile.x + j * dir.x, tile.y + j * dir.y);
                    break;
                }
            }

            var next = links[i];

            if(next != prev){
                //unlinked, disconnect and reflow
                if(prev != null){
                    prev.power.links.removeValue(pos());
                    power.links.removeValue(prev.pos());

                    PowerGraph newgraph = new PowerGraph();
                    //reflow from this point, covering all tiles on this side
                    newgraph.reflow(this);

                    if(prev.power.graph != newgraph){
                        //reflow power for other end
                        PowerGraph og = new PowerGraph();
                        og.reflow(prev);
                    }
                }

                //linked to a new one, connect graphs
                if(next != null){
                    power.links.addUnique(next.pos());
                    next.power.links.addUnique(pos());

                    power.graph.addGraph(next.power.graph);
                }
            }

        }
    }
}
