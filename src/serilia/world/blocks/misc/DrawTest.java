package serilia.world.blocks.misc;

import arc.graphics.g2d.TextureRegion;
import mindustry.gen.Building;
import mindustry.world.Block;
import serilia.SeriliaMain;

import static arc.Core.atlas;
import static serilia.SeriliaMain.parallax;

/**Used for any dumb effects that don't belong to anything in particular, like the paralax*/
public class DrawTest extends Block{
    public TextureRegion f = atlas.find("beryllic-stone1");

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
            parallax.drawFace(x + (size * 4f), y - (size * 4f), x - (size * 4f), y - (size * 4f), f);
            //parallax.drawFace(x + (size * 4f), y + (size * 4f), x + (size * 4f), y - (size * 4f), f);
            //parallax.drawFace(x - (size * 4f), y - (size * 4f), x - (size * 4f), y - (size * 4f), f);
            //parallax.drawFace(x - (size * 4f), y - (size * 4f), x - (size * 4f), y - (size * 4f), f);

        }
    }
}
