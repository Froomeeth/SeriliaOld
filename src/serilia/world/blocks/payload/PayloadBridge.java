package serilia.world.blocks.payload;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.geom.Geometry;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.world.Tile;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

public class PayloadBridge extends PayloadBlock{
    public int range = 6;

    public PayloadBridge(String name) {
        super(name);
        update = true;
        acceptsPayload = true;
        outputsPayload = true;
        rotate = true;
        payloadSpeed = 2f;

        group = BlockGroup.payloads;
        envEnabled |= Env.space;
    }

    public class PayBridgeBuild extends PayloadBlockBuild<Payload>{
        public Building[] occupied = new Building[4];
        public PayBridgeBuild link = findLink();

        @Override
        public void updateTile() {
            if (payload != null){
                if(moveInPayload()) {
                    if (link != null && link.acceptPayload(this, payload)) {
                        link.occupied[rotation % 4] = this;
                        link.handlePayload(this, payload);

                    } else moveOutPayload();
                }
            }
        }

        PayBridgeBuild findLink() {
            for (int i = 1; i <= range; i++) {
                Tile other = tile.nearby(Geometry.d4x(rotation) * i, Geometry.d4y(rotation) * i);
                if (other != null && other.build instanceof PayBridgeBuild && other.build.block == PayloadBridge.this && other.build.team == team) {
                    return (PayBridgeBuild)other.build;
                }
            }
            return null;
        }

        @Override
        public void handlePayload(Building source, Payload payload){
            this.payload = payload;
            this.payRotation = payload.rotation();

            updatePayload();
        }

        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();

            link = findLink();
        }

        @Override
        public void draw(){
            if(link == null) return;
            Draw.z(Layer.blockAdditive);
            Lines.stroke(3f, Color.red);
            Lines.line(x, y, link.x, link.y);
        }

        @Override
        public void drawSelect(){
            /*drawPlace(tile.x, tile.y, rotation, true);
            //draw incoming bridges
            for(int dir = 0; dir < 4; dir++){
                if(dir != rotation){
                    int dx = Geometry.d4x(dir), dy = Geometry.d4y(dir);
                    Building found = occupied[(dir + 2) % 4];

                    if(found != null){
                        int length = Math.max(Math.abs(found.tileX() - tileX()), Math.abs(found.tileY() - tileY()));
                        Drawf.dashLine(Pal.place,
                                found.x - dx * (tilesize / 2f + 2),
                                found.y - dy * (tilesize / 2f + 2),
                                found.x - dx * (length) * tilesize,
                                found.y - dy * (length) * tilesize
                        );

                        Drawf.square(found.x, found.y, 2f, 45f, Pal.place);
                    }
                }
            }*/
        }
    }
}
