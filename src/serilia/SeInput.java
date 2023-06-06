package serilia;

import arc.Core;
import arc.math.Angles;
import arc.util.Log;
import mindustry.Vars;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.input.*;
import serilia.gen.entities.TractorBeamc;

public class SeInput extends DesktopInput{

    @Override
    protected void updateMovement(Unit unit) {
        boolean omni = unit.type.omniMovement;
        float speed = unit.speed();
        float xa = Core.input.axis(Binding.move_x);
        float ya = Core.input.axis(Binding.move_y);
        boolean boosted = unit instanceof Mechc && unit.isFlying();
        movement.set(xa, ya).nor().scl(speed);
        if (Core.input.keyDown(Binding.mouse_move)) {
            movement.add(Core.input.mouseWorld().sub(Vars.player).scl(0.04F * speed)).limit(speed);
        }

        float mouseAngle = Angles.mouseAngle(unit.x, unit.y);
        boolean aimCursor = omni && Vars.player.shooting && unit.type.hasWeapons() && unit.type.faceTarget && !boosted;
        if (aimCursor) {
            unit.lookAt(mouseAngle);
        } else {
            unit.lookAt(unit.prefRotation());
        }

        unit.movePref(movement);
        unit.aim(Core.input.mouseWorld());
        unit.controlWeapons(true, Vars.player.shooting && !boosted);
        Vars.player.boosting = Core.input.keyDown(Binding.boost);
        Vars.player.mouseX = unit.aimX();
        Vars.player.mouseY = unit.aimY();

        if (unit instanceof PayloadUnit) {
            if (Core.input.keyTap(Binding.pickupCargo)) {
                tryPickupPayload();
            }

            if (Core.input.keyTap(Binding.dropCargo)) {
                tryDropPayload();
            }
        }
    }

    @Override
    public void tryDropPayload() {
        Unit unit = Vars.player.unit();
        if (unit instanceof TractorBeamc) {
            if(((TractorBeamc)unit).beamHeld() != null) {
                Call.requestDropPayload(Vars.player, Vars.player.x, Vars.player.y);
            } else ((TractorBeamc)unit).moveOut();
        } else if (unit instanceof Payloadc) {
            Call.requestDropPayload(Vars.player, Vars.player.x, Vars.player.y);
        }
    }

    @Override
    public void tryPickupPayload() {
        Unit unit = Vars.player.unit();
        if (unit instanceof TractorBeamc) {
            TractorBeamc pay = (TractorBeamc)unit;
            Unit target = Units.closest(Vars.player.team(), pay.x(), pay.y(), unit.type.hitSize * 2.0F, (u) -> {
                return u.isAI() && u.isGrounded() && pay.canPickup(u) && u.within(unit, u.hitSize + unit.hitSize);
            });
            if(pay.beamHeld() == null) {
                if (target != null) {
                    Call.requestUnitPayload(Vars.player, target);
                } else {
                    Building build = Vars.world.buildWorld(pay.aimX(), pay.aimY());
                    if (build != null && Vars.state.teams.canInteract(unit.team, build.team)) {
                        Call.requestBuildPayload(Vars.player, build);
                    }
                }
            } else pay.movingIn(true);
        } else if (unit instanceof Payloadc) {
            Payloadc pay = (Payloadc)unit;
            Unit target = Units.closest(Vars.player.team(), pay.x(), pay.y(), unit.type.hitSize * 2.0F, (u) -> {
                return u.isAI() && u.isGrounded() && pay.canPickup(u) && u.within(unit, u.hitSize + unit.hitSize);
            });
            if (target != null) {
                Call.requestUnitPayload(Vars.player, target);
            } else {
                Building build = Vars.world.buildWorld(pay.x(), pay.y());
                if (build != null && Vars.state.teams.canInteract(unit.team, build.team)) {
                    Call.requestBuildPayload(Vars.player, build);
                    Log.info("no");
                }
            }
        }
    }
}
