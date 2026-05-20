package me.ravriv.lite.mixins;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu extends GuiScreen {
    @Inject(method = "initGui", at = @At("TAIL"))
    private void onInitGui(CallbackInfo ci) {
        Iterator<GuiButton> iterator = this.buttonList.iterator();

        while (iterator.hasNext()) {
            GuiButton button = iterator.next();

            if (button.id == 14) {
                iterator.remove();
            }

            if (button.id == 6) {
                button.width = 200;
            }
        }
    }
}