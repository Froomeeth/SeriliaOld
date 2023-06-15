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
import mindustry.entities.bullet.BulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.ShootAlternate;
import mindustry.gen.MechUnit;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.type.UnitType;
import mindustry.type.unit.MissileUnitType;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.production.AttributeCrafter;
import mindustry.world.blocks.production.BurstDrill;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.draw.*;
import mindustry.world.meta.Attribute;
import serilia.world.blocks.distribution.ShadedDuct;
import serilia.world.blocks.misc.DrawTest;
import serilia.world.blocks.payload.MoreGenericCrafter;

import static mindustry.gen.Sounds.plasmaboom;
import static mindustry.type.Category.*;
import static mindustry.type.ItemStack.with;
import static mindustry.world.meta.BuildVisibility.sandboxOnly;
import static serilia.content.SeResources.*;

public class CaliBlocks {
    public static Block

        //turret
        ballista,

        //drill
        methaneExtractor, heatDrill, largeHeatDrill, ignitionDrill, radiatorBore, bulkQuarry, bulkDrill,

        //distribution (payload too)
        ducter,

        //liquid

        //power

        //defense
        iridiumWall, smallIridiumWall, largeIridiumWall, allay,

        //crafting
        fragisteelPress, bulkRefinery,

        //unit
        mechManufactor, droneManufactor, shipManufactor,

        //effect
        coreSprout, coreBurgeon, coreGreenhouse,

        //payloads
        vanadiniteRock,

        //misc
        drawTest, bitTiler;

    public static void load() {
        //turret
        ballista = new ItemTurret("ballista") {{requirements(Category.turret, with(SeResources.iridium, 175, SeResources.tarnide, 100, SeResources.chirokyn, 75));

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
                    SeResources.iridium, new BasicBulletType(10f, 95){{
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
                    SeResources.fragisteel, new BasicBulletType(10f, 115){{
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
        allay = new PowerTurret("allay"){{
            scaledHealth = 75f;
            size = 4;
            requirements(Category.turret, with( Items.silicon, 200, Items.graphite, 250, iridium, 500, chirokyn, 100));
            liquidCapacity = 40/60f;
            consumePower(200/60f);
            consumeLiquid(steam, 20/60f);
            range = 300f;
            inaccuracy = 0;
            reload = 60f * 4.5f;
            targetAir = false;
            targetHealing = true;
            targetGround = false;

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
                    fragBullets = 1;
                    fragSpread = 0;
                    fragAngle = 0;
                    fragBullet = new BulletType() {{
                        speed = 0f;
                        spawnUnit = new MissileUnitType("allay-field") {{
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
                                hitSize = 0;
                                deathExplosionEffect = Fx.healWaveMend;
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
        }};

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
        //unit

        mechManufactor = new UnitFactory("mech-manufactor"){{
            requirements(Category.units, with(Items.graphite, 55, Items.silicon, 200, iridium, 100, chirokyn, 100));
            plans = Seq.with(
                    new UnitPlan(UnitTypes.fortress, 60f * 15, with(Items.silicon, 95, Items.lead, 10)),
                    new UnitPlan(UnitTypes.cleroi, 60f * 10, with(Items.silicon, 50, Items.coal, 10))
            );
            size = 4;
            consumePower(4f);
        }};
        droneManufactor = new UnitFactory("drone-manufactor"){{
            requirements(Category.units, with(Items.graphite, 55, Items.silicon, 200, iridium, 100, chirokyn, 100));
            plans = Seq.with(
                    new UnitPlan(UnitTypes.mega, 60f * 15, with(Items.silicon, 85, Items.lead, 10)),
                    new UnitPlan(UnitTypes.elude, 60f * 10, with(Items.silicon, 90, Items.coal, 10))
            );
            size = 4;
            consumePower(4f);
        }};
        shipManufactor = new UnitFactory("ship-manufactor"){{
            requirements(Category.units, with(Items.graphite, 55, Items.silicon, 200, iridium, 100, chirokyn, 100));
            plans = Seq.with(
                    new UnitPlan(UnitTypes.minke, 60f * 10, with(Items.silicon, 180, Items.metaglass, 95))
            );
            size = 4;
            consumePower(4f);
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