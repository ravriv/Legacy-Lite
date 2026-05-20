package me.ravriv.lite.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LayerHeldItem.class)
public class MixinLayerHeldItem {
    @Shadow @Final private RendererLivingEntity<?> livingEntityRenderer;

    @Overwrite
    public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!(entity instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) entity;
        ItemStack stack = player.getHeldItem();
        if (stack == null) return;

        GlStateManager.pushMatrix();
        ModelBiped model = (ModelBiped) this.livingEntityRenderer.getMainModel();

        if (player.isBlocking()) {
            model.postRenderArm(0.0325F);
            GlStateManager.scale(1.05F, 1.05F, 1.05F);
            if (player.isSneaking()) {
                GlStateManager.translate(-0.37F, 0.57F, 0.02F);
            } else {
                GlStateManager.translate(-0.23F, 0.48F, 0.02F);
            }
            GlStateManager.rotate(-24405.0F, 137290.0F, -2009900.0F, -2654900.0F);
        } else {
            model.postRenderArm(0.0625F);
            GlStateManager.translate(-0.0855F, 0.4775F, 0.1585F);
            GlStateManager.rotate(-19.0F, 20.0F, 0.0F, -6.0F);
        }

        if (player.fishEntity != null) {
            stack = new ItemStack(Items.fishing_rod, 0);
        }

        if (player.isSneaking()) {
            GlStateManager.translate(0.0F, 0.203125F, 0.0F);
        }

        Minecraft.getMinecraft().getItemRenderer().renderItem(player, stack, ItemCameraTransforms.TransformType.THIRD_PERSON);
        GlStateManager.popMatrix();
    }
}