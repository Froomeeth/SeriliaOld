package serilia.types;

import arc.math.Mathf;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.gen.Building;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.draw.*;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.*;

import static mindustry.type.ItemStack.with;

@SuppressWarnings("unused")
public class PayloadItem extends Block{
    public float paySize;
    public Item item = Items.copper;
    public float partValue = 8000f;

    public DrawBlock drawer = new DrawDefault();
    public boolean updateProgress = false, overrideWarmup = false;
    public float progressIncrease = 0.05f;


    public PayloadItem(String name, float size){
        super(name);
        update = true;
        paySize = size;
        requirements(Category.units, BuildVisibility.shown, with(Items.dormantCyst, 1));
    }
    public PayloadItem(String name, float size, DrawBlock drawer){
        super(name);
        update = true;
        paySize = size;
        requirements(Category.units, BuildVisibility.shown, with(Items.dormantCyst, 1));
        this.drawer = drawer;
        updateProgress = true;
    }

    @Override
    public void setStats(){
        stats.add(Stat.size, StatValues.squared(paySize, StatUnit.blocksSquared));
        if(partValue > 0) stats.add(new Stat("part-value"), partValue);
    }

    @Override
    public void load(){
        super.load();
        String localized = localizedName;

        item = Vars.content.item("hearth-" + name);
        if(item == null) return;

        item.hidden = true;
        item.fullIcon = item.uiIcon = region;
        item.localizedName = localized;
        item.description = "[red]Stop! You have violated the Law! Pay the court a fine or serve your sentence. Your stolen goods are now forfeit.[]";
    }

    public class PayloadItemBuild extends Building{
        public float remainingValue = partValue;
        public float progress = 0f, totalProgress = 0f, warmup = 0f;

        public PayloadItemBuild() {}

        @Override
        public void updateTile(){
            super.updateTile();

            if(updateProgress){
                progress += progressIncrease * fractionValue();
                warmup = Mathf.approachDelta(warmup, 1f, 5f * 60f);
                totalProgress += warmup * Time.delta;
            }
        }

        @Override
        public void draw(){
            drawer.draw(this);
        }

        public Item item(){
            return Vars.content.item("hearth-" + name);
        }

        public float fractionValue(){
            return remainingValue / partValue;
        }
        @Override
        public float progress(){
            return Mathf.clamp(progress);
        }
        @Override
        public float totalProgress(){
            return totalProgress;
        }
        @Override
        public float warmup(){
            return overrideWarmup ? fractionValue() : warmup;
        }
    }
}
