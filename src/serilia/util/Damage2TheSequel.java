package serilia.util;

import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import mindustry.core.World;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Healthc;
import mindustry.gen.Unit;

import static mindustry.Vars.world;

public class Damage2TheSequel{
    private static final Vec2 vec = new Vec2(), seg1 = new Vec2(), seg2 = new Vec2();
    private static final Rect rect = new Rect();
    private static final Rect hitrect = new Rect();

    private static Building tmpBuilding;
    private static Unit tmpUnit;

    public static Healthc linecast(float x, float y, float angle, float length, boolean ground, boolean air, Team team){ //todo needs a tile detection thing
        vec.trns(angle, length);

        tmpBuilding = null;

        if(ground){
            World.raycastEachWorld(x, y, x + vec.x, y + vec.y, (cx, cy) -> {
                Building tile = world.build(cx, cy);
                if(tile != null && tile.team != team){
                    tmpBuilding = tile;
                    return true;
                }
                return false;
            });
        }

        rect.setPosition(x, y).setSize(vec.x, vec.y);
        float x2 = vec.x + x, y2 = vec.y + y;

        if(rect.width < 0){
            rect.x += rect.width;
            rect.width *= -1;
        }

        if(rect.height < 0){
            rect.y += rect.height;
            rect.height *= -1;
        }

        float expand = 3f;

        rect.y -= expand;
        rect.x -= expand;
        rect.width += expand * 2;
        rect.height += expand * 2;

        tmpUnit = null;

        Units.nearbyEnemies(team, rect, e -> {
            if((tmpUnit != null && e.dst2(x, y) > tmpUnit.dst2(x, y)) || !e.checkTarget(air, ground) || !e.targetable(team)) return;

            e.hitbox(hitrect);
            Rect other = hitrect;
            other.y -= expand;
            other.x -= expand;
            other.width += expand * 2;
            other.height += expand * 2;

            Vec2 vec = Geometry.raycastRect(x, y, x2, y2, other);

            if(vec != null){
                tmpUnit = e;
            }
        });

        if(tmpBuilding != null && tmpUnit != null){
            if(Mathf.dst2(x, y, tmpBuilding.getX(), tmpBuilding.getY()) <= Mathf.dst2(x, y, tmpUnit.getX(), tmpUnit.getY())){
                return tmpBuilding;
            }
        }else if(tmpBuilding != null){
            return tmpBuilding;
        }

        return tmpUnit;
    }
}
