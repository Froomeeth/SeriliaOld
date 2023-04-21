package serilia.world.blocks.payload;

import arc.Core;
import arc.graphics.g2d.Draw;
import mindustry.gen.Building;
import mindustry.world.blocks.payloads.*;
import mindustry.world.blocks.storage.CoreBlock;
import serilia.types.ItemPayload;
import serilia.types.PayloadItem;

@SuppressWarnings("unused")
public class ComponentReceiver extends PayloadBlock{
    public float maxSize = 2;

    public ComponentReceiver(String name) {
        super(name);
        acceptsPayload = true;
        outputsPayload = false;
        rotate = true;
    }

    public class ComponentReceiverBuild extends PayloadBlockBuild<BuildPayload>{
        public CoreBlock.CoreBuild linkedCore = null;

        @Override
        public boolean acceptPayload(Building source, Payload payload){ //todo lock input to backside
            return super.acceptPayload(source, payload) && linkedCore != null &&
                payload.fits(maxSize) &&
                (
                    (payload instanceof ItemPayload && ((ItemPayload)payload).item() != null) && linkedCore.acceptItem(source, ((ItemPayload)payload).item()) ||
                    (payload instanceof BuildPayload && ((PayloadItem)((BuildPayload)payload).build.block).item != null  && linkedCore.acceptItem(source, ((PayloadItem)((BuildPayload)payload).build.block).item))
                );
        }

        @Override
        public void updateTile() {
            super.updateTile();

            if(linkedCore != null && moveInPayload()){
                payload = null;
            }
        }

        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();
            if(linkedCore == null) {
                proximity.each(t -> {
                    if (t instanceof CoreBlock.CoreBuild) {
                        linkedCore = (CoreBlock.CoreBuild) t;
                    }
                });
            } else if(linkedCore.tile.build == null) linkedCore = null;
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);
            Draw.rect(inRegion, x, y, rotdeg());

            drawPayload();

            Draw.rect(topRegion, x, y);
            if (block.teamRegions[team.id] == block.teamRegion) Draw.color(team.color);
            Draw.rect(block.teamRegions[team.id], x, y);
            Draw.rect(rotation >= 2 ?
                    Core.atlas.find(name+"-out2-"+team.name, Core.atlas.find(name+"-out2")) :
                    Core.atlas.find(name+"-out1-"+team.name, Core.atlas.find(name+"-out1")),
                    x, y, size * (rotation % 2 != 0 ? -8f : 8f), size * (rotation % 2 != 0 ? -8f : 8f), rotdeg());
            Draw.color();
        }
    }
}
