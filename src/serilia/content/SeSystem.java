package serilia.content;

import arc.graphics.Color;
import mindustry.content.Planets;
import mindustry.game.Team;
import mindustry.graphics.Pal;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.SunMesh;
import mindustry.maps.planet.SerpuloPlanetGenerator;
import mindustry.type.Planet;
import mindustry.world.meta.Env;
import serilia.AhkarPlanetGenerator;

import static mindustry.content.Blocks.*;

public class SeSystem {
    public static Planet serilia, caliterra, ahkar, rogueParent, rogue; //TODO asteroids

    /**Due to skybox issues, keep orbit radii below 150, or some of the system may disappear when not focussed.*/
    public static void load(){
        serilia = new Planet("serilia", Planets.sun, 33f, 3){{
            bloom = true;
            //accessible = false;
            drawOrbit = false;
            orbitRadius = 1000f;
            lightColor = Color.valueOf("ff5738");

            meshLoader = () -> new SunMesh(
                    this, 7,
                    5, 0.3, 1.7, 1.2, 1,
                    1.1f,
                    Color.valueOf("ff5738"),
                    Color.valueOf("ff8d4c"),
                    Color.valueOf("ff3b38"),
                    Color.valueOf("ff3b38"),
                    Color.valueOf("ff5738"),
                    Color.valueOf("ff3b38"),
                    Color.valueOf("ff3b38"),
                    Color.valueOf("ff5738"),
                    Color.valueOf("ff5738"),
                    Color.valueOf("ff8d4c"),
                    Color.valueOf("ffe371"),
                    Color.valueOf("ffb671"),
                    Color.valueOf("ffb671")
            );
        }};

        caliterra = new Planet("caliterra", serilia, 2.1f, 2){{
            generator = new SerpuloPlanetGenerator();
            meshLoader = () -> new HexMesh(this, 5);
            alwaysUnlocked = true;
            solarSystem = serilia;
            orbitRadius = /*You have been in relaxation for:*/ 99.9999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999f;
        }};

        ahkar = new Planet("ahkar", serilia, 0.6f, 1){{
            orbitRadius = 56f;
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
            totalRadius = 0.65f;
            lightSrcTo = 0.5f;
            lightDstFrom = 0.2f;
            solarSystem = serilia;
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
                r.unitAmmo = true;
            };

            unlockedOnLand.add(coreBastion);
        }};


        //rogue planet for underice underwater dark levels
        /*rogueParent = new Planet("rogue-parent", Planets.sun, 33f, 3){{
            bloom = true;
            accessible = false;
            drawOrbit = false;
            orbitRadius = 1000f;
            lightColor = Color.valueOf("00000000");

            meshLoader = () -> new SunMesh(
                    this, 7,
                    5, 0.3, 1.7, 1.2, 1,
                    1.1f,
                    Color.valueOf("ff5738")
            );
        }};

        rogue = new Planet("rogue", rogueParent, 1.5f, 1){{
            orbitRadius = 1000f;
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
            totalRadius = 0.65f;
            lightSrcTo = 0.5f;
            lightDstFrom = 0.2f;
            solarSystem = rogueParent;
            clearSectorOnLose = true;
            defaultCore = coreAcropolis;
            drawOrbit = false;
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
                r.unitAmmo = true;
            };

            unlockedOnLand.add(coreBastion);
        }};*/
    }
}