package serilia.types;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;

public class SolidShieldAbility extends Ability {
    /** Shield radius. */
    public float radius = 60f;
    /** Shield regen speed in damage/tick. */
    public float regen = 0.1f;
    /** Maximum shield. */
    public float max = 200f;
    /** Cooldown after the shield is broken, in ticks. */
    public float cooldown = 60f * 5;
    /** Sides of shield polygon. */
    public int sides = 6;
    /** Rotation of shield. */
    public float rotation = 0f;

    /** State. */
    protected float radiusScale, alpha;

    private static float realRad;
    private static Unit paramUnit;
    private static SolidShieldAbility paramField;
    private static final Cons<Bullet> bulletConsumer = bullet -> {
        if(bullet.team != paramUnit.team && bullet.type.absorbable && paramUnit.shield > 0 && Intersector.isInRegularPolygon(paramField.sides, paramUnit.x, paramUnit.y, realRad, paramField.rotation, bullet.x, bullet.y)){
            bullet.absorb();
            Fx.absorb.at(bullet);

            //break shield
            if(paramUnit.shield <= bullet.damage()){
                paramUnit.shield -= paramField.cooldown * paramField.regen;

                Fx.shieldBreak.at(paramUnit.x, paramUnit.y, paramField.radius, paramUnit.team.color, paramUnit);
            }

            paramUnit.shield -= bullet.damage();
            paramField.alpha = 1f;
        }
    };

    protected static final Cons<Unit> unitConsumer = unit -> {
        float overlapDst = (unit.hitSize/2f + realRad) - unit.dst(paramUnit);

        if(Intersector.isInRegularPolygon(paramField.sides, paramUnit.x, paramUnit.y, realRad + unit.hitSize/2f, paramField.rotation, unit.x, unit.y)){
            //if(overlapDst > unit.hitSize) unit.damage(50f);

            //get out
            unit.set(Tmp.v1.set(unit).sub(paramUnit).setLength(overlapDst - 0.1f).add(unit));

            if(Mathf.chanceDelta(0.12f * Time.delta)){
                Fx.circleColorSpark.at(unit.x, unit.y, paramUnit.team.color);
            }

            if(paramUnit.shield <= 3f){
                paramUnit.shield -= paramField.cooldown * paramField.regen;

                Fx.shieldBreak.at(paramUnit.x, paramUnit.y, paramField.radius, paramUnit.team.color, paramUnit);
            }

            paramUnit.shield -= 3f;
            paramField.alpha = 1f;
        }
    };

    public SolidShieldAbility(float radius, float regen, float max, float cooldown){
        this.radius = radius;
        this.regen = regen;
        this.max = max;
        this.cooldown = cooldown;
    }

    public SolidShieldAbility(float radius, float regen, float max, float cooldown, int sides, float rotation){
        this.radius = radius;
        this.regen = regen;
        this.max = max;
        this.cooldown = cooldown;
        this.sides = sides;
        this.rotation = rotation;
    }

    SolidShieldAbility(){}

    @Override
    public void update(Unit unit){
        if(unit.shield < max){
            unit.shield += Time.delta * regen;
        }

        alpha = Math.max(alpha - Time.delta/10f, 0f);

        if(unit.shield > 0){
            radiusScale = Mathf.lerpDelta(radiusScale, 1f, 0.06f);
            paramUnit = unit;
            paramField = this;
            checkRadius(unit);

            Groups.bullet.intersect(unit.x - realRad, unit.y - realRad, realRad * 2f, realRad * 2f, bulletConsumer);
            Units.nearbyEnemies(unit.team, unit.x, unit.y, realRad * 2 + 20f, unitConsumer);
        }else{
            radiusScale = 0f;
        }
    }

    @Override
    public void draw(Unit unit){
        checkRadius(unit);

        if(unit.shield > 0){
            Draw.z(Layer.shields);

            Draw.color(unit.team.color, Color.white, Mathf.clamp(alpha));

            if(Vars.renderer.animateShields){
                Fill.poly(unit.x, unit.y, sides, realRad, rotation);
            }else{
                Lines.stroke(1.5f);
                Draw.alpha(0.09f);
                Fill.poly(unit.x, unit.y, sides, radius, rotation);
                Draw.alpha(1f);
                Lines.poly(unit.x, unit.y, sides, radius, rotation);
            }
        }
    }

    @Override
    public void displayBars(Unit unit, Table bars){
        bars.add(new Bar("stat.shieldhealth", Pal.accent, () -> unit.shield / max)).row();
    }

    public void checkRadius(Unit unit){
        //timer2 is used to store radius scale as an effect
        realRad = radiusScale * radius;
    }
}
