package serilia.world.blocks.unicrafter;

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
import mindustry.ctype.UnlockableContent;
import mindustry.entities.Effect;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.io.TypeIO;
import mindustry.type.*;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.blocks.heat.HeatConsumer;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.world.blocks.storage.StorageBlock;
import mindustry.world.consumers.ConsumeItemDynamic;
import mindustry.world.consumers.ConsumePayloadDynamic;
import mindustry.world.consumers.ConsumePowerDynamic;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawRegion;
import mindustry.world.meta.Stat;
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
* [X] separator recipe ui
* [X] add missing bars
*
* [-] attributes (floor + side)
* [-] container IO
* [-] add heat/attributes to recipe ui
* [ ] schematic compat
* [ ] produce liquids //needs support for multiple
* [ ] produce power
* [L] heat //fat fucking L
*
* [ ] generator explosion/death/damage + more
* [ ] liquid container IO
* [ ] logic?
* [ ] aqueduct/etc. ports
*/

public class UniversalCrafter extends PayloadBlock{
    public Seq<Recipe> recipes;

    //general
    public float warmupSpeed = 0.019f;
    public float heatIncreaseSpeed = 0.15f;

    //io
    /**instantInput will consume a payload as soon as it enters. Best used with UniFx.payInstantDespawn.*/
    public boolean instantInput = true;
    /**Outputs the first payload without an effect, for if you need the payload to look like it was already there.*/
    public boolean instantFirstOutput = false;
    /**The block waits for the spawn effect to finish before outputting again, use Fx.none to get instant output.*/
    public Effect paySpawnEffect = UniFx.payRespawn;
    /**Enables the above behavior.*/
    public boolean waitForSpawnEffect = true;
    /**Use UniFx.payDespawn instead for unspecial non-instant ones*/
    public Effect payDespawnEffect = UniFx.payInstantDespawn;

    public boolean dumpExtraLiquid = false;
    public boolean ignoreLiquidFullness = false;
    public int[] liquidOutputDirections = {-1};

    //visuals
    /**Recipe drawers are drawn between these.*/
    public DrawBlock drawerBottom = new DrawDefault(), drawerTop = new DrawRegion("-top");
    /**Whether to draw vanilla payload sprites. TODO convert to drawer or no?*/
    public boolean vanillaIO = false;

    public UniversalCrafter(String name){
        super(name);
        configurable = true;
        solid = true;
    }

    /*--- Set automatically ---*/
    public int[] capacities = {};
    public boolean hasHeat;
    public boolean hasPayloads, hasLiquidIn;

