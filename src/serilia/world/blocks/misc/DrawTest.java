package serilia.world.blocks.misc;

import arc.graphics.g2d.Draw;
import arc.math.geom.Position;
import arc.struct.Seq;
import mindustry.entities.UnitSorts;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.world.Block;
import serilia.gen.entities.Laser;
import serilia.world.blocks.power.LaserHolder;

/**Used for any dumb effects that don't belong to anything in particular, like the parallax*/
public class DrawTest extends Block{

    public DrawTest(String name) {
        super(name);
        update = true;
        size = 1;
        hasShadow = false;
    }

    public class DrawTestBuild extends Building  implements LaserHolder{
        public Seq<Laser> lasers = new Seq<>();

        @Override
        public void updateTile(){
            super.updateTile();
            if(lasers.size == 0) lasers.add(createLaser(x, y, this, 100f,  true, true, Team.derelict));

            lasers.each(laser -> {
                if(Units.bestTarget(Team.derelict, x, y, laser.length, e -> !e.dead(), b -> laser.ground, UnitSorts.closest) != null){
                    laser.updatePosition(x, y, Units.bestTarget(Team.derelict, x, y, laser.length, e -> !e.dead(), b -> laser.ground, UnitSorts.closest));
            }});

            lasers.each(Laser::update);
        }
        @Override
        public void drawTeam(){}

        @Override
        public void draw(){
            Draw.rect("serilia-silicon-furnace", x, y);
            lasers.each(Laser::draw);

            //parallax.drawFace(x + (size * 4f), y - (size * 4f), x - (size * 4f), y - (size * 4f), f);
            //parallax.drawFace(x + (size * 4f), y + (size * 4f), x + (size * 4f), y - (size * 4f), f);
            //parallax.drawFace(x - (size * 4f), y - (size * 4f), x - (size * 4f), y - (size * 4f), f);
            //parallax.drawFace(x - (size * 4f), y - (size * 4f), x - (size * 4f), y - (size * 4f), f);
        }

        @Override
        public void remove(){
            super.remove();
            lasers.each(Laser::remove);
        }

        @Override
        public Seq<Position> getAcceptors(){
            return null;
        }

        @Override
        public void acceptLaser(){

        }
    }
}
