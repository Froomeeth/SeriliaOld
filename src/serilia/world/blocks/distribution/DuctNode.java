package serilia.world.blocks.distribution;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.*;
import mindustry.world.blocks.distribution.Duct;

public class DuctNode extends Duct{

    public TextureRegion topRegion;
    @Override
    public void load() {
        super.load();
        topRegion = Core.atlas.find(name + "-top");
    }
    public DuctNode(String name) {
        super(name);

    }
    public class DuctBuild extends Building{
        @Override
        public void draw(){
            Draw.rect(region, x, y);
            Draw.rect(topRegion, x, y, rotdeg());

        }
    }
}
