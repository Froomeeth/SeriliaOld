package serilia.world.blocks.distribution;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.util.Tmp;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.ui.Bar;
import serilia.content.SeFxPal;
import serilia.world.blocks.distribution.TubeMotor.TubeMotorBuild;

import static mindustry.Vars.itemSize;
import static mindustry.Vars.tilesize;

public class ConveyorTube extends ShadedDuct{
    public int[] ductArrows = {1, 1, 1, 6, 1, 1, 6, 6, 1, 6, 1, 6, 6, 6, 6, 1};

    @Override
    public void setBars(){
        super.setBars();
        addBar("carrydist", (ConveyorTubeBuild e) ->
                new Bar(() -> Core.bundle.format("bar.carrydist", e.carryDst), () -> SeFxPal.coreReactor, () -> e.lastMotor == null ? 0 : (e.carryDst + 1f) / (((TubeMotorBuild)e.lastMotor).carryDst + 1f))
        );
    }

    public ConveyorTube(String name){
        super(name);
        canOverdrive = false;
    }

    public class ConveyorTubeBuild extends ShadedDuctBuild{
        public Building back, lastMotor;
        public int carryDst;
        public float driveSpeed;

        @Override
        public void update(){
            super.update();

            if(back != null && back.front() == this && (lastMotor == null || lastMotor != this)){
                carryDst = ((ConveyorTubeBuild) back).carryDst - 1;
                lastMotor = ((ConveyorTubeBuild) back).lastMotor;
                if(lastMotor != null) driveSpeed = ((ConveyorTubeBuild)lastMotor).driveSpeed;
            }
        }

        @Override
        public void updateTile(){
            if(carryDst >= 0){
                if(lastMotor == null) return;
                progress += edelta() / driveSpeed * 2f;

                if(current != null && next != null){
                    if(progress >= (1f - 1f / driveSpeed) && moveForward(current)){
                        items.remove(current, 1);
                        current = null;
                        progress %= (1f - 1f / driveSpeed);
                    }
                }else{
                    progress = 0;
                }

                if(current == null && items.total() > 0){
                    current = items.first();
                }
            }
        }

        @Override
        public void draw(){
            Draw.z(Layer.blockUnder);
            Draw.rect(regionLayers[1][0], x, y, 0f);

            //draw item
            if(current != null){
                Draw.z(Layer.blockUnder + 0.1f);
                Tmp.v1.set(Geometry.d4x(recDir) * tilesize / 2f, Geometry.d4y(recDir) * tilesize / 2f)
                        .lerp(Geometry.d4x(rotation) * tilesize / 2f, Geometry.d4y(rotation) * tilesize / 2f,
                                Mathf.clamp((progress + 1f) / 2f));

                Draw.rect(current.fullIcon, x + Tmp.v1.x, y + Tmp.v1.y, itemSize, itemSize);
            }

            Draw.z(Layer.blockUnder + 0.2f);
            Draw.rect(regionLayers[0][tiling], x, y, 0f);
            Draw.rect(regionLayers[1][ductArrows[tiling] + rotation], x, y , -8f, 8f, -90f);
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
            lastMotor = null;
            driveSpeed = 0f;
        }
    }
}
