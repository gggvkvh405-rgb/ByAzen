package rtx.kimiko.api.modules.impl.Utils;
import rtx.kimiko.api.events.EventHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import rtx.kimiko.api.events.impl.game.TickEvent;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.settings.impl.BindSetting;
import rtx.kimiko.utils.inventory.ElytraSwapper;
import rtx.kimiko.utils.inventory.InventoryClicks;
import rtx.kimiko.utils.inventory.InventorySequence;
import rtx.kimiko.utils.chat.ChatMessage;
import rtx.kimiko.utils.time.StopWatch;

public final class ElytraSwap
extends Module {
    private static final long COOLDOWN_MS = 350L;
    private static final long OPEN_DELAY_MS = 70L;
    private static final long CLICK_DELAY_MS = 70L;
    private static final long CLOSE_DELAY_MS = 70L;
    private final BindSetting swapKey = this.register(new BindSetting("\u041a\u043b\u0430\u0432\u0438\u0448\u0430 \u0441\u0432\u0430\u043f\u0430", "\u041e\u0442\u043a\u0440\u044b\u0432\u0430\u0435\u0442 \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u044c, \u043c\u0435\u043d\u044f\u0435\u0442 \u044d\u043b\u0438\u0442\u0440\u0443 \u043d\u0430 \u043d\u0430\u0433\u0440\u0443\u0434\u043d\u0438\u043a \u0438 \u0437\u0430\u043a\u0440\u044b\u0432\u0430\u0435\u0442.").setKey(-1));
    private final InventorySequence sequence = new InventorySequence();
    private final StopWatch cooldown = new StopWatch();
    private boolean lastDown;
    private boolean weOpened;
    private int targetSlot = -1;

    public ElytraSwap() {
        super("Elytra Swap", "\u041b\u0435\u0433\u0438\u0442\u043d\u044b\u0439 \u0441\u0432\u0430\u043f \u044d\u043b\u0438\u0442\u0440\u044b \u043d\u0430 \u043d\u0430\u0433\u0440\u0443\u0434\u043d\u0438\u043a \u0447\u0435\u0440\u0435\u0437 \u043e\u0442\u043a\u0440\u044b\u0442\u0438\u0435 \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u044f.", Category.UTILS);
    }

    private void clickInInventory(int n) {
        if (this.mc.currentScreen instanceof InventoryScreen) {
            InventoryClicks.pickup((int)n);
        }
    }

    private void openInventory() {
        if (this.mc.player != null && this.mc.currentScreen == null) {
            this.mc.setScreen((Screen)new InventoryScreen((PlayerEntity)(Object)this.mc.player));
            this.weOpened = true;
        }
    }

    @Override
    protected void onDisable() {
        this.sequence.cancel();
        if (this.weOpened) {
            this.closeInventory();
        }
        this.targetSlot = -1;
        this.lastDown = false;
    }

    @EventHandler
    private void onTick(TickEvent tickEvent) {
        boolean bl;
        if (!tickEvent.isPre()) {
            return;
        }
        this.sequence.tick();
        if (this.mc.player == null || this.mc.world == null || this.mc.getWindow() == null) {
            this.lastDown = false;
            return;
        }
        boolean bl2 = this.swapKey.isBound() && this.swapKey.getValue().isDown(this.mc.getWindow().getHandle());
        boolean bl3 = bl = !this.sequence.isRunning() && this.mc.currentScreen == null && this.cooldown.finished(350.0);
        if (bl2 && !this.lastDown && bl) {
            this.triggerSwap();
            this.cooldown.reset();
        }
        this.lastDown = bl2;
    }

    private void closeInventory() {
        if (this.mc.player != null && this.mc.currentScreen instanceof InventoryScreen) {
            this.mc.player.closeHandledScreen();
            this.mc.setScreen(null);
        }
        this.weOpened = false;
        this.targetSlot = -1;
    }

    private void triggerSwap() {
        this.targetSlot = ElytraSwapper.findTargetSlot();
        if (this.targetSlot == -1) {
            ChatMessage.brandmessage((String)"\u041d\u0435\u0442 \u044d\u043b\u0438\u0442\u0440\u044b \u0438\u043b\u0438 \u043d\u0430\u0433\u0440\u0443\u0434\u043d\u0438\u043a\u0430 \u0434\u043b\u044f \u0441\u0432\u0430\u043f\u0430.");
            return;
        }
        this.sequence.cancel();
        this.sequence.then(this::openInventory).thenAfter(70L, () -> this.clickInInventory(6)).thenAfter(70L, () -> this.clickInInventory(this.targetSlot)).thenAfter(70L, () -> this.clickInInventory(6)).thenAfter(70L, this::closeInventory).start();
    }
}

