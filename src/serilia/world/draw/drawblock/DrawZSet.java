package serilia.world.draw.drawblock;

import arc.graphics.g2d.Draw;
import mindustry.gen.Building;
import mindustry.world.draw.DrawBlock;

public class DrawZSet extends DrawBlock{
    public float layer;

    public DrawZSet(float layer){{
        this.layer = layer;
    }}

    public void draw(Building build) {
        Draw.z(layer);
    }
}