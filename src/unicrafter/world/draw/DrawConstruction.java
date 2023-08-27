package unicrafter.world.draw;

import arc.graphics.g2d.Draw;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.world.draw.DrawBlock;
import unicrafter.world.UniversalCrafter.UniversalBuild;

public class DrawConstruction extends DrawBlock{
    public boolean reconstruct;

    public DrawConstruction(boolean reconstruct){
        this.reconstruct = reconstruct;
    }

    @Override
    public void draw(Building build){
        Draw.draw(Layer.blockOver, () -> {
            if(reconstruct && fromContent(build) != null){
                Draw.alpha(1f - build.progress());
                Draw.rect(fromContent(build).fullIcon, build.x, build.y, ((UniversalBuild)build).payload instanceof UnitPayload ? build.rotation() - 90 : 0f);
                Draw.reset();
            }
            if(toContent(build) != null) Drawf.construct(build, toContent(build), build.rotdeg() - 90f, build.progress(), build.warmup(), build.totalProgress());
        });
    }

    /**Override in the instances if you want something other than these exact payloads.
     * I have no real way of knowing what you want this to do without having you manually set fields per recipe.*/
    public UnlockableContent fromContent(Building build){
        return ((UniversalBuild)build).lastPayload != null && ((UniversalBuild)build).mmmDelish.get(((UniversalBuild)build).lastPayload) > 0 ? ((UniversalBuild)build).lastPayload : null;
    }
    public UnlockableContent toContent(Building build){
        return ((UniversalBuild)build).currentRecipe.payOut.get(0).item;
    }
}
