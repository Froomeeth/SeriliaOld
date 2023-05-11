package serilia.world.blocks.storage;

import arc.math.Mathf;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.draw.DrawBlock;

public class DrawerCore extends CoreBlock{
    public DrawBlock drawer;
    public float progressTime = 80f, warmupSpeed = 0.2f;

    public DrawerCore(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();
        drawer.load(this);
    }

    public class DrawerCoreBuild extends CoreBuild{
        public float progress, warmup, totalProgress;


        @Override
        public void updateTile() {
            super.updateTile();
            progress += getProgressIncrease(progressTime);
            warmup = Mathf.approachDelta(warmup, 1, warmupSpeed);
        }

        @Override
        public void draw(){
            drawer.draw(this);
        }

        @Override
        public void drawTeamTop(){}

        @Override
        public float warmup(){
            return warmup;
        }

        @Override
        public float progress(){
            return progress;
        }

        @Override
        public float totalProgress(){
            return totalProgress;
        }
    }
}
