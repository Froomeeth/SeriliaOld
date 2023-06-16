package serilia.world.blocks.distribution;

import mindustry.gen.Building;
import serilia.world.blocks.distribution.TubeMotor.TubeMotorBuild;

public interface TubeThing{
    Building lastMotor();
    void lastMotor(TubeMotorBuild set);

    int carryDst();
    void carryDst(int set);

    float driveSpeed();
    void driveSpeed(float set);
}
