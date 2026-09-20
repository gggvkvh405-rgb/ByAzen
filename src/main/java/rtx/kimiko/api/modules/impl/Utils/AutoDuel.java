package rtx.kimiko.api.modules.impl.Utils;
import rtx.kimiko.api.events.EventHandler;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import rtx.kimiko.api.events.impl.game.TickEvent;
import rtx.kimiko.api.events.impl.network.PacketEvent;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.settings.impl.BooleanSetting;
import rtx.kimiko.api.modules.settings.impl.ModeSetting;
import rtx.kimiko.api.modules.settings.impl.NumberSetting;
import rtx.kimiko.api.modules.settings.impl.SeparatorSetting;
import rtx.kimiko.api.modules.settings.impl.StringSetting;
import rtx.kimiko.utils.time.StopWatch;

public final class AutoDuel
extends Module {
    private static final Pattern NICK_PATTERN = Pattern.compile("^\\w{3,16}$");
    private final SeparatorSetting duelSeparator = this.register(new SeparatorSetting("\u0414\u0443\u044d\u043b\u044c"));
    private final ModeSetting mode = this.register(new ModeSetting("\u0420\u0435\u0436\u0438\u043c", "\u041d\u0430\u0431\u043e\u0440 \u0434\u043b\u044f \u0434\u0443\u044d\u043b\u0438.", "Spheres", "Spheres", "Shield", "Spikes 3", "Netherite", "Cheater Paradise", "Bow", "Classic", "Totems", "NoDebuff"));
    private final NumberSetting slowTime = this.register(new NumberSetting("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u043e\u0442\u043f\u0440\u0430\u0432\u043a\u0438", "\u0417\u0430\u0434\u0435\u0440\u0436\u043a\u0430 \u043c\u0435\u0436\u0434\u0443 \u0437\u0430\u043f\u0440\u043e\u0441\u0430\u043c\u0438 \u043d\u0430 \u0434\u0443\u044d\u043b\u044c.", 500.0, 300.0, 1000.0, 10.0));
    private final SeparatorSetting betSeparator = this.register(new SeparatorSetting("\u0421\u0442\u0430\u0432\u043a\u0430"));
    private final BooleanSetting moneyMode = this.register(new BooleanSetting("\u0418\u0433\u0440\u0430 \u043d\u0430 \u0434\u0435\u043d\u044c\u0433\u0438", "\u0414\u043e\u0431\u0430\u0432\u043b\u044f\u0435\u0442 \u0441\u0443\u043c\u043c\u0443 \u0434\u0435\u043d\u0435\u0433 \u043a \u0437\u0430\u043f\u0440\u043e\u0441\u0443 \u043d\u0430 \u0434\u0443\u044d\u043b\u044c.", false));
    private final StringSetting money = this.register(new StringSetting("\u0414\u0435\u043d\u044c\u0433\u0438", "\u0421\u0443\u043c\u043c\u0430 \u0434\u0435\u043d\u0435\u0433 \u0434\u043b\u044f \u0437\u0430\u043f\u0440\u043e\u0441\u0430 \u043d\u0430 \u0434\u0443\u044d\u043b\u044c.", "10000", 16));
    private final List<String> sent = new ArrayList<String>();
    private final StopWatch counter = new StopWatch();
    private final StopWatch clearCounter = new StopWatch();
    private final StopWatch choiceCounter = new StopWatch();
    private final StopWatch confirmCounter = new StopWatch();
    private double lastPosX;
    private double lastPosY;
    private double lastPosZ;

    public AutoDuel() {
        super("Auto Duel", "\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0437\u0438\u0440\u0443\u0435\u0442 \u0437\u0430\u043f\u0440\u043e\u0441\u044b \u043d\u0430 \u0434\u0443\u044d\u043b\u0438.", Category.UTILS);
        this.money.visibleWhen(this.moneyMode::getValue);
    }

    private int getKitSlot() {
        return switch (this.mode.getValue()) {
            case "Shield" -> 0;
            case "Spikes 3" -> 1;
            case "Bow" -> 2;
            case "Totems" -> 3;
            case "NoDebuff" -> 4;
            case "Spheres" -> 5;
            case "Classic" -> 6;
            case "Cheater Paradise" -> 7;
            case "Netherite" -> 8;
            default -> -1;
        };
    }

    @EventHandler
    private void onTick(TickEvent tickEvent) {
        if (this.mc.player == null || this.mc.world == null || this.mc.player.networkHandler == null || this.mc.interactionManager == null) {
            return;
        }
        this.handleDuelLogic();
        this.handleScreenInteraction();
    }

    @Override
    protected void onEnable() {
        this.counter.reset();
        this.clearCounter.reset();
        this.choiceCounter.reset();
        this.confirmCounter.reset();
        this.sent.clear();
        if (this.mc.player != null) {
            this.lastPosX = this.mc.player.getX();
            this.lastPosY = this.mc.player.getY();
            this.lastPosZ = this.mc.player.getZ();
        }
    }

    @EventHandler
    private void onPacket(PacketEvent packetEvent) {
        Object object;
        if (!packetEvent.isReceive() || !((object = packetEvent.getPacket()) instanceof GameMessageS2CPacket)) {
            return;
        }
        GameMessageS2CPacket gameMessageS2CPacket = (GameMessageS2CPacket)object;
        object = gameMessageS2CPacket.content().getString().toLowerCase(Locale.ROOT);
        if (((String)object).contains("\u043d\u0430\u0447\u0430\u043b\u043e") && ((String)object).contains("\u0447\u0435\u0440\u0435\u0437") && ((String)object).contains("\u0441\u0435\u043a\u0443\u043d\u0434") || ((String)object).contains("\u0434\u0443\u044d\u043b\u0438") && ((String)object).contains("\u0437\u0430\u043f\u0440\u0435\u0449\u0435\u043d\u043e") && ((String)object).contains("\u043a\u043e\u043c\u0430\u043d\u0434")) {
            this.setEnabled(false);
        }
    }

    private void handleDuelLogic() {
        List<String> list = this.getOnlinePlayers();
        double d = Math.sqrt(Math.pow(this.lastPosX - this.mc.player.getX(), 2.0) + Math.pow(this.lastPosY - this.mc.player.getY(), 2.0) + Math.pow(this.lastPosZ - this.mc.player.getZ(), 2.0));
        if (d > 500.0) {
            this.setEnabled(false);
            return;
        }
        this.lastPosX = this.mc.player.getX();
        this.lastPosY = this.mc.player.getY();
        this.lastPosZ = this.mc.player.getZ();
        if (this.clearCounter.finished(800L * (long)Math.max(1, list.size()))) {
            this.sent.clear();
            this.clearCounter.reset();
        }
        String string = this.mc.player.getGameProfile().name();
        for (String string2 : list) {
            if (this.sent.contains(string2) || string2.equals(string) || !this.counter.finished(this.slowTime.getValue())) continue;
            this.sendDuelRequest(string2);
            this.sent.add(string2);
            this.counter.reset();
        }
    }

    private List<String> getOnlinePlayers() {
        ArrayList<String> arrayList = new ArrayList<String>();
        if (this.mc.player == null || this.mc.player.networkHandler == null) {
            return arrayList;
        }
        for (PlayerListEntry playerListEntry : this.mc.player.networkHandler.getPlayerList()) {
            GameProfile gameProfile = playerListEntry.getProfile();
            String string = gameProfile.name();
            if (!NICK_PATTERN.matcher(string).matches()) continue;
            arrayList.add(string);
        }
        return arrayList;
    }

    private void sendDuelRequest(String string) {
        if (this.moneyMode.getValue()) {
            this.mc.player.networkHandler.sendChatCommand("duel " + string + " " + this.money.getValue());
        } else {
            this.mc.player.networkHandler.sendChatCommand("duel " + string);
        }
    }

    private void handleScreenInteraction() {
        if (this.mc.currentScreen == null || this.mc.player.currentScreenHandler == null) {
            return;
        }
        ScreenHandler screenHandler = this.mc.player.currentScreenHandler;
        String title = this.mc.currentScreen.getTitle().getString().toLowerCase(Locale.ROOT);
        if (title.contains("\u0432\u044b\u0431\u043e\u0440 \u043d\u0430\u0431\u043e\u0440\u0430") && this.choiceCounter.finished(150.0)) {
            int n = this.getKitSlot();
            if (n >= 0) {
                this.mc.interactionManager.clickSlot(screenHandler.syncId, n, 0, SlotActionType.QUICK_MOVE, (PlayerEntity)(Object)this.mc.player);
            }
            this.choiceCounter.reset();
            return;
        }
        if (title.contains("\u043d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0430 \u043f\u043e\u0435\u0434\u0438\u043d\u043a\u0430") && this.confirmCounter.finished(150.0)) {
            this.mc.interactionManager.clickSlot(screenHandler.syncId, 0, 0, SlotActionType.QUICK_MOVE, (PlayerEntity)(Object)this.mc.player);
            this.confirmCounter.reset();
        }
    }
}

