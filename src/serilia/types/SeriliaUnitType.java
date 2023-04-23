package serilia.types;

import mindustry.type.UnitType;
import serilia.vfx.SeVFX;
import mindustry.graphics.*;

public class SeriliaUnitType extends UnitType {
    public SeriliaUnitType(String name) {
        super(name);
        outlineColor = SeVFX.seOutline;
        groundLayer = Layer.flyingUnit;
    }
}
