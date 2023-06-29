package serilia.content;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.UnitTypes;
import mindustry.entities.Effect;
import mindustry.entities.abilities.EnergyFieldAbility;
import mindustry.entities.bullet.ArtilleryBulletType;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.FlakBulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.ShootAlternate;
import mindustry.entities.pattern.ShootMulti;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.type.unit.MissileUnitType;
import mindustry.world.Block;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.distribution.Junction;
import mindustry.world.blocks.distribution.Router;
import mindustry.world.blocks.liquid.LiquidRouter;
import mindustry.world.blocks.production.AttributeCrafter;
import mindustry.world.blocks.production.BurstDrill;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.draw.*;
import mindustry.world.meta.Attribute;
import serilia.util.SeUtil;
import serilia.world.blocks.distribution.ShadedDuct;
import serilia.world.blocks.liquid.ShadedConduit;
import serilia.world.blocks.misc.DrawTest;
import serilia.world.blocks.misc.DrillTurret;
import serilia.world.blocks.storage.DrawerCore;
import serilia.world.draw.drawblock.DrawTeam;

import static mindustry.content.Items.*;
import static mindustry.gen.Sounds.drill;
import static mindustry.gen.Sounds.plasmaboom;
import static mindustry.type.Category.*;
import static mindustry.type.ItemStack.with;
import static mindustry.world.meta.BuildVisibility.sandboxOnly;
import static mindustry.world.meta.BuildVisibility.shown;
import static serilia.content.SeResources.*;

public class CaliBlocks {
    public static Block

        //turret
        quiver, scourge, quail,
        ballista, overturn,

        //drill
        methaneExtractor, heatDrill, largeHeatDrill, ignitionDrill, radiatorBore, bulkDrill, bulkQuarry,

        //distribution (payload too)
        ducter, ductjunction, ductRouter,

        //liquid
        fluidDuct, fluidRouter,

        //power

        //defense
        iridiumWall, fragisteelWall,
        allay,

        //crafting
        fragisteelPress, bulkRefinery,

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
        ballista = new ItemTurret("ballista") {{requirements(turret, with(iridium, 175, tarnide, 100, chirokyn, 75));

            outlineColor = Color.valueOf("473a3a");
            squareSprite = false;

            health = 575;
            armor = 1f;
            size = 3;

            itemCapacity = 1/2;
            ammoPerShot = 3;

            range = 280;
            shootCone = 5;
            reload = 60;

            targetAir = true;
            targetGround = false;

            rotateSpeed = 2.5f;

            shootSound = Sounds.shootAlt;
            shootY = 3;
            shoot = new ShootAlternate(){{
                spread = 4.7f;
                shots = 2;
                barrels = 2;
            }};

            minWarmup = 0.94f;
            shootWarmupSpeed = 0.07f;

            Effect sfe = Fx.colorSparkBig;

            ammo(
                    iridium, new BasicBulletType(10f, 95){{
                        width = 12f;
                        hitSize = 7f;
                        height = 20f;
                        lifetime = 28f;
                        shootEffect = sfe;
                        smokeEffect = Fx.shootBigSmoke;
                        ammoMultiplier = 1;
                        pierceCap = 2;
                        pierce = true;
                        pierceBuilding = true;
                        hitColor = backColor = trailColor = Color.valueOf("738184");
                        frontColor = Color.white;
                        trailWidth = 2.1f;
                        trailLength = 10;
                        hitEffect = despawnEffect = Fx.hitBulletColor;
                        buildingDamageMultiplier = 0.3f;
                        collidesGround = false;

                        backSprite = "large-bomb-back";
                        sprite = "mine-bullet";
                    }},
                    fragisteel, new BasicBulletType(10f, 115){{
                        width = 13f;
                        height = 19f;
                        lifetime = 28f;
                        hitSize = 7f;
                        shootEffect = sfe;
                        smokeEffect = Fx.shootBigSmoke;
                        ammoMultiplier = 1;
                        reloadMultiplier = 1f;
                        pierceCap = 3;
                        pierce = true;
                        pierceBuilding = true;
                        hitColor = backColor = trailColor = Pal.tungstenShot;
                        frontColor = Color.white;
                        trailWidth = 2.2f;
                        trailLength = 11;
                        hitEffect = despawnEffect = Fx.hitBulletColor;
                        buildingDamageMultiplier = 0.3f;
                        collidesGround = false;

                        backSprite = "large-bomb-back";
                        sprite = "mine-bullet";
                    }}
            );
            drawer = new DrawTurret(){{
                parts.add(
                        new RegionPart("-front"){{
                            progress = PartProgress.warmup;
                            heatProgress = PartProgress.warmup.add(-0.2f).add(p -> Mathf.sin(9f, 0.2f) * p.warmup);
                            mirror = false;
                            under = true;
                            moveX = 0f;
                            moveY = -2f;
                        }},
                        new  RegionPart("-lower-plate"){{
                            progress = PartProgress.warmup;
                            heatProgress = PartProgress.warmup.add(-0.2f).add(p -> Mathf.sin(9f, 0.2f) * p.warmup);
                            heatColor = Color.red;
                            moveX = -1f;
                            moveY = -1f;
                            mirror = true;
                            moves.add(new PartMove(PartProgress.recoil, 0f, -1.5f, 0f));
                        }},
                        new RegionPart("-upper-plate"){{
                            progress = PartProgress.warmup;
                            heatProgress = PartProgress.warmup.add(-0.2f).add(p -> Mathf.sin(9f, 0.2f) * p.warmup);
                            mirror = true;
                            moveX = -1f;
                            moveY = -1f;
                            moves.add(new PartMove(PartProgress.recoil, 0f, -3f, 0f));
                        }},

                        new RegionPart("-middle-plate"){{
                            progress = PartProgress.warmup;
                            heatProgress = PartProgress.warmup.add(-0.2f).add(p -> Mathf.sin(9f, 0.2f) * p.warmup);
                            mirror = true;
                            moveY = -3.5f;
                            moveX = -1f;
                            moves.add(new PartMove(PartProgress.recoil, 2f, 3f, -30f));
                        }});
            }};

        }};

