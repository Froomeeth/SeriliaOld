package serilia.types;

import mindustry.content.UnitTypes;
import mindustry.type.UnitType;
import serilia.content.SePalette;
import mindustry.graphics.*;

public class SerilianUnitType extends UnitType {
    public SerilianUnitType(String name) {
        super(name);
        outlineColor = SePalette.seOutline;
        groundLayer = Layer.flyingUnit;
    }
}
