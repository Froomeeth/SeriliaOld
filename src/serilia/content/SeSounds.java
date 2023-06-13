package serilia.content;

import arc.audio.Sound;
import mindustry.Vars;

public class SeSounds{
    public static Sound how, blobber;

    public static void load(){
        how = Vars.tree.loadSound("how");
        blobber = Vars.tree.loadSound("blobber");
    }
}
