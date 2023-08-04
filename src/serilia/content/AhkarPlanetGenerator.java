package serilia.content;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.util.Tmp;
import arc.util.noise.Ridged;
import arc.util.noise.Simplex;
import mindustry.ai.Astar;
import mindustry.content.Blocks;
import mindustry.content.Loadouts;
import mindustry.game.Schematics;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.type.Sector;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.TileGen;
import mindustry.world.blocks.environment.TallBlock;

import static mindustry.Vars.state;
import static mindustry.Vars.world;
import static mindustry.content.Blocks.*;
import static mindustry.content.Blocks.basalt;

public class AhkarPlanetGenerator extends PlanetGenerator {
    public float heightScl = 0.5f, octaves = 8, persistence = 0.5f, heightPow = 2f, heightMult = 1f;

    public static float equatorSize = 3.3f;
    public static float arkThresh = 0.28f, arkScl = 0.83f;
    public static int arkSeed = 7, arkOct = 2;
    public static float redThresh = 4.1f, noArkThresh = 0.3f;
    public static int iceSeed = 8, iceOct = 2;
    public static float iceScl = 0.9f, iceMag = 0.3f;
    public static float airThresh = 0.13f, airScl = 14;

    Block[] terrain = {slag, slag, slag, slag, carbonStone, carbonStone, carbonStone, carbonStone, beryllicStone, stone, stone, stone, stone, stone, stone, stone}; //todo custom tiles

    {
        baseSeed = 2;
        defaultLoadout = Loadouts.basicBastion; //TODO CHANGE
    }

    @Override
    public void generateSector(Sector sector){}

    @Override
    public float getHeight(Vec3 position){
        return Mathf.pow(rawHeight(position), heightPow) * heightMult;
    }

    @Override
    public Color getColor(Vec3 position){
        Block block = getBlock(position);

        //more obvious color
        if(block == Blocks.crystallineStone) block = Blocks.crystalFloor;

        return Tmp.c1.set(block.mapColor).a(1f - block.albedo);
    }

    @Override
    public float getSizeScl(){
        //should sectors be 600, or 500 blocks?
        return 2000 * 1.07f * 6f / 5f;
    }

    float rawHeight(Vec3 position){
        return Simplex.noise3d(seed, octaves, persistence, 1f/heightScl, 10f + position.x, 10f + position.y, 10f + position.z);
    }

    float rawTemp(Vec3 position){
        return Math.min(position.dst(0, 1, 0), position.dst(0, -1, 0)) * equatorSize - Simplex.noise3d(seed, 8, 0.54f, 1f, 10f + position.x, 10f + position.y, 10f + position.z) * 2.9f;
    }

    Block getBlock(Vec3 position){
        float ice = rawTemp(position);
        Tmp.v32.set(position);

        float height = rawHeight(position);
        Tmp.v31.set(position);
        height *= 1.2f;
        height = Mathf.clamp(height);

        Block result = terrain[Mathf.clamp((int)(height * terrain.length), 0, terrain.length - 1)];


        //ice caps
        if(ice < 0.15 + Math.abs(Ridged.noise3d(seed + iceSeed, position.x + 4f, position.y + 8f, position.z + 1f, iceOct, iceScl)) * iceMag){
            return redIce;
        }
        if(ice < 0.7){
            if(result == Blocks.stone || result == carbonStone){
                return redStone;
            }
        }
        if(ice < 1){
            if(result == Blocks.stone || result == carbonStone){
                return beryllicStone;
            }
        }

        position = Tmp.v32;

        //veins todo decrease intensity
        if(ice < redThresh - noArkThresh && Ridged.noise3d(seed + arkSeed, position.x + 2f, position.y + 8f, position.z + 1f, arkOct, arkScl) > arkThresh){
            result = beryllicStone;
        }

        return result;
    }

    @Override
    public void genTile(Vec3 position, TileGen tile){
        tile.floor = getBlock(position);

        //add craters
        /*if(tile.floor == Blocks.rhyolite && rand.chance(0.01)){
            tile.floor = Blocks.rhyoliteCrater;
        }*/

        tile.block = tile.floor.asFloor().wall;

        if(Ridged.noise3d(seed + 1, position.x, position.y, position.z, 2, airScl) > airThresh){
            tile.block = Blocks.air;
        }

        /*if(Ridged.noise3d(seed + 2, position.x, position.y + 4f, position.z, 3, 6f) > 0.6){
            tile.floor = Blocks.carbonStone;
        }*/
    }

