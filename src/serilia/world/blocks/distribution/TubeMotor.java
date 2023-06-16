package serilia.world.blocks.distribution;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.world.Tile;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class TubeMotor extends ConveyorTube{
    public float baseCarryDst = 7;
    public int maxDst = 10;

    public TubeMotor(String name){
        super(name);
        canOverdrive = true;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        int dx = Geometry.d4x(rotation), dy = Geometry.d4y(rotation);
        Drawf.dashLine(Pal.placing,
                x * tilesize + dx * (tilesize / 2f + 2),
                y * tilesize + dy * (tilesize / 2f + 2),
                x * tilesize + dx * (baseCarryDst * tilesize),
                y * tilesize + dy * (baseCarryDst * tilesize)
        );
    }

    public class TubeMotorBuild extends ConveyorTubeBuild{


        @Override
        public void updateTile(){
            super.updateTile();
            carryDst = Mathf.floor(baseCarryDst * efficiency); //todo boost
            driveSpeed = (speed * efficiency / timeScale);
        }

        public void updateLine(int dst, int carDst, float drvSpd, TubeMotorBuild motor){
            int dx = Geometry.d4x(rotation), dy = Geometry.d4y(rotation);
            for(int i = 1; i <= dst; i++){
                Tile other = world.tile(tileOn().x + dx * i, tileOn().y + dy * i);

                if(other.build instanceof TubeThing tube && !(other.build instanceof TubeMotorBuild)){
                    tube.carryDst(Mathf.floor(carDst - i));
                    tube.driveSpeed(drvSpd);
                    if(carDst - i >= 0) tube.lastMotor(motor);
                } else break; //todo bridge
            }
        }

        @Override
        public void update(){
            super.update();
            updateLine(maxDst, carryDst, speed * efficiency / timeScale, this);
        }

        @Override
        public void remove(){
            super.remove();
            updateLine(maxDst, 0, 0f,null);
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
            lastMotor = this;
        }

        @Override
        public void draw(){
            Draw.rect(block.region, this.x, this.y, drawrot());
            Draw.rect("arrow", this.x, this.y, drawrot());
        }
    }
}
