package serilia.world.blocks.misc;

import mindustry.gen.Building;
import mindustry.world.Block;

/**Used for any dumb effects that don't belong to anything in particular, like the parallax*/
public class DrawTest extends Block{

    public DrawTest(String name) {
        super(name);
        update = true;
        size = 1;
        hasShadow = false;
    }

    public class DrawTestBuild extends Building{

        @Override
        public void drawTeam(){}

        @Override
        public void draw(){
            //parallax.drawFace(x + (size * 4f), y - (size * 4f), x - (size * 4f), y - (size * 4f), f);
            //parallax.drawFace(x + (size * 4f), y + (size * 4f), x + (size * 4f), y - (size * 4f), f);
            //parallax.drawFace(x - (size * 4f), y - (size * 4f), x - (size * 4f), y - (size * 4f), f);
            //parallax.drawFace(x - (size * 4f), y - (size * 4f), x - (size * 4f), y - (size * 4f), f);
        }

        @Override
        public void remove(){
            super.remove();
        }
    }
}
