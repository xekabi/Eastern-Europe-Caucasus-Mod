package com.eac16;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.minecraft.util.Mth;

public final class FirearmItem extends Item {
    private final float damage, spread;
    private final int reloadTicks, cooldown;
    public FirearmItem(Properties properties,float damage,float spread,int reloadTicks,int cooldown) {
        super(properties);this.damage=damage;this.spread=spread;this.reloadTicks=reloadTicks;this.cooldown=cooldown;
    }
    private boolean loaded(ItemStack stack){return stack.getOrCreateTag().getBoolean("Loaded");}
    @Override public int getUseDuration(ItemStack stack){return reloadTicks;}
    @Override public UseAnim getUseAnimation(ItemStack stack){return UseAnim.BOW;}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand) {
        ItemStack gun=player.getItemInHand(hand);
        if (!loaded(gun)) {
            if (!player.getAbilities().instabuild && (find(player,ModRegistry.IRON_SHOT.get())<0 || find(player,Items.GUNPOWDER)<0)) {
                if(!level.isClientSide) player.displayClientMessage(Component.translatable("message.eac16.ammo"),true);
                return InteractionResultHolder.fail(gun);
            }
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(gun);
        }
        if (!level.isClientSide) fire(level,player,gun);
        player.getCooldowns().addCooldown(this,cooldown);
        return InteractionResultHolder.sidedSuccess(gun,level.isClientSide);
    }
    @Override public ItemStack finishUsingItem(ItemStack gun,Level level,LivingEntity entity) {
        if (!level.isClientSide && entity instanceof Player player && !loaded(gun)) {
            if (player.getAbilities().instabuild || (take(player,ModRegistry.IRON_SHOT.get()) && take(player,Items.GUNPOWDER))) {
                gun.getOrCreateTag().putBoolean("Loaded",true);
                level.playSound(null,player.blockPosition(),SoundEvents.CROSSBOW_LOADING_END,SoundSource.PLAYERS,0.55f,0.65f);
            }
        }
        return gun;
    }
    private static int find(Player player,Item item) {
        for(int i=0;i<player.getInventory().getContainerSize();i++) if(player.getInventory().getItem(i).is(item)) return i;
        return -1;
    }
    private static boolean take(Player player,Item item){int i=find(player,item);if(i<0)return false;player.getInventory().getItem(i).shrink(1);return true;}
    private void fire(Level level,Player player,ItemStack gun) {
        gun.getOrCreateTag().putBoolean("Loaded",false);
        gun.hurtAndBreak(1,player,e->e.broadcastBreakEvent(player.getUsedItemHand()));
        Vec3 from=player.getEyePosition();
        Vec3 direction=player.getLookAngle().add(player.getRandom().nextGaussian()*spread,player.getRandom().nextGaussian()*spread,player.getRandom().nextGaussian()*spread).normalize();
        Vec3 end=from.add(direction.scale(58));
        BlockHitResult solid=level.clip(new ClipContext(from,end,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,player));
        if(solid.getType()!=HitResult.Type.MISS)end=solid.getLocation();
        LivingEntity hit=null;double best=from.distanceToSqr(end);
        var bounds=player.getBoundingBox().expandTowards(end.subtract(from)).inflate(1.0);
        for(LivingEntity candidate:level.getEntitiesOfClass(LivingEntity.class,bounds, e->e!=player && e.isAlive())) {
            var intersection=candidate.getBoundingBox().inflate(0.2).clip(from,end);
            if(intersection.isPresent()){
                double dist=from.distanceToSqr(intersection.get());
                if(dist<best){best=dist;hit=candidate;}
            }
        }
        if(hit!=null)hit.hurt(level.damageSources().playerAttack(player),damage);
        level.playSound(null,player.blockPosition(),SoundEvents.GENERIC_EXPLODE,SoundSource.PLAYERS,0.75f,1.2f);
        if(level instanceof ServerLevel server) server.sendParticles(ParticleTypes.SMOKE,from.x,from.y,from.z,18,0.16,0.13,0.16,0.045);
    }
    @Override public void appendHoverText(ItemStack stack,Level level,List<Component> list,TooltipFlag flag) {
        list.add(Component.translatable(loaded(stack)?"tooltip.eac16.loaded":"tooltip.eac16.unloaded").withStyle(loaded(stack)?ChatFormatting.GREEN:ChatFormatting.RED));
        list.add(Component.literal("Shot + gunpowder | "+reloadTicks/20f+"s reload").withStyle(ChatFormatting.GRAY));
    }
}
