package serilia.world.blocks.distribution;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.world.blocks.distribution.Duct;

public class DuctNode extends Duct{
    public TextureRegion topRegion;
    @Override
    public void load() {
        super.load();
        topRegion = Core.atlas.find(name + "-top");
    }
    @Override
    public void drawPlanRegion(){
        Draw.rect(region, x, y);
        Draw.color(Pal.accent);
        Draw.rect(topRegion, x, y, rotdeg());
        Draw.color();
    }
    public DuctNode(String name) {
        super(name);

    }
    public class DuctNodeBuild extends DuctBuild{
        public int chainCount;
        @Override
        public void draw(){
            Draw.rect(region, x, y);
            Draw.color(chainCount >= 2 ? Color.red : Pal.accent);
            Draw.rect(topRegion, x, y, rotdeg());
            Draw.color();
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
            if (next != null && next instanceof DuctNodeBuild duct){
                duct.chainCount = chainCount+1;
            }
        }
        @Override
        public void update(){
            if(chainCount < 2){
                super.update();
            }
        }
    }
}
