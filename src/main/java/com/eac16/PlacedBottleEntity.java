package com.eac16;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class PlacedBottleEntity extends BlockEntity {
    private ItemStack bottle=ItemStack.EMPTY;
    public PlacedBottleEntity(BlockPos pos,BlockState state){super(ModRegistry.BOTTLE_ENTITY.get(),pos,state);}
    public void setBottle(ItemStack stack){bottle=stack.copyWithCount(1);setChanged();if(level!=null)level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),3);}
    public ItemStack take(){ItemStack copy=bottle;bottle=ItemStack.EMPTY;setChanged();return copy;}
    public ItemStack getBottle(){return bottle;}
    @Override protected void saveAdditional(CompoundTag tag){super.saveAdditional(tag);if(!bottle.isEmpty())tag.put("Bottle",bottle.save(new CompoundTag()));}
    @Override public void load(CompoundTag tag){super.load(tag);bottle=tag.contains("Bottle")?ItemStack.of(tag.getCompound("Bottle")):ItemStack.EMPTY;}
    @Override public CompoundTag getUpdateTag(){return saveWithoutMetadata();}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
}
