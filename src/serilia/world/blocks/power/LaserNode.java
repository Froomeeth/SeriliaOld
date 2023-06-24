package serilia.world.blocks.power;

import arc.Core;
import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.gen.Building;
import mindustry.ui.Bar;
import mindustry.world.Block;
import serilia.world.blocks.power.laserbase.Laser;
import serilia.world.blocks.power.laserbase.LaserThing;

public class LaserNode extends Block{
    public float maxEmit = -1f, powerEfficiency = 0.7f;
    public int range = 15, height = 0;
    public boolean accept, emit = true, produce = true;

    public LaserNode(String name){
        super(name);
        update = true;
        rotate = true;
    }

    @Override
    public void setBars(){
        super.setBars();

        if(accept){
            addBar("laserpower", (LaserNodeBuild e) ->
                    new Bar(() -> Core.bundle.format("bar.poweramount", e.powerIn), () -> Color.white, () -> 1f)
            );
        }
        if(maxEmit > 0){
            addBar("laseremit", (LaserNodeBuild e) ->
                    new Bar(() -> Core.bundle.format("bar.unitcap", Core.bundle.get("bar.laser"), maxEmit * e.efficiency, maxEmit), () -> Color.white, () -> 1f)
            );
        }
    }

    public class LaserNodeBuild extends Building implements LaserThing{ //todo: LOOP PREVENTION AND FLUIDIC REWRITE
        public Seq<Laser> lasersIn = new Seq<>();
        public Laser laserOut;
        public float powerIn;

        @Override
        public void update(){
            super.update();
            if(laserOut != null) laserOut.update(produce ? maxEmit * efficiency : powerIn);

            if(accept){
                powerIn = 0;
                lasersIn.each(laser -> powerIn += laser.power);
                lasersIn.clear();
            }
        }

        @Override
        public void remove(){
            super.remove();
            if(laserOut != null){
                laserOut.update(0);
            }
        }

        @Override
        public void accept(Laser laser){ //todo turn down clock speed of these
            if(accept){
                lasersIn.add(laser);
            } else Laser.damage(this, laser);
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();

            if(laserOut == null && emit){
                laserOut = new Laser(range * 8f, height, this);
            }

            if(laserOut != null){
                laserOut.set(x, y, rotdeg());
                clipSize = laserOut.length + 24f;
            }
        }

        @Override
        public float getPowerProduction(){
            return (produce && accept) ? powerIn : 0f;
        }

        @Override
        public void draw(){
            if(laserOut != null) laserOut.draw();
        }
    }
}
