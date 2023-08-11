package serilia.world.blocks.payload;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.*;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.io.TypeIO;
import mindustry.type.*;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.world.consumers.ConsumeItemDynamic;
import mindustry.world.consumers.ConsumePayloadDynamic;
import mindustry.world.consumers.ConsumePowerDynamic;
import mindustry.world.draw.DrawBlock;
import serilia.types.ConsumeLiquidsDynamic;

import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;

/**A multicrafter that should support all in- and output types.
 * @author nullevoy */

/* the list
* [X] produce items
* [ ] produce liquids //needs support for multiple
* [X] produce payloads
* [X] consume items
* [X] consume liquids
* [X] consume payloads
* [X] consume power
* [X] switch configs
* [#] deconfig
* [-] gradual liquid
* [X] outDirs
* [ ] heat
* [ ] attributes
* [ ] UI
* [ ] stats
* [ ] ports
* [ ] logic?
* [X] unit cost mul
* [ ] drawing
*/

public class UniversalCrafter extends PayloadBlock{
    public Seq<Recipe> recipes;
    public float warmupSpeed = 0.019f;

    public boolean dumpExtraLiquid = false;
    public boolean ignoreLiquidFullness = false;
    public int[] liquidOutputDirections = {- 1};

    /*--- Set automatically ---*/
    private boolean hasPayloads, liquidIn;
    private int[] capacities = {};

    public UniversalCrafter(String name){
        super(name);
        configurable = true;
        solid = true;
    }

    @Override
    public void init(){
        capacities = new int[Vars.content.items().size];
        consumesPower = false;

        recipes.each(recipe -> {
            hasItems |= recipe.itemReq.size != 0 || recipe.itemOut.size != 0;
            hasLiquids |= (liquidIn |= recipe.liquidReq.size != 0) || (outputsLiquid |= recipe.liquidOut.size != 0);
            hasPayloads |= (acceptsPayload |= recipe.payReq.size != 0) || (outputsPayload |= recipe.payOut.size != 0);
            rotate |= outputsPayload;
            commandable |= recipe.isUnit;
            consumesPower |= recipe.powerReq > - 1;

            if(recipe.itemReq.size != 0){
                recipe.itemReqArray = recipe.itemReq.toArray(ItemStack.class);
                for(ItemStack stack : recipe.itemReqArray){
                    capacities[stack.item.id] = Math.max(capacities[stack.item.id], stack.amount * 2);
                    itemCapacity = Math.max(itemCapacity, stack.amount * 2);
                }
            }
            if(recipe.liquidReq.size != 0){
                recipe.liquidReqArray = recipe.liquidReq.toArray(LiquidStack.class);
            }
        });


        if(hasPayloads)
            consume(new ConsumePayloadDynamic((UniversalBuild b) -> b.currentRecipe != null && b.currentRecipe.payReq.size != 0 ? b.currentRecipe.payReq : Seq.with()));
        if(hasItems)
            consume(new ConsumeItemDynamic((UniversalBuild b) -> b.currentRecipe != null && b.currentRecipe.itemReqArray != null ? b.currentRecipe.itemReqArray : ItemStack.empty));
        if(consumesPower)
            consume(new ConsumePowerDynamic((Building b) -> ((UniversalBuild) b).currentRecipe != null && ((UniversalBuild) b).currentRecipe.powerReq > - 1 ? ((UniversalBuild) b).currentRecipe.powerReq : 0f));
        if(liquidIn)
            consume(new ConsumeLiquidsDynamic((UniversalBuild b) -> b.currentRecipe != null && b.currentRecipe.liquidReqArray != null ? b.currentRecipe.liquidReqArray : LiquidStack.empty));

        consumeBuilder.each(c -> c.multiplier = b -> ((UniversalBuild)b).currentRecipe.isUnit ? state.rules.unitCost(b.team) : 1);

        if(recipes.size == 1){
            configurable = false;
        }

        config(Recipe.class, (UniversalBuild tile, Recipe val) -> {
            if(! configurable || tile.currentRecipe == val) return;

            tile.currentRecipe = val;
            tile.progress = 0;
            tile.currentRecipe.liquidReq.each(bar ->
                    addLiquidBar(bar.liquid)
            );
            tile.currentRecipe.liquidOut.each(bar ->
                    addLiquidBar(bar.liquid)
            );
        });

        super.init();

        recipes.each(recipe -> {
            if(recipe.liquidReq.size != 0){
                recipe.liquidReq.each(req -> {
                    liquidFilter[req.liquid.id] = true;
                    liquidCapacity = Math.max(liquidCapacity, req.amount * 2);
                });
                recipe.liquidOut.each(out -> {
                    liquidFilter[out.liquid.id] = true;
                    liquidCapacity = Math.max(liquidCapacity, out.amount * 2);
                });
            }
        });

        for(int i = 0; i < recipes.size; i++){
            recipes.get(i).index = i;
        }
    }

