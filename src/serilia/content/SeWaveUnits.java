package serilia.content;

import mindustry.content.Fx;
import mindustry.entities.bullet.FlakBulletType;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.Weapon;

public class SeWaveUnits {
    public static UnitType
    scout;
    public static void load(){

        scout = new UnitType("scout"){{
            health = 200;
            armor = 2;
            hitSize = 8f;

            flying = false;
            speed = 0.41f;
            canBoost = false;
            itemCapacity = 0;
            weapons.add(new Weapon("serilia-scout-weapon"){{
                reload = 20f;
                inaccuracy = 1;

                top = false;
                x = 0f;
                y = 0f;
                shootX = 3;
                shootY = 2;
                ejectEffect = Fx.hitBulletBig;
                bullet = new FlakBulletType(3f, 15){{
                    lifetime = 60f;
                    despawnEffect = Fx.flakExplosion;
                    explodeRange = 16;
                    flakDelay = 4;

                    width = 7f;
                    height = 9f;
                    trailColor = backColor = Pal.lightFlame;
                }};
            }});
        }};
    }
}
