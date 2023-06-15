package serilia.world.draw.drawblock;

import arc.Core;
import arc.graphics.g2d.*;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.PayloadBlock.*;
import mindustry.world.draw.DrawBlock;

public class DrawPayloadIO extends DrawBlock{

    public String suffix = "-heavy";
    public boolean drawIn = true, drawOut = true;
    public boolean fallback = true;
    public TextureRegion in, out;

    public DrawPayloadIO(){}
    
    @Override
    public void draw(Building build) {
        PayloadBlockBuild b = (PayloadBlockBuild) build;
        if(drawIn) {
            boolean fall = fallback;
            for (int i = 0; i < 4; ++i) {
                if (b.blends(i) && i != b.rotation) {
                    Draw.rect(in, b.x, b.y, (float) (i * 90 - 180));
                    fall = false;
                }
            }
            if (fall) Draw.rect(in, b.x, b.y, (float) (b.rotation * 90));
        }
        if(drawOut) Draw.rect(out, b.x, b.y, b.rotdeg());

        float z = Draw.z();
        b.drawPayload();
        Draw.z(z);

        /*if (b.payload != null) {
            float z = Draw.z();
            b.updatePayload();
            if (payLayer > 0.0F) Draw.z(payLayer);
            b.payload.draw();
            Draw.z(z);
        }*/
    }

    @Override
    public void load(Block block) {
        in = Core.atlas.find(block.name + "-in", Core.atlas.find("hearth-factory-in-" + block.size + suffix));
        out = Core.atlas.find(block.name + "-out", Core.atlas.find("hearth-factory-out-" + block.size + suffix));
    }
}
