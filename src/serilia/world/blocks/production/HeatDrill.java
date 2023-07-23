package serilia.world.blocks.production;

import arc.Core;
import arc.math.Mathf;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.heat.HeatConsumer;

public class HeatDrill extends DrawerDrill{
    /** Base heat requirement for 100% efficiency. */
    public float heatRequirement = 10f;
    /** After heat meets this requirement, excess heat will be scaled by this number. */
    public float overheatScale = 1f;

    public HeatDrill(String name){
        super(name);
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("heat", (HeatDrillBuild entity) ->
                new Bar(() ->
                        Core.bundle.format("bar.heatpercent", (int)(entity.heat + 0.01f), (int)(entity.efficiencyScale() * 100 + 0.01f)),
                        () -> Pal.lightOrange,
                        () -> entity.heat / heatRequirement));
    }

    public class HeatDrillBuild extends DrawerDrillBuild implements HeatConsumer{
        public float[] sideHeat = new float[4];
        public float heat = 0f;

        @Override
        public void updateTile(){
            heat = calculateHeat(sideHeat);

            super.updateTile();
        }

        @Override
        public float heatRequirement(){
            return heatRequirement;
        }

        @Override
        public float[] sideHeat(){
            return sideHeat;
        }

        public float warmupTarget(){
            return Mathf.clamp(heat / heatRequirement);
        }

        @Override
        public float efficiencyScale(){
            float over = Math.max(heat - heatRequirement, 0f);
            return Mathf.clamp(heat / heatRequirement) + over / heatRequirement * overheatScale;
        }

        @Override
        public float timeScale(){
            float over = Math.max(heat - heatRequirement, 0f);
            return super.timeScale() * (Mathf.clamp(heat / heatRequirement) + over / heatRequirement * overheatScale);
        }
    }
}