package serilia.world.blocks.production;

import arc.audio.Sound;
import arc.math.Interp;
import arc.math.Mathf;
import arc.struct.ObjectFloatMap;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Sounds;
import mindustry.type.Item;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawMulti;
import mindustry.world.draw.DrawRegion;

public class DrawerBurstDrill extends DrawerDrill {
    public float shake = 2f;
    public Interp speedCurve = Interp.pow2In;

    public Sound drillSound = Sounds.drillImpact;
    public float drillSoundVolume = 0.6f, drillSoundPitchRand = 0.1f;

    /** Multipliers of drill speed for each item. Defaults to 1. */
    public ObjectFloatMap<Item> drillMultipliers = new ObjectFloatMap<>();

    public DrawerBurstDrill(String name){
        super(name);

        //does not drill in the traditional sense, so this is not even used
        hardnessDrillMultiplier = 0f;
        liquidBoostIntensity = 1f;
        //generally at center
        drillEffectRnd = 0f;
        drillEffect = Fx.shockwave;
        ambientSoundVolume = 0.18f;
        ambientSound = Sounds.drillCharge;
        drawer = new DrawMulti(
            new DrawDefault(),
            new DrawRegion("-top")
        );
    }

    @Override
    public float getDrillTime(Item item){
        return drillTime / drillMultipliers.get(item, 1f);
    }

    public class DrawerBurstBuild extends DrawerDrillBuild{
        //used so the lights don't fade out immediately
        public float smoothProgress = 0f;

        @Override
        public void updateTile(){
            if(dominantItem == null){
                return;
            }

            if(timer(timerDump, dumpTime)){
                dump(items.has(dominantItem) ? dominantItem : null);
            }

            float drillTime = getDrillTime(dominantItem);

            smoothProgress = Mathf.lerpDelta(smoothProgress, progress / (drillTime - 20f), 0.1f);

            if(items.total() <= itemCapacity - dominantItems && dominantItems > 0 && efficiency > 0){
                warmup = Mathf.approachDelta(warmup, progress / drillTime, 0.01f);

                float speed = efficiency;

                totalProgress += speedCurve.apply(progress / drillTime) * speed;

                lastDrillSpeed = 1f / drillTime * speed * dominantItems;
                progress += delta() * speed;
            }else{
                warmup = Mathf.approachDelta(warmup, 0f, 0.01f);
                lastDrillSpeed = 0f;
                return;
            }

            if(dominantItems > 0 && progress >= drillTime && items.total() < itemCapacity){
                for(int i = 0; i < dominantItems; i++){
                    offload(dominantItem);
                }

                progress %= drillTime;

                if(wasVisible){
                    Effect.shake(shake, shake, this);
                    drillSound.at(x, y, 1f + Mathf.range(drillSoundPitchRand), drillSoundVolume);
                    drillEffect.at(x + Mathf.range(drillEffectRnd), y + Mathf.range(drillEffectRnd), dominantItem.color);
                }
            }
        }

        @Override
        public float ambientVolume(){
            return super.ambientVolume() * Mathf.pow(progress(), 4f);
        }

        @Override
        public boolean shouldConsume(){
            return items.total() <= itemCapacity - dominantItems && enabled;
        }

        @Override
        public void draw(){
            drawer.draw(this);
        }
    }
}
