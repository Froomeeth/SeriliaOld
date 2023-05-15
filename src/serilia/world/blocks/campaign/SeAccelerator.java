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
import serilia.content.SeSystem;

public class SeAccelerator extends Block {
    public Sector sector =  SeSystem.ahkar.sectors.get(0);
    public Block core = AhkarBlocks.ahkarDropPod; //use -folded region

    public SeAccelerator(String name) {
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
