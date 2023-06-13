package serilia.content;

import arc.graphics.Color;
import mindustry.ai.types.BuilderAI;
import mindustry.content.Fx;
import mindustry.entities.bullet.MissileBulletType;
import mindustry.entities.bullet.PointBulletType;
import mindustry.entities.pattern.ShootAlternate;
import mindustry.entities.pattern.ShootPattern;
import mindustry.gen.Sounds;
import mindustry.gen.UnitEntity;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.unit.ErekirUnitType;
import serilia.types.SeriliaUnitType;

public class SeUnits {
    //public static @EntityDef({Unitc.class, ElevationMovec.class/*, TractorBeamc.class*/}) UnitType glow;

    public static UnitType
            merun,
    scion,
    converge,
    youth,

    //assault
    sunburst;

    public static void load() {
        scion = new SeriliaUnitType("scion") {{

            aiController = BuilderAI::new;
            isEnemy = false;
            constructor = UnitEntity::create;
            groundLayer = Layer.flyingUnit;

            health = 270f;
            armor = 3f;
            hitSize = 13f;

            flying = true;
            speed = 4.3f;
            rotateSpeed = 10f;
            drag = 0.09f;
            accel = 0.14f;

            itemCapacity = 30;
            buildSpeed = 3;
            mineTier = 1;
            mineSpeed = 2;

            engineSize = 0;
            engineOffset = 0;

            weapons.add(new Weapon() {{
                x = 3f;
                y = 0f;
                mirror = true;

                rotate = false;
                shootCone = 5;
                inaccuracy = 6;
                reload = 15f;

                shootSound = Sounds.missile;

                bullet = new MissileBulletType() {{
                    sprite = "missile";
                    width = 6f;
                    height = 10f;

                    speed = 2.6f;
                    lifetime = 50f;
                    damage = 18;

                    trailWidth = 2;
                    trailLength = 16;

                    homingDelay = 20;
                    homingRange = 60;
                }};
            }});

            setEnginesMirror(
                    new UnitEngine(14 / 4f, -22 / 4f, 1.9f, 270f)
            );
        }};

        /*glow = new UnitType("glow"){{
            //homeWorld = 1;
            flying = true;

            health = 700f;
            armor = 3f;
            hitSize = 64f/4f;
            buildSpeed = 1.5f;
            drag = 0.08f;
            speed = 7.5f;
            rotateSpeed = 8f;
            accel = 0.08f;

            mineWalls = true;
            mineFloor = false;
            mineHardnessScaling = false;
            mineSpeed = 9f;
            mineTier = 3;
            itemCapacity = 110;

            coreUnitDock = true;
            controller = u -> new BuilderAI(true, 500f);

            payloadCapacity = 4f * 4f * 8 * 8;
            pickupUnits = false;

            vulnerableWithPayloads = true;
            fogRadius = 0f;
            targetable = false;
            hittable = false;
            isEnemy = false;
            targetPriority = -2;

            buildBeamOffset = 6f;
            trailLength = 5;
            engineOffset = 6f;
            engineSize = 1.8f;
        }};*/

        sunburst = new ErekirUnitType("sunburst") {
            {
                speed = 0.65f;
                drag = 0.1f;
                hitSize = 21f;
                rotateSpeed = 3f;
                health = 2900;
                armor = 7f;
                fogRadius = 40f;
                stepShake = 0f;

                legCount = 6;
                legLength = 18f;
                legGroupSize = 3;
                lockLegBase = true;
                legContinuousMove = true;
                legExtension = -3f;
                legBaseOffset = 7f;
                legMaxLength = 1.1f;
                legMinLength = 0.2f;
                legLengthScl = 0.95f;
                legForwardScl = 0.9f;

                legMoveSpace = 1f;
                hovering = true;

                shadowElevation = 0.2f;
                groundLayer = Layer.legUnit - 1f;

                weapons.add(new Weapon("large-weapon"){{
                    reload = 13f;
                    x = 4f;
                    y = 2f;
                    top = false;
                    ejectEffect = Fx.casing1;
                    bullet = new PointBulletType(){{
                        shootEffect = Fx.instShoot;
                        hitEffect = Fx.instHit;
                        smokeEffect = Fx.smokeCloud;
                        trailEffect = Fx.instTrail;
                        despawnEffect = Fx.instBomb;
                        trailSpacing = 3f;
                        damage = 70;
                        splashDamage = 35;
                        splashDamageRadius = 35;
                        buildingDamageMultiplier = 0.2f;
                        hitShake = 6f;
                        ammoMultiplier = 1f;
                    }};
                }});
            }
        };
    }}