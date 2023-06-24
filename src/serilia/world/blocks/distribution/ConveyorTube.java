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

@SuppressWarnings("DanglingJavadoc")
public class ConveyorTube extends ShadedDuct{
    public int[] ductArrows = {1, 1, 1, 6, 1, 1, 6, 6, 1, 6, 1, 6, 6, 6, 6, 6};

    @Override
    public void setBars(){
        super.setBars();
        /**You can ignore this for a while, I would in your case.
         * Lambda function (() -> {}). I still don't entirely understand how they know certain things yet. The () is its arguments.
         * Used as providers (provs) for the values of the bar here.*/
        addBar("carrydist", (ConveyorTubeBuild entity) ->
                new Bar(() -> entity.carryDst < 0 ? Core.bundle.get("bar.nomotor") : Core.bundle.format("bar.carrydist", entity.carryDst), //Select between the bundles. See "? :" below.
                        () -> SeFxPal.coreReactor, //Provide color of bar.
                        () -> entity.lastMotor == null ? 0 : (entity.carryDst + 1f) / (((TubeMotorBuild)entity.lastMotor).carryDst + 1f) //Provide the percentage the bar should be at. 0-1.
                )
        );
    }

    /**Constructor. Used when you do 'new ConveyorTube("powered-conveyor-tube")'*/
    public ConveyorTube(String name){
        /**super() is used to call the function you're overriding. Constructors are a bit special, so they don't actually override anything.*/
        super(name);
        canOverdrive = false;
    }

    //This implements the TubeThing interface, see motor code (when I comment that).
    public class ConveyorTubeBuild extends ShadedDuctBuild implements TubeThing{
        Building back, lastMotor;
        int carryDst = -150;
        float driveSpeed;

        /**updateTile() is called only when this block is enabled.
         * if you need something to change regardless, override update() instead.*/
        @Override
        public void updateTile(){
            //the only part that actually matters
            if(carryDst >= 0 && lastMotor != null){

                //copy paste of duct
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
            //set layer to below blocks
            Draw.z(Layer.blockUnder);
            /**Array contents are accessed with [index].
             * Arrays of arrays are made by adding more [] to the definition (so public TextureRegion[][] regionLayers).
             * Here I select array 1 (the second layer's sprites) and sprite 0 (the bottom sprite).*/
            Draw.rect(regionLayers[1][0], x, y, 0f);

            //draw item (stolen from duct)
            if(current != null){
                Draw.z(Layer.blockUnder + 0.1f);
                Tmp.v1.set(Geometry.d4x(recDir) * tilesize / 2f, Geometry.d4y(recDir) * tilesize / 2f)
                        .lerp(Geometry.d4x(rotation) * tilesize / 2f, Geometry.d4y(rotation) * tilesize / 2f,
                                Mathf.clamp((progress + 1f) / 2f));

                Draw.rect(current.fullIcon, x + Tmp.v1.x, y + Tmp.v1.y, itemSize, itemSize);
            }

            Draw.z(Layer.blockUnder + 0.2f);
            //Draw a rectangle with the TextureRegion in array 0, using sprite at <tiling>
            Draw.rect(regionLayers[0][tiling], x, y, 0f);
            //yes you can get numbers from an array and use them as an index to get something from another array
            Draw.rect(regionLayers[1][ductArrows[tiling] + rotation], x, y , -8f, 8f, -90f);
        }

        /**onProximityUpdate is called when something in the proximity changes.
         * Use for things like resetting the tiling of this block and other values that depend on other blocks and/or their existence.*/
        @Override
        public void onProximityUpdate(){
            //copy paste
            noSleep();
            next = front();
            nextc = next instanceof DuctBuild d ? d : null;

            //reset to 0
            tiling = 0;

            /**Use a for loop to do something for a certain amount of times/objects.*/
            for(
                int i = 0; //The first is done once before it starts looping at all.
                i < 4;     //This one is the condition for it actually loop. If you want to run code until one condition completes, use while(boolean).
                i++        //The last is done after all the code below has finished. i++ counts i upwards by 1.
            ){
                /**Get an adjacent building at rotation i using d4.
                 * d4/d8 are just lists of numbers from -1 to 1 that you use to do something in 4/8 directions.*/
                Building b = nearby(Geometry.d4(i).x, Geometry.d4(i).y);

                //I think you know what an if() is
                if(
                    i == rotation || //if this direction is the one it's facing OR (||)
                    b != null &&     //the building is not null (prevent NPE) AND (&&)
                                     //the condition in () below gave a true.

                    ( //() so it's considered as one value
                        /**Use ? : to switch between values depending on a condition.*/
                        b instanceof ConveyorTubeBuild ?               //Is the adjacent building a conveyor tube?
                            (b.front() != null && b.front() == this) : //Yes, so I check if it's facing this one.
                            b.block.outputsItems()                     //No, so we just check if it outputs items.
                    )
                ){
                    /**The bitmasking thing willycow so violently opposed. Also used in Env.
                     * If we have a corner for example, the bits might look something like 0110 (6 in integer form):
                     *        1
                     *     1  #  0   "#" representing the tile in the middle
                     *        0
                     * Here we use that if() to dictate which bits should be true and which not.*/
                    tiling |= (1 << i); //Bitwise operators. << moves the 1 to the left by i digits while |= is used to add that bit to the existing ones.
                }
            }

            //reset all of these
            carryDst = -150;
            lastMotor = null;
            driveSpeed = 0f;
            if(back() != null && back() instanceof ConveyorTubeBuild){
                back = back();
            }
        }

        @Override
        public Building lastMotor(){
            return lastMotor;
        }

        @Override
        public void lastMotor(TubeMotorBuild set){
            lastMotor = set;
        }

        @Override
        public int carryDst(){
            return carryDst;
        }

        @Override
        public void carryDst(int set){
            carryDst = set;
        }

        @Override
        public float driveSpeed(){
            return driveSpeed;
        }

        @Override
        public void driveSpeed(float set){
            driveSpeed = set;
        }
    }
}
