package com.eac16;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** Displays the specific 16x16 bottle sprite stored by this block entity.
 *  The renderer uses the vanilla ItemRenderer instead of a complex voxel mesh. */
public final class BottleRenderer implements BlockEntityRenderer<PlacedBottleEntity> {
    public BottleRenderer(BlockEntityRendererProvider.Context ignored) {}

    @Override public void render(PlacedBottleEntity be, float delta, PoseStack pose,
                                MultiBufferSource buffers, int light, int overlay) {
        ItemStack stack = be.getBottle();
        if (stack.isEmpty()) return;
        pose.pushPose();
        pose.translate(0.5, 0.43, 0.5);
        pose.scale(0.72f, 0.72f, 0.72f);
        Minecraft.getInstance().getItemRenderer().renderStatic(stack,
            ItemDisplayContext.FIXED, light, overlay, pose, buffers, be.getLevel(), 0);
        pose.popPose();
    }
}
