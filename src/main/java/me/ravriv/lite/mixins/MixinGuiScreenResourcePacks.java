package me.ravriv.lite.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraftforge.fml.common.Loader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.io.File;
import java.io.IOException;

@Mixin(GuiScreenResourcePacks.class)
public class MixinGuiScreenResourcePacks {
    @Inject(method = "actionPerformed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/ResourcePackRepository;getDirResourcepacks()Ljava/io/File;", shift = At.Shift.AFTER), cancellable = true)
    private void fixFolderOpening(CallbackInfo ci) {
        File dir = Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks();
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(dir);
                ci.cancel();
            } catch (IOException ignored) {
            }
        }
    }

    @Inject(method = "actionPerformed", at = @At(value = "INVOKE", target = "Ljava/util/Collections;reverse(Ljava/util/List;)V", remap = false))
    private void clearHandles(CallbackInfo ci) {
        ResourcePackRepository repository = Minecraft.getMinecraft().getResourcePackRepository();
        IResourcePack current = repository.getResourcePackInstance();
        for (ResourcePackRepository.Entry entry : repository.getRepositoryEntries()) {
            if (current == null || !entry.getResourcePackName().equals(current.getPackName())) {
                entry.closeResourcePack();
            }
        }
    }

    @ModifyConstant(method = "drawScreen", constant = @Constant(intValue = 77))
    private int drawScreen(int original) {
        return !Loader.isModLoaded("ResourcePackOrganizer") ? 102 : original;
    }
}