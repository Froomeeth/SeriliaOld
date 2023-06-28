package serilia.world.blocks.misc;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.util.Nullable;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;

import static mindustry.world.meta.Stat.output;

public class PointsDerogatorily extends Block {
    public  TextureRegion finger;
    public  TextureRegion fingerStart;
    public  TextureRegion fingerEnd;
    public Effect trailEffect = Fx.hitBulletSmall;
    public @Nullable ItemStack outputItem;

    public float craftTime = 30;
    public PointsDerogatorily(String name) {
        super(name);
        update = true;
    }

    @Override
    public void load() {
        super.load();
        finger = Core.atlas.find(name + "-finger");
        fingerStart = Core.atlas.find(name + "-finger-start");
        fingerEnd = Core.atlas.find(name + "-finger-end");
    }

    public class PointerBuild extends Building{
        public float counter;
        public Unit target;
        public @Nullable ItemStack outputItem;
        @Override
        public void updateTile() {
            target = Units.closest(team, x, y, Unit::isFlying);

            if (target != null){
                counter += edelta();
                    items.set(outputItem.item, outputItem.amount);
                    dump(outputItem.item);
            }

        }
        public void draw(){
            if (target != null){
                Drawf.laser(finger, fingerStart, fingerEnd,
                        target.x,target.y, x, y, efficiency);
            }
            if (target != null) {
                trailEffect.at(target.x, target.y);
            }
        }
    }
}