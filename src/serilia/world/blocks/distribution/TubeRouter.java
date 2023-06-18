package serilia.world.blocks.distribution;

import arc.struct.Seq;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.Router;
import serilia.world.blocks.distribution.TubeMotor.TubeMotorBuild;

public class TubeRouter extends Router{
    public int minAllSideMotors = 2;

    public TubeRouter(String name){
        super(name);
    }

    public class TubeRouterBuild extends RouterBuild implements TubeThing{
        public float driveSpeed;
        public Seq<TubeMotorBuild> motors = new Seq<>(), lastMotors = new Seq<>();

        @Override
        public void updateTile(){
            if(lastItem == null && items.any()){
                lastItem = items.first();
            }

            if(lastItem != null){
                time += 1f / driveSpeed * delta();
                Building target = getTileTarget(lastItem, lastInput, false);

                if(target != null && (time >= 1f || !(target.block instanceof Router || target.block.instantTransfer))){
                    getTileTarget(lastItem, lastInput, true);
                    target.handleItem(this, lastItem);
                    items.remove(lastItem, 1);
                    lastItem = null;
                }
            }
        }

        @Override
        public Building getTileTarget(Item item, Tile from, boolean set){
            int counter = rotation;
            for(int i = 0; i < proximity.size; i++){
                Building other = proximity.get((i + counter) % proximity.size);
                if(set) rotation = ((byte)((rotation + 1) % proximity.size));
                if(other.acceptItem(this, item) && (motors.size >= minAllSideMotors && !(other instanceof TubeThing)|| other instanceof TubeThing tube && tube.carryDst() >= 0)){
                    return other;
                }
            }
            return null;
        }

        @Override
        public void drawSelect(){ //todo draw motor connections
            Drawf.square(x, y, size * 8f, 0f);
            lastMotors.each(motor -> {
                Drawf.square(x, y, 8f, 0f);
            });
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return super.acceptItem(source, item) && source instanceof TubeThing;
        }

        @Override
        public void update(){ //todo awful awful awful
            super.update();
            lastMotors.set(motors);
            clearMotors();
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
            clearMotors();
        }

        @Override
        public float driveSpeed(){
            return driveSpeed;
        }

        @Override
        public void driveSpeed(float set){
            driveSpeed = set;
        }

        @Override
        public void addMotor(TubeMotorBuild add){
            motors.add(add);
        }

        @Override
        public void clearMotors(){
            motors.clear();
        }
    }
}
