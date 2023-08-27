package unicrafter.recipes.style;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Scaling;
import arc.util.Strings;
import mindustry.gen.Icon;
import mindustry.gen.Iconc;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.type.PayloadStack;
import mindustry.ui.ItemImage;
import unicrafter.recipes.Recipe;

import static mindustry.Vars.iconMed;

public class RecipeStyle{

    /**The final table, used for both recipe and block descriptions.*/
    public Table recipeTable(boolean icon, Recipe recipe){
        return null;
    }

    public Table recipeArrowTable(Recipe recipe){
        Table t = new Table();

        if(recipe.powerReq > 0){
            t.table(power -> {
                power.add("[accent]" + Iconc.power + "[]");
                power.row();
                power.add(Strings.autoFixed(recipe.powerReq * 60f, 1));
            }).pad(5f).bottom();
            t.row();
        }

        t.image(Icon.play).pad(5f).padTop(10f).padBottom(10f).center();

        if(recipe.heatReq > 0){
            t.row();
            t.table(heat -> {
                heat.add("[red]" + Iconc.waves + "[]");
                heat.row();
                heat.add(Strings.autoFixed(recipe.heatReq, 1));
            }).pad(5f).top();
            t.row();
        }

        return t;
    }

    public void addRecipeBottomTable(Table table, Recipe recipe){
        if(recipe.heatReq > 0f){
            table.add("[red]" + Iconc.waves + "[lightgray] Max %: " + Mathf.round(recipe.maxHeatEfficiency * 100f) + "%").pad(5f).padLeft(10f).left();
            table.row();
        }
        //if(attribute != null) todo attributes
    }

    public Table contentListVertical(Seq<PayloadStack> pay, Seq<ItemStack> item, Seq<LiquidStack> liq, float power, float heat, float time, boolean flip, float timeDiv){
        Table t = new Table();

        if(pay != null) pay.each(requirement -> {
            if(flip) t.add(new ItemImage(requirement.item.uiIcon, requirement.amount)).left();

            t.table(req -> {
                req.add(Strings.autoFixed(requirement.amount / ((time / timeDiv) / 60f), 2) + "/s").pad(4f).color(Color.lightGray);
            });

            if(!flip) t.add(new ItemImage(requirement.item.uiIcon, requirement.amount)).right();
            t.row();
        });
        if(item != null) item.each(requirement -> {
            if(flip) t.add(new ItemImage(requirement.item.uiIcon, requirement.amount)).left();

            t.table(req -> {
                req.add(Strings.autoFixed(requirement.amount / ((time * timeDiv) / 60f), 2) + "/s").pad(4f).color(Color.lightGray);
            });
            if(!flip) t.add(new ItemImage(requirement.item.uiIcon, requirement.amount)).right();
            t.row();
        });
        if(liq != null) liq.each(requirement -> {
            if(flip) t.image(requirement.liquid.fullIcon).size(iconMed).left().scaling(Scaling.fit);

            t.table(req -> {
                req.add(Strings.autoFixed(requirement.amount * 60f, 2) + "/s").color(Color.lightGray).pad(4f);
            });
            if(!flip) t.image(requirement.liquid.fullIcon).size(iconMed).right().scaling(Scaling.fit);
            t.row();
        });
        t.row();
        if(power > 0f) t.add("[accent]" + Iconc.power + "[] " + Strings.autoFixed(power * 60f, 2)).pad(4f);
        if(heat > 0f) t.add("[red]" + Iconc.waves + "[] " + Strings.autoFixed(heat, 2)).pad(4f);

        return t;
    }

    public Table contentListHorizontal(Seq<PayloadStack> pay, Seq<ItemStack> item, Seq<LiquidStack> liq, float power, float heat, float time, float timeDiv){
        Table t = new Table();

        if(pay != null) pay.each(requirement -> t.table(req -> {
            req.add(new ItemImage(requirement.item.uiIcon, requirement.amount));
            req.row();
            req.add(Strings.autoFixed(requirement.amount / ((time / timeDiv) / 60f), 2) + "/s").pad(4f).color(Color.lightGray);
        }));
        if(item != null) item.each(requirement -> t.table(req -> {
            req.add(new ItemImage(requirement.item.uiIcon, requirement.amount));
            req.row();
            req.add(Strings.autoFixed(requirement.amount / ((time / timeDiv) / 60f), 2) + "/s").pad(4f).color(Color.lightGray);
        }));
        if(liq != null) liq.each(requirement -> t.table(req -> {
            req.image(requirement.liquid.fullIcon).size(iconMed).scaling(Scaling.fit);
            req.row();
            req.add(Strings.autoFixed(requirement.amount * 60f, 2) + "/s").pad(4f).color(Color.lightGray);
        }));
        if(power > 0f || heat > 0f) t.table(req -> {
            if(power > 0f) req.add("[accent]" + Iconc.power + "[] " + Strings.autoFixed(power * 60f, 2)).pad(4f);
            req.row();
            if(heat > 0f) req.add("[red]" + Iconc.waves + "[] " + Strings.autoFixed(heat, 2)).pad(4f);
        });

        return t;
    }
}
