package serilia.world.blocks.payload;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.Tmp;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.world.blocks.payloads.*;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

import static mindustry.Vars.tilesize;

public class PayDuctRouter extends PayloadBlock{
    public TextureRegion botRegion, dirRegion;
    public float dirPulseTime = 10f;

    public PayDuctRouter(String name) {
        super(name);
        update = true;
        acceptsPayload = true;
        outputsPayload = true;
        payloadSpeed = 2f;
        outputFacing = false;
        configurable = true;

        group = BlockGroup.payloads;
        envEnabled |= Env.space;
    }

    @Override
    public void load(){
        super.load();

        botRegion = Core.atlas.find(name + "-bottom");
        dirRegion = Core.atlas.find(name + "-dir");
    }

    public class PayDuctRouterBuild extends PayloadBlockBuild<Payload>{
        public @Nullable UnlockableContent sorted;
        public int dir = 0, recDir;
        public boolean matches, blocked;
        public @Nullable Building next;

        public void pickNext(Payload payload){
            int trns = this.block.size / 2 + 1;
            int rots = 0;

            dir = (dir + 1) % 4;
            next = this.tile.nearby(Geometry.d4(dir).x * trns, Geometry.d4(dir).y * trns).build;

            while((next == null || next.team != this.team || !(payload != null && next.acceptPayload(this, payload)) || dir == recDir) && rots < 5){
                dir = (dir + 1) % 4;
                rots++;
                if(rots == 5) Log.warn("cock and ball torture is a");
            }
        }

        /*public void checkMatch(){
            matches = sorted != null &&
                    (payload instanceof BuildPayload && ((BuildPayload)payload).block() == sorted) ||
                    (payload instanceof UnitPayload && ((UnitPayload)payload).unit.type == sorted);
        }*/

        @Override
        public void handlePayload(Building source, Payload payload){
            super.handlePayload(source, payload);
            recDir = source == null ? rotation : source.relativeTo(this);

            //checkMatch();
        }

        @Override
        public void updateTile(){
            if(payload == null) return;
            super.updateTile();

            moveOutPayload();
        }

        @Override
        public void moveOutPayload(){
            if(payload == null) return;

            updatePayload();

            Vec2 dest = Tmp.v1.trns(dir, size * tilesize/2f);

            payRotation = Angles.moveToward(payRotation, rotdeg(), payloadRotateSpeed * delta());
            payVector.approach(dest, payloadSpeed * delta());

            boolean canMove = next != null && (next.block.outputsPayload || next.block.acceptsPayload);

            if(payVector.within(dest, 0.001f)){
                payVector.clamp(-size * tilesize / 2f, -size * tilesize / 2f, size * tilesize / 2f, size * tilesize / 2f);

                if(movePayload(payload)){
                    payload = null;
                }
            }
        }

        @Override
        public boolean movePayload(Payload todump){

            if (next != null && next.team == this.team && next.acceptPayload(this, todump)) {
                next.handlePayload(this, todump);
                pickNext(payload);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void draw(){
            Draw.rect(botRegion, x, y);
            drawPayload();
            Draw.rect(region, x, y);

            Draw.z(35.1f); Draw.color(team.color, 1f); Draw.blend(Blending.additive);
            Draw.rect(dirRegion, x, y, dir * 90f);
            Draw.reset(); Draw.blend();
        }
    }
}
