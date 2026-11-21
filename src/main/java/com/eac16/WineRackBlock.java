package com.eac16;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.Containers;
import javax.annotation.Nullable;

public class WineRackBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE=Block.box(0,0,1,16,16,15);
    public WineRackBlock(Properties props){super(props);}
    @Override public RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Nullable @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new WineRackEntity(pos,state);}
    @Override public VoxelShape getShape(BlockState state,net.minecraft.world.level.BlockGetter getter,BlockPos pos,CollisionContext ctx){return SHAPE;}
    @Override public InteractionResult use(BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
        ItemStack held=player.getItemInHand(hand);
        if(!(held.isEmpty() || held.getItem() instanceof DrinkItem))return InteractionResult.PASS;
        if(!level.isClientSide && level.getBlockEntity(pos) instanceof WineRackEntity rack){
            if(held.getItem() instanceof DrinkItem){
                if(rack.insert(held) && !player.getAbilities().instabuild)held.shrink(1);
            } else {
                ItemStack out=rack.removeLast();if(!out.isEmpty() && !player.getInventory().add(out))player.drop(out,false);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override public void onRemove(BlockState old,Level level,BlockPos pos,BlockState now,boolean moving){
        if(!old.is(now.getBlock()) && level.getBlockEntity(pos) instanceof WineRackEntity rack){
            if(!level.isClientSide) for(ItemStack item:rack.bottles())if(!item.isEmpty())Containers.dropItemStack(level,pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,item);
        }
        super.onRemove(old,level,pos,now,moving);
    }
}
