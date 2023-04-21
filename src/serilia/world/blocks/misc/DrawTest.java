package serilia.world.blocks.misc;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.graphics.Shaders;
import mindustry.world.Block;

import static arc.Core.atlas;

/**Used for any dumb effects that don't belong to anything in particular, like the paralax*/
public class DrawTest extends Block{

    public DrawTest(String name) {
        super(name);
        update = true;
        size = 2;
        hasShadow = false;
    }

    public class DrawTestBuild extends Building{
        @Override
        public void drawTeam(){}

        @Override
        public void draw(){
        }
    }
}
