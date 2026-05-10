package me.uxokpro1234.salt.event.events;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.client.entity.EntityPlayerSP;

@Cancelable
public class PlayerMoveEvent extends Event {
    private final EntityPlayerSP player;
    private double moveForward;
    private double moveStrafe;

    public PlayerMoveEvent(EntityPlayerSP player, double moveForward, double moveStrafe) {
        this.player = player;
        this.moveForward = moveForward;
        this.moveStrafe = moveStrafe;
    }

    public EntityPlayerSP getPlayer() { return player; }
    public double getMoveForward() { return moveForward; }
    public double getMoveStrafe() { return moveStrafe; }
    public void setMoveForward(double moveForward) { this.moveForward = moveForward; }
    public void setMoveStrafe(double moveStrafe) { this.moveStrafe = moveStrafe; }
}