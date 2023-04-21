package serilia.content;

import mindustry.ai.types.BuilderAI;
import mindustry.entities.bullet.MissileBulletType;
import mindustry.gen.Sounds;
import mindustry.gen.UnitEntity;
import mindustry.graphics.Layer;
import mindustry.type.*;
import serilia.types.SeriliaUnitType;

public class SeUnits {
    public static UnitType

    scion,
    converge,
    youth;

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
    }
}