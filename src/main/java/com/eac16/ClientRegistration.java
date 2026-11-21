package com.eac16;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=Eac16Mod.ID,bus=Mod.EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class ClientRegistration {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){
        event.registerBlockEntityRenderer(ModRegistry.RACK_ENTITY.get(),RackRenderer::new);
        event.registerBlockEntityRenderer(ModRegistry.BOTTLE_ENTITY.get(),BottleRenderer::new);
    }
}
