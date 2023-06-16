package serilia.world.blocks.payload;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.struct.Seq;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.Autotiler;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

import static mindustry.Vars.tilesize;

public class PayloadDuct extends PayloadBlock implements Autotiler{
    public TextureRegion[] topRegions;
    public TextureRegion[] botRegions;
    public Color transparentColor = new Color(0.4f, 0.4f, 0.4f, 0.1f);

    public PayloadDuct(String name) {
        super(name);
        update = true;
        acceptsPayload = true;
        outputsPayload = true;
        rotate = true;
        payloadSpeed = 2f;

        group = BlockGroup.payloads;
        envEnabled |= Env.space;
    }

    @Override
    public void load(){
        super.load();
        Seq<TextureRegion> reg = new Seq<>();

        for(int i = 1; i < 6; i++){
            reg.add(Core.atlas.find(name + "-top-" + i));
        } topRegions = reg.toArray(TextureRegion.class); reg.clear();

        for(int i = 1; i < 6; i++){
            reg.add(Core.atlas.find(name + "-bottom-" + i));
        } botRegions = reg.toArray(TextureRegion.class);
    }

    public static boolean blends(Building build, int direction){
        int size = build.block.size;
        int trns = build.block.size/2 + 1;
        Building accept = build.nearby(Geometry.d4(direction).x * trns, Geometry.d4(direction).y * trns);
        return accept != null &&
                accept.block.outputsPayload &&

                //if size is the same, block must either be facing this one, or not be rotating
                ((accept.block.size == size
                        && Math.abs(accept.tileX() - build.tileX()) % size == 0 //check alignment
                        && Math.abs(accept.tileY() - build.tileY()) % size == 0
                        && ((accept.block.rotate && accept.tileX() + Geometry.d4(accept.rotation).x * size == build.tileX() && accept.tileY() + Geometry.d4(accept.rotation).y * size == build.tileY())
                        || !accept.block.rotate
                        || !accept.block.outputFacing)) ||

                        //if the other block is smaller, check alignment
                        (accept.block.size != size &&
                                (accept.rotation % 2 == 0 ? //check orientation; make sure it's aligned properly with this block.
                                        Math.abs(accept.y - build.y) <= Math.abs(size * tilesize - accept.block.size * tilesize)/2f : //check Y alignment
                                        Math.abs(accept.x - build.x) <= Math.abs(size * tilesize - accept.block.size * tilesize)/2f   //check X alignment
                                )) && (!accept.block.rotate || accept.front() == build || !accept.block.outputFacing) //make sure it's facing this block
                );
    }

    @Override
    public boolean blends(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock){
        return (otherblock.acceptsPayload || otherblock.outputsPayload) && /*lookingAt(tile, rotation, otherx, othery, otherblock) &&*/ lookingAtEither(tile, rotation, otherx, othery, otherrot, otherblock)
                ;
    }

    public class PayDuctBuild extends PayloadBlockBuild<Payload>{
        boolean out = false;
        public int blendbits, xscl, yscl, blending;
        Building next, nextc;

        @Override
        public void updateTile(){
            super.updateTile();

            if(payload == null) return;

            if(out){
                moveOutPayload();
            }else if(moveInPayload()) out = true;
        }

        @Override
        public void draw(){
            float rotation = rotdeg();
            int r = this.rotation;

            drawPayload();

            Draw.scl(xscl, yscl);
            drawAt(x, y, blendbits, rotation, Autotiler.SliceMode.none);
            Draw.reset();
        }

        protected void drawAt(float x, float y, int bits, float rotation, SliceMode slice){
            Draw.z(Layer.blockUnder);
            Draw.rect(sliced(botRegions[bits], slice), x, y, rotation);

            Draw.color(transparentColor);
            Draw.rect(sliced(botRegions[bits], slice), x, y, rotation);
            Draw.color();
            Draw.z(35f + 0.2f);
            Draw.rect(sliced(topRegions[bits], slice), x, y, rotation);
        }

        @Override
        public boolean hasArrived(){
            return payVector.isZero(size * 8f / 2f);
        }

        @Override
        public boolean movePayload(Payload todump){
            if (next != null && next.team == this.team && next.acceptPayload(this, todump)) {
                next.handlePayload(this, todump);
                out = false; //added just this
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();

            int[] bits = buildBlending(tile, rotation, null, true);
            blendbits = bits[0];
            xscl = bits[1];
            yscl = bits[2];
            blending = bits[4];
            next = front();
            nextc = next instanceof PayDuctBuild ? next : null;
        }

        @Override
        public boolean canControlSelect(Unit unit){
            return false;
        }
    }
}
