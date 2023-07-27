package serilia.world.blocks.power;

import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.blocks.power.PowerGraph;

import static serilia.util.SeUtil.getGraphWithin;

public class LaserShidder extends Block{
    public float baseShitAmount = 2f;
    public int laserRange = 10;

    public LaserShidder(String name){
        super(name);
        update = rotate = insulated = solid = true;
    }

    public class LaserShidderBuild extends Building{
        public float shidding = baseShitAmount;
        public PowerGraph laserGraph;

        @Override
        public void updateTile(){
            if(laserGraph != null){ laserGraph.transferPower(shidding * edelta());
                Fx.landShock.at(this);
            }
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
            laserGraph = getGraphWithin(this, laserRange, rotation); //add port positions?
        }
    }
}
