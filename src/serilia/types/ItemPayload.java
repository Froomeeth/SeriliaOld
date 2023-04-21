package serilia.types;

import mindustry.game.Team;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.BuildPayload;

public class ItemPayload extends BuildPayload{
    public PayloadItem payItem;

    public ItemPayload(Block block, Team team){
        super(block, team);
        this.payItem = (PayloadItem)block;
    }

    @Override
    public float size() {
        return payItem.paySize * 8;
    }

    public Item item(){
        return payItem.item;
    }
}
