package rtx.kimiko.mixin;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerList.class)
public abstract class ServerListPinnedServerMixin {
    private static final String KIMIKO_PINNED_NAME = "BreakProject";
    private static final String KIMIKO_PINNED_ADDRESS = "mc.breakproject.pro";
    @Shadow
    @Final
    private List<ServerInfo> servers;
    @Shadow
    @Final
    private List<ServerInfo> hiddenServers;

    @Shadow
    public abstract void saveFile();

    @Inject(method="loadFile", at={@At(value="TAIL")}, require = 0)
    private void kimiko_pinBreakProject(CallbackInfo ci) {
        ServerInfo server;
        ServerInfo pinned = null;
        boolean changed = false;
        Iterator<ServerInfo> iterator = this.servers.iterator();
        while (iterator.hasNext()) {
            server = iterator.next();
            if (!ServerListPinnedServerMixin.kimiko_isPinned(server)) continue;
            if (pinned == null) {
                pinned = server;
                continue;
            }
            iterator.remove();
            changed = true;
        }
        iterator = this.hiddenServers.iterator();
        while (iterator.hasNext()) {
            server = iterator.next();
            if (!ServerListPinnedServerMixin.kimiko_isPinned(server)) continue;
            iterator.remove();
            if (pinned == null) {
                pinned = server;
            }
            changed = true;
        }
        if (pinned == null) {
            pinned = new ServerInfo(KIMIKO_PINNED_NAME, KIMIKO_PINNED_ADDRESS, ServerInfo.ServerType.OTHER);
            changed = true;
        }
        if (!KIMIKO_PINNED_NAME.equals(pinned.name) || !KIMIKO_PINNED_ADDRESS.equals(pinned.address)) {
            pinned.name = KIMIKO_PINNED_NAME;
            pinned.address = KIMIKO_PINNED_ADDRESS;
            changed = true;
        }
        if (this.servers.isEmpty() || this.servers.getFirst() != pinned) {
            this.servers.remove(pinned);
            this.servers.addFirst(pinned);
            changed = true;
        }
        if (changed) {
            this.saveFile();
        }
    }

    @Inject(method="add(Lnet/minecraft/client/network/ServerInfo;Z)V", at={@At(value="HEAD")}, cancellable=true, require = 0)
    private void kimiko_preventDuplicate(ServerInfo server, boolean hidden, CallbackInfo ci) {
        if (ServerListPinnedServerMixin.kimiko_isPinned(server) && this.servers.stream().anyMatch(ServerListPinnedServerMixin::kimiko_isPinned)) {
            ci.cancel();
        }
    }

    @Inject(method="remove", at={@At(value="HEAD")}, cancellable=true, require = 0)
    private void kimiko_preventRemoval(ServerInfo server, CallbackInfo ci) {
        if (ServerListPinnedServerMixin.kimiko_isPinned(server)) {
            ci.cancel();
        }
    }

    @Inject(method="swapEntries", at={@At(value="HEAD")}, cancellable=true, require = 0)
    private void kimiko_preventMove(int first, int second, CallbackInfo ci) {
        if (ServerListPinnedServerMixin.kimiko_isPinned(this.servers.get(first)) || ServerListPinnedServerMixin.kimiko_isPinned(this.servers.get(second))) {
            ci.cancel();
        }
    }

    @Inject(method="set", at={@At(value="HEAD")}, cancellable=true, require = 0)
    private void kimiko_preventEdit(int index, ServerInfo replacement, CallbackInfo ci) {
        if (ServerListPinnedServerMixin.kimiko_isPinned(this.servers.get(index))) {
            ci.cancel();
        }
    }

    private static boolean kimiko_isPinned(ServerInfo server) {
        if (server == null || server.address == null) {
            return false;
        }
        String address = server.address.trim().toLowerCase(Locale.ROOT);
        return KIMIKO_PINNED_ADDRESS.equals(address) || "mc.breakproject.pro:25565".equals(address);
    }
}
