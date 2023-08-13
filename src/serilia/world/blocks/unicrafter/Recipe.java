package serilia.world.blocks.unicrafter;

import arc.Core;
import arc.graphics.Color;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Scaling;
import arc.util.Strings;
import mindustry.content.Fx;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.Effect;
import mindustry.gen.Icon;
import mindustry.gen.Iconc;
import mindustry.type.*;
import mindustry.ui.ItemImage;
import mindustry.ui.Styles;
import mindustry.world.draw.DrawBlock;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.Stat;

import static mindustry.Vars.iconMed;

/**Basic recipe with support for items, liquids and payloads. Can be researched.
 * Use ChanceRecipe for separators and ContainerRecipe for adding container IO for certain stacks.*/
public class Recipe extends UnlockableContent{
    public DrawBlock drawer;
    public boolean isUnit = false, outputUnitToTop = false, showPayOutput = true;

    public Attribute attribute = null;
    public float baseAttributeEfficiency = 1f;
    public float attributeBoostScale = 1f;
    public float maxAttributeEfficiency = 4f;
    public float minAttributeEfficiency = -1f;

    public float overheatScale = 1f;
    public float maxHeatEfficiency = 4f;

    public Effect craftEffect = Fx.none, updateEffect = Fx.none;
    public double updateEffectChance = 0.02;

    /**iconContent copies the content's icon for this recipe. Use the other constructor for a custom one.*/
    public Recipe(String name, UnlockableContent iconContent, float time){
        this(name, time);
        this.iconContent = iconContent;
    }

    public Recipe(String name, float time){
        super(name);
        localizedName = Core.bundle.get("recipe." + this.name + ".name", this.name);
        description = Core.bundle.getOrNull("recipe." + this.name + ".description");
        details = Core.bundle.getOrNull("recipe." + this.name + ".details");
        unlocked = Core.settings != null && Core.settings.getBool(this.name + "-unlocked", false);

        this.time = time;
    }

    /**Conveniently sets the requirements at once, instead of needing you to type 5 fields manually.
     * @return the array it performed the checks on, for super chaining.*/
    public Object[] req(Object... items){
        for(int i = 0; i < items.length; i += 2){
            if(items[i] instanceof Item)
                itemReq.add(new ItemStack((Item) items[i], ((Number) items[i + 1]).intValue()));
            else if(items[i] instanceof Liquid)
                liqReq.add(new LiquidStack((Liquid) items[i], ((Number) items[i + 1]).floatValue()));
            else if(items[i] instanceof UnlockableContent)
                payReq.add(new PayloadStack((UnlockableContent) items[i], ((Number) items[i + 1]).intValue()));
            else if(items[i] instanceof String)
                if(items[i] == "power")
                    powerReq = ((Number) items[i + 1]).floatValue();
                else if(items[i] == "heat")
                    heatReq = ((Number) items[i + 1]).floatValue();
        }
        return items;
    }

    /**Conveniently sets the outputs at once, instead of needing you to type 5 fields manually.
     * @return the array it performed the checks on, for super chaining.*/
    public Object[] out(Object... items){
        for(int i = 0; i < items.length; i += 2){
            if(items[i] instanceof Item)
                itemOut.add(new ItemStack((Item) items[i], ((Number) items[i + 1]).intValue()));
            else if(items[i] instanceof Liquid)
                liqOut.add(new LiquidStack((Liquid) items[i], ((Number) items[i + 1]).floatValue()));
            else if(items[i] instanceof UnlockableContent)
                payOut.add(new PayloadStack((UnlockableContent) items[i], ((Number) items[i + 1]).intValue()));
            else if(items[i] instanceof String)
                if(items[i] == "power")
                    powerOut = ((Number) items[i + 1]).floatValue();
                else if(items[i] == "heat")
                    heatOut = ((Number) items[i + 1]).floatValue();
        }
        return items;
    }

    /*--- Set automatically ---*/
    public int index;
    public float time;
    private UnlockableContent iconContent;
    public final Seq<ItemStack> itemReq = new Seq<>(ItemStack.class), itemOut = new Seq<>();
    public final Seq<LiquidStack> liqReq = new Seq<>(LiquidStack.class), liqOut = new Seq<>();
    public final Seq<PayloadStack> payReq = new Seq<>(), payOut = new Seq<>();
    public float powerReq = -555f, powerOut = -12f, heatReq = -42f, heatOut = -9999999999999999f;
    public ItemStack[] itemReqArray;
    public LiquidStack[] liqReqArray;

    @Override
    public ContentType getContentType(){
        return ContentType.loadout_UNUSED;
    }

    @Override
    public void loadIcon(){
        fullIcon = uiIcon = (iconContent != null ? iconContent.fullIcon : Core.atlas.find("recipe-" + name));
    }

    @Override
    public void setStats(){
        stats.add(Stat.output, table -> {
            table.row();
            table.add(recipeTable(false));
        });
    }