    @Override
    public boolean outputsItems(){
        return true;
    }

    @Override
    public void setBars(){
        super.setBars();
        removeBar("liquid");

        addBar("progress", (UniversalBuild entity) -> new Bar("bar.progress", Pal.ammo, () -> entity.progress));
    }

    public class UniversalBuild extends PayloadBlockBuild<Payload>{ //todo saving IO
        public @Nullable Vec2 commandPos;
        public Recipe currentRecipe;
        public PayloadSeq mmmDelish = new PayloadSeq();
        public Seq<UnlockableContent> shitQueue = new Seq<>();
        public float progress, warmup, totalProgress;
        public boolean shitting;

        //works consistently
        @Override
        public void updateTile(){
            if(! configurable){
                currentRecipe = recipes.get(0);
            }

            if(currentRecipe != null){
                boolean yum = isTasty();

                if(hasPayloads){
                    if(! yum){
                        if(payload == null){
                            attemptShitting();
                        }else{
                            moveOutPayload();
                        }
                    }

                    if(yum && moveInPayload()){
                        mmmDelish.add(payload.content());
                        payload = null;
                    }
                }

                if(efficiency > 0){
                    progress += getProgressIncrease(currentRecipe.time) * (currentRecipe.isUnit ? state.rules.unitBuildSpeed(team) : 1f);
                    warmup = Mathf.approachDelta(warmup, 1, warmupSpeed);
                }
                dumpOutputs();
            }else{
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
            }

            if(progress >= 1f){
                craft();
            }


            totalProgress += warmup * Time.delta;
        }

        public void craft(){
            consume();

            if(currentRecipe.itemOut.size != 0){
                currentRecipe.itemOut.each(output -> {
                    for(int i = 0; i < output.amount; i++){
                        offload(output.item);
                    }
                });
            }

            if(currentRecipe.liquidOut.size != 0){
                currentRecipe.liquidOut.each(output -> {
                    for(int i = 0; i < output.amount; i++){
                        handleLiquid(this, output.liquid, Math.min(output.amount, liquidCapacity - liquids.get(output.liquid)));
                    }
                });
            }

            if(currentRecipe.payOut.size != 0){
                currentRecipe.payOut.each(output -> {
                    for(int i = 0; i < output.amount; i++){
                        shitQueue.add(output.item);
                    }
                });
            }

            if(wasVisible){
                //todo craft effect
            }

            progress %= 1f;
        }


        public void dumpOutputs(){
            if(currentRecipe.itemOut.size != 0 && timer(timerDump, dumpTime / timeScale)){
                currentRecipe.itemOut.each(output -> dump(output.item));
            }

            if(currentRecipe.liquidOut.size != 0 && timer(timerDump, dumpTime / timeScale)){
                for(int i = 0; i < currentRecipe.liquidOut.size; i++){
                    int dir = liquidOutputDirections.length > i ? liquidOutputDirections[i] : - 1;
                    dumpLiquid(currentRecipe.liquidOut.get(i).liquid, 2f, dir);
                }
            }
        }

