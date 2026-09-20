package rtx.byazen.utils.network;

import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;

public final class FunTimeJoinGuard {
    public static boolean bypass = false;

    private FunTimeJoinGuard() {}

    public static boolean matches(ServerAddress serverAddress, ServerInfo serverData, boolean hasTransferState) {
        if (serverAddress != null && serverAddress.getAddress().toLowerCase().contains("funtime")) {
            return true;
        }
        if (serverData != null && serverData.address.toLowerCase().contains("funtime")) {
            return true;
        }
        return false;
    }
}
