package serilia.content;

import arc.graphics.*;
import mindustry.type.*;
public class SeResources {
    public static Item
            iridium, vanadinite, fragisteel, tarnide, leticen, azulite, arsenicDust, paragonite, uranium, tanzanite, graphene;
    public static Liquid
            methane, chlorine;

    public static void load(){
        iridium = new Item("iridium", Color.valueOf("656e83")){{

            alwaysUnlocked = true;

            hardness = 4;
            cost = 2f;

            explosiveness = 0f;
            flammability = 0f;
            radioactivity = 0f;
            charge = 0f;
        }};
        vanadinite = new Item("vanadinite", Color.valueOf("c95568")){{

            alwaysUnlocked = true;

            hardness = 10;
            cost = 1f;

            explosiveness = 0f;
            flammability = 0f;
            radioactivity = 0f;
            charge = 0f;
        }};
        fragisteel = new Item("fragisteel", Color.valueOf("bee8fe")){{

            alwaysUnlocked = true;

            hardness = 10;
            cost = 2f;

            explosiveness = 0f;
            flammability = 0f;
            radioactivity = 0f;
            charge = 1f/10;
        }};
        tarnide = new Item("tarnide", Color.valueOf("6a5d4d")){{

            alwaysUnlocked = true;

            hardness = 10;
            cost = 1.5f;

            explosiveness = 0f;
            flammability = 0f;
            radioactivity = 0f;
            charge = 0f;
        }};
        leticen = new Item("leticen", Color.valueOf("87ceeb")){{

            alwaysUnlocked = true;

            hardness = 10;
            cost = 1.3f;

            explosiveness = 0f;
            flammability = 0f;
            radioactivity = 0f;
            charge = 1f;
        }};
        azulite = new Item("azulite", Color.valueOf("6974c4")){{

            alwaysUnlocked = true;

            hardness = 10;
            cost = 1.7f;

            explosiveness = 0f;
            flammability = 0f;
            radioactivity = 0f;
            charge = 0f;
        }};
        arsenicDust = new Item("arsenic-dust", Color.valueOf("7a8593")){{

            alwaysUnlocked = true;

            hardness = 11;
            lowPriority = true;
            buildable = false;

            explosiveness = 0f;
            flammability = 0f;
            radioactivity = 0f;
            charge = 0f;
        }};
        paragonite = new Item("paragonite", Color.valueOf("eab678")){{

            alwaysUnlocked = true;

            hardness = 10;
            cost = 2f;

            explosiveness = 0f;
            flammability = 0f;
            radioactivity = 0f;
            charge = 0f;
        }};
        uranium = new Item("uranium", Color.valueOf("a9e0b5")){{

            alwaysUnlocked = true;

            hardness = 11;
            cost = 1f;

            explosiveness = 0f;
            flammability = 0f;
            radioactivity = 1.75f;
            charge = 0f;
        }};

        //liquids

        methane = new Liquid("methane", Color.valueOf("80b061")){{

            alwaysUnlocked = true;

            gas = true;
            explosiveness = 0.15f;
            flammability = 0.53f;
            heatCapacity = 0.8f;
            viscosity = 0.13f;
            temperature = 1.6f;
        }};
        chlorine = new Liquid("chlorine", Color.valueOf("bc5452")){{

            alwaysUnlocked = true;

            gas = true;
            explosiveness = 0f;
            flammability = 0f;
            heatCapacity = 0.85f;
            viscosity = 0.68f;
            temperature = 0.34f;
        }};
    }
}