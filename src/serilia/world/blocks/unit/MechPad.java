package serilia.world.blocks.unit;

import arc.Core;
import arc.audio.Sound;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.*;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.UnitTypes;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.graphics.Shaders;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;
import mindustry.ui.Bar;
import mindustry.ui.ItemDisplay;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.ControlBlock;
import mindustry.world.meta.Stat;

import static mindustry.Vars.*;

public class MechPad extends Block{

    TextureRegion glowRegion;

    public UnitType unitType;
    public float buildTime = 60 * 5;
    public ItemStack[] mechReqs;
    public int[] capacities = {};
    public Sound spawnSound = Sounds.respawn;

    @Override
    public void setBars() {
        super.setBars();
        addBar("progress", (MechPad.MechPadBuild e) -> new Bar("bar.progress", Pal.ammo, e::fraction));
    }

    @Override
    public boolean outputsItems(){
        return false;
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.remove(Stat.itemCapacity);
        stats.remove(Stat.input);

        stats.add(Stat.output, table -> {
            table.row();

            table.table(Styles.grayPanel, t -> {
                t.image(unitType.uiIcon).size(40).pad(10f).left().scaling(Scaling.fit);
                t.table(info -> {
                    info.add(unitType.localizedName).left();
                    info.row();
                    info.add(Strings.autoFixed(buildTime / 60f, 1) + " " + Core.bundle.get("unit.seconds")).color(Color.lightGray);
                }).left();

                t.table(req -> {
                    req.right();
                    for(int i = 0; i < mechReqs.length; i++){
                        if(i % 6 == 0){
                            req.row();
                        }

                        ItemStack stack = mechReqs[i];
                        req.add(new ItemDisplay(stack.item, stack.amount, false)).pad(5);
                    }
                }).right().grow().pad(10f);
            }).growX().pad(5);
            table.row();
        });
    }

    @Override
    public void init(){
        glowRegion = Core.atlas.find(name + "-glow");

        capacities = new int[Vars.content.items().size];
        for(ItemStack stack : mechReqs){
            capacities[stack.item.id] = Math.max(capacities[stack.item.id], stack.amount * 2);
            itemCapacity = Math.max(itemCapacity, stack.amount * 2);
        }

        consumeItems(mechReqs);
        consumeBuilder.each(c -> c.multiplier = b -> state.rules.unitCost(b.team));

        super.init();
    }

    public static void spawnMech(UnitType unitType, Tile tile, Player player){
        //do not try to respawn in unsupported environments at all
        if(!unitType.supportsEnv(state.rules.env)) return;

        if (Vars.net.server() || !Vars.net.active()){
            playerSpawn(tile, player);
        }

        if (Vars.net.server()) {
            PlayerSpawnCallPacket packet = new PlayerSpawnCallPacket();
            packet.tile = tile;
            packet.player = player;
            Vars.net.send(packet, true);
        }
    }

    public static void playerSpawn(Tile tile, Player player){
        if(player == null || tile == null || !(tile.build instanceof MechPad.MechPadBuild)) return;

        Building pad = tile.build;

        MechPad block = (MechPad)tile.block();
        if(pad.wasVisible){
            Fx.spawn.at(pad);
        }

        player.set(pad);

        if(!net.client()){
            Unit unit = block.unitType.create(tile.team());
            unit.set(pad);
            unit.rotation(90f);
            unit.impulse(0f, 3f);
            unit.controller(player);
            unit.spawnedByCore(true);
            unit.add();
        }

        if(state.isCampaign() && player == Vars.player){
            block.unitType.unlock();
        }
    }

    public MechPad(String name){
        super(name);
        update = true;
        solid = false;
        hasPower = true;
        ambientSound = Sounds.extractLoop;
        ambientSoundVolume = 0.8f;
    }

    public class MechPadBuild extends Building implements ControlBlock{

        public float progress, time, speedScl;
        public @Nullable BlockUnitc unit;

        @Override
        public Unit unit(){
            if(unit == null){
                unit = (BlockUnitc) UnitTypes.block.create(team);
                unit.tile(this);
            }
            return (Unit)unit;
        }

        public float fraction(){ return progress / buildTime; }

        @Override
        public void updateTile(){
            if(isControlled() && efficiency > 0.1f){
                unit.tile(this);
                time += edelta() * speedScl * Vars.state.rules.unitBuildSpeed(team);
                progress += edelta() * Vars.state.rules.unitBuildSpeed(team);
                speedScl = Mathf.lerpDelta(speedScl, 1f, 0.05f);
                unit.ammo(unit.type().ammoCapacity * fraction());
            }else{
                if(!isControlled()) { progress = 0f;}
            }

            if(progress >= buildTime && isControlled()) {
                progress %= 1f;

                Player player = unit.getPlayer();

                Fx.spawn.at(player);
                spawnSound.at(player);
                if(net.client() && player == Vars.player){
                    control.input.controlledType = null;
                }

                consume();
                player.clearUnit();
                player.deathTimer = Player.deathDelay + 1f;
                spawnMech(unitType, this.tile, player);
            }
        }

        @Override
        public boolean shouldConsume(){
            return isControlled();
        }

        @Override
        public void draw(){
            super.draw();

            if(isControlled() && efficiency > 0.001f){
                float polyRad = unitType.hitSize + 6f;
                float pos = Mathf.sin(time, polyRad,polyRad - 2f);
                TextureRegion full = unitType.fullIcon;
                float shadowSize = Math.max(full.height, full.width) / 2f;

                Draw.color(Pal.darkOutline);
                Lines.stroke(2f);
                Fill.poly(x, y, 4, polyRad);
                Draw.reset();

                Draw.color(0f, 0f, 0f, fraction());
                Draw.rect("circle-shadow", x, y, shadowSize, shadowSize);
                Draw.color();

                Draw.draw(Layer.blockOver, () -> {
                    Shaders.build.region = full;
                    Shaders.build.progress = fraction();
                    Shaders.build.color.set(Pal.accent);
                    Shaders.build.time = -time / 10f;

                    Draw.shader(Shaders.build, true);
                    Draw.rect(full, tile.drawx(), tile.drawy());
                    Draw.shader();
                });

                Draw.color(Pal.accent);
                Lines.lineAngleCenter(x + pos, y, 90, (polyRad - Math.abs(pos)) * 2f - 4f);

                Draw.color(Pal.accentBack);
                Lines.stroke(1.5f);
                Lines.poly(tile.drawx(), tile.drawy(), 4, polyRad - 2f);
                Draw.reset();
            } else if(potentialEfficiency > 0){
                Drawf.additive(glowRegion, Pal.accent, /*warmup * */(1f - 0.5f + Mathf.absin(Time.time, 8f, 0.5f)), x, y, 0f, Layer.blockAdditive);
            }
        }
    }
}