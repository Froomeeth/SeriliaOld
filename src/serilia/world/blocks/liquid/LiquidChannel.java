package serilia.world.blocks.liquid;

import arc.func.Boolf;
import arc.func.Cons;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.IntSet;
import arc.struct.Queue;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.Tmp;
import arc.util.io.Writes;
import mindustry.content.Blocks;
import mindustry.entities.TargetPriority;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.input.Placement;
import mindustry.logic.LAccess;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.ChainedBuilding;
import mindustry.world.blocks.distribution.DirectionBridge;
import mindustry.world.blocks.distribution.ItemBridge;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.liquid.LiquidJunction;
import mindustry.world.modules.LiquidModule;
import serilia.gen.entities.LiquidGraphUpdater;

public class LiquidChannel extends Block{
    static final boolean debugGraphs = false;
    static final float mergeThreshold = 0.2f;
    static final float rotatePad = 6, hpad = rotatePad / 2f / 4f;
    static final float[][] rotateOffsets = {{hpad, hpad}, {-hpad, hpad}, {-hpad, -hpad}, {hpad, -hpad}};
    static final LiquidModule tempLiquids = new LiquidModule();

    public final int timerFlow = timers++;

    //indices: [rotation] [fluid type] [frame]
    public TextureRegion[][][] rotateRegions;

    public boolean leaks = true;
    public @Nullable Block junctionReplacement, bridgeReplacement, rotBridgeReplacement;

    public LiquidChannel(String name){
        super(name);
        rotate = true;
        solid = false;
        floating = true;
        underBullets = true;
        conveyorPlacement = true;
        noUpdateDisabled = true;
        canOverdrive = false;
        priority = TargetPriority.transport;

        //conduits don't need to update
        update = false;
        destructible = true;
    }

    @Override
    public void init(){
        super.init();

        if(junctionReplacement == null) junctionReplacement = Blocks.liquidJunction;
        if(bridgeReplacement == null || !(bridgeReplacement instanceof ItemBridge)) bridgeReplacement = Blocks.bridgeConduit;
    }

    @Override
    public void load(){
        super.load();
    }

