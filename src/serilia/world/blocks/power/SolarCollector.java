package serilia.world.blocks.power;

import arc.math.Mathf;
import mindustry.world.blocks.power.SolarGenerator;
import mindustry.world.meta.Attribute;

import static mindustry.Vars.state;

public class SolarCollector extends SolarGenerator {
    public float dirtyTime = 20 * 60f;

    public SolarCollector(String name) {
        super(name);
    }

    public class SolarCollectorBuild extends SolarGeneratorBuild{
        public float progress;

        @Override
        public void updateTile(){
            progress += getProgressIncrease(dirtyTime);
            progress %= 1f;

            productionEfficiency = enabled ?
                    state.rules.solarMultiplier *
                    (1f - progress) *
                    Mathf.maxZero(Attribute.light.env() +
                            (state.rules.lighting ?
                                    1f - state.rules.ambientLight.a :
                                    1f
                            )) : 0f;
        }
    }
}