    @Override
    protected void generate(){
        float temp = rawTemp(sector.tile.v);

        if(temp > 0.7){

            pass((x, y) -> {
                if(floor != Blocks.redIce){
                    float noise = noise(x + 782, y, 7, 0.8f, 280f, 1f);
                    if(noise > 0.62f){
                        if(noise > 0.635f){
                            floor = Blocks.slag;
                        }else{
                            floor = basalt;
                        }
                        ore = Blocks.air;
                    }
                }
            });
        }

        cells(4);

        pass((x, y) -> {
            if(floor == stone && noise(x, y, 3, 0.4f, 13f, 1f) > 0.59f){
                block = stoneWall;
            }
        });

        float length = width/2.6f;
        Vec2 trns = Tmp.v1.trns(rand.random(360f), length);
        int
                spawnX = (int)(trns.x + width/2f), spawnY = (int)(trns.y + height/2f),
                endX = (int)(-trns.x + width/2f), endY = (int)(-trns.y + height/2f);
        float maxd = Mathf.dst(width/2f, height/2f);

        erase(spawnX, spawnY, 15);
        brush(pathfind(spawnX, spawnY, endX, endY, tile -> (tile.solid() ? 300f : 0f) + maxd - tile.dst(width/2f, height/2f)/10f, Astar.manhattan), 9);
        erase(endX, endY, 15);

        blend(Blocks.slag, basalt, 5);

        distort(10f, 12f);
        distort(5f, 7f);

        //smooth out slag to prevent random 1-tile patches
        median(3, 0.6, Blocks.slag);

        pass((x, y) -> {
            //rough rhyolite
            /*if(noise(x, y + 600 + x, 5, 0.86f, 60f, 1f) < 0.41f && floor == Blocks.rhyolite){
                floor = Blocks.roughRhyolite;
            }*/

            if(floor == Blocks.slag && Mathf.within(x, y, spawnX, spawnY, 30f + noise(x, y, 2, 0.8f, 9f, 15f))){
                floor = Blocks.yellowStonePlates;
            }

            float max = 0;
            for(Point2 p : Geometry.d8){
                //I think this is the cause of lag
                max = Math.max(max, world.getDarkness(x + p.x, y + p.y));
            }
            if(max > 0){
                block = floor.asFloor().wall;
                if(block == Blocks.air) block = Blocks.yellowStoneWall;
            }

            if(floor == Blocks.yellowStonePlates && noise(x + 78 + y, y, 3, 0.8f, 6f, 1f) > 0.44f){
                floor = Blocks.yellowStone;
            }

            if(floor == Blocks.redStone && noise(x + 78 - y, y, 4, 0.73f, 19f, 1f) > 0.63f){
                floor = Blocks.denseRedStone;
            }
        });

        inverseFloodFill(tiles.getn(spawnX, spawnY));

        blend(Blocks.redStoneWall, Blocks.denseRedStone, 4);

        //make sure enemies have room
        erase(endX, endY, 6);

        tiles.getn(endX, endY).setOverlay(Blocks.spawn);

        //ores
        pass((x, y) -> {

            if(block != Blocks.air){
                if(nearAir(x, y)){
                    if(block == Blocks.carbonWall && noise(x + 78, y, 4, 0.7f, 33f, 1f) > 0.52f){
                        block = Blocks.graphiticWall;
                    }else if(block != Blocks.carbonWall && noise(x + 782, y, 4, 0.8f, 38f, 1f) > 0.665f){
                        ore = Blocks.wallOreBeryllium;
                    }

                }
            }else if(!nearWall(x, y)){

                if(noise(x + 150, y + x*2 + 100, 4, 0.8f, 55f, 1f) > 0.76f){
                    ore = Blocks.oreTungsten;
                }

                if(noise(x + 999, y + 600 - x, 4, 0.63f, 45f, 1f) < 0.27f && floor == Blocks.crystallineStone){
                    ore = Blocks.oreCrystalThorium;
                }

            }
        });

        //remove props near ores, they're too annoying
        pass((x, y) -> {
            if(ore.asFloor().wallOre || block.itemDrop != null || (block == Blocks.air && ore != Blocks.air)){
                removeWall(x, y, 3, b -> b instanceof TallBlock);
            }
        });

        trimDark();

        for(Tile tile : tiles){
            if(tile.overlay().needsSurface && !tile.floor().hasSurface()){
                tile.setOverlay(Blocks.air);
            }
        }

        decoration(0.017f);

        state.rules.env = sector.planet.defaultEnv;
        state.rules.placeRangeCheck = true;


        Schematics.placeLaunchLoadout(spawnX, spawnY);

        //all sectors are wave sectors
        state.rules.waves = false;
        state.rules.showSpawns = true;
    }
}
