package serilia.content;

import mindustry.world.Block;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.OreBlock;
import mindustry.world.blocks.environment.StaticWall;

import static mindustry.content.Items.graphite;
import static mindustry.content.Items.metaglass;
import static serilia.content.SeResources.iridium;
import static serilia.content.SeResources.tarnide;

public class SeEnvBlocks {
    public static Block

        //cali
        oreIridium, oreGraphite, oreTarnide,

        dauricSoil,
        //ahkar
        quartz,
        ashRock,     blooRock    , rockRock    ,
        ashRockWall, blooRockWall, rockRockWall;




    public static void load() {

        //cali
        oreIridium = new OreBlock(iridium); //variants are 3 by default
        oreGraphite = new OreBlock(graphite);
        oreTarnide = new OreBlock(tarnide);

        dauricSoil = new Floor("dauric-soil"){{
            variants = 3;
        }};

        //ahkar
        quartz = new OreBlock(metaglass);

            //needs brackets because the variables would be the same otherwise
        (ashRock = new Floor("ash-rock")).asFloor().wall = ashRockWall = new StaticWall("ash-rock-wall");
        (blooRock = new Floor("bloo-rock")).asFloor().wall = blooRockWall = new StaticWall("bloo-rock-wall");
        (rockRock = new Floor("rock-rock")).asFloor().wall = rockRockWall = new StaticWall("rock-rock-wall");
    }}
