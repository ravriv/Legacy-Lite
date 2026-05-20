package me.ravriv.lite.mixins;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderEntityItem.class)
public abstract class MixinRenderEntityItem {
    @Shadow protected abstract int func_177078_a(ItemStack stack);

    @Inject(method = "func_177077_a", at = @At("HEAD"), cancellable = true)
    private void renderItem(EntityItem entity, double x, double y, double z, float partialTicks, IBakedModel model, CallbackInfoReturnable<Integer> cir) {
        ItemStack itemstack = entity.getEntityItem();
        Item item = itemstack.getItem();

        if (item == null) cir.setReturnValue(0);

        boolean is3D = model.isGui3d();

        float offset = is3D ? -0.125f : -0.175f;

        float scale = model.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y;

        GlStateManager.translate((float) x, (float) y + offset + 0.25F * scale, (float) z);

        if (!is3D && entity.onGround) GlStateManager.rotate(180, 0f, 1f, 1f);

        float speed = 10;

        if (!entity.onGround) {
            float rotAmount = ((float) entity.getAge() * speed) % 360;
            GlStateManager.rotate(rotAmount, 1f, 0f, 1f);
        }

        GlStateManager.resetColor();
        cir.setReturnValue(func_177078_a(itemstack));
    }

    @Inject(method = "func_177078_a", at = @At("HEAD"), cancellable = true)
    private void changeStackType(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(1);
    }

    @Inject(method = "shouldBob", at = @At("HEAD"), cancellable = true, remap = false)
    private void shouldBob(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}