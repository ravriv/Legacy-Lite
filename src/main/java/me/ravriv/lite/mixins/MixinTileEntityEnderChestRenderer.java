package me.ravriv.lite.mixins;

import net.minecraft.client.renderer.tileentity.TileEntityEnderChestRenderer;
import net.minecraft.tileentity.TileEntityEnderChest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityEnderChestRenderer.class)
public class MixinTileEntityEnderChestRenderer {
    @Inject(method = "renderTileEntityAt*", at = @At("HEAD"))
    private void onERender(TileEntityEnderChest te, double x, double y, double z, float partialTicks, int destroyStage, CallbackInfo ci) {
        te.lidAngle = 0.0F;
        te.prevLidAngle = 0.0F;
    }
}

