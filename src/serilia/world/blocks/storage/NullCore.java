package serilia.world.blocks.storage;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.gen.Player;
import mindustry.gen.PlayerSpawnCallPacket;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.type.ItemStack;
import mindustry.type.PayloadSeq;
import mindustry.type.UnitType;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.consumers.ConsumeItemDynamic;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawRegion;
import serilia.types.UnitRecipe;
import unicrafter.recipes.Recipe;

import static mindustry.Vars.state;

/**this is so nullcore*/
public class NullCore extends CoreBlock{ //todo
    public Seq<UnitRecipe> recipes;

    public DrawBlock drawerBottom = new DrawDefault(), drawerTop = new DrawRegion("-top");

    public float warmupSpeed = 0.019f, maxDeconSize = 3f * 8f;

    public NullCore(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();

        drawerTop.load(this);
        drawerBottom.load(this);
        recipes.each(recipe -> {
            if(recipe.drawer != null) recipe.drawer.load(this);
        });
    }

    @Override
    public void init(){
        super.init();
        consume(new ConsumeItemDynamic((NullCoreBuild b) -> b.currentRecipe != null && b.currentRecipe.itemReqArray() != null ? b.currentRecipe.itemReqArray() : ItemStack.empty));
    }

    public class NullCoreBuild extends CoreBuild{
        public Recipe currentRecipe = recipes.get(0);
        public PayloadSeq mmmDelish = new PayloadSeq(); //maybe?
        public UnitType deconType;

        public float progress, warmup, totalProgress;

        @Override
        public void updateTile() {
            if(efficiency > 0){
                progress += getProgressIncrease(currentRecipe.time) * (currentRecipe.isUnit ? state.rules.unitBuildSpeed(team) : 1f);
                warmup = Mathf.approachDelta(warmup, 1f, warmupSpeed);

                if(wasVisible && Mathf.chanceDelta(currentRecipe.updateEffectChance)){
                    currentRecipe.updateEffect.at(this);
                }
            }else{
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
                if(currentRecipe.loseProgressOnIdle) progress = Mathf.approachDelta(progress, 0f, currentRecipe.progressLoseSpeed);
            }


        }

        @Override
        public void requestSpawn(Player player){
            //do not try to respawn in unsupported environments at all
            if(!unitType.supportsEnv(state.rules.env)) return;

            if (Vars.net.server() || !Vars.net.active()) {
                playerSpawn(tile, player);
            }

            if (Vars.net.server()) {
                PlayerSpawnCallPacket packet = new PlayerSpawnCallPacket();
                packet.tile = tile;
                packet.player = player;
                Vars.net.send(packet, true);
            }
        }

        @Override
        public void draw(){
            drawerBottom.draw(this);
            /*if(vanillaIO){
                for(int i = 0; i < 4; i++){
                    if(blends(i) && i != rotation){
                        Draw.rect(inRegion, x, y, (i * 90) - 180);
                    }
                }
            }*/
            Draw.z(Layer.blockOver);
            if(currentRecipe != null && currentRecipe.drawer != null) currentRecipe.drawer.draw(this);
            drawerTop.draw(this);
        }

        @Override public boolean canControlSelect(Unit unit){
            return deconType == null && unit.type.allowedInPayloads && !unit.spawnedByCore && unit.hitSize <= maxDeconSize && unit.buildOn() == this;
        }
        @Override public void update(){
            iframes -= Time.delta;
            thrusterTime -= Time.delta/90f;
            super.update();
        }
        @Override public void drawTeamTop(){}
        @Override public float warmup(){
            return warmup;
        }
        @Override public float progress(){
            return progress;
        }
        @Override public float totalProgress(){
            return totalProgress;
        }
    }
}
