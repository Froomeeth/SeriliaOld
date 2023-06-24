package serilia.world.blocks.power;

import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.world.blocks.defense.Wall;
import serilia.util.SeUtil;

/**No special functionality, it just uses bitmask tiling.*/
public class PowerWire extends Wall{
    public TextureRegion[][] regionLayers;

    public PowerWire(String name){
        super(name);
        conductivePower = update = hasPower = true; //todo add limit?
        rotate = consumesPower = false;
    }

    @Override
    public void load(){
        super.load();
        regionLayers = SeUtil.splitLayers(name + "-sheet", 32, 2);
    }

    public class PowerWireBuild extends WallBuild{
        public int tiling = 0;

        @Override
        public void draw(){
            Draw.z(Layer.blockUnder);
            Draw.rect(regionLayers[0][tiling], x, y, 0f);

            if(power != null){
                Draw.blend(Blending.additive);
                Draw.alpha(power.graph.getLastPowerProduced() / 100f);
                Draw.rect(regionLayers[1][tiling], x, y, 0f);
                Draw.blend();
            }
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();

            tiling = 0;

            for(int i = 0; i < 4; i++){
                Building b = nearby(Geometry.d4(i).x, Geometry.d4(i).y);
                if(b != null && (b instanceof PowerWireBuild || b.block.hasPower)){
                    tiling |= (1 << i);
                }
            }
        }
    }
}
