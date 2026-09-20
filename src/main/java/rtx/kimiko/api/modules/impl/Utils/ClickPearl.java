package rtx.kimiko.api.modules.impl.Utils;
import rtx.kimiko.api.events.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import rtx.kimiko.api.events.impl.game.TickEvent;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.settings.impl.BindSetting;
import rtx.kimiko.mixin.accessor.MultiPlayerGameModeAccessor;
import rtx.kimiko.utils.chat.ChatMessage;

public final class ClickPearl
extends Module {
    private static final long SWAP_DELAY_MS = 50L;
    private static final long RESTORE_DELAY_MS = 50L;
    private static final long COOLDOWN_MS = 200L;
    private final BindSetting keySetting = new BindSetting("\u041a\u043b\u0430\u0432\u0438\u0448\u0430", "\u041a\u043b\u0430\u0432\u0438\u0448\u0430 \u0434\u043b\u044f \u0431\u0440\u043e\u0441\u043a\u0430 \u044d\u043d\u0434\u0435\u0440-\u0436\u0435\u043c\u0447\u0443\u0433\u0430.").setKey(-1);
    private boolean lastBindDown;
    private long lastUseTime;
    private long actionTimer;
    private int previousSlot = -1;
    private int pearlSlot = -1;
    private ClickPearl.State state = ClickPearl.State.IDLE;

    public ClickPearl() {
        super("ClickPearl", "\u0411\u0440\u043e\u0441\u0430\u0435\u0442 \u044d\u043d\u0434\u0435\u0440-\u0436\u0435\u043c\u0447\u0443\u0433 \u0438\u0437 \u0445\u043e\u0442\u0431\u0430\u0440\u0430 \u043f\u043e \u043d\u0430\u0436\u0430\u0442\u0438\u044e \u043a\u043b\u0430\u0432\u0438\u0448\u0438.", Category.UTILS);
        this.register(this.keySetting);
    }

    private void resetState() {
        this.state = ClickPearl.State.IDLE;
        this.actionTimer = 0L;
        this.previousSlot = -1;
        this.pearlSlot = -1;
    }

    @Override
    protected void onDisable() {
        this.lastBindDown = false;
        this.resetState();
    }

    private int findPearlInHotbar() {
        for (int i = 0; i < 9; ++i) {
            if (this.mc.player.getInventory().getStack(i).getItem() != Items.ENDER_PEARL) continue;
            return i;
        }
        return -1;
    }

    private void selectHotbarSlot(int n) {
        if (this.mc.player == null || this.mc.interactionManager == null || n < 0 || n > 8) {
            return;
        }
        if (this.mc.player.getInventory().getSelectedSlot() == n) {
            return;
        }
        this.mc.player.getInventory().setSelectedSlot(n);
        ((MultiPlayerGameModeAccessor)(Object)this.mc.interactionManager).kimiko_ensureHasSentCarriedItem();
    }

    private void processThrow() {
        switch (this.state.ordinal()) {
            case 1: {
                if (System.currentTimeMillis() - this.actionTimer < 50L) {
                    return;
                }
                this.selectHotbarSlot(this.pearlSlot);
                this.mc.interactionManager.interactItem((PlayerEntity)(Object)this.mc.player, Hand.MAIN_HAND);
                this.mc.player.swingHand(Hand.MAIN_HAND);
                this.lastUseTime = System.currentTimeMillis();
                this.state = ClickPearl.State.WAIT_BEFORE_RESTORE;
                this.actionTimer = System.currentTimeMillis();
                break;
            }
            case 2: {
                if (System.currentTimeMillis() - this.actionTimer < 50L) {
                    return;
                }
                if (this.previousSlot != -1) {
                    this.selectHotbarSlot(this.previousSlot);
                }
                this.resetState();
                break;
            }
            default: {
                this.resetState();
            }
        }
    }

    private boolean bindJustPressed() {
        if (this.mc.getWindow() == null || this.mc.currentScreen != null) {
            this.lastBindDown = false;
            return false;
        }
        boolean bl = this.keySetting.getValue().isDown(this.mc.getWindow().getHandle());
        boolean bl2 = bl && !this.lastBindDown;
        this.lastBindDown = bl;
        return bl2;
    }

    @EventHandler
    public void onPreTick(TickEvent tickEvent) {
        if (!tickEvent.isPre()) {
            return;
        }
        if (this.mc.player == null || this.mc.world == null) {
            this.resetState();
            this.lastBindDown = false;
            return;
        }
        if (this.state != ClickPearl.State.IDLE) {
            this.processThrow();
            return;
        }
        if (!this.bindJustPressed() || this.mc.currentScreen != null) {
            return;
        }
        if (System.currentTimeMillis() - this.lastUseTime < 200L) {
            return;
        }
        if (this.mc.player.getItemCooldownManager().isCoolingDown(Items.ENDER_PEARL.getDefaultStack())) {
            return;
        }
        int n = this.findPearlInHotbar();
        if (n == -1) {
            ChatMessage.brandmessage((String)"\u042d\u043d\u0434\u0435\u0440-\u0436\u0435\u043c\u0447\u0443\u0433 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d \u0432 \u0445\u043e\u0442\u0431\u0430\u0440\u0435.");
            return;
        }
        this.previousSlot = this.mc.player.getInventory().getSelectedSlot();
        this.pearlSlot = n;
        this.state = ClickPearl.State.WAIT_BEFORE_SWAP;
        this.actionTimer = System.currentTimeMillis();
    }


    public static enum State {
        IDLE,
        WAIT_BEFORE_SWAP,
        WAIT_BEFORE_RESTORE;
    
    }
}

