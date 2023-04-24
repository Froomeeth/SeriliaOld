package serilia.content;

import mindustry.content.Fx;
import mindustry.graphics.Layer;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.DirectionalForceProjector;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawMulti;
import mindustry.world.draw.DrawRegion;
import serilia.world.blocks.misc.BitmaskTiler;
import serilia.world.blocks.misc.DrawTest;
import serilia.world.blocks.payload.MoreGenericCrafter;
import serilia.world.blocks.payload.PayloadDuct;
import serilia.world.blocks.production.DrawerDrill;
import serilia.world.draw.DrawHalfSpinner;
import serilia.world.draw.DrawMineItem;
import serilia.world.draw.DrawSealedDust;
import serilia.world.draw.DrawZSet;

import static mindustry.content.Items.*;
import static mindustry.type.Category.*;
import static mindustry.type.ItemStack.with;
import static mindustry.world.meta.BuildVisibility.sandboxOnly;
import static mindustry.world.meta.BuildVisibility.shown;
import static serilia.content.SeResources.*;

public class SeBlocks{
    public static Block

        //turret

        //drill
        bulkDrill, sealedBore,

        //distribution (payload too)
        transporter, splitter, transporterBridge,

        //liquid

        //power

        //defense
        barrierProjector,

        //crafting
        bulkRefinery,

        //unit

        //effect
        coreSprout, coreBurgeon, coreGreenhouse,

        //payloads
        vanadiniteRock,

        //misc
        drawTest, bitTiler;

    public static void load() {

        //drill
        sealedBore = new DrawerDrill("sealed-bore"){{
            requirements(production, shown, with());
            size = 3;
            tier = 2;
            drillTime = 300;

            squareSprite = false;
            drawer = new DrawMulti(
                    new DrawDefault(),
                    new DrawSealedDust(),
                    new DrawHalfSpinner("-rotator", 2f),        new DrawZSet(Layer.blockOver),
                    new DrawRegion("-top"),
                    new DrawMineItem()
            );
        }};

        //distribution
        transporter = new PayloadDuct("transporter"){{
            requirements(distribution, sandboxOnly, with());
            size = 2;
        }};

        //defense
        barrierProjector = new DirectionalForceProjector("barrier-projector"){{
            requirements(effect, with(surgeAlloy, 100, silicon, 125));
            size = 3;
            width = 50f;
            length = 36;
            shieldHealth = 2000f;
            cooldownNormal = 3f;
            cooldownBrokenBase = 0.35f;

            consumePower(4f);
        }};

        //crafting
        bulkRefinery = new MoreGenericCrafter("bulk-refinery"){{
            requirements(crafting, sandboxOnly, with());
            size = 4;

            consumeLiquid(methane, 2);
            inputPayload = vanadiniteRock;
            outputItems = with(vanadium, 6, sand, 6, metaglass, 2);
            outputLiquids = LiquidStack.with(chlorine, 0.3f);
            craftTime = 120f;
            craftEffect = Fx.breakProp;
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
            requirements(effect, with(iridium, 4000, tarnide, 2000, azulite, 1500, paragonite, 1500));
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

        bitTiler = new BitmaskTiler("connect-wall-large"){{
            requirements(effect, sandboxOnly, with());
            size = 2;
        }};
    }
}