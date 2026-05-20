package me.ravriv.lite.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat {
    @Shadow @Final private Minecraft mc;
    @Shadow public abstract int getLineCount();

    @Inject(method = "printChatMessage", at = @At("HEAD"), cancellable = true)
    private void removeEmptyPrint(IChatComponent chatComponent, CallbackInfo ci) {
        if (isEmptyChat(chatComponent)) ci.cancel();
    }

    @Inject(method = "setChatLine", at = @At("HEAD"), cancellable = true)
    private void removeEmptySet(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly, CallbackInfo ci) {
        if (isEmptyChat(chatComponent)) ci.cancel();
    }

    @Unique
    private boolean isEmptyChat(IChatComponent component) {
        if (component == null) return true;
        String text = component.getUnformattedText().trim();
        return text.isEmpty();
    }

    @Inject(method = "getChatComponent", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/GuiNewChat;scrollPos:I"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void getChatComponent(int mouseX, int mouseY, CallbackInfoReturnable<IChatComponent> cir, ScaledResolution scaledresolution, int i, float f, int j, int k, int l) {
        int line = k / mc.fontRendererObj.FONT_HEIGHT;
        if (line >= getLineCount()) cir.setReturnValue(null);
    }

    @Redirect(method = "deleteChatLine", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ChatLine;getChatLineID()I"))
    private int deleteChatLine(ChatLine instance) {
        if (instance == null) return -1;
        return instance.getChatLineID();
    }
}