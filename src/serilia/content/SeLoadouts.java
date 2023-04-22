package serilia.content;

import mindustry.game.Schematic;
import mindustry.game.Schematics;

public class SeLoadouts {
    public static Schematic
            someCoreSchem;

    public static void load() {
        someCoreSchem = Schematics.readBase64("put schematic in here");
    }
}
