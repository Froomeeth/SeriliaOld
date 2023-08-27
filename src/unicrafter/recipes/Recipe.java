package unicrafter.recipes;

import arc.Core;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Structs;
import mindustry.content.Fx;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.type.*;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.draw.DrawBlock;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.Stat;
import unicrafter.recipes.style.RecipeStyle;
import unicrafter.recipes.style.RecipeStyles;
import unicrafter.world.UniversalCrafter;
import unicrafter.world.UniversalCrafter.UniversalBuild;

import static mindustry.Vars.*;

/**Basic recipe with support for items, liquids and payloads. Can be researched.
 * Use ChanceRecipe for separators/other chances and todo ContainerRecipe for adding container IO for certain stacks.*/
public class Recipe extends UnlockableContent{

    //general
    /**Draws between top and bottom drawer of the block.*/
    public DrawBlock drawer;
    /**Lets you choose different layouts for your recipe.*/
    public RecipeStyle style = RecipeStyles.normal;
    public int reqItemCapMul = 2, outItemCapMul = 2;
    public boolean loseProgressOnIdle = false;
    public float progressLoseSpeed = 0.019f;

    //liquid
    public boolean dumpExtraLiquid = false;
    public boolean ignoreLiquidFullness = false;
    public int[] liquidOutputDirections = {-1};

    //payload
    public boolean isUnit = false;
    public boolean outputUnitToTop = false;
    public boolean showVanillaOutput = true;

    //attribute
    public Attribute attribute = null;
    public float baseAttributeEfficiency = 1f;
    public float attributeBoostScale = 1f;
    public float maxAttributeEfficiency = 4f;
    public float minAttributeEfficiency = -1f;

    //heat
    public float overheatScale = 1f;
    public float maxHeatEfficiency = 4f;

