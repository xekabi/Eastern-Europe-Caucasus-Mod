package com.eac16;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import java.util.function.Supplier;

public final class NetPackets {
    private static final String VERSION = "1";
    public static final SimpleChannel CHANNEL=NetworkRegistry.newSimpleChannel(new ResourceLocation(Eac16Mod.ID,"network"),()->VERSION,VERSION::equals,VERSION::equals);
    public static void register() {CHANNEL.registerMessage(0,IntoxPacket.class,IntoxPacket::encode,IntoxPacket::decode,IntoxPacket::handle);}
    public record IntoxPacket(float amount) {
        static void encode(IntoxPacket packet,FriendlyByteBuf buf) {buf.writeFloat(packet.amount);}
        static IntoxPacket decode(FriendlyByteBuf buf){return new IntoxPacket(buf.readFloat());}
        static void handle(IntoxPacket msg,Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientAlcohol.set(msg.amount)));
            context.get().setPacketHandled(true);
        }
    }
    private NetPackets() {}
}
