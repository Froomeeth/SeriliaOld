package serilia.content;

import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.content.UnitTypes;
import mindustry.entities.abilities.EnergyFieldAbility;
import mindustry.entities.bullet.ArtilleryBulletType;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.ExplosionBulletType;
import mindustry.entities.bullet.FlakBulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.ShootAlternate;
import mindustry.gen.Sounds;
import mindustry.gen.UnitEntity;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.type.Weapon;
import mindustry.type.unit.MissileUnitType;
import mindustry.world.Block;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.distribution.Junction;
import mindustry.world.blocks.distribution.Router;
import mindustry.world.blocks.liquid.LiquidBridge;
import mindustry.world.blocks.liquid.LiquidRouter;
import mindustry.world.blocks.power.BeamNode;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.blocks.production.AttributeCrafter;
import mindustry.world.blocks.production.BurstDrill;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.draw.*;
import mindustry.world.meta.Attribute;
import serilia.util.SeUtil;
import serilia.world.blocks.distribution.DuctNode;
import serilia.world.blocks.distribution.HeavyDuct;
import serilia.world.blocks.liquid.BurstPump;
import serilia.world.blocks.liquid.ShadedConduit;
import serilia.world.blocks.misc.DrillTurret;
import serilia.world.draw.drawblock.DrawWeaveColor;

import static mindustry.content.Items.*;
import static mindustry.gen.Sounds.plasmaboom;
import static mindustry.gen.Sounds.techloop;
import static mindustry.type.Category.*;
import static mindustry.type.ItemStack.with;
import static mindustry.world.meta.BuildVisibility.sandboxOnly;
import static mindustry.world.meta.BuildVisibility.shown;
import static serilia.content.SeResources.*;

public class CaliBlocks {
    public static Block

        //turret
            quail, scourge, quiver,
        ballista, overturn,

        //drill
        methaneExtractor, sandBore, combustionDrill, largeCombustionDrill, ignitionDrill, radiatorBore, bulkDrill, bulkQuarry,

        //distribution (payload too)
        heavyDuct, ductNode, ductJunction, ductDustributor,

        //liquid
        burstPump, fluidDuct, fluidRouter, fluidBridge,

        //power
        galvaniumNode, combustionChamber,

        //defense
        iridiumWall, fragisteelWall,
        allay,

        //crafting
        fragisteelSmelter, galvaniumPrinter, chirokynSmelter, electrolysisModule, bulkRefinery,

        //unit
        mechManufactor, droneManufactor, shipManufactor,

        //effect
        coreSprout, coreBurgeon, coreGreenhouse,

        //payloads
        vanadiniteRock,

        //misc
        drillTurret, drawTest, bitTiler;

