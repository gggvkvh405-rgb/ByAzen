package rtx.kimiko.api.modules.impl.Utils;
import rtx.kimiko.api.events.EventHandler;
import java.util.Locale;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import rtx.kimiko.api.events.impl.game.TickEvent;
import rtx.kimiko.api.events.impl.network.PacketEvent;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.settings.impl.BooleanSetting;
import rtx.kimiko.utils.storage.friend.FriendUtils;

public final class AutoTpAccept
extends Module {
    private static final String[] TELEPORT_MESSAGES = new String[]{"has requested teleport", "\u043f\u0440\u043e\u0441\u0438\u0442 \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f", "\u0445\u043e\u0447\u0435\u0442 \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f \u043a \u0432\u0430\u043c", "\u043f\u0440\u043e\u0441\u0438\u0442 \u043a \u0432\u0430\u043c \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f"};
    private final BooleanSetting friendsOnly = this.register(new BooleanSetting("\u0422\u043e\u043b\u044c\u043a\u043e \u0434\u0440\u0443\u0437\u044c\u044f", "\u041f\u0440\u0438\u043d\u0438\u043c\u0430\u0442\u044c \u0437\u0430\u043f\u0440\u043e\u0441\u044b \u043d\u0430 \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442 \u0442\u043e\u043b\u044c\u043a\u043e \u043e\u0442 \u0434\u0440\u0443\u0437\u0435\u0439.", false));
    private boolean canAccept;

    public AutoTpAccept() {
        super("Auto Accept", "\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438 \u043f\u0440\u0438\u043d\u0438\u043c\u0430\u0435\u0442 \u0437\u0430\u043f\u0440\u043e\u0441\u044b \u043d\u0430 \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442.", Category.UTILS);
    }

    @Override
    protected void onDisable() {
        this.canAccept = false;
    }

    @EventHandler
    private void onTick(TickEvent tickEvent) {
        if (this.mc.player == null || this.mc.player.networkHandler == null || !this.canAccept) {
            return;
        }
        this.mc.player.networkHandler.sendChatCommand("tpaccept");
        this.canAccept = false;
    }

    @Override
    protected void onEnable() {
        this.canAccept = false;
    }

    @EventHandler
    private void onPacket(PacketEvent packetEvent) {
        String string;
        Object object;
        if (!packetEvent.isReceive() || !((object = packetEvent.getPacket()) instanceof GameMessageS2CPacket)) {
            return;
        }
        GameMessageS2CPacket gameMessageS2CPacket = (GameMessageS2CPacket)object;
        if (this.isOverlay(gameMessageS2CPacket)) {
            return;
        }
        object = gameMessageS2CPacket.content().getString();
        String string2 = string = object == null ? "" : ((String)object).toLowerCase(Locale.ROOT);
        if (!this.isTeleportMessage(string)) {
            return;
        }
        this.canAccept = !this.friendsOnly.getValue() || this.containsFriendName(string);
    }

    private boolean isOverlay(GameMessageS2CPacket gameMessageS2CPacket) {
        try {
            return gameMessageS2CPacket.overlay();
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    private boolean containsFriendName(String string) {
        for (String string2 : FriendUtils.friends()) {
            if (string2 == null || !string.contains(string2.toLowerCase(Locale.ROOT))) continue;
            return true;
        }
        return false;
    }

    private boolean isTeleportMessage(String string) {
        for (String string2 : TELEPORT_MESSAGES) {
            if (!string.contains(string2.toLowerCase(Locale.ROOT))) continue;
            return true;
        }
        return false;
    }
}

