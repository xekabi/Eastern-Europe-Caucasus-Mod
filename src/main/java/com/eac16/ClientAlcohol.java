package com.eac16;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid=Eac16Mod.ID,value=Dist.CLIENT,bus=Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientAlcohol {
    private static float target=0;
    private static float smooth=0;
    public static void set(float amount) {target=Math.max(0, Math.min(100, amount));}
    @SubscribeEvent public static void camera(ViewportEvent.ComputeCameraAngles evt) {
        var mc=Minecraft.getInstance();
        if (mc.player==null || mc.isPaused() || mc.options.getCameraType().isDetached()) return;
        smooth=Mth.lerp(0.025f,smooth,target);
        float intensity=Math.max(0f,(smooth-14f)/86f)*(float)(double)VisualConfig.CAMERA_SWAY.get();
        if (intensity<=0.001f) return;
        double t=mc.level.getGameTime()+evt.getPartialTick();
        float roll=(float)(Math.sin(t*0.067)+0.45*Math.sin(t*0.113))*1.8f*intensity;
        float pitch=(float)Math.sin(t*0.051)*0.55f*intensity;
        float yaw=(float)Math.sin(t*0.039)*0.65f*intensity;
        evt.setRoll(evt.getRoll()+roll);
        evt.setPitch(evt.getPitch()+pitch);
        evt.setYaw(evt.getYaw()+yaw);
    }
    private ClientAlcohol() {}
}
