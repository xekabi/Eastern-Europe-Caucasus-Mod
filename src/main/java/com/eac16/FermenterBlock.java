package com.eac16;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.Containers;
import net.minecraft.world.level.block.Block;
import javax.annotation.Nullable;

public final class FermenterBlock extends BaseEntityBlock {
    public FermenterBlock(Properties properties){super(properties);}
    @Override public RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Nullable @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new FermenterEntity(pos,state);}
    @Nullable @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){
        return level.isClientSide?null:createTickerHelper(type,ModRegistry.FERMENTER_ENTITY.get(),FermenterEntity::tick);
    }
    @Override public InteractionResult use(BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit) {
        ItemStack held=player.getItemInHand(hand);
        if(!level.isClientSide && level.getBlockEntity(pos) instanceof FermenterEntity be) {
            if(held.is(ModRegistry.GRAPE_MUST.get()) && !be.isReady() && be.servings()<12){be.addMust();if(!player.getAbilities().instabuild)held.shrink(1);return InteractionResult.CONSUME;}
            if(held.is(Items.GLASS_BOTTLE) && be.isReady() && be.servings()>0){
                ItemStack wine=be.result();be.takeServing();
                if(!player.getAbilities().instabuild)held.shrink(1);
                if(!player.getInventory().add(wine))player.drop(wine,false);
                return InteractionResult.CONSUME;
            }
            player.displayClientMessage(Component.literal(be.isReady()?"Wine ready: "+be.servings()+" servings": "Must: "+be.servings()+"/12 | fermentation: "+(be.progress()*100/FermenterEntity.FERMENT_TICKS)+"%"),true);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override public void onRemove(BlockState old,Level level,BlockPos pos,BlockState now,boolean moving){
        if(!old.is(now.getBlock()) && !level.isClientSide && level.getBlockEntity(pos) instanceof FermenterEntity be){
            for(int i=0;i<be.servings();i++){
                ItemStack drop=be.isReady()?be.result():ModRegistry.GRAPE_MUST.get().getDefaultInstance();
                Containers.dropItemStack(level,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,drop);
            }
        }
        super.onRemove(old,level,pos,now,moving);
    }
}
