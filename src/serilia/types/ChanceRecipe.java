package serilia.types;

import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.ctype.UnlockableContent;
import mindustry.type.*;
import serilia.world.blocks.payload.UniversalCrafter.Recipe;

/**Recipe with support for adding chance based outputs.*/
public class ChanceRecipe extends Recipe{
    public ChanceRecipe(String name, UnlockableContent iconContent, float time){
        super(name, iconContent, time);
    }
    public ChanceRecipe(String name, float time){
        super(name, time);
    }

    /**Add an output that will be chosen if the randomizer hits between min and max range.
     * This will add onto the normal out().
     *
     * Example of ranges for 2 recipes with a 20/75 split:
     * 0   - 0.2
     * 0.2 - 0.75*/
    public void addChanceOut(float rangeMin, float rangeMax, Object... items){
        ChanceOut add = new ChanceOut(rangeMin, rangeMax);
        for(int i = 0; i < items.length; i += 2){
            if(items[i] instanceof Item)
                add.itemOutChance.add(new ItemStack((Item) items[i], ((Number) items[i + 1]).intValue()));
            else if(items[i] instanceof Liquid)
                add.liqOutChance.add(new LiquidStack((Liquid) items[i], ((Number) items[i + 1]).floatValue()));
            else if(items[i] instanceof UnlockableContent)
                add.payOutChance.add(new PayloadStack((UnlockableContent) items[i], ((Number) items[i + 1]).intValue()));
        }
    }

    private final Seq<ChanceOut> outs = new Seq<>();
    private Seq<ItemStack> itemOutResult;
    private Seq<LiquidStack> liqOutResult;
    private Seq<PayloadStack> payOutResult;

    @Override
    public void craft(){
        float rand = Mathf.random();

        for(int i = 0; i < outs.size; i++){
            if(outs.get(i).rangeMin <= rand && rand <= outs.get(i).rangeMax){
                itemOutResult = outs.get(i).itemOutChance;
                liqOutResult = outs.get(i).liqOutChance;
                payOutResult = outs.get(i).payOutChance;
            }
        }
    }

    @Override
    public Seq<ItemStack> itemOut(){
        return itemOutResult != null ? itemOutResult.add(itemOut) : itemOut;
    }
    @Override
    public Seq<LiquidStack> liqOut(){
        return liqOutResult != null ? liqOutResult.add(liqOut) : liqOut;
    }
    @Override
    public Seq<PayloadStack> payOut(){
        return payOutResult != null ? payOutResult.add(payOut) : payOut;
    }

    private static class ChanceOut{
        private ChanceOut(float rangeMin, float rangeMax){
            this.rangeMin = rangeMin;
            this.rangeMax = rangeMax;
        }

        private final float rangeMin, rangeMax;
        private final Seq<ItemStack> itemOutChance = new Seq<>();
        private final Seq<LiquidStack> liqOutChance = new Seq<>();
        private final Seq<PayloadStack> payOutChance = new Seq<>();
    }
}
