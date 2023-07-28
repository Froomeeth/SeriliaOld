package serilia.content;

import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.FlakBulletType;
import mindustry.gen.MechUnit;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.Weapon;

public class SeWaveUnits {
    public static UnitType
    scout,

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
                reload = 30f;
                inaccuracy = 1;

                top = false;
                x = 0f;
                y = 0f;
                shootX = 3;
                shootY = 2;
                bullet = new BasicBulletType(3f, 12){{
                    lifetime = 50f;
                    despawnEffect = Fx.none;

                    width = 7f;
                    height = 9f;
                    trailColor = backColor = Pal.meltdownHit;
                    trailLength = 16;
                    trailWidth = 1.2f;
                }};
                drawCell = false;
                mechLegColor = outlineColor = Color.valueOf("322727");
            }});
        }};
    }
}
