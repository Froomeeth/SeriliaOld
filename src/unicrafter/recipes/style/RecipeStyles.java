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
                t.row();

                t.table(Styles.grayPanel, display -> {
                    if(icon){display.add(recipeTopTable(recipe)).pad(10f).padBottom(0f); display.row();}

                    display.table(io -> { //encompasses input/output
                        //input
                        io.table(Styles.black5, input -> input.add(contentListVertical(recipe.payOut, recipe.itemOut, recipe.liqOut, -20f, -20f, recipe.time, true, 1f)).pad(5f).grow()).pad(5f).grow(); //not adding directly to get an easy outline
                        /*if(recipe instanceof ContainerRecipe cont && cont.itemReqContainer.size > 0){
                            io.row(); io.image(Blocks.reinforcedContainer.uiIcon).size(iconSmall); io.row();
                            io.table(Styles.black5, input -> input.add(contentListVertical(null, cont.itemReqContainer, null, -20f, -20f, cont.time, true, 1f)).pad(5f).grow()).pad(5f).grow();
                        }*/
                        //middle
                        io.add(recipeArrowTable(recipe)).padBottom(5f).padTop(5f);
                        //output
                        io.table(Styles.black5, output -> {
                            if(recipe instanceof ChanceRecipe){output.add("100%").color(Color.white); output.row();} //I don't know if these make it more or less readable to others, but I don't think row()s deserve their own line
                            output.add(contentListVertical(recipe.payOut, recipe.itemOut, recipe.liqOut, recipe.powerOut, recipe.heatOut, recipe.time, true, 1f)).pad(5f).grow();
                        }).pad(5f).grow();

                        if(recipe instanceof ChanceRecipe ch) ch.outs.each(chanceOut -> {
                            io.row(); io.add(Mathf.round((chanceOut.rangeMax - chanceOut.rangeMin) * 100f) + "%").color(Pal.accent); io.row();
                            io.table(Styles.black5, output -> output.add(contentListVertical(chanceOut.payOutChance, chanceOut.itemOutChance, chanceOut.liqOutChance, -20f, -20f, recipe.time, true, chanceOut.rangeMax - chanceOut.rangeMin)).pad(5f).grow()).pad(5f).grow();
                        });
                        /*else if(recipe instanceof ContainerRecipe cont && cont.itemOutContainer.size > 0){
                            io.row(); io.image(Blocks.reinforcedContainer.uiIcon).size(iconSmall); io.row();
                            io.table(Styles.black5, output -> output.add(contentListVertical(null, cont.itemOutContainer, null, -20f, -20f, cont.time, true, 1f)).pad(5f).grow()).pad(5f).grow();
                        }*/
                        display.row();
                        addRecipeBottomTable(display, recipe);

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
