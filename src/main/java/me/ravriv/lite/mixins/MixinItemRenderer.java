package me.ravriv.lite.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {
    @Shadow @Final private Minecraft mc;
    @Shadow private ItemStack itemToRender;
    @Shadow private float equippedProgress;
    @Shadow private float prevEquippedProgress;
    @Unique private float capturedSwingProgress;

    @Inject(method = "renderFireInFirstPerson", at = @At("HEAD"), cancellable = true)
    private void handleFireRenderStart(CallbackInfo ci) {
        if (mc.thePlayer.isPotionActive(Potion.fireResistance)) {
            ci.cancel();
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(0f, -0.2f, 0f);
    }

    @Inject(method = "renderFireInFirstPerson", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;pushMatrix()V", shift = At.Shift.AFTER))
    private void applyFireOverlayOpacity(CallbackInfo ci) {
        GlStateManager.color(1f, 1f, 1f, 0.2f);
    }

    @Inject(method = "renderFireInFirstPerson", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;popMatrix()V"))
    private void resetFireOverlayColor(CallbackInfo ci) {
        GlStateManager.color(1f, 1f, 1f, 1f);
    }

    @Inject(method = "renderFireInFirstPerson", at = @At("TAIL"))
    private void handleFireRenderEnd(CallbackInfo ci) {
        GlStateManager.popMatrix();
    }

    @Inject(method = "renderWaterOverlayTexture", at = @At("HEAD"), cancellable = true)
    private void renderWaterOverlayTexture(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "renderItemInFirstPerson", at = @At("HEAD"))
    private void captureSwingProgress(float partialTicks, CallbackInfo ci) {
        capturedSwingProgress = mc.thePlayer.getSwingProgress(partialTicks);
    }

    @ModifyConstant(method = "renderItemInFirstPerson", constant = @Constant(floatValue = 0.0F))
    private float overrideSwingProgress(float original) {
        return capturedSwingProgress;
    }

    @ModifyConstant(method = "doBowTransformations", constant = @Constant(floatValue = 0.01F), require = 1, expect = 1)
    private float cancelBowShake(float original) {
        return 0F;
    }

    @Inject(method = "updateEquippedItem", at = @At("TAIL"))
    private void equipAnimation(CallbackInfo ci) {
        this.itemToRender = Minecraft.getMinecraft().thePlayer.getHeldItem();
        this.equippedProgress = 1.0F;
        this.prevEquippedProgress = 1.0F;
    }

    @Inject(method = "renderItemInFirstPerson", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private void transformFishingRod(float partialTicks, CallbackInfo ci) {
        if (itemToRender != null && itemToRender.getItem() == Items.fishing_rod) {
            GL11.glTranslatef(0.08f, -0.03f, -0.33f);
            GL11.glScalef(0.93f, 1.0f, 1.0f);
        }
    }

    @Inject(method = "renderItemInFirstPerson", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;doBlockTransformations()V", shift = At.Shift.AFTER))
    private void transformBlockingSword(float partialTicks, CallbackInfo ci) {
        GL11.glScalef(0.83f, 0.88f, 0.85f);
        GL11.glTranslatef(-0.3f, 0.1f, 0.0f);
    }

    @Inject(method = "doBowTransformations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;scale(FFF)V"))
    private void preBowScale(CallbackInfo ci) {
        GlStateManager.rotate(-335.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(-50.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(0.0F, 0.5F, 0.0F);
    }

    @Inject(method = "doBowTransformations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;scale(FFF)V", shift = At.Shift.AFTER))
    private void postBowScale(CallbackInfo ci) {
        GlStateManager.translate(0.0F, -0.5F, 0.0F);
        GlStateManager.rotate(50.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(335.0F, 0.0F, 0.0F, 1.0F);
    }

    @Inject(method = "renderItemInFirstPerson", at = @At("HEAD"))
    private void blockHitSwing(float partialTicks, CallbackInfo ci) {
        if (mc.gameSettings.keyBindAttack.isKeyDown()
                && mc.gameSettings.keyBindUseItem.isKeyDown()
                && mc.objectMouseOver != null
                && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {

            EntityPlayerSP player = mc.thePlayer;
            int swingDuration = 6;

            PotionEffect haste = player.getActivePotionEffect(Potion.digSpeed);
            PotionEffect fatigue = player.getActivePotionEffect(Potion.digSlowdown);

            if (haste != null) {
                swingDuration -= (1 + haste.getAmplifier());
            } else if (fatigue != null) {
                swingDuration += (1 + fatigue.getAmplifier()) * 2;
            }

            if (!player.isSwingInProgress || player.swingProgressInt >= swingDuration / 2 || player.swingProgressInt < 0) {
                player.swingProgressInt = -1;
                player.isSwingInProgress = true;
            }
        }
    }
}