package serilia.world.blocks.payload;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.*;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.Effect;
import mindustry.game.EventType;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.io.TypeIO;
import mindustry.type.*;
import mindustry.ui.Bar;
import mindustry.ui.ItemImage;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.blocks.heat.HeatConsumer;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.world.consumers.ConsumeItemDynamic;
import mindustry.world.consumers.ConsumePayloadDynamic;
import mindustry.world.consumers.ConsumePowerDynamic;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawRegion;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.Stat;
import serilia.content.SeFxPal;
import serilia.types.ConsumeLiquidsDynamic;

import static mindustry.Vars.*;

/**A multicrafter that should support all in- and output types.
 * @author nullevoy */

/* the list
* [X] produce items
* [X] produce payloads
* [X] consume items
* [X] consume liquids
* [X] consume payloads
* [X] consume power
* [X] switch configs
* [X] outDirs
* [X] UI
* [X] stats
* [X] unit cost mul
* [X] drawing
* [X] separator recipe class
* [X] Call/etc. for unit spawns
* [?] heat
* [ ] produce liquids //needs support for multiple
* [ ] produce power
* [-] attributes (floor + side)
* [ ] ports
* [ ] logic?
* [ ] schematic compat
* [ ] container IO
* [ ] add heat/attributes/...
* [ ] ...separator to recipe ui
* [ ] add missing bars
*/

public class UniversalCrafter extends PayloadBlock{
    public Seq<Recipe> recipes;

    //behavior
    public float warmupSpeed = 0.019f;
    public boolean loseProgressOnIdle = false;
    public float progressLoseSpeed = 0.019f;
    public float heatIncreaseSpeed = 0.15f;

    //io
    /**instantInput will consume a payload as soon as it enters. Best used with SeFxPal.payInstantDespawn.*/
    public boolean instantInput = true;
    /**Outputs the first payload without an effect, for if you need the payload to look like it was already there.*/
    public boolean instantFirstOutput = false;
    /**The block waits for the spawn effect to finish before outputting again, use Fx.none to get instant output.*/
    public Effect paySpawnEffect = SeFxPal.payRespawn;
    /**Enables the above behavior.*/
    public boolean waitForSpawnEffect = true;
    /**Use SeFxPal.payDespawn instead for unspecial non-instant ones*/
    public Effect payDespawnEffect = SeFxPal.payInstantDespawn;

    public boolean dumpExtraLiquid = false;
    public boolean ignoreLiquidFullness = false;
    public int[] liquidOutputDirections = {- 1};

    //visuals
    /**Recipe drawers are drawn between these.*/
    public DrawBlock drawerBottom = new DrawDefault(), drawerTop = new DrawRegion("-top");
    public boolean vanillaIO = false;

    public UniversalCrafter(String name){
        super(name);
        configurable = true;
        solid = true;
    }

    /*--- Set automatically ---*/
    private boolean hasPayloads, liquidIn, hasHeat, hasAttribute;
    private int[] capacities = {};

