package serilia.world.blocks.distribution;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.util.Nullable;
import arc.util.Tmp;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.type.Item;
import mindustry.world.blocks.distribution.Duct;

import static mindustry.Vars.itemSize;
import static mindustry.Vars.tilesize;

/**Routes through rotation like a payload conveyor. heehoo rainer world ref*/
public class RotRouter extends Duct{
    public RotRouter(String name){
        super(name);
    }

    public class RotRouterBuild extends DuctBuild{
        public @Nullable Item sorted;
        public int recDir;
        public boolean matches, blocked;

        public void pickNext(){
            if(current != null){
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
        public void handleItem(Building source, Item item){
            super.handleItem(source, item);
            matches = sorted != null && current == sorted;
            pickNext();
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
            blocked = next == null || !(next.acceptItem(this, current));
        }

        @Override
        public void draw(){
            //draw item
            if(current != null){
                Draw.z(Layer.blockUnder + 0.1f);
                Tmp.v1.set(Geometry.d4x(recDir) * tilesize / 2f, Geometry.d4y(recDir) * tilesize / 2f)
                        .lerp(Geometry.d4x(rotation) * tilesize / 2f, Geometry.d4y(rotation) * tilesize / 2f,
                                Mathf.clamp((progress + 1f) / 2f));

                Draw.rect(current.fullIcon, x + Tmp.v1.x, y + Tmp.v1.y, itemSize, itemSize);
            }
        }
    }
}
