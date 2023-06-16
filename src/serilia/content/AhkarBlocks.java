package serilia.content;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.content.UnitTypes;
import mindustry.graphics.Layer;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.DirectionalForceProjector;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.Separator;
import mindustry.world.draw.*;
import serilia.util.SeUtil;
import serilia.world.blocks.distribution.TubeMotor;
import serilia.world.blocks.distribution.ConveyorTube;
import serilia.world.blocks.distribution.TubeRouter;
import serilia.world.blocks.liquid.LiquidChannel;
import serilia.world.blocks.payload.MoreGenericCrafter;
import serilia.world.blocks.payload.PayloadDuct;
import serilia.world.blocks.power.SolarCollector;
import serilia.world.blocks.production.DrawerDrill;
import serilia.world.blocks.storage.DrawerCore;
import serilia.world.draw.drawblock.*;

import static mindustry.content.Items.*;
import static mindustry.content.Liquids.nitrogen;
import static mindustry.content.Liquids.slag;
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
        poweredConveyorTube, conveyorTubeMotor, conveyorTubeSplitter,
        transporter, splitter, transporterBridge,

        //liquid
        channel, valve, pistonPump,

        //power
        solarCollector,

        //defense
        barrierProjector,

        nickelWall,

        //crafting
        siliconFurnace, heater, foundry, centrifuge,

        //unit

        //effect
        caliAccelerator, ahkarDropPod, coreFramework;
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
                    new DrawRegion("-bottom"),
                    new DrawSealedDust(),
                    new DrawHalfSpinner("-rotator", 2f),        new DrawZSet(Layer.blockOver),
                    new DrawDefault(),
                    new DrawMineItem()
            );
        }};


        //distribution
        poweredConveyorTube = new ConveyorTube("powered-conveyor-tube"){{
            requirements(distribution, sandboxOnly, with());
        }};
        conveyorTubeMotor = new TubeMotor("conveyor-tube-motor"){{
            requirements(distribution, sandboxOnly, with());
            consumePower(0.5f);
        }};
        conveyorTubeSplitter = new TubeRouter("conveyor-tube-splitter"){{
            requirements(distribution, sandboxOnly, with());
        }};

        transporter = new PayloadDuct("transporter"){{
            requirements(distribution, sandboxOnly, with());
            size = 2;
        }};


        //liquid
        channel = new LiquidChannel("channel"){{
            requirements(liquid, with(nickel, 2, metaglass, 1));
        }};

        valve = new Conduit("valve"){{ //todo make togglable
            requirements(liquid, sandboxOnly, with());
        }};


        //power
        solarCollector = new SolarCollector("solar-collector"){{
            requirements(power, sandboxOnly, with());
            size = 6;
            powerProduction = 2f;

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

        nickelWall = new Wall("nickel-wall"){{
            requirements(defense, shown, with(nickel, 24));
            size = 2;
            scaledHealth = 400f;
        }};


        //crafting
        siliconFurnace = new GenericCrafter("silicon-furnace"){{ //todo hothot liquid
            requirements(production, with(nickel, 6, tarnide, 10, metaglass, 2));

            craftTime = 80f;
            consumeItems(with(sand, 2));
            outputItems = with(silicon, 1);

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawGlowRegion("-inner"){{
                        blending = Blending.normal;
                        layer = -1f;
                    }},
                    new DrawDefault(),
                    new DrawGlowRegion()
            );
        }};

        heater = new GenericCrafter("heater"){{
            requirements(production, with(nickel, 10, tarnide, 25, metaglass, 12));
            size = 2;

            craftTime = 30f;
            consumeItems(with(nickel, 1));
            outputLiquids = LiquidStack.with(slag, 0.5f); //todo metal variant gen
        }};

        foundry = new MoreGenericCrafter("foundry"){{
            requirements(production, with(nickel, 10, tarnide, 25, metaglass, 12));
            size = 4;

            craftTime = 180f;
            consumeLiquids(LiquidStack.with(slag, 0.5f));
            consumeItems(with(metaglass, 6));
            outputPayload = lens;
        }};

        centrifuge = new Separator("centrifuge"){{
            requirements(production, with(surgeAlloy, 100, silicon, 125));
            size = 6;

            consumeLiquids(LiquidStack.with(acidicSolution, 80f/60f, nitrogen, 12f/60f));
            consumePower(20);
            results = with(thorium, 4, fissileMatter, 1);
            craftTime = 320f;


        }};


        /*//effect
        caliAccelerator = new SeAccelerator("hardened-accelerator"){{
            requirements(effect, sandboxOnly, with());
            size = 5;
        }};*/


        ahkarDropPod = new DrawerCore("drop-pod"){{
            requirements(effect, debugOnly, with());
            size = 3;
            scaledHealth = 100f;

            drawer = new DrawMulti(
                new DrawDefault(),
                new DrawRegion("-top"),
                new DrawTeam()
            );
        }};


        coreFramework = new DrawerCore("core-framework"){{
            requirements(effect, shown, with(nickel, 300, tarnide, 200));
            size = 3;
            scaledHealth = 160f;
            unitType = UnitTypes.incite; //SeUnits.glow;

            drawer = new DrawMulti(
                new DrawDefault(),
                new DrawGlowRegion(){{color = SeFxPal.coreReactor;}},
                new DrawRegion("-top"),
                new DrawTeam()
            );
        }};


        //payloads


        //misc



        Seq<Block> ahkarBlocks = Seq.with(
                sealedBore,

                //distribution (payload too)
                transporter, splitter, transporterBridge,

                //liquid
                channel, valve, pistonPump,

                //power
                solarCollector,

                //defense
                barrierProjector,

                nickelWall,

                //crafting
                siliconFurnace, heater, foundry, centrifuge,

                //unit

                //effect
                caliAccelerator, ahkarDropPod, coreFramework
        );
        ahkarBlocks.each(b -> {
            if(b != null){
                b.placeEffect = SeFxPal.ahkarPlace;
                b.breakEffect = SeFxPal.ahkarBreak;

                if(b instanceof Wall) SeUtil.generateWalls((Wall)b, Seq.with(3));
            }
        });
    }
}