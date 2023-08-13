package serilia.world.blocks.unicrafter;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import mindustry.ctype.UnlockableContent;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.ui.Styles;

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
        outs.add(add);
    }

    public final Seq<ChanceOut> outs = new Seq<>();

    private final Seq<ItemStack> itemOutResult = new Seq<>(4);
    private final Seq<LiquidStack> liqOutResult = new Seq<>(4);
    private final Seq<PayloadStack> payOutResult = new Seq<>(4);

    @Override
    public void craft(){
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

    @Override
    public void addRecipeOutputTable(Table table){
        table.table(out -> { //output
            out.row();
            out.add("100%").color(Color.white);
            out.row();

            super.addRecipeOutputTable(table);

            outs.each(chanceOut -> {
                out.row();
                out.add(Mathf.round((chanceOut.rangeMax - chanceOut.rangeMin) * 100f) + "%").color(Pal.accent);
                out.row();

                out.table(Styles.black5, output -> { //chance
                    output.add(contentListTable(chanceOut.payOutChance, chanceOut.itemOutChance, chanceOut.liqOutChance, -20f, -20f, time, true, chanceOut.rangeMax - chanceOut.rangeMin)).pad(5f).grow();
                }).pad(5f).grow();
            });
        });
    }
}
