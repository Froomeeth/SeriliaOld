package serilia.content;

import mindustry.ai.types.BuilderAI;
import mindustry.entities.bullet.MissileBulletType;
import mindustry.gen.Sounds;
import mindustry.gen.UnitEntity;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import serilia.types.SeriliaUnitType;

import static mindustry.Vars.tilesize;

public class SeUnits {
    public static UnitType

    scion,
    converge,
    youth,

    glow;

    public static void load(){
        scion = new SeriliaUnitType("scion"){{

            aiController = BuilderAI::new;
            isEnemy = false;
            constructor = UnitEntity::create;
            groundLayer = Layer.flyingUnit;

            health = 270f;
            armor = 3f;
            hitSize = 15f;

            flying = true;
            speed = 3.2f;
            rotateSpeed = 9f;
            drag = 0.09f;
            accel = 0.15f;

            itemCapacity = 60;
            buildSpeed = 3;
            mineTier = 10;
            mineSpeed = 10;

            engineSize = 3;
            engineOffset = 7;

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
        }};

        glow = new SeriliaUnitType("glow"){{
            homeWorld = 1;
            constructor = UnitEntity::create;
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

            payloadCapacity = 2f * 2f * tilesize * tilesize;
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
        }};
    }
}