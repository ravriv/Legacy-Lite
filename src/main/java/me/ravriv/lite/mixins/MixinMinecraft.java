package me.ravriv.lite.mixins;

import me.ravriv.lite.handlers.AfkHandler;
import me.ravriv.lite.mixins.accessors.KeyBindingAccessor;
import me.ravriv.lite.mixins.accessors.MinecraftForgeClientAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.stream.IStream;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraftforge.client.MinecraftForgeClient;
import org.apache.commons.lang3.SystemUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Shadow public MouseHelper mouseHelper;
    @Shadow public EntityPlayerSP thePlayer;
    @Shadow public WorldClient theWorld;
    @Shadow public EntityRenderer entityRenderer;
    @Shadow public GuiScreen currentScreen;
    @Shadow private boolean fullscreen;
    @Shadow private boolean enableGLErrorChecking;
    @Unique private int idle = 0;

    @Inject(method = "runTick", at = @At("HEAD"))
    private void onTickStart(CallbackInfo ci) {
        if (thePlayer == null) return;

        boolean active = thePlayer.movementInput.moveForward != 0.0f || thePlayer.movementInput.moveStrafe != 0.0f || thePlayer.movementInput.jump || thePlayer.movementInput.sneak || mouseHelper.deltaX != 0 || mouseHelper.deltaY != 0;
        EnhancementManager.getInstance().tick();
        
        if (active) {
            if (AfkHandler.isAfk()) AfkHandler.setAfk(false);
            idle = 0;
        } else if (idle++ >= 6000 && !AfkHandler.isAfk()) {
            AfkHandler.setAfk(true);
        }
    }

    @Inject(method = "startGame", at = @At("TAIL"))
    private void disableGlErrorChecking(CallbackInfo ci) {
        this.enableGLErrorChecking = false;
    }

    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
    private void clearLoadedMaps(WorldClient worldClientIn, String loadingMessage, CallbackInfo ci) {
        if (worldClientIn != this.theWorld) {
            this.entityRenderer.getMapItemRenderer().clearLoadedMaps();
        }
    }

    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;theWorld:Lnet/minecraft/client/multiplayer/WorldClient;", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
    private void clearRenderCache(CallbackInfo ci) {
        MinecraftForgeClient.getRenderPass();
        MinecraftForgeClientAccessor.getRegionCache().invalidateAll();
        MinecraftForgeClientAccessor.getRegionCache().cleanUp();
    }

    @Inject(method = "getLimitFramerate", at = @At("HEAD"), cancellable = true)
    private void getLimitFramerate(CallbackInfoReturnable<Integer> cir) {
        if (!Display.isActive() || AfkHandler.isAfk()) {
            AfkHandler.volumeState(true);
            cir.setReturnValue(10);
        } else {
            AfkHandler.volumeState(false);
        }
    }

    @Inject(method = "toggleFullscreen", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;setFullscreen(Z)V", remap = false))
    private void toggleFullscreen(CallbackInfo ci) {
        if (!this.fullscreen && SystemUtils.IS_OS_WINDOWS) {
            Display.setResizable(false);
            Display.setResizable(true);
        }
    }

    @Inject(method = "setIngameFocus", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MouseHelper;grabMouseCursor()V"))
    private void makeKeysReRegister(CallbackInfo ci) {
        if (!Minecraft.isRunningOnMac) {
            for (KeyBinding keybinding : KeyBindingAccessor.getKeybindArray()) {
                try {
                    int keyCode = keybinding.getKeyCode();
                    KeyBinding.setKeyBindState(keyCode, keyCode < 256 && Keyboard.isKeyDown(keyCode));
                } catch (IndexOutOfBoundsException ignored) {}
            }
        }
    }

    @ModifyConstant(method = "rightClickMouse", constant = @Constant(intValue = 4))
    private int rightClickMouse(int original) {
        ItemStack heldItem = thePlayer.getHeldItem();

        if (currentScreen == null && heldItem != null && heldItem.getItem() == Items.water_bucket && thePlayer.motionY < -0.6D && theWorld.getCollidingBoundingBoxes(thePlayer, thePlayer.getEntityBoundingBox().offset(0.0D, -3.0D, 0.0D)).isEmpty()) {
                return 0;
        }
        return original;
    }

    @Redirect(method = "runTick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;thirdPersonView:I", opcode = Opcodes.PUTFIELD))
    private void thirdPersonFront(GameSettings camera, int val) {
        camera.thirdPersonView = (camera.thirdPersonView == 0) ? 2 : 0;
    }

    @Redirect(method = "clickMouse", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;leftClickCounter:I", opcode = Opcodes.PUTFIELD, ordinal = 1))
    private void clickMouse(Minecraft instance, int value) {}

    @Redirect(method = "dispatchKeypresses", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;getEventCharacter()C", remap = false))
    private char dispatchKeypresses() {
        return (char) (Keyboard.getEventCharacter() + 256);
    }

    @Redirect(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/stream/IStream;func_152935_j()V"))
    private void twitchPollStream(IStream instance) {}

    @Redirect(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/stream/IStream;func_152922_k()V"))
    private void twitchSendStreamMetadata(IStream instance) {}

    @Redirect(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Ljava/lang/System;gc()V"))
    private void loadWorld() {}
}
