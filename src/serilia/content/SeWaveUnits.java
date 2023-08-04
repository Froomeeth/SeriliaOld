package serilia.content;

import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.MissileBulletType;
import mindustry.entities.pattern.ShootPattern;
import mindustry.gen.MechUnit;
import mindustry.gen.Sounds;
import mindustry.gen.UnitEntity;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.Weapon;

public class SeWaveUnits {
    public static UnitType
    scout,
    tier1Mech, tier2Mech, tier3Mech,
    tier1Aircraft, tier2Aircraft, tier3Aircraft,

    uncertainty, alert, distress, tension, emergency, danger, crisis, contingency;

    public static void load(){

        scout = new UnitType("scout"){{
            constructor = MechUnit::create;
            health = 200;
            armor = 2;
            hitSize = 8f;

            flying = false;
            speed = 0.41f;
            canBoost = false;
            itemCapacity = 0;
            weapons.add(new Weapon("serilia-scout-weapon"){{
                top = false;
                x = 0f;
                y = 0f;
                shootX = 3;
                shootY = 2;

                reload = 30f;
                inaccuracy = 1;
                bullet = new BasicBulletType(3f, 12){{
                    lifetime = 50f;
                    despawnEffect = Fx.none;

                    width = 7f;
                    height = 9f;
                    trailColor = backColor = Pal.meltdownHit;
                    trailLength = 16;
                    trailWidth = 1.2f;
                }};
            }});
            drawCell = false;
            mechLegColor = outlineColor = Color.valueOf("322727");
        }};
        tier1Mech = new UnitType("tier-1-mech"){{
            constructor = MechUnit::create;
            health = 700;
            armor = 1;
            hitSize = 17f;

            flying = false;
            speed = 0.40f;
            rotateSpeed = 2;
            stepShake = 0.1f;
            canBoost = false;
            itemCapacity = 0;
            weapons.add(new Weapon("serilia-tier-1-mech-weapon"){{
                top = false;
                x = 0f;
                y = 0f;
                layerOffset = -0.002f;

                reload = 60f;
                inaccuracy = 3;

                shootX = 6;
                shootY = 6;
                shootSound = Sounds.shotgun;
                shoot = new ShootPattern(){{
                    shots = 2;
                    shotDelay = 3;
                }};
                bullet = new BasicBulletType(4f, 20){{
                    lifetime = 30f;

                    pierce = false;
                    pierceBuilding = true;
                    pierceCap = 3;

                    width = 8f;
                    height = 10f;
                    trailColor = backColor = Pal.meltdownHit;
                    trailLength = 20;
                    trailWidth = 1.4f;

                    despawnEffect = Fx.hitBulletBig;
                }};
            }});
            drawCell = false;
            mechLegColor = outlineColor = Color.valueOf("322727");
        }};
        tier2Aircraft = new UnitType("tier-2-aircraft"){{
            constructor = UnitEntity::create;
            health = 600;
            armor = 1;
            hitSize = 17;

            flying = true;
            speed = 4f;
            rotateSpeed = 5;
            accel = 0.06f;
            drag = 0.03f;
            itemCapacity = 0;

            weapons.add(new Weapon(){{
                x = -3f;
                y = -1.5f;
                mirror = true;
                top = false;

                baseRotation = 30;
                shootCone = 50;
                rotate = false;

                reload = 70;
                inaccuracy = 3;

                shootX = 0;
                shootY = 0;
                shootSound = Sounds.missile;
                shoot = new ShootPattern(){{
                   shots = 3;
                   shotDelay = 4;
                }};
                bullet = new MissileBulletType(3f, 13){{
                    width = 7f;
                    height = 10f;

                    lifetime = 50;

                    homingPower = 0.05f;
                    homingDelay = 4;
                }};
            }});
            drawCell = false;
            outlineColor = Color.valueOf("322727");
            engineOffset = 8.2f;
            engineSize = 3.4f;
        }};
    }
}
