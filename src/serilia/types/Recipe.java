package serilia.types;

import arc.Core;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;

public class Recipe extends UnlockableContent{



    public Recipe(String name){
        super(name);

        this.localizedName = Core.bundle.get("recipe." + this.name + ".name", this.name);
        this.description = Core.bundle.getOrNull("recipe." + this.name + ".description");
        this.details = Core.bundle.getOrNull("recipe." + this.name + ".details");
        this.unlocked = Core.settings != null && Core.settings.getBool(this.name + "-unlocked", false);
    }

    @Override
    public ContentType getContentType(){
        return ContentType.loadout_UNUSED;
    }

    @Override
    public void loadIcon(){
        fullIcon = uiIcon = Core.atlas.find("recipe-" + name);
    }
}
