package com.eac16;

import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
import javax.annotation.Nullable;

public final class PlacedBottleBlock extends BaseEntityBlock {
    public PlacedBottleBlock(Properties props){super(props);}
    @Override public RenderShape getRenderShape(BlockState state){return RenderShape.INVISIBLE;}
    @Nullable @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new PlacedBottleEntity(pos,state);}
    @Override public VoxelShape getShape(BlockState state,BlockGetter getter,BlockPos pos,CollisionContext context){return Block.box(6,0,6,10,14,10);}
    @Override public InteractionResult use(BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
        if(!level.isClientSide && level.getBlockEntity(pos) instanceof PlacedBottleEntity entity){
            ItemStack out=entity.take();if(!out.isEmpty() && !player.getInventory().add(out))player.drop(out,false);
            level.removeBlock(pos,false);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override public void onRemove(BlockState old,Level level,BlockPos pos,BlockState now,boolean moving){
        if(!old.is(now.getBlock()) && !level.isClientSide && level.getBlockEntity(pos) instanceof PlacedBottleEntity entity){
            ItemStack out=entity.take();if(!out.isEmpty())Containers.dropItemStack(level,pos.getX()+.5,pos.getY()+.4,pos.getZ()+.5,out);
        }
        super.onRemove(old,level,pos,now,moving);
    }
}
