package serilia.world.blocks.distribution;

import arc.Core;
import arc.math.geom.Geometry;
import mindustry.gen.Building;
import mindustry.ui.Bar;
import serilia.content.SeFxPal;
import serilia.world.blocks.distribution.TubeMotor.TubeMotorBuild;

public class ConveyorTube extends ShadedDuct{
    public int[][] ductArrows = {
            {1, 1, 1, 0, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 2, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 0, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 2, 1, 1, 1}
    };

    @Override
    public void setBars(){
        super.setBars();
        addBar("carrydist", (ConveyorTubeBuild e) ->
                new Bar(() -> Core.bundle.format("bar.carrydist", e.carryDst), () -> SeFxPal.coreReactor, () -> e.lastMotor == null ? 0 : (e.carryDst + 1f) / (((TubeMotorBuild)e.lastMotor).carryDst + 1f))
        );
    }

    public ConveyorTube(String name){
        super(name);
    }

    public class ConveyorTubeBuild extends ShadedDuctBuild{
        public Building back, lastMotor;
        public int carryDst;

        @Override
        public void update(){
            super.update();

            if(back != null && back.front() == this && (lastMotor == null || lastMotor != this)){
                carryDst = ((ConveyorTubeBuild) back).carryDst - 1;
                lastMotor = ((ConveyorTubeBuild) back).lastMotor;
            }

            enabled = carryDst >= 0; //todo circuit network support
        }

        @Override
        public void onProximityUpdate(){
            //copy paste
            noSleep();
            next = front();
            nextc = next instanceof DuctBuild d ? d : null;

            tiling = 0;
            for(int i = 0; i < 4; i++){
                Building b = nearby(Geometry.d4(i).x, Geometry.d4(i).y);
                if(i == rotation || b != null && (b instanceof ConveyorTubeBuild ? (b.front() != null && b.front() == this) : b.block.outputsItems())){
                    tiling |= (1 << i);
                }
            }

            //not copy paste
            if(back() != null && back() instanceof ConveyorTubeBuild){
                back = back();
            }
            carryDst = 0;
        }
    }
}
