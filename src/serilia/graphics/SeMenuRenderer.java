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
    public PlanetParams params = new PlanetParams(){{
        camPos.set(0f, -1f, 4f);
        alwaysDrawAtmosphere = true;
        drawUi = false;
        planet = SeSystem.serilia;
        zoom = 0.3f;
    }};



    @Override
    public void render() {
        int size = Math.max(graphics.getWidth(), graphics.getHeight());

        if(buffer == null) buffer = new FrameBuffer(size, size);

        buffer.begin(Color.clear);

        params.camPos.rotate(Vec3.Y, 0.10f);
        params.camPos.rotate(Vec3.Y, -0.10f); //I don't know how, but it still moves with this. At least I can bypass the lower speed limit with it.

        renderer.planets.render(params);

        buffer.end();

        Draw.rect(Draw.wrap(buffer.getTexture()), (float) graphics.getWidth() / 2, (float) graphics.getHeight() / 2, graphics.getWidth(), graphics.getHeight());
    }
}