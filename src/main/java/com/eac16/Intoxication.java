package com.eac16;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

public final class Intoxication {
    private static final String KEY="eac16_intox";
    private static final String TICK="eac16_intox_tick";
    public static double value(Player player) {return player.getPersistentData().getDouble(KEY);}
    // Pure ethanol (ml), converted to gameplay points. Not a blood-alcohol model.
    public static void consume(Player player, double ethanolMl) {
        double next=Math.min(100.0, value(player)+ethanolMl*0.65);
        player.getPersistentData().putDouble(KEY,next);
        sync(player);
    }
    private static void sync(Player player) {
        if (player instanceof ServerPlayer sp) NetPackets.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp), new NetPackets.IntoxPacket((float)value(player)));
    }
    @SubscribeEvent public static void tick(TickEvent.PlayerTickEvent evt) {
        Player player=evt.player;
        if (evt.phase != TickEvent.Phase.END || player.level().isClientSide) return;
        CompoundTag tag=player.getPersistentData();
        int ticks=tag.getInt(TICK)+1;
        if (ticks>=100) {
            ticks=0;
            if (value(player)>0) {
                tag.putDouble(KEY, Math.max(0, value(player)-1));
            }
            sync(player);
        }
        tag.putInt(TICK,ticks);
        // Gradual deterministic lateral influence, never abrupt view rotation.
        if (!player.isPassenger() && player.onGround() && value(player)>=60 && player.zza != 0 && !player.isShiftKeyDown()) {
            double strength=(value(player)-59)/41.0 * 0.018;
            double side=Math.sin((player.tickCount + player.getId()*27)*0.115)*strength;
            player.push(Math.cos(Math.toRadians(player.getYRot()))*side,0,Math.sin(Math.toRadians(player.getYRot()))*side);
        }
    }
    @SubscribeEvent public static void login(PlayerEvent.PlayerLoggedInEvent evt){sync(evt.getEntity());}
    @SubscribeEvent public static void clone(PlayerEvent.Clone evt) {
        CompoundTag old=evt.getOriginal().getPersistentData();
        if (old.contains(KEY)) evt.getEntity().getPersistentData().putDouble(KEY, old.getDouble(KEY));
    }
    @SubscribeEvent public static void respawn(PlayerEvent.PlayerRespawnEvent evt){sync(evt.getEntity());}
    private Intoxication() {}
}
