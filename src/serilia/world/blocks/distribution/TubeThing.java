package serilia.world.blocks.distribution;

import mindustry.gen.Building;
import serilia.world.blocks.distribution.TubeMotor.TubeMotorBuild;

public interface TubeThing{
    default void lastMotor(TubeMotorBuild set){}
    default Building lastMotor(){
        return null;
    }

    default void carryDst(int set){}
    default int carryDst(){
        return 0;
    }

    default void driveSpeed(float set){}
    default float driveSpeed(){
        return 0;
    }

    default void addMotor(TubeMotorBuild add){}
    default void clearMotors(){};
}