        @Override
        public boolean shouldConsume(){
            if(currentRecipe != null){
                if(currentRecipe.itemOut.size != 0){
                    for(int i = 0; i < currentRecipe.itemOut.size; i++){
                        if(items.get(currentRecipe.itemOut.get(i).item) + currentRecipe.itemOut.get(i).amount > itemCapacity){
                            return false;
                        }
                    }
                }

                if(currentRecipe.liquidOut.size != 0 && ! ignoreLiquidFullness){
                    boolean allFull = true;
                    for(int i = 0; i < currentRecipe.liquidOut.size; i++){
                        if(liquids.get(currentRecipe.liquidOut.get(i).liquid) + currentRecipe.liquidOut.get(i).amount > liquidCapacity){
                            if(! dumpExtraLiquid){
                                return false;
                            }
                        }else{
                            //if there's still space left, it's not full for all liquids
                            allFull = false;
                        }
                    }

                    if(allFull){
                        return false;
                    }
                }

                return enabled && ! shitting;
            }
            return false;
        }

        public boolean isTasty(){
            return payload != null && currentRecipe != null && currentRecipe.payReq.size != 0 && currentRecipe.payReq.contains(b -> b.item == payload.content());
        }

        public boolean isTasty(Payload pay){
            return pay != null && currentRecipe != null && currentRecipe.payReq.size != 0 && currentRecipe.payReq.contains(b ->
                    b.item == pay.content() &&
                            mmmDelish.get(pay.content()) < b.amount /*Mathf.round(b.amount * team.rules().unitCostMultiplier todo*/
            );
        }

        public float fraction(){
            return currentRecipe == null ? 0 : progress / currentRecipe.time;
        }

        public void attemptShitting(){
            if(shitQueue.isEmpty()) return;

            shitting = true;
            UnlockableContent m = shitQueue.pop();

            if(m instanceof Block b){
                payload = new BuildPayload(b, team);
            }else{
                Unit diarrhea = ((UnitType) m).create(team);
                if(commandPos != null && diarrhea.isCommandable()){
                    diarrhea.command().commandPosition(commandPos);
                }
                payload = new UnitPayload(diarrhea);
                payVector.setZero();
                Events.fire(new EventType.UnitCreateEvent(((UnitPayload) payload).unit, this));
            }
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload){
            Log.info("e"+(this.payload == null) + (currentRecipe != null) + (shitQueue.isEmpty()) + (!shitting) + (isTasty(payload)));
            return this.payload == null && currentRecipe != null && shitQueue.isEmpty() && !shitting && isTasty(payload);
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return currentRecipe != null && currentRecipe.itemReqArray != null && items.get(item) < getMaximumAccepted(item) &&
                    Structs.contains(currentRecipe.itemReqArray, stack -> stack.item == item);
        }

        @Override
        public void buildConfiguration(Table table){
            Seq<Recipe> bRecipes = Seq.with(recipes).filter(r -> {
                if(r.payOut.size != 0){
                    for(int i = 0; i < r.payOut.size; i++){
                        if(r.payOut.get(i).item instanceof Block b) return ! state.rules.isBanned(b);
                        if(r.payOut.get(i).item instanceof UnitType u) return ! u.isBanned();
                    }
                }
                return true;
            });

            if(bRecipes.any()){
                ItemSelection.buildTable(UniversalCrafter.this, table, bRecipes, () -> currentRecipe, this::configure, selectionRows, selectionColumns);
            }else{
                table.table(Styles.black3, t -> t.add("@none").color(Color.lightGray));
            }
        }

        @Override
        public void draw(){
            drawPayload();
        }

        @Override
        public void moveOutPayload(){
            if(payload == null) return;

            updatePayload();

            Vec2 dest = Tmp.v1.trns(rotdeg(), size * tilesize / 2f);

            payRotation = Angles.moveToward(payRotation, rotdeg(), payloadRotateSpeed * delta());
            payVector.approach(dest, payloadSpeed * delta());

            Building front = front();
            boolean canDump = front == null || ! front.tile().solid();
            boolean canMove = front != null && (front.block.outputsPayload || front.block.acceptsPayload);

            if(canDump && ! canMove){
                pushOutput(payload, 1f - (payVector.dst(dest) / (size * tilesize / 2f)));
            }

            if(payVector.within(dest, 0.001f)){
                payVector.clamp(- size * tilesize / 2f, - size * tilesize / 2f, size * tilesize / 2f, size * tilesize / 2f);

                if(canMove){
                    if(movePayload(payload)){
                        payload = null;
                        shitting = false;
                    }
                }else if(canDump){
                    dumpPayload();
                    shitting = false;
                }
            }
        }

