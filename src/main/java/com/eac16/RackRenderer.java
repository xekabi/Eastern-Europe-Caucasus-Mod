package com.eac16;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

public final class RackRenderer implements BlockEntityRenderer<WineRackEntity> {
    public RackRenderer(BlockEntityRendererProvider.Context ignored) {}
    @Override public void render(WineRackEntity rack,float delta,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        for(int i=0;i<rack.bottles().size();i++){
            ItemStack bottle=rack.bottles().get(i);if(bottle.isEmpty())continue;
            pose.pushPose();
            pose.translate(0.26+0.24*(i%3),0.29+0.43*(i/3),0.51);
            pose.scale(0.42f,0.42f,0.42f);
            Minecraft.getInstance().getItemRenderer().renderStatic(bottle,ItemDisplayContext.FIXED,light,overlay,pose,buffers,rack.getLevel(),i);
            pose.popPose();
        }
    }
}
