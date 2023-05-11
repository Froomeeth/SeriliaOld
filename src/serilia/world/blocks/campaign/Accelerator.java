package serilia.world.blocks.campaign;

import arc.util.Nullable;
import mindustry.Vars;
import mindustry.content.UnitTypes;
import mindustry.gen.BlockUnitc;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.type.Sector;
import mindustry.world.Block;
import mindustry.world.blocks.ControlBlock;
import serilia.content.AhkarBlocks;

public class Accelerator extends Block {
    public Sector sector;
    public Block core = AhkarBlocks.ahkarDropPod; //use -folded region

    public Accelerator(String name) {
        super(name);
    }
    public class AcceleratorBuild extends Building implements ControlBlock{
        public @Nullable BlockUnitc unit;

        @Override
        public Unit unit(){
            if(unit == null){
                unit = (BlockUnitc) UnitTypes.block.create(team);
                unit.tile(this);
            }
            return (Unit)unit;
        }

        @Override
        public void updateTile(){
            if(efficiency > 0.98f && isControlled()){
                Vars.control.playSector(sector);
            }
        }
    }
}
