package serilia.graphics;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.FrameBuffer;
import arc.math.geom.Vec3;
import mindustry.graphics.MenuRenderer;
import mindustry.graphics.g3d.PlanetParams;
import serilia.content.SeSystem;

import static arc.Core.graphics;
import static mindustry.Vars.renderer;

/**Stolen from FOS, thanks slot*/
public class SeMenuRenderer extends MenuRenderer{
    public static FrameBuffer buffer;
    public PlanetParams params = new PlanetParams();

    @Override
    public void render() {
        int size = Math.max(graphics.getWidth(), graphics.getHeight());

        if(buffer == null) buffer = new FrameBuffer(size, size);

        buffer.begin(Color.clear);

        params.alwaysDrawAtmosphere = true;
        params.drawUi = false;
        params.planet = SeSystem.serilia;
        params.zoom = 0.3f;

        params.camPos.rotate(Vec3.Y, 0.03f);

        renderer.planets.render(params);

        buffer.end();

        Draw.rect(Draw.wrap(buffer.getTexture()), (float) graphics.getWidth() / 2, (float) graphics.getHeight() / 2, graphics.getWidth(), graphics.getHeight());
    }
}