    public static void load() {
        //turret
        quail = new PowerTurret("quail"){{
           health = 500;
           size = 2;
           requirements (turret, with(iridium, 50, fragisteel, 50));

           consumePower(100/60f);

           hasLiquids = false;

           range = 200;
           inaccuracy = 0;
           reload = 80;
           minWarmup = 0.9f;
           warmupMaintainTime = 30f;
           targetAir = true;
           targetGround = false;

           shootY = 0;

           shootSound = Sounds.blaster;
           shootEffect = Fx.colorSparkBig;
           shootType = new BasicBulletType(10, 150){{
               sprite = "serilia-arrow-bullet";
               width = 4.5f;
               height = 24;
               shrinkX = 0;
               shrinkY = 0;
               frontColor = Color.valueOf("c0ecff");
               backColor = Color.valueOf("87ceeb");
               trailColor = Color.valueOf("6586b0");
               trailWidth = 1.2f;
               trailLength = 20;

               lifetime = 21;
               pierce = true;
               collidesGround = false;

               hitColor = Color.valueOf("87ceeb");
               hitEffect = despawnEffect = Fx.hitBulletColor;
           }};
            drawer = new DrawTurret(){{
                parts.add(
                        new RegionPart("-wing") {{
                            x = 16/4f;
                            y = 14/4f;
                            progress = PartProgress.warmup;
                            mirror = true;
                            under = true;
                            moveX = -4/4f;
                            moveY = -2/4f;
                            moveRot = 30;
                            moves.add(new PartMove(PartProgress.recoil, 0f, 0f, -7f));
                        }},
                        new RegionPart("-front") {{
                            progress = PartProgress.warmup;
                            mirror = false;
                            under = true;
                            moveX = 0f;
                            moveY = 1f;
                            moves.add(new PartMove(PartProgress.recoil, 0f, -1f, 0f));
                        }});
            }};

            outlineColor = Color.valueOf("313a3b");
        }};
        scourge = new ItemTurret("scourge"){{
            scaledHealth = 140;
            size = 2;
            buildCostMultiplier = 10/3.05f;
            requirements (turret, with(iridium, 70, graphite, 30, galvanium, 10));

            liquidCapacity = 10;
            maxAmmo = 20;
            ammoPerShot = 10;

            range = 290;
            shootY = 0.7f;
            shootSound = Sounds.missileLaunch;
            inaccuracy = 0;
            rotateSpeed = 1.4f;
            reload = 140;
            minWarmup = 0.90f;
            targetAir = false;
            targetGround = true;

            ammo(
                graphite, new BasicBulletType(0f, 0){{
                    shootEffect = Fx.shootBig;
                    ammoMultiplier = 1f;
                    spawnUnit = new MissileUnitType("scourge-missile"){{
                        health = 100;
                        speed =  7;
                        missileAccelTime = 30f;
                        lifetime = 62f;
                        targetAir = false;

                        trailLength = 10;

                        engineColor = trailColor = Color.valueOf("d4806b");
                        engineLayer = Layer.effect;
                        engineSize = 1.1f;

                        drawCell = false;
                        lowAltitude = true;
                        outlineColor = Color.valueOf("313a3b");
                        weapons.add(new Weapon(){{
                            shootCone = 360f;
                            mirror = false;
                            reload = 1f;
                            shootOnDeath = true;
                            x = 0;
                            y = 0;
                            shootY = 0;
                            shootX = 0;
                            bullet = new ExplosionBulletType(130f, 25f){{
                                shootEffect = Fx.massiveExplosion;
                                collidesAir = false;
                                shootSound = Sounds.none;
                            }};
                        }});
                    }};
                }}
            );

            outlineColor = Color.valueOf("313a3b");

        }};
        overturn = new ItemTurret("overturn"){{
           scaledHealth = 350;
           size = 3;
           buildCostMultiplier = 20/7.12f;
           requirements (turret, with(iridium, 100, fragisteel, 50, chirokyn, 75));

           liquidCapacity = 10;
           maxAmmo = 10;
           ammoPerShot = 2;

           range = 240;
           shootY = 0.7f;
           inaccuracy = 7;
           velocityRnd = 0.4f;
           minWarmup = 0.99f;
           warmupMaintainTime = 30f;
           reload = 20;
           targetAir = true;
           targetGround = false;
           shoot = new ShootAlternate(){{
                spread = 2.8f * 1.9f;
                shots = barrels = 3;
                shotDelay = 3;
           }};
           ammo(
               fragisteel, new FlakBulletType(10, 35){{
                   sprite = "missile-large";
                   width = 6f;
                   height = 10f;
                   hitSize = 7f;
                   trailWidth = 2.1f;
                   trailLength = 18;
                   trailEffect = Fx.colorSparkBig;
                   trailRotation = true;

                   frontColor = Color.white;
                   hitColor = backColor = trailColor = Color.valueOf("88a9bd");

                   lifetime = 30;

                   splashDamage = 50;
                   splashDamageRadius = 35;
                   explodeRange = 20;
                   explodeDelay = 0f;

                   hitEffect = new MultiEffect(Fx.flakExplosion, Fx.colorSpark);
                   collidesGround = collideFloor = false;
            }}
           );

           outlineColor = Color.valueOf("313a3b");
           heatColor = Color.red;
           cooldownTime = 50;
           shootEffect = Fx.colorSparkBig;
            drawer = new DrawTurret(){{
                    parts.add(
                            new RegionPart("-outer-gun") {{
                                progress = PartProgress.warmup;
                                mirror = true;
                                under = true;
                                moveX = 0f;
                                moveY = -2f;
                                moves.add(new PartMove(PartProgress.recoil, 0f, -1f, 0f));
                            }},
                            new RegionPart("-inner-gun") {{
                                progress = PartProgress.warmup;
                                mirror = true;
                                under = false;
                                moveX = 0f;
                                moveY = -1f;
                            }});
                }};
        }};

        //drill
        methaneExtractor = new AttributeCrafter("methane-extractor"){{
            requirements(production, with(iridium, 70));
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

        combustionDrill = new BurstDrill("combustion-drill"){{
            scaledHealth = 75;
            size = 2;
            buildCostMultiplier = 0.99f/0.66f;
            requirements(production, with(iridium, 20));

            drillTime = 60f * 10f;
            tier = 1;
            drillEffect = Fx.mineBig;
            shake = 0.5f;
            itemCapacity = 16;
            arrowOffset = 0f;
            arrowSpacing = 0f;
            arrows = 1;

            drillMultipliers.put(iridium, 1.5f);

            consumeLiquids(LiquidStack.with(methane, 5f / 60f));

        }};
        largeCombustionDrill = new BurstDrill("large-combustion-drill"){{
            requirements(production, with(graphite, 60, iridium, 80, chirokyn, 50));
            scaledHealth = 95;
            drillTime = 60f * 6.5f;
            size = 3;
            tier = 2;
            drillEffect = Fx.mineHuge;
            shake = 1.8f;
            itemCapacity = 30;
            arrowOffset = 0f;
            arrowSpacing = 0f;
            arrows = 1;

            drillMultipliers.put(iridium, 1.5f);

            consumeLiquids(LiquidStack.with(methane, 10f / 60f));
            consumePower(25/60f);

        }};

        //Distribution

        heavyDuct = new HeavyDuct("heavy-duct"){{
            health = 200;
            size = 1;
            requirements(distribution, with(iridium, 2));

            armored = true;
            speed = 5.5f;


        }};
        ductNode = new DuctNode("duct-node"){{
            requirements(Category.distribution, with(iridium, 5));
            health = 75;
            buildCostMultiplier = 6f;
            speed = 5.5f;
        }};
        ductJunction = new Junction("duct-junction"){{
            health = 75;
            buildCostMultiplier = 6f;
            requirements(Category.distribution, with(iridium, 5));

            speed = 5.5f;
            capacity = 2;
            ((HeavyDuct) heavyDuct).junctionReplacement = this;
        }};
        ductDustributor = new Router("duct-distributor"){{
            health = 75;
            size = 2;
            requirements(Category.distribution, with(iridium, 10));
        }};

        ((HeavyDuct)heavyDuct).acceptFrom = Seq.with(ductNode, ductJunction, ductDustributor);

        //liquid
        burstPump = new BurstPump("burst-pump"){{
            requirements(liquid, with(iridium, 100));
            size = 2;

            liquidCapacity = 100f;
            pumpTime = 360;
            outputAmount = 100;

            consumePower(150/60f);
        }};
        fluidDuct = new ShadedConduit("fluid-duct"){{
            requirements(liquid, with(iridium, 1));
        }};
        fluidRouter = new LiquidRouter("fluid-router"){{
            requirements(Category.liquid, with(iridium, 3));
            liquidCapacity = 20f;
            underBullets = true;
            solid = false;
        }};
        fluidBridge = new LiquidBridge("fluid-bridge"){{
            health = 80;
            requirements(Category.liquid, with(iridium, 5));

            hasPower = false;

            range = 4;
            ((ShadedConduit) fluidDuct).bridgeReplacement = this;
        }};
        //power
        galvaniumNode = new BeamNode("galvanium-node"){{
            requirements(Category.power, with(fragisteel, 5, galvanium,10));
            consumesPower = outputsPower = true;
            health = 90;
            range = 10;

            consumePowerBuffered(500f);
        }};
        combustionChamber = new ConsumeGenerator("combustion-reactor"){{
            scaledHealth = 45;
            size = 2;
            requirements(Category.power, with(iridium,65,galvanium,35));

            powerProduction = 3f;
            ambientSound = Sounds.smelter;
            ambientSoundVolume = 0.03f;
            generateEffect = Fx.none;

            consumeLiquid(methane, 5f/60);
        }};

        //defense
        SeUtil.generateWalls(iridiumWall = new Wall("iridium-wall"){{
           scaledHealth = 425;
           size = 1;
           requirements(Category.defense, with( graphite, 4, iridium, 4));
        }}, Seq.with(2, 3));

        SeUtil.generateWalls(fragisteelWall = new Wall("fragisteel-wall"){{
            scaledHealth = 325;
            size = 1;
            requirements(Category.defense, with( fragisteel, 6));
            absorbLasers = true;
        }}, Seq.with(2, 3));

        allay = new PowerTurret("allay"){{
            scaledHealth = 75f;
            size = 4;
            requirements(Category.defense, with( silicon, 200, graphite, 250, iridium, 500, chirokyn, 100));
            liquidCapacity = 40f;
            consumePower(200/60f);
            consumeLiquid(steam, 20/60f);
            range = 300f;
            inaccuracy = 0;
            reload = 60f * 4.5f;
            shootY = 0;
            targetAir = false;
            targetHealing = true;
            shootType = new ArtilleryBulletType(){
                {
                    shootEffect = new MultiEffect(Fx.colorSparkBig);
                    hitColor = backColor = trailColor = Pal.heal;
                    damage = 0;
                    speed = 5;
                    lifetime = 120;
                    height = 19f;
                    width = 17f;

                    shrinkX = 0.2f;
                    shrinkY = 0.1f;

                    trailLength = 32;
                    trailWidth = 3.35f;
                    trailEffect = Fx.none;
                    despawnEffect = Fx.greenBomb;
                    despawnShake = 3;
                    despawnSound = Sounds.shootSmite;
                    despawnUnitRadius = 0;
                    despawnUnitCount = 1;
                    collides = false;
                    despawnUnit = new MissileUnitType("allay-field") {{
                                constructor = UnitEntity::create;
                                targetAir = false;
                                targetGround = false;
                                speed = 0;
                                lifetime = 60f * 5;
                                physics = false;
                                engineSize = 0;
                                targetable = false;
                                hittable = false;
                                rotateSpeed = 0;
                                playerControllable = false;
                                drawCell = false;
                                createWreck = createScorch = false;
                                deathSound = plasmaboom;
                                abilities.add(new EnergyFieldAbility(0f, 30f, 100f) {{
                                    x = 0;
                                    y = 0;
                                    statusDuration = 0 * 6f;
                                    maxTargets = 40;
                                    healAmount = 100;
                                    targetAir = false;
                                    targetHealing = false;
                                }});
                            }
                        };
                    }};
                }};

        //crafting
        fragisteelSmelter = new GenericCrafter("fragisteel-smelter"){{
            requirements(crafting, with(iridium, 50, graphite, 40));

            craftEffect = Fx.smeltsmoke;
            scaledHealth = 90;
            outputItem = new ItemStack(fragisteel, 1);
            craftTime = 85f;
            size = 2;
            hasItems = true;
            liquidCapacity = 10f;

            consumeItem(iridium, 2);
            consumeLiquid(methane, 1f/60);

            drawer = new DrawMulti(new DrawRegion("-bottom"),new DrawCrucibleFlame(), new DrawDefault());
        }};
        galvaniumPrinter = new GenericCrafter("galvanium-printer"){{
            requirements(crafting, with(iridium, 70, fragisteel, 30));

            craftEffect = Fx.hitLancer;
            loopSound = techloop;
            scaledHealth = 80;
            outputItem = new ItemStack(galvanium, 3);
            craftTime = 65f;
            size = 3;
            hasItems = true;
            liquidCapacity = 10f;

            consumeItems(with(iridium, 1, Items.sand, 2));
            consumeLiquid(methane, 1f/60);

            drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawRegion("-rods"), new DrawWeaveColor(Color.valueOf("c0ecff")), new DrawDefault());
        }};
        chirokynSmelter = new GenericCrafter("chirokyn-smelter"){{
            requirements(crafting, with(iridium,60, fragisteel,50, galvanium,25));

            craftEffect = Fx.smeltsmoke;
            scaledHealth = 80;
            outputItem = new ItemStack(chirokyn, 2);
            craftTime = 70f;
            size = 3;
            hasItems = true;
            liquidCapacity = 10f;

            consumeItems(with(iridium, 3, graphite, 2));
            consumePower(1.5f);
        }};
        electrolysisModule = new GenericCrafter("electrolysis-module"){{
            requirements(Category.crafting, with());
            size = 2;

            researchCostMultiplier = 1.2f;
            craftTime = 10f;
            rotate = true;
            invertFlip = true;

            liquidCapacity = 30f;

            consumeLiquid(Liquids.arkycite, 20f / 60f);
            consumePower(0.5f);

            drawer = new DrawMulti(
                    new DrawRegion("-bottom"),
                    new DrawLiquidTile(Liquids.arkycite, 2f),
                    new DrawBubbles(Color.valueOf("7693e3")){{
                        sides = 4;
                        recurrence = 3f;
                        spread = 6;
                        radius = 1.5f;
                        amount = 20;
                    }},
                    new DrawRegion(),
                    new DrawLiquidOutputs(),
                    new DrawGlowRegion(){{
                        alpha = 0.7f;
                        color = Color.valueOf("c4bdf3");
                        glowIntensity = 0.2f;
                        glowScale = 6f;
                    }}
            );

            ambientSound = Sounds.electricHum;
            ambientSoundVolume = 0.08f;

            regionRotated1 = 3;
            outputLiquids = LiquidStack.with(methane, 5f / 60, chlorine, 15f / 60);
            liquidOutputDirections = new int[]{1, 3};
        }};

        //unit

        mechManufactor = new UnitFactory("mech-manufactor"){{
            requirements(units, with(graphite, 55, silicon, 200, iridium, 100, chirokyn, 100));
            plans = Seq.with(
                    new UnitPlan(UnitTypes.fortress, 60f * 15, with(silicon, 95, lead, 10)),
                    new UnitPlan(UnitTypes.cleroi, 60f * 10, with(silicon, 50, coal, 10))
            );
            size = 4;
            consumePower(4f);
        }};
        droneManufactor = new UnitFactory("drone-manufactor"){{
            requirements(units, with(graphite, 55, silicon, 200, iridium, 100, chirokyn, 100));
            plans = Seq.with(
                    new UnitPlan(UnitTypes.mega, 60f * 15, with(silicon, 85, lead, 10)),
                    new UnitPlan(UnitTypes.elude, 60f * 10, with(silicon, 90, coal, 10))
            );
            size = 4;
            consumePower(4f);
        }};
        shipManufactor = new UnitFactory("ship-manufactor"){{ //TODO no kill yourself
            requirements(units, with(graphite, 55, silicon, 200, iridium, 100, chirokyn, 100));
            plans = Seq.with(
                    new UnitPlan(UnitTypes.minke, 60f * 10, with(silicon, 180, metaglass, 95))
            );
            size = 4;
            consumePower(4f);
        }};

        //effect
        coreSprout = new CoreBlock("core-sprout"){{
            requirements(effect, with(Items.graphite, 1000, iridium, 1000, fragisteel, 500));
            alwaysUnlocked = true;

            scaledHealth = 220;
            armor = 1f;
            size = 3;
            itemCapacity = 1500;

            isFirstTier = true;
            unitType = SeUnits.scion;
            thrusterLength = 34/4f;

            unitCapModifier = 5;

            //drawer = new DrawMulti( todo I don't care to fix it you do it
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
        drillTurret = new DrillTurret("drill-turret"){{
            requirements(effect, shown, with(nickel, 80000, tarnide, 2, carbide, 12));
            size = 2;
            itemCapacity = 10;
            hasItems = true;
            outputItem = new ItemStack(silicon, 1);
            scaledHealth = 1;
        }};
    }
}