package serilia.world.blocks.unicrafter;

import arc.math.Interp;
import arc.util.Tmp;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.UnitPayload;

import static arc.graphics.g2d.Draw.*;

public class UniFx{

    public static Effect
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
