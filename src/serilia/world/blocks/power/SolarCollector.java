package serilia.world.blocks.power;

import arc.math.Mathf;
import mindustry.world.meta.Attribute;

import static mindustry.Vars.state;

public class SolarCollector extends LaserShidder{
    public SolarCollector(String name) {
        super(name);
    }

    public class SolarCollectorBuild extends LaserShidderBuild{
        @Override
        public void updateTile(){
            super.updateTile();
            shidding = baseShitAmount * state.rules.solarMultiplier * Mathf.maxZero(Attribute.light.env() +
                (state.rules.lighting ? 1f - state.rules.ambientLight.a : 1f));
        }
    }
}
