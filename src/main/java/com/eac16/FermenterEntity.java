package com.eac16;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FermenterEntity extends BlockEntity {
    private int servings=0;
    private int progress=0;
    private boolean ready=false;
    public static final int FERMENT_TICKS=2400;
    public FermenterEntity(BlockPos pos,BlockState state){super(ModRegistry.FERMENTER_ENTITY.get(),pos,state);}
    public void addMust(){if(servings<12){servings++;setChanged();}}
    public int servings(){return servings;}
    public int progress(){return progress;}
    public boolean isReady(){return ready;}
    public void takeServing(){if(ready&&servings>0){servings--;if(servings==0){ready=false;progress=0;}setChanged();}}
    public ItemStack result(){return (getBlockState().is(ModRegistry.QVEVRI.get())?ModRegistry.DRINKS.get("kakhetian_amber_wine"):ModRegistry.DRINKS.get("kakhetian_red_wine")).get().getDefaultInstance();}
    public static void tick(Level level,BlockPos pos,BlockState state,FermenterEntity be){
        if(level.isClientSide||be.ready||be.servings==0)return;
        be.progress++;
        if(be.progress>=FERMENT_TICKS){be.ready=true;be.setChanged();level.sendBlockUpdated(pos,state,state,3);}
        if(be.progress%200==0)be.setChanged();
    }
    @Override protected void saveAdditional(CompoundTag tag){super.saveAdditional(tag);tag.putInt("Servings",servings);tag.putInt("Progress",progress);tag.putBoolean("Ready",ready);}
    @Override public void load(CompoundTag tag){super.load(tag);servings=tag.getInt("Servings");progress=tag.getInt("Progress");ready=tag.getBoolean("Ready");}
    @Override public CompoundTag getUpdateTag(){return saveWithoutMetadata();}
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
}
