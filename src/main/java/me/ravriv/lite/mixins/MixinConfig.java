package me.ravriv.lite.mixins;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Pseudo @Mixin(targets = "Config")
public class MixinConfig {
    @Dynamic("OptiFine")
    @Inject(method = "drawFps", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/renderer/RenderGlobal;getCountTileEntitiesRendered()I"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT, remap = false)
    private static void simplifyFpsCounter(CallbackInfo ci, int fps) {
        final String fpsStr = "FPS: " + fps;
        Minecraft.getMinecraft().fontRendererObj.drawString(fpsStr, 2, 2, -2039584);
        ci.cancel();
    }
}