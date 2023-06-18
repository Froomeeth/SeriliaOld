package serilia.world.blocks.payload;

import arc.util.Nullable;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.world.blocks.payloads.*;

public class PayDuctRouter extends PayloadDuct{

    public PayDuctRouter(String name){
        super(name);
    }

    public class PayDuctRouterBuild extends PayDuctBuild{
        public @Nullable UnlockableContent sorted;
        public int recDir;
        public boolean matches, blocked;
        public float[] blinkDurations = {0, 0, 0, 0};

        public void pickNext(){
            if(payload != null){
                if(matches){
                    //when the item matches, always move forward.
                    rotation = recDir;
                    onProximityUpdate();
                }else{
                    int rotations = 0;
                    do{
                        rotation = (rotation + 1) % 4;
                        //if it doesn't match the sort item and this router is facing forward, skip this rotation
                        if(!matches && sorted != null && rotation == recDir){
                            rotation ++;
                        }
                        onProximityUpdate();
                    }while((blocked || next == null) && ++rotations < 4);
                }
            }else{
                onProximityUpdate();
            }
        }

        @Override
        public void handlePayload(Building source, Payload payload){
            super.handlePayload(source, payload);
            checkMatch();
            pickNext();
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
            blocked = (next != null && next.block.solid && !(next.block().outputsPayload || next.block().acceptsPayload)) || (this.next != null && this.next.payloadCheck(rotation));
        }

        public void checkMatch(){
            matches = sorted != null &&
                    (payload instanceof BuildPayload build && build.block() == sorted) ||
                    (payload instanceof UnitPayload unit && unit.unit.type == sorted);
        }
    }
}
