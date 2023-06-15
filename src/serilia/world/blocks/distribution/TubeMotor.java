package serilia.world.blocks.distribution;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import mindustry.gen.Building;
import mindustry.type.Item;

public class TubeMotor extends ConveyorTube{
    public float baseCarryDst = 7;

    public TubeMotor(String name){
        super(name);
    }

    public class TubeMotorBuild extends ConveyorTubeBuild{

        @Override
        public void updateTile(){
            super.updateTile();
            carryDst = Mathf.floor(baseCarryDst * efficiency);
            lastMotor = this;
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return current == null && items.total() == 0 && source != null && source == back;
        }

        @Override
        public void onProximityUpdate(){
            noSleep();
            next = front();
            nextc = next instanceof DuctBuild d ? d : null;
            back = back();
        }

        @Override
        public void draw(){
            Draw.rect(block.region, this.x, this.y, drawrot());
            Draw.rect("arrow", this.x, this.y, drawrot());
        }
    }
}
