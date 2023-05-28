package serilia.types;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.Unit;
import mindustry.graphics.MultiPacker;
import mindustry.type.UnitType;
import serilia.content.SeFxPal;

@SuppressWarnings("SwitchStatementWithTooFewBranches")
public class SeriliaUnitType extends UnitType {
    public int homeWorld = 0; //0 cal, 1 ahk,
    public boolean glowCell = false, glowEngine = false;
    public TextureRegion cellGlow;


    public SeriliaUnitType(String name) {
        super(name);
    }

    public void worldParams(){
        switch (homeWorld) {
            case 1 -> {
                outlineColor = SeFxPal.ahkarOutline;
                glowCell = glowEngine = true;
                lowAltitude = false;
            }
            default -> outlineColor = SeFxPal.caliOutline;
        }
    }
    
    @Override
    public void drawCell(Unit unit) {
        super.drawCell(unit);
        if(cellGlow.found()) {
            Draw.blend(Blending.additive);
            Draw.color(cellColor(unit));
            Draw.rect(cellGlow, unit.x, unit.y, unit.rotation - 90);
            Draw.reset();
            Draw.blend();
        }
    }

    @Override
    public void drawEngines(Unit unit) {
        if(glowEngine) Draw.blend(Blending.additive);
        super.drawEngines(unit);
        Draw.blend();
    }

    @Override
    public void drawTrail(Unit unit) {
        if(glowEngine) Draw.blend(Blending.additive);
        super.drawTrail(unit);
        Draw.blend();
    }

    @Override
    public void load() {
        super.load();
        cellGlow = Core.atlas.find(name + "-cell-glow");
    }

    @Override
    public void createIcons(MultiPacker packer) {
        worldParams();
        super.createIcons(packer);
    }
}
