package me.ravriv.lite.mixins;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Mixin(NetHandlerPlayClient.class)
public abstract class MixinNetHandlerPlayClient {
    @Shadow public abstract NetworkPlayerInfo getPlayerInfo(UUID p_175102_1_);

    @Inject(method = "handleResourcePack", at = @At("HEAD"), cancellable = true)
    private void handleResourcePack(S48PacketResourcePackSend packet, CallbackInfo ci) {
        if (!isValidResourcePackUrl(packet)) {
            ((NetHandlerPlayClient) (Object) this).getNetworkManager().sendPacket(new C19PacketResourcePackStatus(packet.getHash(), C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
            ci.cancel();
        }
    }

    @Unique
    private boolean isValidResourcePackUrl(S48PacketResourcePackSend packet) {
        try {
            String url = packet.getURL();
            URI uri = new URI(url);
            String scheme = uri.getScheme();

            boolean isLevelProtocol = "level".equals(scheme);

            if (!"http".equals(scheme) && !"https".equals(scheme) && !isLevelProtocol) {
                return false;
            }

            if (isLevelProtocol) {
                url = URLDecoder.decode(url.substring("level://".length()), StandardCharsets.UTF_8.toString());

                return !url.contains("..") && url.endsWith("/resources.zip");
            }

            return true;
        } catch (URISyntaxException | UnsupportedEncodingException e) {
            return false;
        }
    }

    @Inject(method = "handleSpawnPlayer", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketThreadUtil;checkThreadAndEnqueue(Lnet/minecraft/network/Packet;Lnet/minecraft/network/INetHandler;Lnet/minecraft/util/IThreadListener;)V", shift = At.Shift.AFTER))
    private void handleSpawnPlayer(S0CPacketSpawnPlayer packetIn, CallbackInfo ci) {
        if (this.getPlayerInfo(packetIn.getPlayer()) == null) {
            ci.cancel();
        }
    }

    @ModifyArg(method = {"handleJoinGame", "handleRespawn"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V"))
    private GuiScreen skipTerrainScreen(GuiScreen original) {
        return null;
    }

    @Redirect(method = "handleUpdateSign", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=Unable to locate sign at ", ordinal = 0)), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;addChatMessage(Lnet/minecraft/util/IChatComponent;)V", ordinal = 0))
    private void handleUpdateSign(EntityPlayerSP instance, IChatComponent component) {}
}