    @Override
    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list){

    }

    @Override
    public Block getReplacement(BuildPlan req, Seq<BuildPlan> plans){
        if(junctionReplacement == null) return this;

        Boolf<Point2> cont = p -> plans.contains(o -> o.x == req.x + p.x && o.y == req.y + p.y && o.rotation == req.rotation && (req.block instanceof Conduit || req.block instanceof LiquidJunction));
        return cont.get(Geometry.d4(req.rotation)) &&
                cont.get(Geometry.d4(req.rotation - 2)) &&
                req.tile() != null &&
                req.tile().block() instanceof Conduit &&
                Mathf.mod(req.build().rotation - req.rotation, 2) == 1 ? junctionReplacement : this;
    }

    @Override
    public void handlePlacementLine(Seq<BuildPlan> plans){
        if(bridgeReplacement == null) return;

        if(rotBridgeReplacement instanceof DirectionBridge duct){
            Placement.calculateBridges(plans, duct, true, b -> b instanceof Conduit);
        }else{
            Placement.calculateBridges(plans, (ItemBridge)bridgeReplacement);
        }
    }

    public class ChannelBuild extends Building implements ChainedBuilding{
        public @Nullable LiquidGraph graph;

        public float smoothLiquid;
        public int blendbits, xscl = 1, yscl = 1, blending;
        public boolean capped, backCapped = false;

        protected void addGraphs(){
            //connect self to every nearby graph
            getConnections(other -> {
                if(other.graph != null){
                    other.graph.merge(this);
                }
            });

            //nothing to connect to
            if(graph == null){
                new LiquidGraph().merge(this);
            }
        }

/*        protected void removeGraphs(){
            //graph is getting recalculated, no longer valid
            if(graph != null){
                graph.checkRemove();
                graph.remove(this);
                graph = null; //TODO ?????
            }

            getConnections(other -> new LiquidGraph().reflow(this, other));
        }*/

        @Override
        public void control(LAccess type, double p1, double p2, double p3, double p4){
            if(type == LAccess.enabled){
                boolean shouldEnable = !Mathf.zero((float)p1);
                if(enabled != shouldEnable){

                    if(graph != null){
                        //keep track of how many conduits are disabled, so the graph can stop working
                        if(shouldEnable){
                            graph.disabledConduits --;
                        }else{
                            graph.disabledConduits ++;
                        }
                    }

                    enabled = shouldEnable;
                }
            }
        }

        @Override
        public void onProximityAdded(){
            super.onProximityAdded();
            addGraphs();
        }

/*        @Override
        public void onProximityRemoved(){
            super.onProximityRemoved();

            removeGraphs();
        }*/

        @Override
        public void draw(){
            Draw.z(Layer.block);
            Draw.rect(fullIcon, x, y, 0f);

            if(debugGraphs){
                //simple visualization that assigns random color to each graph
                Mathf.rand.setSeed(graph == null ? -1 : graph.id);
                Draw.color(Tmp.c1.rand());

                Drawf.selected(tileX(), tileY(), block, Tmp.c1);
                Draw.color(Pal.accent);

                if(this == graph.head){
                    Fill.poly(x, y, 3, 2f, rotdeg());
                }

                Draw.color();
            }
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            noSleep();
            return (liquids.current() == liquid || liquids.currentAmount() < 0.2f)
                    && (tile == null || (source.relativeTo(tile.x, tile.y) + 2) % 4 != rotation);
        }

/*        @Override
        public LiquidModule writeLiquids(){
            //"saved" liquids are based on a fraction, essentially splitting apart and re-joining
            tempLiquids.set(liquids, graph == null ? 1f : block.liquidCapacity / graph.totalCapacity);
            return tempLiquids;
        }

        @Override
        public float liquidCapacity(){
            return graph == null ? block.liquidCapacity : graph.totalCapacity;
        }*/

        @Nullable
        @Override
        public Building next(){
            Tile next = tile.nearby(rotation);
            if(next != null && next.build instanceof ChannelBuild){
                return next.build;
            }
            return null;
        }

        @Override
        public void writeAll(Writes write){
            super.writeAll(write);
        }

        //Calls callback with every conduit that transfers fluids to this one.
        public void getConnections(Cons<ChannelBuild> cons){
            for(var other : proximity){
                if(canMerge(other)){
                    cons.get((ChannelBuild)other);
                }
            }
        }

        public boolean canMerge(Building other){
            return
                    other instanceof ChannelBuild conduit && other.team == team &&
                            (front() == conduit || other.front() == this) &&
                            (other.liquids.current() == liquids.current() || other.liquids.currentAmount() <= mergeThreshold || liquids.currentAmount() <= mergeThreshold);
        }
    }

    public static class LiquidGraph{
        private static final IntSet closedSet = new IntSet(), headSet = new IntSet();
        private static final Queue<ChannelBuild> queue = new Queue<>();

        static int lastId = 0;

        public final int id = lastId ++;
        public float smoothLiquid;

        //if any are disabled, does not update
        private int disabledConduits;
        private Seq<ChannelBuild> conduits = new Seq<>();
        private final @Nullable
        LiquidGraphUpdater entity;
        private LiquidModule liquids = new LiquidModule();
        private float totalCapacity;

        public @Nullable ChannelBuild head;

        public LiquidGraph(){
            entity = LiquidGraphUpdater.create();
            //entity.graph = this;
        }

        public void update(){
            smoothLiquid = Mathf.lerpDelta(smoothLiquid, liquids.currentAmount() / totalCapacity, 0.05f);

            if(disabledConduits > 0) return;

            if(head != null){

                //move forward as the head
                if(liquids.currentAmount() > 0.0001f && head.timer(((Conduit)head.block).timerFlow, 1)){
                    head.moveLiquidForward(((Conduit)head.block).leaks, liquids.current());
                }

                //merge with front if one of the conduits becomes empty
                if(head.front() instanceof ChannelBuild build && build.graph != this && head.canMerge(build)){
                    merge(build);
                }
            }
        }

        public void checkAdd(){
            if(entity != null) entity.add();
        }

        public void checkRemove(){
            if(entity != null) entity.remove();
        }

/*        public void remove(ChannelBuild build){
            float fraction = build.block.liquidCapacity / totalCapacity;
            //remove fraction of liquids based on what part this conduit constituted
            //e.g. 70% of capacity was made up by this conduit = multiply liquids by 0.3 (remove 70%)
            liquids.mul(1f - fraction);

            totalCapacity -= build.block.liquidCapacity;
        }*/

        public void reflow(@Nullable ChannelBuild ignore, ChannelBuild conduit){
            closedSet.clear();
            queue.clear();

            //ignore the starting point and don't add it, as it is being removed
            if(ignore != null) closedSet.add(ignore.id);

            closedSet.add(conduit.id);
            queue.add(conduit);


            while(queue.size > 0){
                var parent = queue.removeFirst();
                assign(parent, ignore);

                parent.getConnections(child -> {
                    if(closedSet.add(child.id)){
                        queue.addLast(child);
                    }
                });
            }

            closedSet.clear();
            queue.clear();
        }

        public void merge(ChannelBuild other){
            if(other.graph == this) return;

            if(other.graph != null){

                //merge graphs - TODO - flip if it is larger, like power graphs?
                for(var cond : other.graph.conduits){
                    assign(cond);
                }
            }else{
                assign(other);
            }
        }

        protected void assign(ChannelBuild build){
            assign(build, null);
        }

        protected void assign(ChannelBuild build, @Nullable Building ignore){
            if(build.graph != this){

/*                //merge graph liquids - TODO - how does this react to different types
                if(build.graph != null){
                    build.graph.checkRemove();

                    //add liquids based on what fraction it made up
                    liquids.add(build.liquids, build.block.liquidCapacity / build.graph.totalCapacity);
                }else{
                    //simple direct liquid merge
                    liquids.add(build.liquids);
                }*/

                totalCapacity += build.block.liquidCapacity;
                build.graph = this;
                build.liquids = liquids;
                conduits.add(build);
                checkAdd();

                //re-validate head
                if(head == null){
                    head = build;
                }

                //find the best head block
                headSet.clear();
                headSet.add(head.id);

                while(true){
                    var next = head.front();

                    if(next instanceof ChannelBuild cond && cond.team == head.team && next != ignore && cond.graph == this){
                        if(!headSet.add(next.id)){
                            //there's a loop, which means a head does not exist
                            head = null;
                            break;
                        }else{
                            head = cond;
                        }
                    }else{
                        //found the end
                        break;
                    }
                }

                //snap smoothLiquid so it doesn't start at 0
                smoothLiquid = liquids.currentAmount() / totalCapacity;
            }
        }
    }
}
