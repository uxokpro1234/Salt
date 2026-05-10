package me.uxokpro1234.salt.module.modules.misc;

import me.uxokpro1234.salt.module.Module;
import org.lwjgl.input.Keyboard;

public class FreeCam extends Module {

    public FreeCam() {
        super("FreeCam", Category.MISC, Keyboard.KEY_NONE);
    }


    /*@Override
    public void onEnable() {
        if (mc.thePlayer == null) return;

        if (motionValue.get()) {
            motionX = mc.thePlayer.motionX;
            motionY = mc.thePlayer.motionY;
            motionZ = mc.thePlayer.motionZ;
        } else {
            motionX = 0;
            motionY = 0;
            motionZ = 0;
        }

        packetCount = 0;

        fakePlayer = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
        fakePlayer.clonePlayer(mc.thePlayer, true);
        fakePlayer.rotationYawHead = mc.thePlayer.rotationYawHead;
        fakePlayer.copyLocationAndAnglesFrom(mc.thePlayer);

        mc.theWorld.addEntityToWorld((int) (Math.random() * 10000), fakePlayer);

        if (noClipValue.get()) {
            mc.thePlayer.noClip = true;
        }
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer == null || fakePlayer == null) return;

        mc.thePlayer.setPositionAndRotation(
                fakePlayer.posX,
                fakePlayer.posY,
                fakePlayer.posZ,
                mc.thePlayer.rotationYaw,
                mc.thePlayer.rotationPitch
        );

        mc.theWorld.removeEntityFromWorld(fakePlayer.getEntityId());
        fakePlayer = null;

        mc.thePlayer.motionX = motionX;
        mc.thePlayer.motionY = motionY;
        mc.thePlayer.motionZ = motionZ;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {

        if (noClipValue.get()) {
            mc.thePlayer.noClip = true;
        }

        mc.thePlayer.fallDistance = 0f;

        if (flyValue.get()) {
            double speed = speedValue.get();

            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionY = 0;
            mc.thePlayer.motionZ = 0;

            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                mc.thePlayer.motionY += speed;
            }

            if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                mc.thePlayer.motionY -= speed;
            }

            MovementUtils.strafe((float) speed);
        }
    }

    public void onPacket(PacketEvent event) {
        Object packet = event.getPacket();

        if (c03SpoofValue.get()) {

            if (packet instanceof C03PacketPlayer) {

                if (packetCount >= 20) {
                    packetCount = 0;

                    mc.getNetHandler().addToSendQueue(
                            new C03PacketPlayer.C06PacketPlayerPosLook(
                                    fakePlayer.posX,
                                    fakePlayer.posY,
                                    fakePlayer.posZ,
                                    fakePlayer.rotationYaw,
                                    fakePlayer.rotationPitch,
                                    fakePlayer.onGround
                            )
                    );
                } else {
                    packetCount++;
                    PacketUtils.sendPacketNoEvent(new C03PacketPlayer(fakePlayer.onGround));
                }

                event.cancelEvent();
            }

        } else if (packet instanceof C03PacketPlayer) {
            event.setCanceled(true);
        }

        if (packet instanceof S08PacketPlayerPosLook) {

            S08PacketPlayerPosLook p = (S08PacketPlayerPosLook) packet;

            fakePlayer.setPosition(p.getX(), p.getY(), p.getZ());

            motionX = motionY = motionZ = 0;

            PacketUtils.sendPacketNoEvent(
                    new C03PacketPlayer.C06PacketPlayerPosLook(
                            fakePlayer.posX,
                            fakePlayer.posY,
                            fakePlayer.posZ,
                            fakePlayer.rotationYaw,
                            fakePlayer.rotationPitch,
                            fakePlayer.onGround
                    )
            );

            event.cancelEvent();
        }
    }*/
}