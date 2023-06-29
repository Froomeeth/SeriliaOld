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

public class DrillTurret extends Block {
    public  TextureRegion laser;
    public  TextureRegion laserStart;
    public  TextureRegion laserEnd;
    public Effect trailEffect = Fx.hitBulletSmall;
    public @Nullable ItemStack outputItem;
    public float craftTime = 30;
    public DrillTurret(String name) {
        super(name);
        update = true;
    }

    @Override
    public void load() {
        super.load();
        laser = Core.atlas.find(name + "-laser");
        laserStart = Core.atlas.find(name + "-laser-start");
        laserEnd = Core.atlas.find(name + "-laser-end");
    }

    public class DrillTurretBuild extends Building{
        public float counter;
        public Unit target;
        public @Nullable ItemStack outputItem;
        @Override
        public void updateTile() {
            target = Units.closest(team, x, y, Unit::isFlying);

            if (target != null){
                counter += edelta();
                    if (outputItem != null) {
                        items.set(outputItem.item, outputItem.amount);
                        offload(outputItem.item);
                    }
            }

        }
        public void draw(){
            if (target != null){
                Drawf.laser(laser, laserStart, laserEnd, target.x,target.y, x, y, efficiency);
            }
            if (target != null) {
                trailEffect.at(target.x, target.y);
            }
        }
    }
}