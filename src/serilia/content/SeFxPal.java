package serilia.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import mindustry.entities.Effect;

import static arc.graphics.g2d.Lines.lineAngle;
import static arc.math.Angles.randLenVectors;

public class SeFxPal{
    public static Color
        caliOutline = Color.valueOf("3b4745"),
        ahkarOutline = Color.valueOf("363136"),


        coreReactor = Color.valueOf("6cbf86");

    public static Effect
        ahkarPlace = new Effect(20, e ->{
            Draw.color(Color.white, 0.6f * Interp.circle.apply(e.fout(Interp.slope)));
            //Lines.stroke(1f);
            Fill.square(e.x, e.y, 4 * e.rotation);

            randLenVectors(e.id + 1, 8, 8 * e.rotation * e.finpow(), (x, y) ->
                    lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + e.fout() * 3f));
        }),

        ahkarBreak = new Effect(15, e ->{
            Draw.color(Color.white, 0.5f * e.fout());
            Fill.square(e.x, e.y, 4 * e.rotation);

            Draw.color(Color.white, 0.2f);
            Lines.stroke(5f * e.fout(Interp.circleOut));
            Lines.square(e.x, e.y, 4 * e.rotation);

            /*randLenVectors(e.id + 1, 8, 4 * e.rotation * e.finpow(), (x, y) ->
                    lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + e.fout() * 3f));*/
        });

}