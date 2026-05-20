package me.ravriv.lite.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {
    @Shadow @Final protected Minecraft mc;
    @Shadow protected int titlesTimer;
    @Shadow protected int titleDisplayTime;

    @Redirect(method = "renderScoreboard", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;III)I", ordinal = 1))
    private int renderScoreboard(FontRenderer fontRenderer, String text, int x, int y, int color) {
        return -1;
    }

    @ModifyConstant(method = "renderScoreboard", constant = @Constant(intValue = 553648127))
    private int renderScoreboard(int original) {
        return -1;
    }

    @Inject(method = "displayTitle", at = @At("TAIL"))
    private void displayTitle(String title, String subTitle, int fadeIn, int displayTime, int fadeOut, CallbackInfo ci) {
        this.titleDisplayTime = 30;
        this.titlesTimer = this.titleDisplayTime;
    }

    @Inject(method = "showCrosshair", at = @At("HEAD"), cancellable = true)
    private void showCrosshair(CallbackInfoReturnable<Boolean> cir) {
        if (mc.currentScreen != null || mc.gameSettings.thirdPersonView != 0) {
            cir.setReturnValue(false);
        }
    }
}