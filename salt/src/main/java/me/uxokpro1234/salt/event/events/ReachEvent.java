package me.uxokpro1234.salt.event.events;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.entity.Entity;

public class ReachEvent extends Event {

    private Entity target;
    private double reach;

    public ReachEvent(Entity target, double reach) {
        this.target = target;
        this.reach = reach;
    }

    public Entity getTarget() {
        return target;
    }

    public double getReach() {
        return reach;
    }

    public void setReach(double reach) {
        this.reach = reach;
    }
}