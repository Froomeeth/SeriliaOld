package serilia.world.blocks.distribution;

import arc.Core;
import arc.func.Boolf;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Tmp;
import mindustry.content.Blocks;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.blocks.distribution.Duct;
import mindustry.world.blocks.distribution.Junction;
import serilia.content.AhkarBlocks;
import serilia.content.CaliBlocks;
import serilia.util.SeUtil;

import static mindustry.Vars.itemSize;
import static mindustry.Vars.tilesize;

public class HeavyDuct extends Duct{
    public TextureRegion[] regions;

    public HeavyDuct(String name){
        super(name);
    }
    public Block junctionReplacement;

    @Override
    public void init(){
        super.init();
        if(junctionReplacement == null) junctionReplacement = Blocks.junction;
    }

    @Override
    public void load(){
        super.load();
        regions = SeUtil.split(name + "-sheet", 32, 0);
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{Core.atlas.find(name)};
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){
        Draw.rect(region, plan.drawx(), plan.drawy(), 8, plan.rotation == 1 || plan.rotation == 2 ? -8 : 8, plan.rotation * 90);
    }

    @Override
    public Block getReplacement(BuildPlan req, Seq<BuildPlan> plans){
        if(junctionReplacement == null) return this;

        Boolf<Point2> cont = p -> plans.contains(o -> o.x == req.x + p.x && o.y == req.y + p.y && (req.block instanceof HeavyDuct || req.block instanceof Junction));
        return cont.get(Geometry.d4(req.rotation)) &&
                cont.get(Geometry.d4(req.rotation - 2)) &&
                req.tile() != null &&
                req.tile().block() instanceof HeavyDuct &&
                Mathf.mod(req.tile().build.rotation - req.rotation, 2) == 1 ? junctionReplacement : this;
    }

    @Override
    public void handlePlacementLine(Seq<BuildPlan> plans){}

    public class HeavyDuctBuild extends DuctBuild{
        public int state = 0;
        public Building last;

        @Override
        public void draw(){ //TODO squaresprite
            Draw.z(Layer.blockUnder);
            Draw.rect(regions[0], x, y, 0f);

            //draw item
            if(current != null){
                Draw.z(Layer.blockUnder + 0.1f);
                Tmp.v1.set(Geometry.d4x(recDir) * tilesize / 2f, Geometry.d4y(recDir) * tilesize / 2f)
                        .lerp(Geometry.d4x(rotation) * tilesize / 2f, Geometry.d4y(rotation) * tilesize / 2f,
                                Mathf.clamp((progress + 1f) / 2f));

                Draw.rect(current.fullIcon, x + Tmp.v1.x, y + Tmp.v1.y, itemSize, itemSize);
            }

            Draw.z(Layer.blockUnder + 0.2f);
            Draw.rect(regions[state == 4 ? 2 : state + 1], x, y, state == 4 ? -8 : 8, rotation == 1 || rotation == 2 ? -8 : 8, rotdeg());
            Draw.rect(regions[4], x, y, rotdeg());
        }


        public boolean acceptFrom(Building build){ //TODO fix ends capping at block inputs
            return build != null && (build == last || build == next) && ((block == build.block && build.rotation == rotation) || build.block == Blocks.itemSource || (
                    block == CaliBlocks.heavyDuct && (CaliBlocks.ductJunction == build.block || CaliBlocks.ductNode == build.block) ||
                    block == AhkarBlocks.heavyDuct && (AhkarBlocks.ductJunction == build.block || AhkarBlocks.ductInserter == build.block)
            ));
        }

        @Override
        public void onProximityUpdate(){
            noSleep();
            next = front();
            last = back();
            nextc = next instanceof DuctBuild d ? d : null;

            state = 0;
            if(acceptFrom(next)){
                state += 1;
                if(acceptFrom(last))
                    state += 1;
            } else if(acceptFrom(last))
                state = 4;
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return current == null && items.total() == 0 && source != next && acceptFrom(source);
        }
    }
}
