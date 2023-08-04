package serilia.content;

import arc.graphics.Blending;
import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.content.Liquids;
import mindustry.content.UnitTypes;
import mindustry.graphics.Layer;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.DirectionalForceProjector;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.distribution.Junction;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.Separator;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawGlowRegion;
import mindustry.world.draw.DrawMulti;
import mindustry.world.draw.DrawRegion;
import serilia.util.SeUtil;
import serilia.world.blocks.distribution.DuctNode;
import serilia.world.blocks.distribution.HeavyDuct;
import serilia.world.blocks.distribution.RotRouter;
import serilia.world.blocks.payload.MoreGenericCrafter;
import serilia.world.blocks.payload.PayDuctRouter;
import serilia.world.blocks.payload.PayloadDuct;
import serilia.world.blocks.payload.UniversalCrafter;
import serilia.world.blocks.power.LaserEaterOrDivider;
import serilia.world.blocks.power.LaserNode;
import serilia.world.blocks.power.PowerWire;
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
        heavyDuct, ductInserter, ductJunction, heavyDuctSplitter,

        transporter, splitter, transporterBridge,

        //liquid
        channel, valve, pistonPump,

        //power
        solarCollector, wire, laserNode, laserEmitter, laserRelay, laserReceiver, laserDivider,

        //defense
        barrierProjector,

        nickelWall,

        //crafting
        siliconFurnace, heater, foundry, centrifuge,

        //unit

        //effect
        caliAccelerator, ahkarDropPod, coreFramework,
        //payloads

        //misc
        multiCraft;

        //prop

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
        heavyDuct = new HeavyDuct("stupid-heavy-duct"){{
            health = 190;
            size = 1;
            requirements(distribution, with(nickel, 2));

            armored = true;
            speed = 5;
        }};
        ductInserter = new DuctNode("stupid-duct-inserter"){{
            requirements(Category.distribution, with(nickel, 5));
            health = 45;
            buildCostMultiplier = 6f;
            speed = 5;
        }};
        ductJunction = new Junction("stupid-heavy-duct-junction"){{
            requirements(Category.distribution, with(nickel, 4));
            speed = 5;
            capacity = 2;
            health = 45;
            buildCostMultiplier = 6f;

            ((HeavyDuct)heavyDuct).junctionReplacement = this;
        }};
        heavyDuctSplitter = new RotRouter("stupid-heavy-duct-router"){{
            requirements(Category.distribution, with(nickel, 4));
        }};

        transporter = new PayloadDuct("transporter"){{
            requirements(distribution, sandboxOnly, with());
            size = 2;
        }};
        splitter = new PayDuctRouter("splitter"){{
            requirements(distribution, sandboxOnly, with());
            size = 2;
        }};


        //liquid
        valve = new Conduit("valve"){{ //todo make togglable
            requirements(liquid, sandboxOnly, with());
        }};


        //power
        laserNode = new LaserNode("laser-node"){{
            requirements(power, sandboxOnly, with());
        }};

        laserReceiver = new LaserEaterOrDivider("laser-receiver"){{
            requirements(power, sandboxOnly, with());
        }};
        laserDivider = new LaserEaterOrDivider("laser-divider"){{
            requirements(power, sandboxOnly, with());
            eat = false;
        }};

        wire = new PowerWire("wire"){{
            requirements(power, sandboxOnly, with());
            hasPower = true;
            consumesPower = false;
            conductivePower = true;
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
        multiCraft = new UniversalCrafter("multi-craft"){{
            requirements(liquid, sandboxOnly, with());
            size = 5;
            rotate = true;
            outputsPayload = true;
            itemCapacity = 50;
            liquidCapacity = 5000000000f;

            recipes = Seq.with(
                    new Recipe("wall-to-stell", UnitTypes.stell,1){{
                        req(silicon, 5, Blocks.tungstenWallLarge, 3);
                        out(UnitTypes.stell, 3);
                    }},
                    new Recipe("wall-deconstruct", Blocks.smallDeconstructor, 10){{
                        req(Blocks.tungstenWallLarge, 3);
                        out(tungsten, 24, Liquids.water, 500);
                    }},
                    new Recipe("serpulo-if-it-was-good", Blocks.exponentialReconstructor, 120){{
                        req(UnitTypes.flare, 15);
                        out(UnitTypes.eclipse, 1);
                        powerReq = 1f;
                    }},
                    new Recipe("ghghghg", null, 1)
            );
        }};

        //prop
        solarCollector = new SolarCollector("solar-collector"){{
            requirements(effect, editorOnly, with());
            size = 5;
        }};

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