package serilia.util;

import arc.Core;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Vec2;
import arc.util.Nullable;

import static arc.graphics.g2d.Fill.*;
import static arc.util.Tmp.*;
import static serilia.util.SeUtil.quadHelper;
import static serilia.util.SeUtil.vecSetLine;

/**Closs containing everything needed to make an illusion of height.*/
public class Parallax{
    public void drawSkyLines(float x, float y, int lineCount, float radius, float height, float rot){
        for(int i = 0; i < lineCount; i++){
            v1.set(1f, 1f); //line start
            v2.set(Core.camera.position);

            vecSetLine(v1, x, y, i * (360f / lineCount) + rot, radius);

            Lines.lineAngle(v1.x, v1.y, v2.sub(v1.x, v1.y).angle() + 180f, v2.dst(0f, 0f) * height);
        }
    }

    public Vec2 parallax(float x, float y, float height){
        return parallax(x, y, height, false);
    }
    public Vec2 parallax(float x, float y, float height, boolean ignoreCamDst){ //todo shadows
        v1.set(1f, 1f);
        v2.set(Core.camera.position);

        vecSetLine(v1, x, y, v2.sub(x, y).angle() + 180f, ignoreCamDst ? height : height * v2.dst(0f, 0f));

        return v1;
    }

    public void drawFace(float x1, float y1, float x2, float y2, @Nullable TextureRegion tex){
        v1.set(parallax(x1, y1, 100f));
        v2.set(parallax(x2, y2, 100f));

        quadHelper(x1, y1, x2, y2, v1.x, v1.y, v2.x, v2.y);
    };

    public void drawOmegaUltraGigaChadDeathRay(float x, float y, float radius, float height, float upScl){
        v1.set(parallax(x, y, height, false));
        v2.set(Core.camera.position);

        poly(x, y, 48, radius);
        //Fill.poly(v1.x, v1.y, 48, radius * upScl);

        vecSetLine(v3, x, y, v2.sub(x, y).angle() - 90f, radius       ); v2.set(Core.camera.position);
        vecSetLine(v4, x, y, v2.sub(x, y).angle() + 90f, radius       ); v2.set(Core.camera.position);
        vecSetLine(v5, v1  , v2.sub(x, y).angle() + 90f, radius * upScl); v2.set(Core.camera.position);
        vecSetLine(v6, v1  , v2.sub(x, y).angle() - 90f, radius * upScl);



        quadHelper(v3.x, v3.y, v4.x, v4.y, v5.x, v5.y, v6.x, v6.y);
    }
}
