package serilia.content;

import arc.graphics.*;
import mindustry.type.*;
public class SeResources {
    public static Item
<<<<<<< Updated upstream
            iridium, vanadium, tarnide, galvanium, chirokyn, paragonite, graphene;
=======
            nickel, iridium, vanadium, tarnide, leticen, azulite, paragonite, graphene;
>>>>>>> Stashed changes
    public static Liquid
            methane, chlorine, acid, acidicSolution;
    //public static PayloadItem

    public static void load(){
        nickel = new Item("nickel", Color.valueOf()){{
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
<<<<<<< Updated upstream

            alwaysUnlocked = true;

            hardness = 1;
=======
            hardness = 3;
>>>>>>> Stashed changes
            cost = 1f;

            explosiveness = flammability = radioactivity = charge = 0f;
        }};

        tarnide = new Item("tarnide", Color.valueOf("6a5d4d")){{
<<<<<<< Updated upstream

            alwaysUnlocked = true;

            hardness = 2;
=======
            hardness = 5;
>>>>>>> Stashed changes
            cost = 1.5f;

            explosiveness = flammability = radioactivity = charge = 0f;
        }};
<<<<<<< Updated upstream
        galvanium = new Item("galvanium", Color.valueOf("87ceeb")){{

            alwaysUnlocked = true;

            hardness = 2;
=======

        leticen = new Item("leticen", Color.valueOf("87ceeb")){{
            hardness = 5;
>>>>>>> Stashed changes
            cost = 1.3f;

            explosiveness = flammability = radioactivity = charge = 0f;
        }};
<<<<<<< Updated upstream
        chirokyn = new Item("chirokyn", Color.valueOf("6974c4")){{

            alwaysUnlocked = true;

            hardness = 3;
=======

        azulite = new Item("azulite", Color.valueOf("6974c4")){{
            hardness = 6;
>>>>>>> Stashed changes
            cost = 1.7f;

            explosiveness = flammability = radioactivity = charge = 0f;
        }};

        paragonite = new Item("paragonite", Color.valueOf("eab678")){{
<<<<<<< Updated upstream

            alwaysUnlocked = true;

            hardness = 3;
=======
            hardness = 7;
>>>>>>> Stashed changes
            cost = 2f;

            explosiveness = flammability = radioactivity = charge = 0f;
        }};

        graphene = new Item("graphene", Color.valueOf("6e7080")){{
<<<<<<< Updated upstream

            alwaysUnlocked = true;

            hardness = 3;
=======
            hardness = 10;
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
=======
        smoke = new Liquid("smoke", Color.valueOf("717171")){{
            gas = true;
            explosiveness = 0f;
            flammability = 0f;
            heatCapacity = 0f;
            viscosity = 0f;
            temperature = 0.5f;
        }};
>>>>>>> Stashed changes

        acid = new Liquid("acid"){{
            explosiveness = flammability = heatCapacity = 0f; //todo acidity *stat*
        }};

        acidicSolution = new Liquid("acidic-solution"){{
            explosiveness = flammability = heatCapacity = 0f;
        }};
    }
}