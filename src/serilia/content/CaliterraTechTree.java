package serilia.content;

import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.content.TechTree;

import static serilia.content.CaliBlocks.coreSprout;
import static serilia.content.SeResources.*;

public class CaliterraTechTree extends TechTree{
    public static void load(){
        SeSystem.caliterra.techTree = nodeRoot("caliterra", coreSprout ,false, () -> {
            //items
            node(iridium, () -> {
                node(methane, () -> {

                });

                node(vanadium, () -> {
                    nodeProduce(Items.titanium, () -> {
                        nodeProduce(Liquids.cryofluid, () -> {

                        });

                        nodeProduce(Items.thorium, () -> {
                            nodeProduce(Items.surgeAlloy, () -> {

                            });

                            nodeProduce(Items.phaseFabric, () -> {

                            });
                        });
                    });

                    nodeProduce(Items.metaglass, () -> {

                    });
                });

                nodeProduce(Items.sand, () -> {
                    nodeProduce(Items.scrap, () -> {
                        nodeProduce(Liquids.slag, () -> {

                        });
                    });

                    nodeProduce(Items.coal, () -> {
                        nodeProduce(Items.graphite, () -> {
                            nodeProduce(Items.silicon, () -> {

                            });
                        });

                        nodeProduce(Items.pyratite, () -> {
                            nodeProduce(Items.blastCompound, () -> {

                            });
                        });

                        nodeProduce(Items.sporePod, () -> {

                        });

                        nodeProduce(Liquids.oil, () -> {
                            nodeProduce(Items.plastanium, () -> {

                            });
                        });
                    });
                });
            });
        });
    }
}