    //effect
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
                if(items[i] == "power") powerOut = ((Number) items[i + 1]).floatValue();
                else if(items[i] == "heat") heatOut = ((Number) items[i + 1]).floatValue();
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
    private ItemStack[] itemReqArray;
    private LiquidStack[] liqReqArray;

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
            table.add(style.recipeTable(false, this));
        });
    }


    //logic
    public void update(UniversalBuild build){
        if(build.crafter().hasPayloads){

            if(acceptPayload(build) && build.moveInPayload() || build.crafter().instantInput){

                build.mmmDelish.add(build.payload.content());
                build.crafter().payDespawnEffect.at(build.x, build.y, build.drawrot(), new UniversalCrafter.YootData(build.payload, build));
                build.payload = null;
            }else{
                if(build.payload == null){
                    build.outputPayQueue();
                }else{
                    build.moveOutPayload();
                }
            }
        }

        if(build.efficiency > 0 && shouldConsume(build)){
            build.progress += build.getProgressIncrease(time) * (isUnit ? state.rules.unitBuildSpeed(build.team) : 1f);
            build.warmup = Mathf.approachDelta(build.warmup, build.warmupTarget(), build.crafter().warmupSpeed);

            if(build.wasVisible && Mathf.chanceDelta(updateEffectChance)){
                updateEffect.at(build);
            }
        }else{
            build.warmup = Mathf.approachDelta(build.warmup, 0f, build.crafter().warmupSpeed);
            if(loseProgressOnIdle) build.progress = Mathf.approachDelta(build.progress, 0f, progressLoseSpeed);
        }

        dumpOutputs(build);

        if(build.progress >= 1f){
            craft(build);
        }
    }

    public void craft(UniversalBuild build){
        build.consume();

        if(build.items != null && itemOut().size != 0) itemOut.each(output -> {
            for(int i = 0; i < output.amount; i++){
                build.offload(output.item);
            }
        });

        if(build.liquids != null && liqOut().size != 0) liqOut.each(output -> {
            for(int i = 0; i < output.amount; i++){
                build.handleLiquid(build, output.liquid, Math.min(output.amount, build.block.liquidCapacity - build.liquids.get(output.liquid)));
            }
        });

        if(payOut().size != 0){
            payOut.each(output -> {
                for(int i = 0; i < output.amount; i++){
                    build.payQueue.add(build.createPayload(output.item));
                }
            });

            if(build.crafter().instantFirstOutput){
                build.payVector.setZero();
                build.payload = build.payQueue.pop();
            }
        }

        if(build.wasVisible) craftEffect.at(build);

        build.progress %= 1f;
    }

    public boolean shouldConsume(UniversalBuild build){
        if(itemOut().size != 0){
            for(int i = 0; i < itemOut().size; i++){
                if(build.items != null && build.items.get(itemOut().get(i).item) + itemOut().get(i).amount > build.block.itemCapacity){
                    return false;
                }
            }
        }

        if(liqOut().size != 0 && !ignoreLiquidFullness){
            boolean allFull = true;
            for(int i = 0; i < liqOut().size; i++){
                if(build.liquids != null && build.liquids.get(liqOut().get(i).liquid) + liqOut().get(i).amount > build.block.liquidCapacity){
                    if(!dumpExtraLiquid){
                        return false;
                    }
                }else{
                    //if there's still space left, it's not full for all liquids
                    allFull = false;
                }
            }

            return !allFull;
        }
        return true;
    }

    public void dumpOutputs(UniversalBuild build){
        if(itemOut().size != 0 && build.timer(build.timerDump(), build.dumpTime() / build.timeScale())){
            itemOut().each(output -> build.dump(output.item));
        }

        if(liqOut().size != 0 && build.timer(build.timerDump(), build.dumpTime() / build.timeScale())){
            for(int i = 0; i < liqOut().size; i++){
                int dir = liquidOutputDirections.length > i ? liquidOutputDirections[i] : -1;
                build.dumpLiquid(liqOut().get(i).liquid, 2f, dir);
            }
        }
    }

    public boolean acceptPayload(UniversalBuild build){
        return build.payload != null && payReq().size != 0 && payReq().contains(b -> b.item == build.payload.content());
    }

    public boolean acceptPayload(UniversalBuild build, Payload pay){
        return build.payload == null && pay != null && payReq().size != 0 && payReq().contains(b ->
                b.item == pay.content() && build.mmmDelish.get(pay.content()) < Mathf.round(b.amount * build.team.rules().unitCostMultiplier));
    }

    public boolean acceptItem(UniversalBuild build, Building source, Item item){
        for(int i = 0; i < itemReqArray().length; i++){
            if(build.items.get(itemReqArray()[i].item) >= build.getMaximumAccepted(item) || !Structs.contains(itemReqArray(), stack -> stack.item == item)){
                return false;
            }
        }
        return true;
    }

    public boolean acceptLiquid(UniversalBuild build, Building source, Liquid liquid) {
        for(int i = 0; i < liqReqArray().length; i++){
            if(!Structs.contains(liqReqArray(), stack -> stack.liquid == liquid)){
                return false;
            }
        }
        return true;
    }

    //everything else
    public void init(UniversalCrafter b){
        b.hasLiquids |= (b.hasLiquidIn |= liqReq.size != 0) || (b.outputsLiquid |= liqOut.size != 0);
        b.hasPayloads |= (b.acceptsPayload |= payReq.size != 0) || (b.outputsPayload |= payOut.size != 0);
        b.rotate |= b.outputsPayload; //doesn't set?
        b.commandable |= isUnit;
        b.consumesPower |= powerReq > -1f;
        b.outputsPower |= powerOut > -1f;
        b.hasHeat |= heatOut > -1f;

        if(itemReq.size != 0){
            itemReqArray = itemReq.toArray(ItemStack.class);
            for(ItemStack stack : itemReqArray){
                b.capacities[stack.item.id] = Math.max(b.capacities[stack.item.id], stack.amount * reqItemCapMul);
                b.itemCapacity = Math.max(b.itemCapacity, stack.amount * outItemCapMul);
            }
        }

        if(liqReq.size != 0){
            liqReqArray = liqReq.toArray(LiquidStack.class);
        }

        if(liqReq.size != 0){
            liqReq.each(req -> {
                b.liquidFilter[req.liquid.id] = true;
                b.liquidCapacity = Math.max(b.liquidCapacity, req.amount * 2);
            });
            liqOut.each(out -> {
                b.liquidFilter[out.liquid.id] = true;
                b.liquidCapacity = Math.max(b.liquidCapacity, out.amount * 2);
            });
        }
    }

    public void load(UniversalCrafter block){
        if(drawer != null) drawer.load(block);
    }

    public void configTo(UniversalBuild build){
        liqReq.each(bar -> build.block.addLiquidBar(bar.liquid)); //this is really stupid. I don't want to make dynamic bars.
        liqOut.each(bar -> build.block.addLiquidBar(bar.liquid));
    }

    public Seq<ItemStack> itemReq(){return itemReq;}
    public Seq<LiquidStack> liqReq(){return liqReq;}
    public Seq<PayloadStack> payReq(){return payReq;}

    public Seq<ItemStack> itemOut(){return itemOut;}
    public Seq<LiquidStack> liqOut(){return liqOut;}
    public Seq<PayloadStack> payOut(){return payOut;}
    //dynamic consumers need these. really wish they didn't.
    public ItemStack[] itemReqArray(){return itemReqArray;}
    public LiquidStack[] liqReqArray(){return liqReqArray;}
}
