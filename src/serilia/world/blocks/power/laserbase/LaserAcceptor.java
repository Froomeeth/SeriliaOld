package serilia.world.blocks.power.laserbase;

import arc.math.geom.Vec2;
import mindustry.gen.Building;

/**Class with functions for convenient acceptor moving.*/
public class LaserAcceptor{
    public Building holder;
    public float x, y, realX, realY, rotation, range;
    public int height;
    public boolean collide;
    Vec2 relativePos;

    public LaserAcceptor(float x, float y, float rotation, float range, int height, Building holder){
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.range = range;
        this.height = height;
        this.holder = holder;
        relativePos.set(x, y);
    }

    public void set(float x, float y, float rotation){
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        relativePos.set(x, y);
    }

    public void setRotation(float rotation){
        float diff = relativePos.angle() - this.rotation;

        relativePos.setAngle(rotation + diff);
        this.rotation = rotation;
        updatePos();
    }

    public void rotate(float rotate){
        relativePos.rotate(rotate);
        this.rotation += rotate;
        updatePos();
    }

    void updatePos(){
        realX = holder.x + relativePos.x;
        realY = holder.y + relativePos.y;
    }
}