    public Table recipeTable(boolean list){
        Table t = new Table();
        t.row();

        var cell = t.table(Styles.grayPanel, display -> {
            if(list){
                display.add(recipeTopTable()).pad(10f).padBottom(0f);
                display.row();
            }
            display.table(io -> { //encompasses input/output
                addRecipeInputTable(io);
                io.add(recipeArrowTable()).padBottom(5f).padTop(5f);
                addRecipeOutputTable(io);
            });
            display.row();
            display.add(recipeBottomTable()).left();

        }).pad(5f).top();

        if(list) cell.growX();

        return t;
    }

    public Table recipeTopTable(){
        Table t = new Table();

        t.add(localizedName);
        t.row();
        t.image(uiIcon).size(60).scaling(Scaling.fit);

        return t;
    } //TODO add info button next to text or rearrange and add to side?
    public Table recipeArrowTable(){
        Table t = new Table();

        if(powerReq > 0){
            t.table(power -> {
                power.add("[accent]" + Iconc.power + "[]");
                power.row();
                power.add(Strings.autoFixed(powerReq  * 60f, 1));
            }).pad(5f).bottom();
            t.row();
        }

        t.image(Icon.play).pad(5f).padTop(10f).padBottom(10f).center();

        if(heatReq > 0){
            t.row();
            t.table(heat -> {
                heat.add("[red]" + Iconc.waves + "[]");
                heat.row();
                heat.add(Strings.autoFixed(heatReq, 1));
            }).pad(5f).top();
            t.row();
        }

        return t;
    }
    public Table recipeBottomTable(){
        Table t = new Table();

        //if(recipe.description != null) t.label(() -> recipe.description).color(Color.lightGray).wrap().pad(10f).left();

        return t;
    } //todo attributes, max boost, etc.

    public void addRecipeInputTable(Table table){
        table.table(Styles.black5, input -> input.add(contentListTable(payReq, itemReq, liqReq, time, false)).pad(5f).grow() //not adding directly to get an easy outline
        ).pad(5f).grow();
    }
    public void addRecipeOutputTable(Table table){
        table.table(Styles.black5, output -> output.add(contentListTable(payOut, itemOut, liqOut, time, true)).pad(5f).grow() //not adding directly to get an easy outline
        ).pad(5f).grow();
    }

    public Table contentListTable(Seq<PayloadStack> pay, Seq<ItemStack> item, Seq<LiquidStack> liq, float time, boolean flip){
        return contentListTable(pay, item, liq, -20f, -20f, time, flip, 1f);
    }
    public Table contentListTable(Seq<PayloadStack> pay, Seq<ItemStack> item, Seq<LiquidStack> liq, float power, float heat, float time, boolean flip, float timeDiv){
        Table t = new Table();

        if(pay != null) pay.each(requirement -> {
            if(flip) t.add(new ItemImage(requirement.item.uiIcon, requirement.amount)).left();

            t.table(req -> {
                req.add(Strings.autoFixed(requirement.amount / ((time / timeDiv) / 60f), 2) + "/s").pad(4f).color(Color.lightGray);
            });

            if(!flip) t.add(new ItemImage(requirement.item.uiIcon, requirement.amount)).right();
            t.row();
        });
        if(item != null) item.each(requirement -> {
            if(flip) t.add(new ItemImage(requirement.item.uiIcon, requirement.amount)).left();

            t.table(req -> {
                req.add(Strings.autoFixed(requirement.amount / ((time * timeDiv) / 60f), 2) + "/s").pad(4f).color(Color.lightGray);
            });
            if(!flip) t.add(new ItemImage(requirement.item.uiIcon, requirement.amount)).right();
            t.row();
        });
        if(liq != null) liq.each(requirement -> {
            if(flip) t.image(requirement.liquid.fullIcon).size(iconMed).left();

            t.table(req -> {
                req.add(Strings.autoFixed(requirement.amount * 60f, 2) + "/s").color(Color.lightGray).pad(4f);
            });
            if(!flip) t.image(requirement.liquid.fullIcon).size(iconMed).right().scaling(Scaling.fit);
            t.row();
        });
        t.row();
        if(power > 0f) t.add("[accent]" + Iconc.power + "[] " + Strings.autoFixed(power * 60f, 2)).pad(4f);
        if(heat > 0f) t.add("[red]" + Iconc.waves + "[] " + Strings.autoFixed(heat, 2)).pad(4f);

        return t;
    }

    public void craft(){}

    public Seq<ItemStack> itemOut(){return itemOut;}
    public Seq<LiquidStack> liqOut(){return liqOut;}
    public Seq<PayloadStack> payOut(){return payOut;}
    public Seq<ItemStack> itemOutContainer(){return null;}
    public Seq<LiquidStack> liqOutContainer(){return null;}
}
