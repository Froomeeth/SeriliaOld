package serilia.world.blocks.unicrafter;

import arc.struct.Seq;
import mindustry.ctype.UnlockableContent;
import mindustry.type.*;

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

    private final Seq<ItemStack> itemReqContainer = new Seq<>(), itemOutContainer = new Seq<>();
    private final Seq<LiquidStack> liqReqContainer = new Seq<>(), liqOutContainer = new Seq<>();
}
