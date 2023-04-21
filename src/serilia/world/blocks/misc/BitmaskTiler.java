package serilia.world.blocks.misc;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.util.Tmp;
import mindustry.entities.TargetPriority;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

public class BitmaskTiler extends Wall{
    public TextureRegion[][] regions;
    public int regionWidth = 64, regionHeight = 64;

    public BitmaskTiler(String name){
        super(name);
        solid = true;
        destructible = true;
        group = BlockGroup.walls;
        buildCostMultiplier = 6f;
        canOverdrive = false;
        drawDisabled = false;
        crushDamageMultiplier = 5f;
        priority = TargetPriority.wall;
        envEnabled = Env.any;
    }

    @Override
    public void load() {
        super.load();

        Tmp.tr1.set(Core.atlas.find(name + "-sheet"));
        regions = Tmp.tr1.split(regionWidth, regionHeight); //todo needs the custom splitter function
    }

    public class BitTileBuild extends WallBuild{
        int tiling = 0;

        public boolean blends(Block block){
            return block == this.block;
        }

        @Override
        public void draw(){
            if(regions == null) return;
            Draw.rect(regions[tiling % 4][Mathf.floor(tiling / 4f)], x, y); //todo no inner corners yet
        }

        @Override
        public void onProximityUpdate(){
            tiling = 0;
            for(int i = 0; i < 4; i++){ //todo only checks cardinal directions rn
                Building build = this.nearby(Geometry.d4(i + 2 % 4).x * block.size + 1, Geometry.d4(i + 2 % 4).y * block.size + 1);
                if(build != null && blends(build.block)){
                    tiling |= (1 << i);
                }
            }
        }
    }
}