    @Override
    public void init(){
        capacities = new int[Vars.content.items().size];
        consumesPower = false;

        recipes.each(recipe -> {
            hasItems |= recipe.itemReq.size != 0 || recipe.itemOut.size != 0;
            hasLiquids |= (liquidIn |= recipe.liqReq.size != 0) || (outputsLiquid |= recipe.liqOut.size != 0);
            hasPayloads |= (acceptsPayload |= recipe.payReq.size != 0) || (outputsPayload |= recipe.payOut.size != 0);
            rotate |= outputsPayload;
            commandable |= recipe.isUnit;
            consumesPower |= recipe.powerReq > -1f;
            outputsPower |= recipe.powerOut > -1f;
            hasHeat |= recipe.heatOut > -1f;
            hasAttribute |= recipe.attribute != null;

            if(recipe.itemReq.size != 0){
                recipe.itemReqArray = recipe.itemReq.toArray(ItemStack.class);
                for(ItemStack stack : recipe.itemReqArray){
                    capacities[stack.item.id] = Math.max(capacities[stack.item.id], stack.amount * 2);
                    itemCapacity = Math.max(itemCapacity, stack.amount * 2);
                }
            }
            if(recipe.liqReq.size != 0){
                recipe.liqReqArray = recipe.liqReq.toArray(LiquidStack.class);
            }
        });


        if(hasPayloads)
            consume(new ConsumePayloadDynamic((UniversalBuild b) -> b.currentRecipe != null && b.currentRecipe.payReq.size != 0 ? b.currentRecipe.payReq : Seq.with()));
        if(hasItems)
            consume(new ConsumeItemDynamic((UniversalBuild b) -> b.currentRecipe != null && b.currentRecipe.itemReqArray != null ? b.currentRecipe.itemReqArray : ItemStack.empty));
        if(consumesPower)
            consume(new ConsumePowerDynamic((Building b) -> ((UniversalBuild) b).currentRecipe != null && ((UniversalBuild) b).currentRecipe.powerReq > - 1 ? ((UniversalBuild) b).currentRecipe.powerReq : 0f));
        if(liquidIn)
            consume(new ConsumeLiquidsDynamic((UniversalBuild b) -> b.currentRecipe != null && b.currentRecipe.liqReqArray != null ? b.currentRecipe.liqReqArray : LiquidStack.empty));

        consumeBuilder.each(c -> c.multiplier = b -> ((UniversalBuild)b).currentRecipe != null && ((UniversalBuild)b).currentRecipe.isUnit ? state.rules.unitCost(b.team) : 1);

        if(recipes.size == 1){
            configurable = false;
        }

        config(Recipe.class, (UniversalBuild tile, Recipe val) -> {
            if(! configurable || tile.currentRecipe == val) return;

            tile.currentRecipe = val;
            tile.progress = 0;
            tile.attributeSum = 0f;
            if(((UniversalCrafter)tile.block).hasAttribute && tile.currentRecipe.attribute != null) tile.attributeSum = sumAttribute(val.attribute, tile.tile.x, tile.tile.y);
            tile.currentRecipe.liqReq.each(bar ->
                    addLiquidBar(bar.liquid)
            );
            tile.currentRecipe.liqOut.each(bar ->
                    addLiquidBar(bar.liquid)
            );
        });

        super.init();

        recipes.each(recipe -> {
            if(recipe.liqReq.size != 0){
                recipe.liqReq.each(req -> {
                    liquidFilter[req.liquid.id] = true;
                    liquidCapacity = Math.max(liquidCapacity, req.amount * 2);
                });
                recipe.liqOut.each(out -> {
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
    public void load(){
        super.load();

        drawerTop.load(this);
        drawerBottom.load(this);
        recipes.each(recipe -> {
            if(recipe.drawer != null) recipe.drawer.load(this);
        });
    }

    @Override
    public void setBars(){
        super.setBars();
        removeBar("liquid");

        addBar("progress", (UniversalBuild entity) -> new Bar("bar.progress", Pal.ammo, () -> entity.progress));
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.remove(Stat.itemCapacity);
        stats.remove(Stat.powerUse);

        stats.add(Stat.output, table -> {
            table.row();
            recipes.each(recipe -> {
                if(recipe.index % 2 == 0) table.row();

                table.table(Styles.grayPanel, display -> {
                    display.add(recipeTopTable(recipe)).pad(10f); //recipe identifiers

                    display.row();

                    display.table(io -> { //encompasses input/output
                        io.table(Styles.black5, input -> //input
                            input.add(contentListTable(recipe.payReq, recipe.itemReq, recipe.liqReq, recipe.time, false)).pad(5f).grow() //not adding directly for outline
                        ).pad(5f).padTop(0f).grow();


                        io.add(recipeArrowTable(recipe)).padBottom(5f); //arrow


                        io.table(Styles.black5, output -> //output
                            output.add(contentListTable(recipe.payOut, recipe.itemOut, recipe.liqOut, recipe.powerOut, recipe.heatOut, recipe.time, true)).pad(5f).grow()
                        ).pad(5f).padTop(0f).grow();
                    });

                    display.row();

                    display.add(recipeBottomTable(recipe)).left();
                }).pad(5f).top().growX();
            });
        });
    }

    public Table recipeTopTable(Recipe recipe){
        Table t = new Table();

        t.add(recipe.localizedName); //todo unit
        t.row();
        t.image(recipe.uiIcon).size(60).scaling(Scaling.fit);

        return t;
    }
    public Table recipeArrowTable(Recipe recipe){
        Table t = new Table();

        if(recipe.powerReq > 0){
            t.table(power -> {
                power.add("[accent]" + Iconc.power + "[]");
                power.row();
                power.add(Strings.autoFixed(recipe.powerReq  * 60f, 1));
            }).pad(5f).bottom();
            t.row();
        }

        t.image(Icon.play).pad(5f).padTop(10f).padBottom(10f).center();

        if(recipe.heatReq > 0){
            t.row();
            t.table(heat -> {
                heat.add("[red]" + Iconc.waves + "[]");
                heat.row();
                heat.add(Strings.autoFixed(recipe.heatReq, 1));
            }).pad(5f).top();
            t.row();
        }

        return t;
    }
    public Table recipeBottomTable(Recipe recipe){
        Table t = new Table();

        //if(recipe.description != null) t.label(() -> recipe.description).color(Color.lightGray).wrap().pad(10f).left();

        return t;
    }//todo add the attributes in here

    public Table contentListTable(Seq<PayloadStack> pay, Seq<ItemStack> item, Seq<LiquidStack> liq, float time, boolean flip){
        return contentListTable(pay, item, liq, -20f, -20f, time, flip);
    }
    public Table contentListTable(Seq<PayloadStack> pay, Seq<ItemStack> item, Seq<LiquidStack> liq, float power, float heat, float time, boolean flip){
        Table t = new Table();

        pay.each(requirement -> {
            if(flip) t.add(new ItemImage(requirement.item.uiIcon, requirement.amount)).left();

            t.table(req -> {
                req.add(Strings.autoFixed(requirement.amount / (time / 60f), 2) + "/s").color(Color.lightGray).pad(4f);
            });

            if(!flip) t.add(new ItemImage(requirement.item.uiIcon, requirement.amount)).right();
            t.row();
        });
        item.each(requirement -> {
            if(flip) t.add(new ItemImage(requirement.item.uiIcon, requirement.amount)).left();

            t.table(req -> {
                req.add(Strings.autoFixed(requirement.amount / (time / 60f), 2) + "/s").color(Color.lightGray).pad(4f);
            });
            if(!flip) t.add(new ItemImage(requirement.item.uiIcon, requirement.amount)).right();
            t.row();
        });
        liq.each(requirement -> {
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

    public class UniversalBuild extends PayloadBlockBuild<Payload> implements HeatBlock, HeatConsumer{
        public @Nullable Vec2 commandPos;
        public Recipe currentRecipe = recipes.get(0);
        public PayloadSeq mmmDelish = new PayloadSeq();
        public Seq<Payload> outQueue = new Seq<>();
        public float progress, warmup, totalProgress, spawnTime, heat = 0f, attributeSum;
        public float[] sideHeat = new float[4];
        public boolean outputting;
        public UnlockableContent lastPayload;

        @Override
        public void updateTile(){
            if(hasHeat) heat = Math.max(calculateHeat(sideHeat), Mathf.approachDelta(heat, currentRecipe.heatOut * efficiency, heatIncreaseSpeed * delta()));

            if(currentRecipe != null){
                boolean yum = wants();

                if(hasPayloads){
                    if(!yum){
                        if(payload == null){
                            outputPayload();
                        }else{
                            moveOutPayload();
                        }
                    }

                    if(yum && (moveInPayload() || instantInput)){
                        mmmDelish.add(payload.content());
                        payDespawnEffect.at(x, y, drawrot(), new YootData(payload, this));
                        payload = null;
                    }
                }

                if(efficiency > 0){
                    progress += getProgressIncrease(currentRecipe.time) * (currentRecipe.isUnit ? state.rules.unitBuildSpeed(team) : 1f);
                    warmup = Mathf.approachDelta(warmup, warmupTarget(), warmupSpeed);

                    if(wasVisible && Mathf.chanceDelta(currentRecipe.updateEffectChance)){
                        currentRecipe.updateEffect.at(this);
                    }
                }else{
                    warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
                    if(loseProgressOnIdle) progress = Mathf.approachDelta(progress, 0f, progressLoseSpeed);
                }
                dumpOutputs();
            }

            if(progress >= 1f){
                craft();
            }

            totalProgress += warmup * Time.delta;
        }

        public void craft(){
            consume();

            currentRecipe.craft();

            var itemOut = currentRecipe.itemOut();
            if(itemOut.size != 0){
                itemOut.each(output -> {
                    for(int i = 0; i < output.amount; i++){
                        offload(output.item);
                    }
                });
            }

            var liqOut = currentRecipe.liqOut();
            if(liqOut.size != 0){
                liqOut.each(output -> {
                    for(int i = 0; i < output.amount; i++){
                        handleLiquid(this, output.liquid, Math.min(output.amount, liquidCapacity - liquids.get(output.liquid)));
                    }
                });
            }

            var payOut = currentRecipe.payOut();
            if(payOut.size != 0){
                payOut.each(output -> {
                    for(int i = 0; i < output.amount; i++){
                        outQueue.add(createPayload(output.item));
                    }
                });

                if(instantFirstOutput){
                    payVector.setZero();
                    payload = outQueue.pop();
                }
            }

            if(wasVisible){
                currentRecipe.craftEffect.at(this);
            }

            progress %= 1f;
        }

        public void dumpOutputs(){
            if(currentRecipe.itemOut.size != 0 && timer(timerDump, dumpTime / timeScale)){
                currentRecipe.itemOut.each(output -> dump(output.item));
            }

            if(currentRecipe.liqOut.size != 0 && timer(timerDump, dumpTime / timeScale)){
                for(int i = 0; i < currentRecipe.liqOut.size; i++){
                    int dir = liquidOutputDirections.length > i ? liquidOutputDirections[i] : - 1;
                    dumpLiquid(currentRecipe.liqOut.get(i).liquid, 2f, dir);
                }
            }
        }

        public boolean wants(){
            return payload != null && currentRecipe != null && currentRecipe.payReq.size != 0 && currentRecipe.payReq.contains(b -> b.item == payload.content());
        }

        public boolean wants(Payload pay){
            return pay != null && currentRecipe != null && currentRecipe.payReq.size != 0 && currentRecipe.payReq.contains(b ->
                    b.item == pay.content() && mmmDelish.get(pay.content()) < Mathf.round(b.amount * team.rules().unitCostMultiplier));
        }

        public void outputPayload(){
            if(outQueue.isEmpty()) return;

            if(!outputting){
                spawnTime = paySpawnEffect.lifetime;
                outputting = true;

                paySpawnEffect.at(x, y, drawrot(), new YootData(outQueue.peek(), this));
            }

            spawnTime = Mathf.approachDelta(spawnTime, -1f, 1f);

            if(!waitForSpawnEffect || spawnTime < 0f){
                payVector.setZero();
                payload = outQueue.pop();
            }
        }

        public Payload createPayload(UnlockableContent content){
            if(content instanceof UnitType e){
                Unit diarrhea = e.create(team);
                if(commandPos != null && diarrhea.isCommandable()){
                    diarrhea.command().commandPosition(commandPos);
                }
                diarrhea.rotation = rotdeg();
                var unit = new UnitPayload(diarrhea);
                Events.fire(new EventType.UnitCreateEvent(unit.unit, this));

                return unit;
            }else{
                return new BuildPayload((Block) content, team);
            }
        }

        public float warmupTarget(){
            return currentRecipe == null ? 0f : currentRecipe.heatReq > 0f ? Mathf.clamp(heat / currentRecipe.heatReq) : 1f;
        }

        @Override
        public float getProgressIncrease(float baseTime){
            return super.getProgressIncrease(baseTime) * (currentRecipe.attribute != null ? currentRecipe.baseAttributeEfficiency + Math.min(currentRecipe.maxAttributeEfficiency, currentRecipe.attributeBoostScale * attributeSum) + currentRecipe.attribute.env() : 1f);
        }

        @Override
        public float efficiencyScale(){
            if(!hasHeat || currentRecipe == null || currentRecipe.heatReq <= 0) return 1f;

            float over = Math.max(heat - currentRecipe.heatReq, 0f);
            return Math.min(Mathf.clamp(heat / currentRecipe.heatReq) + over / currentRecipe.heatReq * currentRecipe.overheatScale, currentRecipe.maxHeatEfficiency);
        }

        @Override
        public void pickedUp(){
            attributeSum = 0f;
            warmup = 0f;
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

                if(currentRecipe.liqOut.size != 0 && ! ignoreLiquidFullness){
                    boolean allFull = true;
                    for(int i = 0; i < currentRecipe.liqOut.size; i++){
                        if(liquids.get(currentRecipe.liqOut.get(i).liquid) + currentRecipe.liqOut.get(i).amount > liquidCapacity){
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

                return enabled && !outputting;
            }
            return false;
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
            if(((UniversalCrafter)block).hasAttribute && currentRecipe.attribute != null) attributeSum = sumAttribute(currentRecipe.attribute, tile.x, tile.y);
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload){
            return this.payload == null && currentRecipe != null && outQueue.isEmpty() && !outputting && wants(payload);
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return currentRecipe != null && currentRecipe.itemReqArray != null && items.get(item) < getMaximumAccepted(item) &&
                    Structs.contains(currentRecipe.itemReqArray, stack -> stack.item == item);
        }

        @Override
        public float heatFrac(){
            return currentRecipe != null && currentRecipe.heatOut > 0f ? heat / currentRecipe.heatOut : 0f;
        }

        @Override
        public float heat(){
            return currentRecipe != null && currentRecipe.heatOut > 0f ? heat : 0f;
        }

        @Override
        public float heatRequirement(){
            return currentRecipe != null && currentRecipe.heatReq > 0f ? currentRecipe.heatReq : 0f;
        }

        @Override
        public float[] sideHeat(){
            return sideHeat;
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
        public void moveOutPayload(){
            if(payload == null) return;

            if(payload instanceof UnitPayload && currentRecipe != null && currentRecipe.outputUnitToTop && payload.dump()) payload = null;

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
                        outputting = false;
                    }
                }else if(canDump){
                    dumpPayload();
                    outputting = false;
                }
            }
        }

        @Override
        public void dumpPayload(){
            //translate payload forward slightly
            float tx = Angles.trnsx(payload.rotation(), 0.1f), ty = Angles.trnsy(payload.rotation(), 0.1f);
            payload.set(payload.x() + tx, payload.y() + ty, payload.rotation());

            if(payload.dump() && payload instanceof UnitPayload){
                payload = null;
                Call.unitBlockSpawn(tile);
            }else{
                payload.set(payload.x() - tx, payload.y() - ty, payload.rotation());
            }
        }

        @Override
        public void draw(){
            drawerBottom.draw(this);

            if(vanillaIO){
                for(int i = 0; i < 4; i++){
                    if(blends(i) && i != rotation){
                        Draw.rect(inRegion, x, y, (i * 90) - 180);
                    }
                }

                if(currentRecipe.showPayOutput) Draw.rect(outRegion, x, y, rotdeg());
            }

            drawPayload();
            Draw.z(35.1f);

            if(currentRecipe != null && currentRecipe.drawer != null) currentRecipe.drawer.draw(this);
            drawerTop.draw(this);
        }

        @Override
        public void display(Table table){
            super.display(table);
            if(team != player.team()) return;

            table.row();
            table.table(t -> {
                t.image(currentRecipe.fullIcon).size(iconMed).scaling(Scaling.fit).padBottom(-4f).padRight(2f).padTop(2f);
                t.label(() -> currentRecipe.name).color(Color.lightGray);
            });
        }

        @Override
        public void handlePayload(Building source, Payload payload){
            super.handlePayload(source, payload);
            lastPayload = payload.content();
        }

        @Override
        public float getPowerProduction(){
            return currentRecipe != null ? currentRecipe.powerOut * efficiency : 0f;
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
            write.f(heat);
            write.i(currentRecipe.index);
            mmmDelish.write(write);
            TypeIO.writeVecNullable(write, commandPos);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            progress = read.f();
            heat = read.f();
            currentRecipe = recipes.get(read.i());
            mmmDelish.read(read);
            commandPos = TypeIO.readVecNullable(read);
        }
    }

    /** gwgejgabhjrewgbh */
    public static class YootData{
        public Payload pay;
        public Position pos;

        public YootData(Payload payload, Position position){
            pay = payload;
            pos = position;
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public static class Recipe extends UnlockableContent{
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

        public void craft(){}

        public Seq<ItemStack> itemOut(){return itemOut;}
        public Seq<LiquidStack> liqOut(){return liqOut;}
        public Seq<PayloadStack> payOut(){return payOut;}

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
    }
}