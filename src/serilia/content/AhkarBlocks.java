package serilia.content;

import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.Liquids;
import mindustry.content.UnitTypes;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.DirectionalForceProjector;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.distribution.Junction;
import mindustry.world.draw.DrawGlowRegion;
import mindustry.world.draw.DrawMulti;
import serilia.util.SeUtil;
import serilia.world.blocks.distribution.DuctNode;
import serilia.world.blocks.distribution.HeavyDuct;
import serilia.world.blocks.distribution.RotRouter;
import serilia.world.blocks.payload.PayDuctRouter;
import serilia.world.blocks.payload.PayloadBuffer;
import serilia.world.blocks.payload.PayloadDuct;
import serilia.world.blocks.power.SolarCollector;
import unicrafter.recipes.ChanceRecipe;
import unicrafter.recipes.Recipe;
import unicrafter.world.UniversalCrafter;
import unicrafter.world.draw.DrawConstruction;

import static mindustry.content.Items.*;
import static mindustry.type.Category.*;
import static mindustry.type.ItemStack.with;
import static mindustry.world.meta.BuildVisibility.*;
import static serilia.content.SeResources.nickel;

public class AhkarBlocks {
    public static Block
        //turret

        //drill

        //distribution (payload too)
        heavyDuct, ductInserter, ductJunction, heavyDuctSplitter,

        transporter, splitter, transporterRail,

        //liquid
        channel,

        //power
        solarCollector,

        //defense
        barrierProjector,

        nickelWall,

        //crafting
        electricFurnace, capsule, capsuleSand, totallyLoader,

        //unit

        //effect

        //payloads

        //misc
        buffer, largeTankRefabricator;

        //prop

    public static void load() {
        //drill


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


        //power


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
        electricFurnace = new UniversalCrafter("electric-furnace"){{
            requirements(crafting, shown, with(nickel, 24));
            size = 4;
            rotate = outputsPayload = true;
            rotateDraw = instantInput = false;

            drawerRecipeDefault = new DrawMulti(
                    new DrawGlowRegion("-coil-heat"){{layer = 40f;}},
                    new DrawGlowRegion("-inner-heat"){{layer = 36f;}}
            );

            recipes = Seq.with(
                    new Recipe("silicon-ef", silicon,50){{
                        req(capsuleSand, 1, "power", 3f);
                        out(capsule, 1, silicon, 40);
                    }}
            );
        }};

        //crafting
        totallyLoader = new UniversalCrafter("capsule-loader"){{
            requirements(crafting, shown, with(nickel, 24));
            size = 2;
            rotate = outputsPayload = true;
            instantInput = false;

            recipes = Seq.with(
                    new Recipe("fill-capsule-sand", sand,50){{
                        req(capsule, 1, sand, 100);
                        out(capsuleSand, 1);
                    }}
            );
        }};

        capsule = new Block("capsule"){{
            requirements(crafting, shown, with(sand, 100));
            size = 2;
            update = true;
        }};

        capsuleSand = new Block("sand-capsule"){{
            requirements(crafting, shown, with(sand, 100));
            size = 2;
            update = true;
        }};


        //effect


        //payloads


        //misc
        buffer = new PayloadBuffer("buffer"){{
            requirements(liquid, sandboxOnly, with());
            size = 4;
        }};

        largeTankRefabricator = new UniversalCrafter("large-tank-refabricator"){{
            requirements(liquid, sandboxOnly, with());
            size = 5;
            rotate = true;
            outputsPayload = true;

            solid = false;
            vanillaIO = true;
            regionSuffix = "-dark";

            //for a 1:1 reconstructor, you want...
            instantInput = false; //...the payload to move in before despawning.
            instantFirstOutput = true; //...the created payload to appear immediately, without an effect.

            payDespawnEffect = Fx.none; //removing the despawn effect here because it might look weird

            recipes = Seq.with(
                    new ChanceRecipe("wall-to-tanks", Blocks.tankRefabricator,50){{
                        req(silicon, 150, Blocks.tungstenWallLarge, 1, Liquids.cyanogen, 1, "power", 1f);
                        out(UnitTypes.precept, 1);

                        addChanceOut(0f, 0.2f, UnitTypes.conquer, 1);
                        addChanceOut(0.2f, 0.3f, UnitTypes.stell, 3);

                        isUnit = true; //makes unit related map rules apply to the recipe

                        drawer = new DrawConstruction(true); //reconstruct makes it draw the last payload it received, only for 1:1 recons
                    }}
            );
        }};

        //prop
        solarCollector = new SolarCollector("solar-collector"){{
            requirements(effect, editorOnly, with());
            size = 5;
        }};

        Seq<Block> ahkarBlocks = Seq.with(
                //distribution (payload too)
                transporter, splitter,

                //liquid
                channel,

                //power
                solarCollector,

                //defense
                barrierProjector,

                nickelWall,

                //crafting
                electricFurnace

                //unit

                //effect
        );
        ahkarBlocks.each(b -> {
            if(b != null){
                b.placeEffect = SeFxPal.ahkarPlace;
                b.breakEffect = SeFxPal.ahkarBreak;

                if(b instanceof Wall) SeUtil.generateWalls(b, Seq.with(3));
            }
        });
    }
}