package unicrafter.world;

import arc.struct.ObjectIntMap;
import arc.struct.Seq;
import mindustry.world.Block;
import unicrafter.recipes.Recipe;

public class TieredUniCrafter extends UniversalCrafter{
    public Seq<Seq<Recipe>> tieredRecipes;
    public ObjectIntMap<Block> moduleTierMap = new ObjectIntMap<>();
    public boolean requirePrevTier = true;
    public boolean requireModuleFacing = true;

    public TieredUniCrafter(String name){
        super(name);
    }

    @Override
    public void init(){
        super.init();
        tieredRecipes.each(this::initRecipes);
    }

    @Override
    public void load(){
        super.load();
        tieredRecipes.each(recs -> recs.each(recipe -> recipe.load(this)));
    }

    @Override
    public void setStats(){
        super.setStats();
        //todo later
    }

    public class TieredBuild extends UniversalBuild{

        public void selectSlot(){
            //later
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
            selectSlot();
        }

        @Override
        public Seq<Recipe> recipes(){
            return tieredRecipes.get(slot);
        }
    }
}
