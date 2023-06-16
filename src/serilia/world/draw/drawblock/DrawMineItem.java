package serilia.world.draw.drawblock;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;
import serilia.world.blocks.production.DrawerDrill;

public class DrawMineItem extends DrawBlock {
    public TextureRegion item;
    public int alpha = 1;
    public Blending blend = Blending.normal;

    @Override
    public void draw(Building build){
        if(!(build instanceof DrawerDrill.DrawerDrillBuild drill) || drill.dominantItem == null) return;

        Draw.color(drill.dominantItem.color, alpha);
        Draw.blend(blend);

        Draw.rect(item, build.x, build.y);

        Draw.color();
        Draw.blend();
    }

    @Override
    public void load(Block block){
        item = Core.atlas.find(block.name + "-item");
    }
}
