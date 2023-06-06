package serilia.content;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.content.Items;
import mindustry.content.UnitTypes;
import mindustry.graphics.Layer;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.DirectionalForceProjector;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.environment.OreBlock;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.Separator;
import mindustry.world.draw.*;
import serilia.util.SeUtil;
import serilia.world.blocks.liquid.LiquidChannel;
import serilia.world.blocks.payload.MoreGenericCrafter;
import serilia.world.blocks.payload.PayloadDuct;
import serilia.world.blocks.power.SolarCollector;
import serilia.world.blocks.production.DrawerDrill;
import serilia.world.blocks.storage.DrawerCore;
import serilia.world.draw.*;

import static mindustry.content.Items.*;
import static mindustry.content.Liquids.nitrogen;
import static mindustry.content.Liquids.slag;
import static mindustry.type.Category.*;
import static mindustry.type.ItemStack.with;
import static mindustry.world.meta.BuildVisibility.*;
import static serilia.content.SeResources.*;

public class SeEnvBlocks {
    public static Block

    oreIridium, oreGraphite, oreTarnide;

    public static void load() {

        oreIridium = new OreBlock(iridium){{
        }};
        oreGraphite = new OreBlock(graphite){{
            variants = 3;
        }};
        oreTarnide = new OreBlock(tarnide){{
        }};
    }}
