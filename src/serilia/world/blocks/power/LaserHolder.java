package serilia.world.blocks.power;

import arc.math.geom.Position;
import arc.struct.Seq;
import arc.util.pooling.Pools;
import mindustry.game.Team;
import serilia.gen.entities.Laser;

public interface LaserHolder{
    float power = 0f;
    Seq<Laser> lasers = new Seq<>();
    default Laser createLaser(float x, float y, Position point, float length, boolean ground, boolean air, Team team){
        Laser l = newLaser();
        l.x = x;
        l.y = y;
        l.length = length;
        l.ground = ground;
        l.air = air;
        l.team = team;
        return l;
    }

    static Laser newLaser(){
        return Pools.obtain(Laser.class, Laser::create);
    }

    default float getPower(){
        return power;
    }

    Seq<Position> getAcceptors();
    void acceptLaser();

    default void drawLasers(){
        lasers.each(Laser::draw);
    }

    default void updateLasers(){
        lasers.each(Laser::update);
    }

    default void removeLasers(){
        lasers.each(Laser::remove);
    }
}
