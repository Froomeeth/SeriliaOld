package serilia.entities.comp;

import arc.Events;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import ent.anno.Annotations;
import ent.anno.Annotations.*;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.core.World;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.type.UnitType;
import mindustry.world.Build;
import mindustry.world.Tile;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.UnitPayload;
import serilia.gen.entities.TractorBeamc;

@EntityComponent
@EntityDef({Posc.class, Rotc.class, Hitboxc.class, Unitc.class, Payloadc.class, TractorBeamc.class})
public abstract class TractorBeamComp implements Posc, Rotc, Hitboxc, Unitc, Payloadc{

    public Payload beamHeld;
    public Vec2 mouse = new Vec2(), payPos = new Vec2(), unitPos = new Vec2();
    public int mouseTileX, mouseTileY;
    public boolean movingIn = false;
    public float beamRange = 8f * 8f;

    @Import float x, y, rotation;
    @Import Team team;
    @Import UnitType type;
    @Import Seq<Payload> payloads = new Seq<>();
    @Import float aimX, aimY;

    public void moveIn(){
        if (beamHeld != null && canPickupPayload(beamHeld)){
            movingIn = false;
            addPayload(beamHeld);
            beamHeld = null;
            payPos.set(unitPos);
        }
    }

    public void moveOut(){
        if(beamHeld == null && payloads.size != 0){
            Payload load = payloads.peek();

            if(!tryDropPayload(beamHeld)){
                beamHeld = load;
                payPos.set(unitPos);

                payloads.pop();
            }
        }
    }
    @Insert("update()")
    public void updateHeld(){
        if(beamHeld == null || Vars.state.isPaused()) return;
        mouse.set(Mathf.clamp(aimX, 0, Vars.world.height() * 8f), Mathf.clamp(aimY, 0f, Vars.world.width() * 8f));

        if(mouse.dst(unitPos) > beamRange){
            mouse.set(mouse.sub(unitPos).setLength(beamRange).add(unitPos));
        }
        mouseTileX = World.toTile(mouse.x); mouseTileY = World.toTile(mouse.y);

        if(beamHeld instanceof BuildPayload){
            payPos.approach(movingIn ? unitPos : mouse, Mathf.clamp(payPos.dst(movingIn ? unitPos : mouse) * 0.1f, movingIn ? 2f : 0.1f, movingIn ? 32f : 16f) * Time.delta);
        }else payPos.set(unitPos);

        beamHeld.update(self(), null);
        beamHeld.set(payPos.x, payPos.y, beamHeld instanceof BuildPayload ? ((BuildPayload)beamHeld).build.rotation() : rotation);
        if(beamHeld instanceof BuildPayload && ((BuildPayload)beamHeld).build.health <= 0){ //todo FUCK
            beamHeld = null;
        }

        if(movingIn && payPos.dst(unitPos) < 3f) moveIn();
    }

    @Annotations.Replace(value = 1)
    public void pickup(Unit unit){
        if(beamHeld == null){
            unit.remove();
            beamHeld = new UnitPayload(unit);
            Fx.unitPickup.at(unit);
            if(Vars.net.client()){
                Vars.netClient.clearRemovedEntity(unit.id);
            }
            Events.fire(new EventType.PickupEvent(self(), unit));
        }
    }

    @Annotations.Replace(value = 1)
    public void pickup(Building build){
        if(beamHeld == null){
            payPos.set(build.x, build.y);
            build.pickedUp();
            build.tile.remove();
            build.afterPickedUp();
            beamHeld = new BuildPayload(build);
            Fx.unitPickup.at(build);
            Events.fire(new EventType.PickupEvent(self(), build));
        }
    }

    @Annotations.Replace(value = 1)
    public boolean dropLastPayload(){
        if(beamHeld == null) return false;

        if(tryDropPayload(beamHeld)){
            beamHeld = null;
            return true;
        }
        return false;
    }

    @Annotations.Replace(value = 1)
    public boolean tryDropPayload(Payload payload){
        if(beamHeld == null || movingIn || payPos.dst(mouse) > 8f) return false;

        Tile on = Vars.world.tileWorld(mouseTileX * 8f, mouseTileY * 8f);
        //clear removed state of unit so it can be synced
        if(Vars.net.client() && payload instanceof UnitPayload){
            Vars.netClient.clearRemovedEntity(((UnitPayload)payload).unit.id);
        }

        //drop off payload on an acceptor if possible todo pickup sends to inventory immediately
        if(on.build != null && on.build.acceptPayload(on.build, payload)){
            Fx.unitDrop.at(on.build);
            on.build.handlePayload(on.build, payload);
            return true;
        }

        if(payload instanceof BuildPayload){
            return dropBlock((BuildPayload)payload);
        }else if(payload instanceof UnitPayload){
            return dropUnit((UnitPayload)payload);
        }
        return false;
    }

    /** @return whether the tile has been successfully placed. */
    @Annotations.Replace(value = 1)
    public boolean dropBlock(BuildPayload payload){
        Building payBuild = payload.build;
        Tile on = Vars.world.tile(mouseTileX, mouseTileY);
        if(on != null && Build.validPlace(payBuild.block, payBuild.team, mouseTileX, mouseTileY, payBuild.rotation, false)){
            payload.place(on, payBuild.rotation);
            Events.fire(new EventType.PayloadDropEvent(self(), payBuild));

            if(getControllerName() != null){
                payload.build.lastAccessed = getControllerName();
            }

            Fx.unitDrop.at(payBuild);
            Fx.placeBlock.at(on.drawx(), on.drawy(), on.block().size);
            return true;
        }

        return false;
    }
}
