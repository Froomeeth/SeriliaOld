package serilia.world.blocks.misc;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.struct.Seq;
import mindustry.entities.TargetPriority;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

public class BitmaskTiler extends Wall{
    Seq<Seq<TextureRegion>> regions = new Seq<>();
    public int layerCount = 1;
    public int regionSize = 32;

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

        for(int i = 0; i < layerCount; i++){
            regions.add(split(Core.atlas.find(name + "-sheet"), regionSize, i));
        }
    }

    public Seq<TextureRegion> split(TextureRegion texture, int size, int layer){
        if(texture == null) return null;
        int x = texture.getX(), y = texture.getY();
        int width = texture.width;
        int margin = 2;

        int countX = width / size;

        Seq<TextureRegion> tiles = new Seq<>();
        
        for(int steppedX = 0; steppedX < countX; steppedX++, x += size){
            tiles.add(new TextureRegion(texture, x + 1 + (steppedX * margin), y + 1 + (layer * margin), size, size));
        }

        return tiles;
    }

    public class BitTileBuild extends WallBuild{
        int tiling = 0;

        public boolean blends(Block block){
            return block == this.block;
        }

        @Override
        public void draw(){
            if(regions == null) return;
            //Log.info(regions.get(regions.size - 1).get(tiling));
            Draw.rect(regions.get(regions.size - 1).get(tiling), x, y); //only draw topmost in base class
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
