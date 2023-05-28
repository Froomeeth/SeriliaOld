package serilia.entities.comp;

import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.util.pooling.Pools;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import serilia.gen.entities.Laserc;
import serilia.util.Damage2TheSequel;
import serilia.world.blocks.power.LaserHolder;

import static ent.anno.Annotations.*;

@EntityDef(value = Laserc.class, genIO = false)
@EntityComponent
public abstract class LaserComp implements Entityc, Posc, Drawc, Teamc{
    @Import float x, y;
    @Import Team team;
    public float length = 500f, drawZ = Layer.blockOver;
    public Vec2 vector = new Vec2(500f, 500f);
    public float power = 100;
    public boolean ground = true, air = false;

    public void updatePosition(float x, float y, Position point){
        set(x, y);
        vector.set(point);
    }

    @Override
    public void remove(){
        Pools.free(self());
    }

    @Override
    public void update(){
        vector.setLength(length);

        Healthc hit = Damage2TheSequel.linecast(x, y, vector.sub(x, y).angle(), length, ground, air, team);

        if(hit == null) return;

        vector.setLength(hit.dst(x, y));
        if(hit instanceof LaserHolder){
            ((LaserHolder)hit).acceptLaser();
        } else {
            hit.damageContinuous(power * 0.3f); //
        }
    }

    @Override
    public void draw(){
        Draw.blend(Blending.additive);
        Draw.z(drawZ);
        Draw.alpha(Math.max(0.3f, power * 0.1f));
        Lines.stroke(2f);
        Lines.line(x, y, vector.x, vector.y);
        Draw.reset();
        Draw.blend();
    }
}
