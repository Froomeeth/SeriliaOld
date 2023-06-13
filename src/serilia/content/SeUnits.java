package serilia.content;

import arc.graphics.Color;
import mindustry.ai.types.BuilderAI;
<<<<<<< HEAD
import mindustry.content.Fx;
=======
import mindustry.entities.bullet.BulletType;
>>>>>>> 6e893c1152ae3837cf9af4b199bfcd97f3ab6b18
import mindustry.entities.bullet.MissileBulletType;
import mindustry.entities.bullet.PointBulletType;
import mindustry.entities.pattern.ShootAlternate;
import mindustry.entities.pattern.ShootPattern;
import mindustry.gen.Sounds;
import mindustry.gen.UnitEntity;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
<<<<<<< HEAD
import mindustry.type.unit.ErekirUnitType;
=======
import mindustry.type.weapons.RepairBeamWeapon;
import serilia.gen.entities.EntityRegistry;
import serilia.gen.entities.TractorBeam;
>>>>>>> 6e893c1152ae3837cf9af4b199bfcd97f3ab6b18
import serilia.types.SeriliaUnitType;

import static mindustry.Vars.tilesize;

public class SeUnits {
    //@Annotations.EntityDef({Posc.class, Rotc.class, Hitboxc.class, Unitc.class, Payloadc.class, TractorBeamc.class}) Object tractorUnit;

    public static UnitType
            glow,
    scion,
    converge,
    youth,

<<<<<<< HEAD
    //assault
    sunburst;

    public static void load() {
        scion = new SeriliaUnitType("scion") {{
=======
    public static void load(){

        glow = EntityRegistry.content("glow", TractorBeam.class, name -> new SeriliaUnitType(name){{
            homeWorld = 1;

            coreUnitDock = true;
            controller = u -> new BuilderAI(true, 60);
            isEnemy = false;
            envDisabled = 0;

            targetPriority = -2;
            lowAltitude = false;
            mineWalls = true;
            mineFloor = false;
            mineHardnessScaling = false;
            flying = true;
            mineSpeed = 9f;
            mineTier = 3;
            buildSpeed = 1.5f;
            drag = 0.08f;
            speed = 7.5f;
            rotateSpeed = 8f;
            accel = 0.08f;
            itemCapacity = 110;
            health = 700f;
            armor = 3f;
            hitSize = 12f;
            buildBeamOffset = 8f;
            payloadCapacity = 2f * 2f * tilesize * tilesize;
            pickupUnits = false;
            vulnerableWithPayloads = true;

            fogRadius = 0f;
            targetable = false;
            hittable = false;

            engineOffset = 7.5f;
            engineSize = 3.4f;

            setEnginesMirror(
                    new UnitEngine(35 / 4f, -13 / 4f, 2.7f, 315f),
                    new UnitEngine(28 / 4f, -35 / 4f, 2.7f, 315f)
            );

            weapons.add(new RepairBeamWeapon(){{
                widthSinMag = 0.11f;
                reload = 20f;
                x = 19f/4f;
                y = 19f/4f;
                rotate = false;
                shootY = 0f;
                beamWidth = 0.7f;
                aimDst = 0f;
                shootCone = 40f;
                mirror = true;

                repairSpeed = 3.6f / 2f;
                fractionRepairSpeed = 0.03f;

                targetUnits = false;
                targetBuildings = true;
                autoTarget = false;
                controllable = true;
                laserColor = Pal.accent;
                healColor = Pal.accent;

                bullet = new BulletType(){{
                    maxRange = 65f;
                }};
            }});
        }});


        scion = new SeriliaUnitType("scion"){{
>>>>>>> 6e893c1152ae3837cf9af4b199bfcd97f3ab6b18

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