package serilia.world.blocks.power;

import mindustry.world.blocks.power.BeamNode;

public class LaserNode extends BeamNode{
    public LaserNode(String name){
        super(name);
        update = true;
        solid = true;
        rotate = true;
    }

    public class LaserNodeBuild extends BeamNodeBuild{

    }
}
