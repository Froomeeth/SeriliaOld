package serilia.content;

import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.production.AttributeCrafter;
import mindustry.world.blocks.production.BurstDrill;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawLiquidTile;
import mindustry.world.draw.DrawMulti;
import mindustry.world.draw.DrawRegion;
import mindustry.world.meta.Attribute;
import serilia.world.blocks.distribution.ShadedDuct;
import serilia.world.blocks.misc.DrawTest;
import serilia.world.blocks.payload.MoreGenericCrafter;

import static mindustry.type.Category.*;
import static mindustry.type.ItemStack.with;
import static mindustry.world.meta.BuildVisibility.sandboxOnly;
import static serilia.content.SeResources.*;

public class CaliBlocks {
    public static Block

        //turret

        //drill
        methaneExtractor, heatDrill, largeheatdrill, ignitionDrill, radiatorBore, bulkQuarry, bulkDrill,

        //distribution (payload too)
        ducter,

        //liquid

        //power

        //defense

        //crafting
        fragisteelPress, bulkRefinery,

        //unit

        //effect
        coreSprout, coreBurgeon, coreGreenhouse,

        //payloads
        vanadiniteRock,

        //misc
        drawTest, bitTiler;

    public static void load() {

        //drill


        methaneExtractor = new AttributeCrafter("methane-extractor"){{
            requirements(Category.production, with(iridium, 70));
            attribute = Attribute.steam;
            minEfficiency = 9f - 0.0001f;
            baseEfficiency = 0f;
            displayEfficiency = false;
            craftEffect = Fx.turbinegenerate;
            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawLiquidTile(methane, 8f / 4f), new DrawDefault());
            craftTime = 120f;
            size = 3;
            ambientSound = Sounds.hum;
            ambientSoundVolume = 0.06f;
            hasLiquids = true;
            boostScale = 1f / 9f;
            outputLiquid = new LiquidStack(methane, 80f / 60f);
            liquidCapacity = 160f;
        }};

        heatDrill = new BurstDrill("heat-drill"){{
            requirements(Category.production, with(SeResources.iridium, 20));
            scaledHealth = 75;
            drillTime = 60f * 10f;
            size = 2;
            tier = 1;
            drillEffect = Fx.mineBig;
            shake = 0.5f;
            itemCapacity = 16;
            arrowOffset = 0f;
            arrowSpacing = 0f;
            arrows = 1;

            drillMultipliers.put(SeResources.iridium, 1.5f);

            consumeLiquids(LiquidStack.with(SeResources.methane, 5f / 60f));

        }};

        //distribution
        ducter = new ShadedDuct("ducter"){{
            requirements(distribution, with(SeResources.iridium, 2));
        }};

        //defense

        //crafting
        fragisteelPress = new GenericCrafter("fragisteel-press"){{
            requirements(Category.crafting, with(SeResources.iridium, 50, Items.graphite, 40));

            craftEffect = Fx.pulverizeMedium;
            health = 200;
            outputItem = new ItemStack(SeResources.fragisteel, 1);
            craftTime = 70f;
            size = 2;
            hasItems = true;

            consumeItem(SeResources.iridium, 2);
        }};

        //effect
        coreSprout = new CoreBlock("core-sprout"){{
            requirements(effect, with(iridium, 2500, vanadium, 2000, tarnide, 1500));
            alwaysUnlocked = true;

            scaledHealth = 220;
            armor = 1f;
            size = 4;
            itemCapacity = 3000;

            isFirstTier = true;
            unitType = SeUnits.scion;
            thrusterLength = 34/4f;

            unitCapModifier = 5;
        }};
        coreBurgeon = new CoreBlock("core-burgeon"){{
            requirements(effect, with(iridium, 4000, tarnide, 2000, chirokyn, 1500, paragonite, 1500));
            alwaysUnlocked = false;

            scaledHealth = 300;
            armor = 4f;
            size = 5;
            itemCapacity = 6000;

            isFirstTier = false;
            unitType = SeUnits.scion;
            thrusterLength = 34/4f;

            unitCapModifier = 10;
        }};

        //payloads
        vanadiniteRock = new Block("vanadinite-rock"){{
            requirements(logic, sandboxOnly, with());
            update = true;
        }};

        //misc
        drawTest = new DrawTest("draw-test"){{
            requirements(effect, sandboxOnly, with());
        }};
    }
}