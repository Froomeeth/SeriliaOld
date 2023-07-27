package serilia.world.blocks.environment;

import arc.graphics.g2d.TextureRegion;
import mindustry.world.Block;
import mindustry.world.Tile;
import serilia.util.SeUtil;

public class Outlet extends Block{
    public float powerGen = 25f/60f*5f;
    public TextureRegion[][] rotateRegions;

    public Outlet(String name){
        super(name);
        rotate = true;
    }

    @Override
    public void init(){
        super.init();
        hasShadow = true;
    }

    @Override
    public void load(){
        super.load();
        SeUtil.splitLayers(name + "-sheet", region.width, 3);
    }

    @Override
    public void drawBase(Tile tile){

    }

    @Override
    public void drawShadow(Tile tile){

    }
}
