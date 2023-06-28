package serilia.world.blocks.distribution;

import arc.Core;
import arc.func.Boolf;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.content.Blocks;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.blocks.distribution.Duct;
import mindustry.world.blocks.distribution.DuctBridge;
import mindustry.world.blocks.distribution.Junction;
import mindustry.world.blocks.distribution.StackConveyor.StackConveyorBuild;
import serilia.util.SeUtil;

import static mindustry.Vars.itemSize;
import static mindustry.Vars.tilesize;

public class ShadedDuct extends Duct{ //todo junction replacement
    public TextureRegion[][] regionLayers;
    public int[][] ductArrows = {
            {1, 1, 1, 0, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 2, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 0, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 2, 1, 1, 1}
    };

    public ShadedDuct(String name){
        super(name);
    }
    public Block junctionReplacement, bridgeReplacement;

    @Override
    public void init(){
        super.init();
        if(junctionReplacement == null) junctionReplacement = Blocks.junction;
    }

    @Override
    public void load(){
        super.load();
        regionLayers = SeUtil.splitLayers(name + "-sheet", 32, 2);
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{Core.atlas.find(name)};
    }

    @Override
    public Block getReplacement(BuildPlan req, Seq<BuildPlan> plans){
        if(junctionReplacement == null) return this;

        Boolf<Point2> cont = p -> plans.contains(o -> o.x == req.x + p.x && o.y == req.y + p.y && (req.block instanceof ShadedDuct || req.block instanceof Junction));
        return cont.get(Geometry.d4(req.rotation)) &&
                cont.get(Geometry.d4(req.rotation - 2)) &&
                req.tile() != null &&
                req.tile().block() instanceof ShadedDuct &&
                Mathf.mod(req.tile().build.rotation - req.rotation, 2) == 1 ? junctionReplacement : this;
    }

    @Override
    public void handlePlacementLine(Seq<BuildPlan> plans){

    }

    public class ShadedDuctBuild extends DuctBuild{
        public int tiling = 0;

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
            Draw.rect(regionLayers[1][ductArrows[rotation][tiling] + 1], x, y, rotdeg());
        }

        @Override
        public void onProximityUpdate(){
            noSleep();
            next = front();
            nextc = next instanceof DuctBuild d ? d : null;

            tiling = 0;

            for(int i = 0; i < 4; i++){
                Building b = nearby(Geometry.d4(i).x, Geometry.d4(i).y);
                if(i == rotation || b != null && (b instanceof DuctBuild ? (b.front() != null && b.front() == this) : (b.block.outputsItems() && !(b instanceof StackConveyorBuild stack && stack.state != 2)))){
                    tiling |= (1 << i);
                }
            }
        }
    }
}
