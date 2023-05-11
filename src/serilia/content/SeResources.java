package serilia.content;

import arc.graphics.*;
import mindustry.type.*;
public class SeResources {
    public static Item
            nickel, iridium, vanadium, tarnide, galvanium, chirokyn, paragonite, graphene;
    public static Liquid
            methane, chlorine, acid, acidicSolution;
    //public static PayloadItem

    public static void load(){
        nickel = new Item("nickel", Color.valueOf("958d6d")){{
            hardness = 1;
            cost = 1.6f;

            explosiveness = flammability = radioactivity = charge = 0f;
        }};

        iridium = new Item("iridium", Color.valueOf("656e83")){{
            hardness = 1;
            cost = 2f;

            explosiveness = flammability = radioactivity = charge = 0f;
        }};

        vanadium = new Item("vanadium", Color.valueOf("c95568")){{
            hardness = 1;
            cost = 1f;

            explosiveness = flammability = radioactivity = charge = 0f;
        }};

        tarnide = new Item("tarnide", Color.valueOf("6a5d4d")){{
            alwaysUnlocked = true;

            hardness = 2;
            cost = 1.5f;

            explosiveness = flammability = radioactivity = charge = 0f;
        }};

        galvanium = new Item("galvanium", Color.valueOf("87ceeb")){{
            hardness = 2;
            cost = 1.3f;

            explosiveness = flammability = radioactivity = charge = 0f;
        }};

        chirokyn = new Item("chirokyn", Color.valueOf("6974c4")){{
            hardness = 3;
            cost = 1.7f;

            explosiveness = flammability = radioactivity = charge = 0f;
        }};

        paragonite = new Item("paragonite", Color.valueOf("eab678")){{
            hardness = 3;
            cost = 2f;

            explosiveness = flammability = radioactivity = charge = 0f;
        }};

        graphene = new Item("graphene", Color.valueOf("6e7080")){{
            hardness = 3;
            cost = 2f;

            explosiveness = flammability = radioactivity = charge = 0f;
        }};

        //liquids

        methane = new Liquid("methane", Color.valueOf("bc5452")){{
            gas = true;
            explosiveness = 0.15f;
            flammability = 0.53f;
            heatCapacity = 0.8f;
            viscosity = 0.13f;
            temperature = 0.5f;
        }};

        chlorine = new Liquid("chlorine", Color.valueOf("80b061")){{
            gas = true;
            explosiveness = 0f;
            flammability = 0f;
            heatCapacity = 0.85f;
            viscosity = 0.68f;
            temperature = 0.5f;
        }};

        acid = new Liquid("acid"){{
            explosiveness = flammability = heatCapacity = 0f; //todo acidity *stat*
        }};

        acidicSolution = new Liquid("acidic-solution"){{
            explosiveness = flammability = heatCapacity = 0f;
        }};
    }
}