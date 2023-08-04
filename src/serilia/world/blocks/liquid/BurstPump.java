package serilia.world.blocks.liquid;


import arc.Core;
import arc.audio.Sound;
import arc.math.Mathf;
import arc.util.Nullable;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.world.Tile;
import mindustry.world.blocks.production.Pump;

public class BurstPump extends Pump {
    public float pumpTime = 180;
    public float outputAmount = 100;
    public Sound pumpSound = Sounds.drillImpact;
    public float pumpSoundVolume = 0.6f, pumpSoundPitchRand = 0.1f;
    public void setBars(){
        super.setBars();

        addBar("pumpProgress", (BurstPumpBuild e) ->
                new Bar(() -> Core.bundle.get("bar.pumpProgress"), () -> Pal.ammo, () -> e.counter/pumpTime));
    }

    public BurstPump(String name) {
        super(name);
    }

    public class BurstPumpBuild extends PumpBuild {
        public float counter;
        public @Nullable Liquid liquidDrop = null;
        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();

            amount = 0f;
            liquidDrop = null;

            for(Tile other : tile.getLinkedTiles(tempTiles)){
                if(canPump(other)){
                    liquidDrop = other.floor().liquidDrop;
                    amount += outputAmount/(size * size);
                }
            }
        }
        @Override
        public void updateTile() {
            if (liquidDrop != null) {
                counter += edelta();
                dumpLiquid(liquidDrop);
                if (counter >= pumpTime) {
                    float maxPump = Math.min(liquidCapacity - liquids.get(liquidDrop), amount);
                    counter %= pumpTime;
                    liquids.add(liquidDrop, maxPump);
                    pumpSound.at(x, y, 1f + Mathf.range(pumpSoundPitchRand), pumpSoundVolume);
                }
            }
        }
    }
}