package serilia.content;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.UnitPayload;
import serilia.world.blocks.payload.UniversalCrafter;

import static arc.graphics.g2d.Draw.*;
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
        }),

        payDespawn = new Effect(30f, e -> {
            if(!(e.data instanceof Payload data)) return;

            scl(e.fout(Interp.pow3Out) * 1.05f);
            data.draw();

        }).layer(34f),

        payInstantDespawn = new Effect(30f, e -> {
            if(!(e.data instanceof UniversalCrafter.YootData data)) return;

            Payload pay = data.pay;
            pay.update(null, null);
            Tmp.v1.set(pay.x(), pay.y()).lerp(data.pos, e.finpow() / 4f);
            pay.set(Tmp.v1.x, Tmp.v1.y, pay.rotation());

            scl(e.fout(Interp.pow3Out) * 1.05f);
            pay.draw();

        }).layer(34f),

        payRespawn = new Effect(30f, e -> { //doesn't technically exist yet even though it does, so drawing it no work
            if(!(e.data instanceof UniversalCrafter.YootData data)) return;
            Payload pay = data.pay;

            scl(e.fin(Interp.pow3Out) * 1.05f);

            if(pay instanceof BuildPayload){
                Drawf.squareShadow(e.x, e.y, pay.size() * 1.85f, 1f);
                rect(pay.content().fullIcon, e.x, e.y, 0f);
            }else if(pay instanceof UnitPayload unit){
                unit.unit.type.drawSoftShadow(e.x, e.y, e.rotation, 1f);
            }

            mixcol(Pal.accent, e.fout());
            rect(pay.content().fullIcon, e.x, e.y, pay instanceof BuildPayload ? 0f : e.rotation - 90f);
        }).layer(34f);

}