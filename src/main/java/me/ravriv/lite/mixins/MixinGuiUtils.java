package me.ravriv.lite.mixins;

import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import me.ravriv.lite.handlers.TooltipHandler;

import java.util.List;

@Mixin(value = GuiUtils.class, remap = false)
public class MixinGuiUtils {
    @Unique private static int tooltipY, tooltipHeight;

    @ModifyVariable(method = "drawHoveringText", at = @At("STORE"), name = "tooltipY", remap = false)
    private static int renderY(int y) {
        tooltipY = y;
        return y;
    }

    @ModifyVariable(method = "drawHoveringText", at = @At("STORE"), name = "tooltipHeight", remap = false)
    private static int renderHeight(int h) {
        tooltipHeight = h;
        return h;
    }

    @Inject(method = "drawHoveringText", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/client/config/GuiUtils;drawGradientRect(IIIIIII)V", ordinal = 0), remap = false)
    private static void beforeRender(List<String> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font, CallbackInfo ci) {
        TooltipHandler.push();
        TooltipHandler.transform(tooltipY, tooltipHeight, screenHeight);
    }

    @Inject(method = "drawHoveringText", at = @At("TAIL"), remap = false)
    private static void afterRender(List<String> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font, CallbackInfo ci) {
        TooltipHandler.pop();
    }
}