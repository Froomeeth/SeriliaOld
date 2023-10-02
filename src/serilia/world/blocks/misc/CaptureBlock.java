package serilia.world.blocks.misc;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;

import static mindustry.Vars.net;

public class CaptureBlock extends Block{
    public float captureTime = 60f * 60f, captureRadius = 20f * 8f, warmupSpeed = 0.019f, idleProgressDecrease = 0.5f;
    /**Explodes if null.*/
    public Block capturedReplace;
    /**May be left null if just the capture part is needed.*/
    public Seq<UnlockableContent> unlocks;
    public boolean gradualUnlock = false;
    public DrawBlock drawer = new DrawDefault();

    public CaptureBlock(String name){
        super(name);
        update = solid = true;
    }

    @Override
    public void load(){
        super.load();
        drawer.load(this);
    }

    public class CaptureBuild extends Building{
        public float progress, warmup, totalProgress, capturingFrac;
        private final Seq<Player> players = new Seq<>();
        private final Color teamColor = team().color.cpy();

        @Override
        public void updateTile(){

            if(playerCheck()){
                warmup = Mathf.approachDelta(warmup, 1, warmupSpeed);
                progress += capturingFrac * warmup * Time.delta;

                if(unlocks != null && gradualUnlock){
                    int unlock = Mathf.floor((captureTime / progress) * unlocks.size);
                    if(unlock > 0) unlocks.get(unlock - 1).unlock();
                }
            }else{
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
                progress = Mathf.approachDelta(progress, 0f, idleProgressDecrease);
            }

            totalProgress += Time.delta;

            if(progress >= captureTime){
                if(unlocks != null) unlocks.each(UnlockableContent::unlock);
                kill();
            }
        }

        public boolean playerCheck(){
            if(!(net.active())){
                capturingFrac = 1;
                return Vars.player.unit().isEnemy() && Vars.player.unit().within(this, captureRadius);
            }

            players.clear();
            Groups.player.copy(players);
            players.filter((Player p) -> p.team() != team); //todo ctf mechanics for user made PvP maps? currently lets *all* enemies collectively cap

            int capturing = 0;

            for(int i = 0; i < players.size; i++){
                Unit unit = players.get(i).unit();
                capturing += unit != null && unit.within(this, captureRadius) ? 1 : 0;
            }

            capturingFrac = (float)players.size / capturing;

            return capturing > 0;
        }

        @Override
        public void draw(){
            Drawf.dashCircle(x, y, captureRadius, teamColor.a(capturingFrac * warmup));
            drawer.draw(this);

            float oy = -7f, len = 6f * progress;
            Lines.stroke(5f);
            Draw.color(Pal.darkMetal);
            Lines.line(tile.drawx() - len, tile.drawy() + oy, tile.drawx() + len, tile.drawy() + oy);
            for(int i : Mathf.signs){
                Fill.tri(tile.drawx() + len * i, tile.drawy() + oy - Lines.getStroke()/2f, tile.drawx() + len * i, tile.drawy() + oy + Lines.getStroke()/2f, tile.drawx() + (len + Lines.getStroke() * progress) * i, tile.drawy() + oy);
            }

            Lines.stroke(3f);
            Draw.color(Pal.accent);
            Lines.line(tile.drawx() - len, tile.drawy() + oy, tile.drawx() - len + len*2 * progress, tile.drawy() + oy);
            for(int i : Mathf.signs){
                Fill.tri(tile.drawx() + len * i, tile.drawy() + oy - Lines.getStroke()/2f, tile.drawx() + len * i, tile.drawy() + oy + Lines.getStroke()/2f, tile.drawx() + (len + Lines.getStroke() * progress) * i, tile.drawy() + oy);
            }
            Draw.reset();
        }

        @Override
        public float totalProgress(){
            return totalProgress;
        }

        @Override
        public float warmup(){
            return warmup;
        }
    }
}
