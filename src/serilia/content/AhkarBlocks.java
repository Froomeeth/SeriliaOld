package serilia.content;

import arc.graphics.Color;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.blocks.defense.DirectionalForceProjector;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawMulti;
import mindustry.world.draw.DrawRegion;
import serilia.world.blocks.payload.PayloadDuct;
import serilia.world.blocks.power.SolarCollector;
import serilia.world.blocks.production.DrawerDrill;
import serilia.world.draw.*;

import static mindustry.content.Items.silicon;
import static mindustry.content.Items.surgeAlloy;
import static mindustry.type.Category.*;
import static mindustry.type.ItemStack.with;
import static mindustry.world.meta.BuildVisibility.sandboxOnly;
import static mindustry.world.meta.BuildVisibility.shown;

public class AhkarBlocks {
    public static Block

        //turret

        //drill
        sealedBore,

        //distribution (payload too)
        transporter, splitter, transporterBridge,

        //liquid

        //power
        solarCollector,

        //defense
        barrierProjector;

        //crafting

        //unit

        //effect

        //payloads

        //misc

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

        //liquid

        //power
        solarCollector = new SolarCollector("solar-collector"){{
            requirements(power, sandboxOnly, with());
            size = 6;

            drawer = new DrawMulti(
                    new DrawDefault(),
                    new DrawProgressFrames(7){{
                        region = "cracks-6-";
                        color = Color.brown;
                    }}
            );
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

        //effect

        //payloads

        //misc

    }
}