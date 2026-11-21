package com.eac16;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(Eac16Mod.ID)
public final class Eac16Mod {
    public static final String ID = "eac16";
    public Eac16Mod() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModRegistry.BLOCKS.register(bus);
        ModRegistry.ITEMS.register(bus);
        ModRegistry.BLOCK_ENTITIES.register(bus);
        ModRegistry.TABS.register(bus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, VisualConfig.SPEC);
        bus.addListener(this::onSetup);
        MinecraftForge.EVENT_BUS.register(Intoxication.class);
    }
    private void onSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(NetPackets::register);
    }
}
