package serilia.types;

import mindustry.type.UnitType;
import unicrafter.recipes.Recipe;

/**Entirely pointless, just makes my job mildly easier.*/
public class UnitRecipe extends Recipe{
    public UnitType unitType;

    public UnitRecipe(UnitType type, float time){
        super("recipe-" + type.name, type, time);
        unitType = type;
    }

    @Override
    public Object[] out(Object... items){
        return items;
    }
}