        @Override
        public int getMaximumAccepted(Item item){
            return capacities[item.id];
        }

        @Override
        public PayloadSeq getPayloads(){
            return mmmDelish;
        }

        @Override
        public float warmup(){
            return warmup;
        }

        @Override
        public float totalProgress(){
            return totalProgress;
        }

        @Override
        public float progress(){
            return Mathf.clamp(progress);
        }

        @Override
        public Vec2 getCommandPosition(){
            return commandPos;
        }

        @Override
        public void onCommand(Vec2 target){
            commandPos = target;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.f(progress);
            write.bool(shitting);
            write.i(currentRecipe.index);
            mmmDelish.write(write);
            TypeIO.writeVecNullable(write, commandPos);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            progress = read.f();
            shitting = read.bool();
            currentRecipe = recipes.get(read.i());
            mmmDelish.read(read);
            commandPos = TypeIO.readVecNullable(read);
        }
    }

    public static class Recipe extends UnlockableContent{
        public DrawBlock drawer;
        public float powerReq = - 555;
        public boolean isUnit = false;

        /*--- Set automatically ---*/
        public int index;
        private final float time;
        private final UnlockableContent iconContent;
        private final Seq<ItemStack> itemReq = new Seq<>(ItemStack.class);
        private final Seq<ItemStack> itemOut = new Seq<>();
        private final Seq<LiquidStack> liquidReq = new Seq<>(LiquidStack.class);
        private final Seq<LiquidStack> liquidOut = new Seq<>();
        private final Seq<PayloadStack> payReq = new Seq<>();
        private final Seq<PayloadStack> payOut = new Seq<>();
        private ItemStack[] itemReqArray;
        private LiquidStack[] liquidReqArray;

        /**Use a null for iconContent to add an icon yourself.*/
        public Recipe(String name, UnlockableContent iconContent, float time){
            super(name);

            localizedName = Core.bundle.get("recipe." + this.name + ".name", this.name);
            description = Core.bundle.getOrNull("recipe." + this.name + ".description");
            details = Core.bundle.getOrNull("recipe." + this.name + ".details");
            unlocked = Core.settings != null && Core.settings.getBool(this.name + "-unlocked", false);

            this.iconContent = iconContent;
            this.time = time;
        }

        @Override
        public ContentType getContentType(){
            return ContentType.loadout_UNUSED;
        }

        @Override
        public void loadIcon(){
            fullIcon = uiIcon = (iconContent != null ? iconContent.fullIcon : Core.atlas.find("recipe-" + name));
        }

        public void req(Object... items){
            for(int i = 0; i < items.length; i += 2){
                if(items[i] instanceof Item)
                    itemReq.add(new ItemStack((Item) items[i], ((Number) items[i + 1]).intValue()));
                else if(items[i] instanceof Liquid)
                    liquidReq.add(new LiquidStack((Liquid) items[i], ((Number) items[i + 1]).floatValue()));
                else
                    payReq.add(new PayloadStack((UnlockableContent) items[i], ((Number) items[i + 1]).intValue()));
            }
        }

        public void out(Object... items){
            for(int i = 0; i < items.length; i += 2){
                if(items[i] instanceof Item)
                    itemOut.add(new ItemStack((Item) items[i], ((Number) items[i + 1]).intValue()));
                else if(items[i] instanceof Liquid)
                    liquidOut.add(new LiquidStack((Liquid) items[i], ((Number) items[i + 1]).floatValue()));
                else
                    payOut.add(new PayloadStack((UnlockableContent) items[i], ((Number) items[i + 1]).intValue()));
            }
        }
    }
}