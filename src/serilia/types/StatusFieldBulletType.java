package serilia.types;

import mindustry.content.StatusEffects;
import mindustry.entities.Units;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;
import mindustry.type.StatusEffect;

/**
 * TO MANGO:
 * if you want just a cloud of effect do collide = hittable = false
 * I have lost faith
 */
public class StatusFieldBulletType extends BasicBulletType{
    //Seq<StatusFieldDrawer> drawers = new Seq<>();
    public boolean drawBullet = true;
    public float fieldRange = 50f, falloff;
    public StatusEffect fieldStatus = StatusEffects.electrified;
    public float fieldStatusDuration = 200f;
    public boolean fieldHitEnemy = true, fieldHitAlly = false;

    @Override
    public void update(Bullet b){
        super.update(b);

        if(b.time % 4 < 1){
            Units.nearby(null, b.x, b.y, fieldRange, unit -> {
                if(fieldHitEnemy && fieldHitAlly || fieldHitEnemy && unit.team != b.team || fieldHitAlly && unit.team == b.team){
                    unit.apply(fieldStatus, fieldStatusDuration);
                }
            });
        }
    }

    @Override
    public void draw(Bullet b){
        if(drawBullet){
            super.draw(b);
        } else {
            drawTrail(b);
            drawParts(b);
        }
    }
}
