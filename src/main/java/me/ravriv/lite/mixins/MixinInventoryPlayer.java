package me.ravriv.lite.mixins;

import net.minecraft.entity.player.InventoryPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryPlayer.class)
public class MixinInventoryPlayer {
    @Inject(method = "changeCurrentItem", at = @At("HEAD"), cancellable = true)
    private void changeCurrentItem(int direction, CallbackInfo ci) {
        if (me.ravriv.lite.handlers.ZoomHandler.isZooming()) {
            ci.cancel();
        }
    }
}