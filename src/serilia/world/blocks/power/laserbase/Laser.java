package serilia.world.blocks.power.laserbase;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Healthc;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import serilia.util.Damage2TheSequel;
import serilia.util.SeUtil;

public class Laser{
    public LaserThing holder;
    public float power, rotation, range, length, stroke = 2f, drawLayer = Layer.blockUnder;
    public int height;
    public Vec2 pos = new Vec2(1f, 1f), targetPos = new Vec2(1f, 1f);
    public Seq<LaserThing> sources = new Seq<>();
    public Healthc hit;


    public Laser(float range, int height, LaserThing holder){
        this.range = range;
        this.height = height;
        this.holder = holder;
    }

    public void set(float x, float y, float rotation){
        pos.set(x, y);
        this.rotation = rotation;
    }

    //TODO: WITHIN(LASERACCEPTOR ACCEPTOR)

    public void update(float power){
        this.power = power;

        hit = Damage2TheSequel.linecast(pos.x, pos.y, rotation, range, height == 0, height == 2, Team.derelict, holder);

        if(hit != null){
            if(hit instanceof LaserThing e){
                e.accept(this);
            }else damage(hit, this);
        }
        SeUtil.vecSetLine(targetPos, pos, rotation, length = hit != null ? (hit.dst(pos.x, pos.y)
                - (hit instanceof Building b ? b.block.size * 4f
                : (hit instanceof Unit u ? u.hitSize / 2f : 0f)))
                : range
        );

    }

    public static void damage(Healthc hit, Laser laser){
        hit.damageContinuous(laser.power * 0.2f * Time.delta);
    }

    public void draw(){
        Draw.alpha(power / 7f);
        Draw.z(drawLayer); //todo automatic drawLayer
        Lines.stroke(stroke);
        Lines.line(pos.x, pos.y, targetPos.x, targetPos.y); //todo do I just use a texture for bloom?
        Draw.reset();
    }
}