        overturn = new ItemTurret("overturn"){{
           scaledHealth = 350;
           size = 3;
           buildCostMultiplier = 1.40449438202f;
           requirements (turret, with(iridium, 100, fragisteel, 50, chirokyn, 75));

           liquidCapacity = 10;
           itemCapacity = 10;

           range = 240;
           inaccuracy = 3;
           velocityRnd = 0.3f;
           reload = 30;
           targetAir = true;
           targetGround = false;
           shoot = new ShootAlternate(){{
                spread = 3.1f * 1.9f;
                shots = barrels = 3;
           }};
           ammo(
               fragisteel, new FlakBulletType(10, 15){{
                   sprite = "missile-large";
                   width = 6f;
                   height = 10f;
                   hitSize = 7f;
                   trailWidth = 2.1f;
                   trailLength = 18;
                   trailEffect = Fx.colorSpark;
                   trailRotation = true;

                   frontColor = Color.white;
                   hitColor = backColor = trailColor = Color.valueOf("88a9bd");

                   lifetime = 24;

                   splashDamage = 50;
                   splashDamageRadius = 15;
                   explodeRange = 30;
                   explodeDelay = 0f;

                   hitEffect = new MultiEffect(Fx.flakExplosion, Fx.colorSparkBig);
            }}
           );
           shootY = 0.7f;
           ammoPerShot = 2;

           outlineColor = Color.valueOf("313a3b");
            drawer = new DrawTurret(){{
                    parts.add(
                            new RegionPart("-outer-gun") {{
                                progress = PartProgress.warmup;
                                heatProgress = PartProgress.recoil.add(-0.2f).add(p -> Mathf.sin(9f, 0.2f) * p.recoil);
                                mirror = true;
                                under = true;
                                moveX = 1.2f;
                                moveY = -2f;
                            }},
                            new RegionPart("-inner-gun") {{
                                progress = PartProgress.warmup;
                                heatProgress = PartProgress.recoil.add(-0.2f).add(p -> Mathf.sin(9f, 0.2f) * p.recoil);
                                mirror = true;
                                under = true;
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

        heatDrill = new BurstDrill("heat-drill"){{
            requirements(production, with(iridium, 20));
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

            drillMultipliers.put(iridium, 1.5f);

            consumeLiquids(LiquidStack.with(methane, 5f / 60f));

        }};
        largeHeatDrill = new BurstDrill("large-heat-drill"){{
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

        //distribution
        ducter = new ShadedDuct("ducter"){{
            requirements(distribution, with(iridium, 2));
        }};


        ductjunction = new Junction("duct-junction"){{
            requirements(Category.distribution, with(iridium, 4));
            speed = 16;
            capacity = 2;
            health = 45;
            buildCostMultiplier = 6f;

            ((ShadedDuct)ducter).junctionReplacement = this;
        }};

        ductRouter = new Router("duct-router"){{
            requirements(Category.distribution, with(iridium, 5));
            health = 45;
            buildCostMultiplier = 6f;
        }};

        //liquid
        fluidDuct = new ShadedConduit("fluid-duct"){{
            requirements(liquid, with(iridium, 1));
        }};

        fluidRouter = new LiquidRouter("fluid-router"){{
            requirements(Category.liquid, with(iridium, 3));
            liquidCapacity = 20f;
            underBullets = true;
            solid = false;
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
        fragisteelPress = new GenericCrafter("fragisteel-press"){{
            requirements(crafting, with(iridium, 50, graphite, 40));

            craftEffect = Fx.pulverizeMedium;
            health = 200;
            outputItem = new ItemStack(fragisteel, 1);
            craftTime = 70f;
            size = 2;
            hasItems = true;
            liquidCapacity = 10/60f;

            consumeItem(iridium, 2);
            consumeLiquid(methane, 1f/60);
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
        shipManufactor = new UnitFactory("ship-manufactor"){{ //TODO CHANGE NAME TO NAVAL FUCK YOU
            requirements(units, with(graphite, 55, silicon, 200, iridium, 100, chirokyn, 100));
            plans = Seq.with(
                    new UnitPlan(UnitTypes.minke, 60f * 10, with(silicon, 180, metaglass, 95))
            );
            size = 4;
            consumePower(4f);
        }};

        //effect
        coreSprout = new DrawerCore("core-sprout"){{
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

            drawer = new DrawMulti(
                    new DrawDefault(),
                    new DrawRegion("-top"),
                    new DrawTeam()
            );
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

        drillTurret = new DrillTurret("drill-turret"){{
            requirements(effect, shown, with(nickel, 80000, tarnide, 2, carbide, 12));
            size = 2;
            itemCapacity = 10;
            hasItems = true;
            outputItem = new ItemStack(silicon, 5);
            scaledHealth = 1;
        }};
    }
}