    @Override
    public void init(){
        capacities = new int[Vars.content.items().size];
        consumesPower = false;

        if(recipes.size == 1){
            configurable = false;
        }

        config(Integer.class, (UniversalBuild tile, Integer num) -> {
            var val = recipes.get(num);
            if(!configurable || tile.currentRecipe == val) return;

            tile.currentRecipe = val;
            tile.currentRecipe.configTo(tile);

            tile.progress = 0;
            tile.attributeSum = 0f;
            if(tile.currentRecipe.attribute != null){
                tile.attributeSum = sumAttribute(val.attribute, tile.tile.x, tile.tile.y);
                addBar("efficiency", (UniversalBuild entity) -> new Bar(
                    () -> Core.bundle.format("bar.efficiency", (int)entity.efficiencyMultiplier() * 100),
                    () -> Pal.lightOrange,
                    entity::efficiencyMultiplier
                ));
            }
            if(tile.currentRecipe.heatReq > 0f){
                addBar("heat", (UniversalBuild entity) ->
                        new Bar(() ->
                                Core.bundle.format("bar.heatpercent", (int)(entity.heat + 0.01f), (int)(entity.efficiencyScale() * 100 + 0.01f)),
                                () -> Pal.lightOrange,
                                () -> entity.currentRecipe.heatReq > 0f ? entity.heat / entity.currentRecipe.heatReq : 1f));
            }
        });

        super.init();

        for(int i = 0; i < recipes.size; i++){
            recipes.get(i).index = i;
            recipes.get(i).init(this);
        }

        if(hasPayloads) consume(new ConsumePayloadDynamic((UniversalBuild b) -> b.currentRecipe != null && b.currentRecipe.payReq().size != 0 ? b.currentRecipe.payReq() : Seq.with()));
        if(hasItems) consume(new ConsumeItemDynamic((UniversalBuild b) -> b.currentRecipe != null && b.currentRecipe.itemReqArray() != null ? b.currentRecipe.itemReqArray() : ItemStack.empty));
        if(consumesPower) consume(new ConsumePowerDynamic((Building b) -> ((UniversalBuild) b).currentRecipe != null && ((UniversalBuild) b).currentRecipe.powerReq > -1 ? ((UniversalBuild) b).currentRecipe.powerReq : 0f));
        if(hasLiquidIn) consume(new ConsumeLiquidsDynamic((UniversalBuild b) -> b.currentRecipe != null && b.currentRecipe.liqReqArray() != null ? b.currentRecipe.liqReqArray() : LiquidStack.empty));

        consumeBuilder.each(c -> c.multiplier = b -> ((UniversalBuild) b).currentRecipe != null && ((UniversalBuild) b).currentRecipe.isUnit ? state.rules.unitCost(b.team) : 1);
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
                table.add(recipe.recipeTable(true)).top();
            });
        });
    }

    public class UniversalBuild extends PayloadBlockBuild<Payload> implements HeatBlock, HeatConsumer{
        public Recipe currentRecipe = recipes.get(0);

        public PayloadSeq mmmDelish = new PayloadSeq();
        public Seq<Payload> outQueue = new Seq<>();
        public BuildPayload container; //TODO contain

        public float progress, warmup, totalProgress, spawnTime, heat = 0f, attributeSum;
        public float[] sideHeat = new float[4];
        public boolean outputting;
        public UnlockableContent lastPayload;
        public @Nullable Vec2 commandPos;

        //crafting logic
        @Override
        public void updateTile(){
            if(hasHeat) heat = Math.max(calculateHeat(sideHeat), Mathf.approachDelta(heat, currentRecipe.heatOut * efficiency, heatIncreaseSpeed * delta()));

            if(currentRecipe != null){
                boolean yum = wants();

                if(hasPayloads){
                    if(yum && (container == null || wantsContainer())){
                        if(currentRecipe instanceof ContainerRecipe && payload.content() instanceof StorageBlock && moveInPayload()){
                            container = (BuildPayload) payload;
                            this.payload = null;

                        }else if(moveInPayload() || instantInput){
                            mmmDelish.add(payload.content());
                            payDespawnEffect.at(x, y, drawrot(), new YootData(payload, this));
                            payload = null;
                        }
                    }else{
                        if(payload == null){
                            outputPayload();
                        }else{
                            moveOutPayload();
                        }
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
                    if(currentRecipe.loseProgressOnIdle) progress = Mathf.approachDelta(progress, 0f, currentRecipe.progressLoseSpeed);
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

            if(currentRecipe instanceof ContainerRecipe r && container != null){
                r.itemReqContainer().each(stack -> container.build.items.remove(stack.item, stack.amount));
                r.itemOutContainer().each(stack -> container.build.items.add(stack.item, stack.amount));
            }

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

        @Override
        public boolean shouldConsume(){
            if(currentRecipe != null){
                if(!wantsContainer()) return false; //todo

                if(currentRecipe.itemOut().size != 0){
                    for(int i = 0; i < currentRecipe.itemOut().size; i++){
                        if(items.get(currentRecipe.itemOut().get(i).item) + currentRecipe.itemOut().get(i).amount > itemCapacity){
                            return false;
                        }
                    }
                }

                if(currentRecipe.liqOut().size != 0 && !ignoreLiquidFullness){
                    boolean allFull = true;
                    for(int i = 0; i < currentRecipe.liqOut().size; i++){
                        if(liquids.get(currentRecipe.liqOut().get(i).liquid) + currentRecipe.liqOut().get(i).amount > liquidCapacity){
                            if(!dumpExtraLiquid){
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
        } //todo add and use total output possibilities instead of just current

        public void dumpOutputs(){
            if(currentRecipe.itemOut().size != 0 && timer(timerDump, dumpTime / timeScale)){
                currentRecipe.itemOut().each(output -> dump(output.item));
            }

            if(currentRecipe.liqOut().size != 0 && timer(timerDump, dumpTime / timeScale)){
                for(int i = 0; i < currentRecipe.liqOut().size; i++){
                    int dir = liquidOutputDirections.length > i ? liquidOutputDirections[i] : -1;
                    dumpLiquid(currentRecipe.liqOut().get(i).liquid, 2f, dir);
                }
            }
        } //todo add and use total output possibilities instead of just current



        //payload
        public boolean wants(){
            return payload != null && currentRecipe != null &&
                    (currentRecipe.payReq().size != 0 && currentRecipe.payReq().contains(b -> b.item == payload.content()) ||
                    currentRecipe instanceof ContainerRecipe && payload instanceof BuildPayload && payload.content() instanceof StorageBlock);
        }

        public boolean wants(Payload pay){
            return pay != null && currentRecipe != null &&
                    (currentRecipe.payReq().size != 0 && currentRecipe.payReq().contains(b ->
                            b.item == pay.content() && mmmDelish.get(pay.content()) < Mathf.round(b.amount * team.rules().unitCostMultiplier)) ||
                            currentRecipe instanceof ContainerRecipe && pay instanceof BuildPayload && pay.content() instanceof StorageBlock);
        }

        public void outputPayload(){
            if(outQueue.isEmpty() && container == null) return;

            if(!outputting){
                if(container != null){
                    payload = container;
                }else{
                    spawnTime = paySpawnEffect.lifetime;
                    paySpawnEffect.at(x, y, drawrot(), new YootData(outQueue.peek(), this));
                }
                outputting = true;
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

        public boolean wantsContainer(){
            if(currentRecipe instanceof ContainerRecipe r){
                if(container != null) return false;

                for(int i = 0; i < r.itemReqContainer().size; i++){
                    if(!container.build.items.has(r.itemReqContainer())) return false;
                }
                for(int i = 0; i < r.itemOutContainer().size; i++){
                    if(!(container.build.items.get(r.itemOutContainer().get(i).item) + r.itemOutContainer().get(i).amount > itemCapacity)) return false;
                }
            }
            return true;
        }

        @Override
        public void moveOutPayload(){
            if(payload == null) return;

            if(payload instanceof UnitPayload && currentRecipe != null && currentRecipe.outputUnitToTop && payload.dump())
                payload = null;

            updatePayload();

            Vec2 dest = Tmp.v1.trns(rotdeg(), size * tilesize / 2f);

            payRotation = Angles.moveToward(payRotation, rotdeg(), payloadRotateSpeed * delta());
            payVector.approach(dest, payloadSpeed * delta());

            Building front = front();
            boolean canDump = front == null || !front.tile().solid();
            boolean canMove = front != null && (front.block.outputsPayload || front.block.acceptsPayload);

            if(canDump && !canMove){
                pushOutput(payload, 1f - (payVector.dst(dest) / (size * tilesize / 2f)));
            }

            if(payVector.within(dest, 0.001f)){
                payVector.clamp(-size * tilesize / 2f, -size * tilesize / 2f, size * tilesize / 2f, size * tilesize / 2f);

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
        public void handlePayload(Building source, Payload payload){
            super.handlePayload(source, payload);
            lastPayload = payload.content();
        }



        //visuals
        @Override
        public void draw(){
            drawerBottom.draw(this);

            if(vanillaIO){
                for(int i = 0; i < 4; i++){
                    if(blends(i) && i != rotation){
                        Draw.rect(inRegion, x, y, (i * 90) - 180);
                    }
                }

                if(currentRecipe.showVanillaOutput) Draw.rect(outRegion, x, y, rotdeg());
            }

            Draw.z(Layer.blockOver);
            if(payload != null){
                updatePayload();
                payload.draw();
            }
            if(container != null){
                payload.set(x + payVector.x, y + payVector.y, payRotation);
                container.draw();
            }

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



        //misc
        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
            if(currentRecipe.attribute != null)
                attributeSum = sumAttribute(currentRecipe.attribute, tile.x, tile.y);
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload){
            return this.payload == null && currentRecipe != null && outQueue.isEmpty() && !outputting && wants(payload) &&
                    !(currentRecipe instanceof ContainerRecipe && payload.content() instanceof StorageBlock);
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return currentRecipe != null && currentRecipe.itemReqArray() != null && items.get(item) < getMaximumAccepted(item) &&
                    Structs.contains(currentRecipe.itemReqArray(), stack -> stack.item == item);
        }

        @Override
        public void buildConfiguration(Table table){
            Seq<Recipe> bRecipes = Seq.with(recipes).filter(r -> {
                if(r.payOut().size != 0){
                    for(int i = 0; i < r.payOut().size; i++){
                        if(r.payOut().get(i).item instanceof Block b) return !state.rules.isBanned(b);
                        if(r.payOut().get(i).item instanceof UnitType u) return !u.isBanned();
                    }
                }
                return true;
            });

            if(bRecipes.any()){
                ItemSelection.buildTable(UniversalCrafter.this, table, bRecipes, () -> currentRecipe, recipe -> configure(recipes.indexOf(rec -> rec == recipe)), selectionRows, selectionColumns);
            }else{
                table.table(Styles.black3, t -> t.add("@none").color(Color.lightGray));
            }
        }



        /*--- the stupid functions ---*/
        @Override
        public float efficiencyScale(){
            if(currentRecipe == null || currentRecipe.heatReq < 0) return 1f;

            float over = Math.max(heat - currentRecipe.heatReq, 0f);
            return Math.min(Mathf.clamp(heat / currentRecipe.heatReq) + over / currentRecipe.heatReq * currentRecipe.overheatScale, currentRecipe.maxHeatEfficiency);
        }

        @Override
        public void pickedUp(){
            attributeSum = 0f;
            warmup = 0f;
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

        public float warmupTarget(){
            return currentRecipe == null ? 0f : currentRecipe.heatReq > 0f ? Mathf.clamp(heat / currentRecipe.heatReq) : 1f;
        }

        public float efficiencyMultiplier(){
            return (currentRecipe.attribute != null ? currentRecipe.baseAttributeEfficiency + Math.min(currentRecipe.maxAttributeEfficiency, currentRecipe.attributeBoostScale * attributeSum) + currentRecipe.attribute.env() : 1f);
        }

        @Override
        public float getProgressIncrease(float baseTime){
            return super.getProgressIncrease(baseTime) * efficiencyMultiplier();
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

    /**gwgejgabhjrewgbh*/
    public static class YootData{
        public Payload pay;
        public Position pos;

        public YootData(Payload payload, Position position){
            pay = payload;
            pos = position;
        }
    }
}