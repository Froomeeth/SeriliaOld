package serilia.types;

import serilia.gen.entities.GraphUpdater;

public class Graph{
    private final GraphUpdater entity;
    public float amount;

    public Graph(){
        entity = GraphUpdater.create();
        entity.graph = this;
    }

    public void update(){

    }

    public void checkAdd(){
        if(entity != null) entity.add();
    }

    public void checkRemove(){
        if(entity != null) entity.remove();
    }
}
