package rtx.kimiko.api.modules.impl.Utils;
import rtx.kimiko.api.events.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import rtx.kimiko.api.events.impl.game.TickEvent;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.settings.impl.ModeSetting;
import rtx.kimiko.utils.time.StopWatch;

public final class AutoResell
extends Module {
    private static final long CYCLE_MS = 65000L;
    private static final long CLICK_MS = 500L;
    private static final int STORAGE_SLOT = 46;
    private static final int RESELL_SLOT = 52;
    private final ModeSetting mode = this.register(new ModeSetting("\u0420\u0435\u0436\u0438\u043c", "\u0421\u043f\u043e\u0441\u043e\u0431 \u043f\u0435\u0440\u0435\u0432\u044b\u0441\u0442\u0430\u0432\u043b\u0435\u043d\u0438\u044f.", "\u041a\u043e\u043c\u0430\u043d\u0434\u0430", "\u041a\u043e\u043c\u0430\u043d\u0434\u0430", "\u0410\u0443\u043a\u0446\u0438\u043e\u043d"));
    private final StopWatch cycleTimer = new StopWatch();
    private final StopWatch stepTimer = new StopWatch();
    private AutoResell.State state = AutoResell.State.IDLE;

    public AutoResell() {
        super("Auto Resell", "\u041f\u0435\u0440\u0435\u0432\u044b\u0441\u0442\u0430\u0432\u043b\u044f\u0435\u0442 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u044b \u043d\u0430 \u0430\u0443\u043a\u0446\u0438\u043e\u043d\u0435.", Category.UTILS);
    }

    private boolean isMenuOpen() {
        return this.mc.currentScreen != null && this.mc.player.currentScreenHandler instanceof ScreenHandler;
    }

    private void endCycle() {
        this.state = AutoResell.State.IDLE;
        this.cycleTimer.reset();
    }

    private void clickSlot(int n) {
        ScreenHandler screenHandler = this.mc.player.currentScreenHandler;
        if (screenHandler == null || n < 0 || n >= screenHandler.slots.size()) {
            return;
        }
        this.mc.interactionManager.clickSlot(screenHandler.syncId, n, 0, SlotActionType.PICKUP, (PlayerEntity)(Object)this.mc.player);
    }

    @Override
    protected void onDisable() {
        this.state = AutoResell.State.IDLE;
    }

    @EventHandler
    private void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre()) {
            return;
        }
        if (this.mc.player == null || this.mc.player.networkHandler == null || this.mc.interactionManager == null) {
            this.state = AutoResell.State.IDLE;
            return;
        }
        if ("\u041a\u043e\u043c\u0430\u043d\u0434\u0430".equals(this.mode.getValue())) {
            this.tickCommand();
        } else {
            this.tickAuction();
        }
    }

    @Override
    protected void onEnable() {
        this.state = AutoResell.State.IDLE;
        this.cycleTimer.setMs(65000L);
        this.stepTimer.reset();
    }

    private void tickCommand() {
        if (!this.cycleTimer.finished(65000.0)) {
            return;
        }
        this.mc.player.networkHandler.sendChatCommand("ah resell");
        this.cycleTimer.reset();
    }

    private void tickAuction() {
        switch (this.state.ordinal()) {
            case 0: {
                if (!this.cycleTimer.finished(65000.0)) {
                    return;
                }
                this.mc.player.networkHandler.sendChatCommand("ah");
                this.state = AutoResell.State.WAIT_MENU;
                this.stepTimer.reset();
                break;
            }
            case 1: {
                if (!this.isMenuOpen()) {
                    if (this.stepTimer.finished(3000.0)) {
                        this.endCycle();
                    }
                    return;
                }
                if (!this.stepTimer.finished(500.0)) {
                    return;
                }
                this.clickSlot(46);
                this.state = AutoResell.State.WAIT_STORAGE;
                this.stepTimer.reset();
                break;
            }
            case 2: {
                if (!this.isMenuOpen()) {
                    this.endCycle();
                    return;
                }
                if (!this.stepTimer.finished(500.0)) {
                    return;
                }
                this.clickSlot(52);
                this.state = AutoResell.State.WAIT_RESELL;
                this.stepTimer.reset();
                break;
            }
            case 3: {
                if (!this.stepTimer.finished(500.0)) {
                    return;
                }
                this.mc.player.closeHandledScreen();
                this.endCycle();
            }
        }
    }


    public static enum State {
        IDLE,
        WAIT_MENU,
        WAIT_STORAGE,
        WAIT_RESELL;
    
    }
}

