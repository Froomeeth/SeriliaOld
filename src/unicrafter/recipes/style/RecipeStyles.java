package unicrafter.recipes.style;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Scaling;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;
import unicrafter.recipes.ChanceRecipe;
import unicrafter.recipes.Recipe;

import static mindustry.Vars.iconMed;
import static mindustry.Vars.ui;

public class RecipeStyles{

    public static RecipeStyle normal, upgrade;

    static{
        normal = new RecipeStyle(){
            @Override
            public Table recipeTable(boolean icon, Recipe recipe){
                Table t = new Table();

                t.row(); t.table(Styles.grayPanel, display -> {
                    if(icon){
                        display.add(recipeTopTable(recipe)).pad(10f).padBottom(0f); display.row();
                    }

                    display.table(io -> { //encompasses input/output
                        io.table(Styles.black5, input -> input.add(contentListVertical(recipe.payReq, recipe.itemReq, recipe.liqReq, -20f, -20f, recipe.time, true, 1f)).pad(5f).grow()).pad(5f).grow(); //not adding directly to get an easy outline

                        io.add(recipeArrowTable(recipe)).padBottom(5f).padTop(5f);

                        io.table(out -> {
                            if(recipe instanceof ChanceRecipe){
                                out.add("100%").color(Color.white); out.row();
                            }

                            out.table(Styles.black5, output -> output.add(contentListVertical(recipe.payOut, recipe.itemOut, recipe.liqOut, recipe.powerOut, recipe.heatOut, recipe.time, true, 1f)).pad(5f).grow()).pad(5f).grow();

                            if(recipe instanceof ChanceRecipe ch){
                                ch.outs.each(chanceOut -> {
                                    out.row(); out.add(Mathf.round((chanceOut.rangeMax - chanceOut.rangeMin) * 100f) + "%").color(Pal.accent);
                                    out.row(); out.table(Styles.black5, output -> output.add(contentListVertical(chanceOut.payOutChance, chanceOut.itemOutChance, chanceOut.liqOutChance, -20f, -20f, recipe.time, true, chanceOut.rangeMax - chanceOut.rangeMin)).pad(5f).grow()).pad(5f).grow();
                                });
                            }
                        });
                        display.row(); addRecipeBottomTable(display, recipe);
                    });
                }).pad(5f).top();

                return t;
            }

            public Table recipeTopTable(Recipe recipe){
                Table t = new Table();

                t.table(name -> {
                    t.add(recipe.localizedName);
                    t.button("?", Styles.flatBordert, () -> ui.content.show(recipe)).size(iconMed).pad(5f).right().grow().visible(recipe::unlockedNow);
                });
                t.row(); t.image(recipe.uiIcon).size(48f).scaling(Scaling.fit);

                return t;
            }
        };
    }
}
