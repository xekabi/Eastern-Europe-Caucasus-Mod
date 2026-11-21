package com.eac16;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;

public class WineRackEntity extends BlockEntity {
    private final NonNullList<ItemStack> bottles=NonNullList.withSize(6,ItemStack.EMPTY);
    public WineRackEntity(BlockPos pos,BlockState state){super(ModRegistry.RACK_ENTITY.get(),pos,state);}
    public NonNullList<ItemStack> bottles(){return bottles;}
    public boolean insert(ItemStack input){
        if (!(input.getItem() instanceof DrinkItem))return false;
        for(int i=0;i<6;i++) if(bottles.get(i).isEmpty()){
            bottles.set(i,input.copyWithCount(1));changed();return true;
        }
        return false;
    }
    public ItemStack removeLast(){
        for(int i=5;i>=0;i--) if(!bottles.get(i).isEmpty()){
            ItemStack out=bottles.get(i);bottles.set(i,ItemStack.EMPTY);changed();return out;
        }
        return ItemStack.EMPTY;
    }
    private void changed(){setChanged();if(level!=null)level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),3);}
    @Override protected void saveAdditional(CompoundTag tag){super.saveAdditional(tag);ContainerHelper.saveAllItems(tag,bottles);}
    @Override public void load(CompoundTag tag){super.load(tag);for(int i=0;i<6;i++)bottles.set(i,ItemStack.EMPTY);ContainerHelper.loadAllItems(tag,bottles);}
    @Override public CompoundTag getUpdateTag(){return saveWithoutMetadata();}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
}
