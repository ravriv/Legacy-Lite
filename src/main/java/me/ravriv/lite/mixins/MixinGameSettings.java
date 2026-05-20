package me.ravriv.lite.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.GameSettings.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameSettings.class)
public class MixinGameSettings {
    @Unique private float lastGamma = -1.0F;

    @Inject(method = "setOptionFloatValue", at = @At("TAIL"))
    private void onGammaChanged(Options option, float value, CallbackInfo ci) {
        if (option == Options.GAMMA) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.theWorld != null && mc.renderGlobal != null) {
                if (lastGamma != value) {
                    mc.renderGlobal.loadRenderers();
                    lastGamma = value;
                }
            }
        }
    }
}