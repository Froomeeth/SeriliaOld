package serilia.world.draw.drawblock;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Interp;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.world.draw.DrawBlock;
import serilia.world.blocks.production.DrawerDrill;

public class DrawSealedDust extends DrawBlock{
    public Color dustColor = Color.valueOf("f58349");

    public int particles = 10;
    public float particleLife = 120f, particleRad = 7f, particleSize = 2f;
    private float overLayerOffset = 0.02f;

    @Override
    public void draw(Building build){
        if(build.warmup() > 0f && dustColor.a > 0.001f){
            rand.setSeed(build.id);

            float base = (Time.time / particleLife);
            for(int i = 0; i < particles; i++){
                float z = Draw.z();
                float fin = (rand.random(1f) + base) % 1f, fout = 1f - fin;
                float angle = rand.random(360f);
                float len = particleRad * (fin < 0.5f ? Interp.circleOut.apply(fin * 2) : (Interp.circleOut.apply(fout * 2) * 1.2f) - 0.2f);

                if(fout - 0.1f > 0.01f){
                    Draw.z(fin > 0.5f ? z + overLayerOffset : z);
                    Draw.mixcol(build instanceof DrawerDrill.DrawerDrillBuild ? build.tile.floor().mapColor : dustColor, Color.black, fout - 0.8f);

                    Fill.poly(build.x + Angles.trnsx(angle, len), build.y + Angles.trnsy(angle, len), 12, particleSize * fout * build.warmup());

                    Draw.z(z);
                    Draw.mixcol();
                }
            }
        }
    }
}
