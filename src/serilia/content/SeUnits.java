package serilia.content;

import mindustry.ai.types.BuilderAI;
import mindustry.content.StatusEffects;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.MissileBulletType;
import mindustry.gen.Sounds;
import mindustry.gen.UnitEntity;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.weapons.RepairBeamWeapon;
import serilia.gen.entities.EntityRegistry;
import serilia.gen.entities.TractorBeam;
import serilia.types.SeriliaUnitType;
import serilia.types.StatusFieldBulletType;
import serilia.world.draw.part.RangeCirclePart;

import static mindustry.Vars.tilesize;

public class SeUnits {
    //@Annotations.EntityDef({Posc.class, Rotc.class, Hitboxc.class, Unitc.class, Payloadc.class, TractorBeamc.class}) Object tractorUnit;

    public static UnitType
            glow,
    scion,
    converge,
    youth;

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

            weapons.add(
                new Weapon(){{
                    bullet = new StatusFieldBulletType(){{
                        fieldRange = 30f;
                        fieldStatus = StatusEffects.melting;
                        parts.add(new RangeCirclePart(30f, 0.5f, 101f, 99, Pal.orangeSpark, Pal.orangeSpark.cpy().a(0f)));
                    }};
                }}
            );
        }});


        scion = new SeriliaUnitType("scion"){{

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

            weapons.add(new Weapon(){{
                x = 3f;
                y = 0f;
                mirror = true;

                rotate = false;
                shootCone = 5;
                inaccuracy = 6;
                reload = 15f;

                shootSound = Sounds.missile;

                bullet = new MissileBulletType(){{
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
    }
}