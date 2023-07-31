package serilia.world.blocks.power;

import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.blocks.power.PowerGraph;

import static serilia.util.SeUtil.getGraphWithin;

public class LaserEaterOrDivider extends Block{
    public boolean eat = true;
    public int laserRange = 7;

    public LaserEaterOrDivider(String name){
        super(name);
        update = rotate = insulated = solid = true;

        //accesses graph power directly, do not want it to connect to anything at all
        hasPower = false;
    }

    public class MMMMMLaserDelishBuild extends Building{
        public PowerGraph graphIn, graphOut, graphLeft, graphRight;
        public float splitPercent = 0.5f;

        @Override
        public void updateTile(){
            if(graphIn == null) return;

            float backStored = graphIn.getBatteryStored();
            graphIn.transferPower(-backStored);

            if(eat && graphOut != null){
                graphOut.transferPower(backStored);
            }else{
                if(graphLeft != null) graphLeft.transferPower(backStored * splitPercent);
                if(graphRight != null) graphRight.transferPower(backStored * (1 - splitPercent));
            }
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
            graphIn = graphOut = graphLeft = graphRight = null;

            //in
            graphIn = getGraphWithin(this, laserRange, rotation + 2, rotation);
            //out
            if(eat){
                if(front() != null && front().block.hasPower) graphOut = front().power.graph;
            }else{
                graphLeft = getGraphWithin(this, laserRange, rotation + 3);
                graphRight = getGraphWithin(this, laserRange, rotation + 1);
            }
        }
    }
}
