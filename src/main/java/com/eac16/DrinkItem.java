package com.eac16;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.item.Items;

public final class DrinkItem extends Item {
    public final int volumeMl;
    public final int abv;
    public DrinkItem(Properties props, int volumeMl, int abv) {super(props);this.volumeMl=volumeMl;this.abv=abv;}
    @Override public int getUseDuration(ItemStack stack) {return 32;}
    @Override public UseAnim getUseAnimation(ItemStack stack) {return UseAnim.DRINK;}
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }
    @Override public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
        if (living instanceof Player player) {
            if (!level.isClientSide) Intoxication.consume(player, volumeMl * abv / 100.0);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
                ItemStack empty = new ItemStack(Items.GLASS_BOTTLE);
                if (stack.isEmpty()) return empty;
                if (!player.getInventory().add(empty)) player.drop(empty, false);
            }
        }
        return stack;
    }
    @Override public InteractionResult useOn(UseOnContext ctx) {
        Level level=ctx.getLevel(); BlockPos target=ctx.getClickedPos().relative(ctx.getClickedFace());
        if (!ctx.getClickedFace().getAxis().isVertical() || ctx.getClickedFace().getStepY()!=1) return InteractionResult.PASS;
        if (!level.getBlockState(target).canBeReplaced()) return InteractionResult.PASS;
        if (!level.getBlockState(ctx.getClickedPos()).isFaceSturdy(level,ctx.getClickedPos(),ctx.getClickedFace())) return InteractionResult.PASS;
        if (!level.isClientSide) {
            level.setBlock(target,ModRegistry.PLACED_BOTTLE.get().defaultBlockState(),3);
            if (level.getBlockEntity(target) instanceof PlacedBottleEntity be) {
                be.setBottle(ctx.getItemInHand().copyWithCount(1));
            }
            if (ctx.getPlayer()==null || !ctx.getPlayer().getAbilities().instabuild) ctx.getItemInHand().shrink(1);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override public void appendHoverText(ItemStack stack, Level level, List<Component> list, TooltipFlag flag) {
        list.add(Component.literal(abv+"% ABV  ·  "+volumeMl+" ml").withStyle(ChatFormatting.GOLD));
        list.add(Component.translatable("tooltip.eac16.place").withStyle(ChatFormatting.GRAY));
    }
}
