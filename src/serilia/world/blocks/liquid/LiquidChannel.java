package serilia.world.blocks.liquid;

import mindustry.gen.Building;
import mindustry.world.Block;

public class LiquidChannel extends Block{
    public LiquidChannel(String name){
        super(name);
        update = true;
        solid = true;
        hasLiquids = true;
        outputsLiquid = true;
    }


    public class LiquidChannelBuild extends Building{ //todo temp support

    }
}
