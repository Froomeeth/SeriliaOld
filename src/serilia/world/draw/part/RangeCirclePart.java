package serilia.world.draw.part;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import mindustry.entities.part.DrawPart;

public class RangeCirclePart extends DrawPart{
    public float range, stroke, layer, backLayer;
    public Color color, backColor;
    public boolean additive, backAdditive;

    public RangeCirclePart(float range, float stroke, float layer, float backlayer, Color color, Color backColor){
        this.range = range;
        this.stroke = stroke;
        this.color = color;
        this.backColor = backColor;
    }
    public RangeCirclePart(float range, float stroke, float layer, float backlayer, Color color, Color backColor, boolean additive, boolean backAdditive){
        this(range, stroke, layer, backlayer, color, backColor);
        this.additive = additive;
        this.backAdditive = backAdditive;
    }

    @Override
    public void draw(PartParams params){
        float z = Draw.z();

        Draw.z(backLayer); Draw.color(backColor); if(backAdditive) Draw.blend(Blending.additive);
        Fill.circle(params.x, params.y, range);

        Draw.z(layer); Draw.color(color); Lines.stroke(stroke); if(additive){
            Draw.blend(Blending.additive);
        } else Draw.blend();
        Lines.circle(params.x, params.y, range);

        Draw.blend();
        Draw.color();
        Draw.z(z);
    }

    @Override
    public void load(String name){

    }
}
