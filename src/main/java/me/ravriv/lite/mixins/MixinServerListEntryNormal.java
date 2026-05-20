package me.ravriv.lite.mixins;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.util.EnumChatFormatting;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.net.UnknownHostException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(ServerListEntryNormal.class)
public abstract class MixinServerListEntryNormal {
    @Shadow @Final private GuiMultiplayer owner;
    @Shadow @Final private ServerData server;
    @Shadow @Final @Mutable private static ThreadPoolExecutor field_148302_b;

    @Unique private static final int serverCountCache;
    @Unique private static final AtomicInteger runningTaskCount = new AtomicInteger(0);

    static {
        serverCountCache = new ServerList(Minecraft.getMinecraft()).countServers();
        field_148302_b = new ScheduledThreadPoolExecutor(Math.min(serverCountCache + 5, 50), new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).build());
    }

    @Unique private final ScheduledExecutorService timeoutExecutor = Executors.newScheduledThreadPool(Math.min(serverCountCache + 5, 100), new ThreadFactoryBuilder().setNameFormat("Server Timeout #%d").setDaemon(true).build());

    @Unique
    private void setServerFail(String error) {
        server.pingToServer = -1L;
        server.serverMOTD = error;
    }

    @Unique
    private void tryPing(boolean retry) {
        try {
            owner.getOldServerPinger().ping(server);
        } catch (Exception e) {
            if (retry) {
                timeoutExecutor.schedule(() -> tryPing(false), 500, TimeUnit.MILLISECONDS);
            } else {
                setServerFail(EnumChatFormatting.DARK_RED + "Can't connect to server.");
            }
        }
    }

    @Unique
    private Runnable getPingTask() {
        return () -> tryPing(true);
    }

    @Redirect(method = "drawEntry", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/ThreadPoolExecutor;submit(Ljava/lang/Runnable;)Ljava/util/concurrent/Future;"))
    public Future<?> onDrawEntry(ThreadPoolExecutor instance, Runnable originalRunnable) {
        if (runningTaskCount.get() > serverCountCache * 2) {
            setServerFail(EnumChatFormatting.GRAY + "Request rate limit exceeded");
            return field_148302_b.submit(() -> {});
        }

        final Future<?> future = timeoutExecutor.submit(getPingTask());
        runningTaskCount.incrementAndGet();

        return field_148302_b.submit(() -> {
            try {
                future.get(4, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                setServerFail(EnumChatFormatting.RED + "Connection timed out");
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof UnknownHostException) {
                    setServerFail(EnumChatFormatting.DARK_RED + "Can't resolve hostname");
                } else {
                    setServerFail(EnumChatFormatting.DARK_RED + "Can't connect to server.");
                }
            } catch (InterruptedException | RuntimeException e) {
                setServerFail(EnumChatFormatting.DARK_RED + "Can't connect to server.");
            } finally {
                runningTaskCount.decrementAndGet();
            }
        });
    }
}