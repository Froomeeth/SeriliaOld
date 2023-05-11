package serilia.content;

import arc.graphics.Color;
import mindustry.entities.part.RegionPart;
import mindustry.graphics.Layer;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.DirectionalForceProjector;
import mindustry.world.blocks.production.Separator;
import mindustry.world.draw.*;
import serilia.vfx.SeVFX;
import serilia.world.blocks.campaign.Accelerator;
import serilia.world.blocks.payload.PayloadDuct;
import serilia.world.blocks.power.SolarCollector;
import serilia.world.blocks.production.DrawerDrill;
import serilia.world.blocks.storage.DrawerCore;
import serilia.world.draw.*;

import static mindustry.content.Items.*;
import static mindustry.content.Liquids.nitrogen;
import static mindustry.type.Category.*;
import static mindustry.type.ItemStack.with;
import static mindustry.world.meta.BuildVisibility.*;
import static serilia.content.SeResources.*;

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
        barrierProjector,

        //crafting
        centrifuge,

        //unit

        //effect
        ahkarAccelerator, ahkarDropPod, coreFramework;
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
            requirements(defense, ItemStack.with(surgeAlloy, 100, silicon, 125));
            size = 3;
            width = 50f;
            length = 36;
            shieldHealth = 2000f;
            cooldownNormal = 3f;
            cooldownBrokenBase = 0.35f;

            consumePower(4f);
        }};

        //crafting
        centrifuge = new Separator("centrifuge"){{
            requirements(production, with(surgeAlloy, 100, silicon, 125));
            size = 6;

            consumeLiquids(LiquidStack.with(acidicSolution, 80f/60f, nitrogen, 12f/60f));
            consumePower(20);
            results = with(thorium, 4, fissileMatter, 1);
            craftTime = 320f;


        }};

        //effect
        ahkarAccelerator = new Accelerator("hardened-accelerator"){{

        }};

        ahkarDropPod = new DrawerCore("drop-pod"){{
            requirements(effect, debugOnly, with());
            size = 3;
            scaledHealth = 100f;

            drawer = new DrawMulti(
                new DrawDefault(),
                new DrawTurret(""){{ //todo hell's gates stand open in wait
                    parts.add(new RegionPart(""){{
                            name = "serilia-drop-pod-power-1";
                            turretShading = true;
                            mirror = true;
                            heatColor = SeVFX.coreReactor;
                            moveX = 10f;
                            heatProgress = progress = PartProgress.warmup;
                        }}, new RegionPart(""){{
                            name = "serilia-drop-pod-power-2";
                            turretShading = true;
                            mirror = true;
                            heatColor = SeVFX.coreReactor;
                            moveX = -10f;
                            heatProgress = progress = PartProgress.warmup;
                    }});
                }},
                new DrawRegion("-top"),
                new DrawTeam()
            );
        }};

        coreFramework = new DrawerCore("coreFramework"){{
            requirements(effect, shown, with(SeResources.nickel, 300, tarnide, 200));
            size = 3;
            scaledHealth = 160f;
            unitType = SeUnits.glow;

            drawer = new DrawMulti(
                new DrawDefault(),
                new DrawGlowRegion(){{color = SeVFX.coreReactor}},
                new DrawRegion("-top"),
                new DrawTeam()
            );
        }};

        //payloads

        //misc

    }
}