package unicrafter.recipes;

import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.ctype.UnlockableContent;
import mindustry.type.*;
import unicrafter.world.UniversalCrafter.UniversalBuild;

/**Recipe with support for adding chance based outputs.*/
public class ChanceRecipe extends Recipe{
    public ChanceRecipe(String name, UnlockableContent iconContent, float time){
        super(name, iconContent, time);
    }
    public ChanceRecipe(String name, float time){
        super(name, time);
    }

    /**Add an output that will be chosen if the randomizer hits between min and max range. This will add onto the normal out().
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
        outs.add(add);
    }

    public final Seq<ChanceOut> outs = new Seq<>();

    private final Seq<ItemStack> itemOutResult = new Seq<>(4);
    private final Seq<LiquidStack> liqOutResult = new Seq<>(4);
    private final Seq<PayloadStack> payOutResult = new Seq<>(4);

    @Override
    public void craft(UniversalBuild build){
        float rand = Mathf.random();

        itemOutResult.clear();
        liqOutResult.clear();
        payOutResult.clear();

        for(int i = 0; i < outs.size; i++){
            if(outs.get(i).rangeMin <= rand && rand <= outs.get(i).rangeMax){
                itemOutResult.add(outs.get(i).itemOutChance);
                liqOutResult.add(outs.get(i).liqOutChance);
                payOutResult.add(outs.get(i).payOutChance);
            }
        }

        super.craft(build);
    }

    @Override
    public void dumpOutputs(UniversalBuild build){
        outs.each(out -> {
            if(out.itemOutChance.size != 0 && build.timer(build.timerDump(), build.dumpTime() / build.timeScale())){
                out.itemOutChance.each(output -> build.dump(output.item));
            }

            if(out.liqOutChance.size != 0 && build.timer(build.timerDump(), build.dumpTime() / build.timeScale())){
                for(int i = 0; i < out.liqOutChance.size; i++){
                    int dir = liquidOutputDirections.length > i ? liquidOutputDirections[i] : -1;
                    build.dumpLiquid(out.liqOutChance.get(i).liquid, 2f, dir);
                }
            }
        });
    }

    @Override
    public boolean shouldConsume(UniversalBuild build){ //likely broken, needs testing
        boolean allFull = true, hasLiquid = false;
        for(int o = 0; o < outs.size; o++){
            if(outs.get(o).itemOutChance.size != 0){
                for(int i = 0; i < outs.get(o).itemOutChance.size; i++){
                    if(build.items.get(outs.get(o).itemOutChance.get(i).item) + outs.get(o).itemOutChance.get(i).amount > build.block.itemCapacity){
                        return false;
                    }
                }
            }

            if(outs.get(o).liqOutChance.size != 0 && !ignoreLiquidFullness){
                hasLiquid = true;
                for(int i = 0; i < outs.get(o).liqOutChance.size; i++){
                    if(build.liquids.get(outs.get(o).liqOutChance.get(i).liquid) + outs.get(o).liqOutChance.get(i).amount > build.block.liquidCapacity){
                        if(!dumpExtraLiquid){
                            return false;
                        }
                    }else{
                        //if there's still space left, it's not full for all liquids
                        allFull = false;
                    }
                }
            }
        }
        return !(allFull && hasLiquid);
    }

    @Override
    public void configTo(UniversalBuild build){
        liqReq.each(bar -> build.block.addLiquidBar(bar.liquid));
        outs.each(out -> {
            out.liqOutChance.each(bar -> build.block.addLiquidBar(bar.liquid)); //this is really stupid. I don't want to make dynamic bars.
        });
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

    public static class ChanceOut{
        private ChanceOut(float rangeMin, float rangeMax){
            this.rangeMin = rangeMin;
            this.rangeMax = rangeMax;
        }

        public final float rangeMin, rangeMax;
        public final Seq<ItemStack> itemOutChance = new Seq<>(4);
        public final Seq<LiquidStack> liqOutChance = new Seq<>(4);
        public final Seq<PayloadStack> payOutChance = new Seq<>(4);
    }
}
