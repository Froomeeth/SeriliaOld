package serilia.world.blocks.payload;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;
import serilia.util.SeUtil;

public class PayloadDuct extends PayloadBlock{
    public TextureRegion[][] regionLayers;
    public int[][] ductArrows = {
            {1, 1, 1, 0, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 2, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 0, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 2, 1, 1, 1}
    };
    public boolean duct = true;

    public PayloadDuct(String name) {
        super(name);
        update = true;
        acceptsPayload = true;
        outputsPayload = true;
        rotate = true;
        payloadSpeed = 2f;
    }

    @Override
    public void load(){
        super.load();
        if(duct) regionLayers = SeUtil.splitLayers(name + "-sheet", Math.max(region.height, region.width), 2);
    }

    public class PayDuctBuild extends PayloadBlockBuild<Payload>{
        public int tiling = 0;
        boolean out = false;
        Building next;

        @Override
        public void updateTile(){
            super.updateTile();
            if(payload == null) return;

            if(out){
                moveOutPayload();
            }else if(moveInPayload()) out = true;
        }

        @Override
        public boolean hasArrived(){
            return payVector.isZero(size * 4f);
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

        /*public boolean nextDuctMerge(int rotation){
            Building the = nearby(Geometry.d4x(rotation), Geometry.d4y(rotation));
            return Mathf.within(the.x - x, the.y - y, 4f);
        }*/

        @Override
        public void onProximityUpdate() {
            noSleep();
            next = front();

            tiling = 0;

            for(int i = 0; i < 4; i++){
                Building b = nearby(Geometry.d4x(i), Geometry.d4y(i));
                if(i == rotation || b != null && (b instanceof PayDuctBuild pay ? (pay.next == this && Mathf.within(pay.x - x, pay.y - y, 4f)) : b.block.outputsPayload)){
                    tiling |= (1 << i);
                }
            }
        }

        @Override
        public boolean canControlSelect(Unit unit){
            return false;
        }

        @Override
        public void draw(){ //todo draw
            Draw.z(Layer.blockUnder);
            Draw.rect(regionLayers[0][tiling], x, y, 0f);

            drawPayload();

            Draw.z(35.1f);
            //Draw.rect(regionLayers[1][tiling], x, y, 0f);
        }
    }
}
