package serilia.content;

import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.type.ItemStack.*;

public class SeCores{
    public static Block
            coreSprout, coreBurgeon, coreGreenhouse;
    public static void load() {
        coreSprout = new CoreBlock("core-sprout"){{
            requirements(Category.effect, with(SeResources.iridium, 2500, SeResources.vanadinite, 2000, SeResources.tarnide, 1500));
            alwaysUnlocked = true;

            health = 3500;
            armor = 1f;
            size = 4;
            itemCapacity = 3000;

            isFirstTier = true;
            unitType = SeUnits.scion;
            thrusterLength = 34/4f;

            unitCapModifier = 5;
        }};
        coreBurgeon = new CoreBlock("core-burgeon"){{
            requirements(Category.effect, with(SeResources.iridium, 4000, SeResources.tarnide, 2000, SeResources.azulite, 1500, SeResources.paragonite, 1500));
            alwaysUnlocked = false;

            health = 4000;
            armor = 2f;
            size = 5;
            itemCapacity = 6000;

            isFirstTier = false;
            unitType = SeUnits.scion;
            thrusterLength = 34/4f;

            unitCapModifier = 10;
        }};
    }
}