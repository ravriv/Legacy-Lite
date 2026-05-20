package me.ravriv.lite.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity {
    @Shadow public boolean onGround;

    @Unique private long displayNameCachedAt;
    @Unique private IChatComponent cachedDisplayName;

    @Inject(method = "spawnRunningParticles", at = @At("HEAD"), cancellable = true)
    private void spawnRunningParticles(CallbackInfo ci) {
        if (!this.onGround) ci.cancel();
    }

    @Inject(method = "getDisplayName", at = @At("RETURN"))
    protected void cacheDisplayName(CallbackInfoReturnable<IChatComponent> cir) {
        cachedDisplayName = cir.getReturnValue();
        displayNameCachedAt = System.currentTimeMillis();
    }

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    protected void returnCachedDisplayName(CallbackInfoReturnable<IChatComponent> cir) {
        if (System.currentTimeMillis() - displayNameCachedAt < 50L) {
            cir.setReturnValue(cachedDisplayName);
        }
    }

    @Redirect(method = "getDisplayName", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getHoverEvent()Lnet/minecraft/event/HoverEvent;"))
    private HoverEvent doNotGetHoverEvent(Entity instance) {
        return null;
    }

    @Redirect(method = "getDisplayName", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ChatStyle;setChatHoverEvent(Lnet/minecraft/event/HoverEvent;)Lnet/minecraft/util/ChatStyle;"))
    private ChatStyle doNotSetHoverEvent(ChatStyle instance, HoverEvent event) {
        return null;
    }
}