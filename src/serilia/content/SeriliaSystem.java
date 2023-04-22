package serilia.content;

import arc.graphics.Color;
import mindustry.content.Planets;
import mindustry.game.Team;
import mindustry.graphics.Pal;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.SunMesh;
import mindustry.type.Planet;
import mindustry.world.meta.Env;
import serilia.AhkarPlanetGenerator;

import static mindustry.content.Blocks.*;

public class SeriliaSystem {
    public static Planet serilia, ahkar; //TODO asteroids

    public static void load(){
        serilia = new Planet("serilia", Planets.sun, 64f*8f, 3){{
            bloom = true;
            accessible = false;
            drawOrbit = false;

            meshLoader = () -> new SunMesh(
                    this, 7,
                    5, 0.3, 1.7, 1.2, 1,
                    1.1f,
                    Color.valueOf("ff3b38"),
                    Color.valueOf("ff5738"),
                    Color.valueOf("ff5738"),
                    Color.valueOf("ff8d4c"),
                    Color.valueOf("ffe371"),
                    Color.valueOf("ffb671")
            );
        }};

        ahkar = new Planet("ahkar", serilia, 0.6f, 2){{
            generator = new AhkarPlanetGenerator();
            meshLoader = () -> new HexMesh(this, 5);
            alwaysUnlocked = true;
            landCloudColor = Pal.darkishGray.cpy().a(0f);
            hasAtmosphere = false;
            defaultEnv = Env.terrestrial;
            startSector = 1;
            atmosphereRadIn = 0f;
            atmosphereRadOut = 0f;
            tidalLock = false;
            orbitSpacing = 2f;
            totalRadius = 0.65f;
            lightSrcTo = 0.5f;
            lightDstFrom = 0.2f;
            clearSectorOnLose = true;
            defaultCore = coreAcropolis;
            iconColor = beryllicStone.mapColor;
            //hiddenItems.addAll(Items.serpuloItems).addAll(Items.erekirItems).removeAll(HResources.ahkarItems);

            ruleSetter = r -> {
                r.waveTeam = Team.green;
                r.placeRangeCheck = false;
                //r.attributes.set(Attribute.heat, 0.8f);
                r.showSpawns = true;
                //r.fog = true;
                //r.staticFog = true;
                r.lighting = false;
                r.coreDestroyClear = true;
                r.onlyDepositCore = true;
                r.infiniteResources = true;
            };

            unlockedOnLand.add(coreBastion);
        }};
    }
}
