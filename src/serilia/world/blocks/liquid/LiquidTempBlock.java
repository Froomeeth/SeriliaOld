package serilia.world.blocks.liquid;

import mindustry.type.Liquid;

public interface LiquidTempBlock{
    void inputWithTemp(Liquid liquid, float amount, float temp);
}
