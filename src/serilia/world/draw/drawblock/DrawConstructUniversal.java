package serilia.world.draw.drawblock;

import arc.graphics.g2d.Draw;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.world.draw.DrawBlock;
import serilia.world.blocks.payload.UniversalCrafter.UniversalBuild;

public class DrawConstructUniversal extends DrawBlock{
    public boolean reconstruct;

    public DrawConstructUniversal(boolean reconstruct){
        this.reconstruct = reconstruct;
    }

    @Override
    public void draw(Building build){
        Draw.draw(Layer.blockOver, () -> {
            if(reconstruct){
                Draw.alpha(1f - build.progress());
                Draw.rect(fromContent((UniversalBuild)build).fullIcon, build.x, build.y, ((UniversalBuild)build).payload instanceof UnitPayload ? build.rotation() - 90 : 0f);
                Draw.reset();
            }
            Drawf.construct(build, toContent((UniversalBuild) build), build.rotdeg() - 90f, build.progress(), build.warmup(), build.totalProgress());
        });
    }

    /**Override in the "new DrawConstructUniversal(){{}}" if you want something other than payloads.
     * I know you have no idea how, so just ask me or check an example if you want that.*/
    public UnlockableContent fromContent(UniversalBuild build){
        return build.lastPayload;
    }
    public UnlockableContent toContent(UniversalBuild build){
        return build.currentRecipe.payOut.get(0).item;
    }
}
