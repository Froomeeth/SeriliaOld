package serilia.types;

import arc.struct.Seq;
import mindustry.ctype.UnlockableContent;
import mindustry.type.*;
import serilia.world.blocks.payload.UniversalCrafter.Recipe;

public class ContainerRecipe extends Recipe{
    public ContainerRecipe(String name, UnlockableContent iconContent, float time){
        super(name, iconContent, time);
    }
    public ContainerRecipe(String name, float time){
        super(name, time);
    }

    public Object[] containerReq(Object... items){
        for(int i = 0; i < items.length; i += 2){
            if(items[i] instanceof Item)
                itemOut.add(new ItemStack((Item) items[i], ((Number) items[i + 1]).intValue()));
            else if(items[i] instanceof Liquid)
                liqOut.add(new LiquidStack((Liquid) items[i], ((Number) items[i + 1]).floatValue()));
        }
        return items;
    }
    public Object[] containerOut(Object... items){
        for(int i = 0; i < items.length; i += 2){
            if(items[i] instanceof Item)
                itemOut.add(new ItemStack((Item) items[i], ((Number) items[i + 1]).intValue()));
            else if(items[i] instanceof Liquid)
                liqOut.add(new LiquidStack((Liquid) items[i], ((Number) items[i + 1]).floatValue()));
        }
        return items;
    }

    private final Seq<ItemStack> itemOutContainer = new Seq<>();
    private final Seq<LiquidStack> liqOutContainer = new Seq<>();
}
