package serilia.world.draw.drawblock;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.Building;
import mindustry.world.draw.DrawBlock;

public class DrawTeam extends DrawBlock {
    public TextureRegion[] teamRegions; //todo
    public TextureRegion teamRegion;
    public String suffix = "-team";

    @Override
    public void draw(Building build) {
        if (build.block.teamRegion.found()) {
            if (build.block.teamRegions[build.team.id] == build.block.teamRegion) {
                Draw.color(build.team.color);
            }

            Draw.rect(build.block.teamRegions[build.team.id], build.x, build.y);
            Draw.color();
        }
    }
}
