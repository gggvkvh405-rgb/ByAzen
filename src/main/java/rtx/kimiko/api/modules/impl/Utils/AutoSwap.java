package rtx.kimiko.api.modules.impl.Utils;
import rtx.kimiko.api.events.EventHandler;
import java.util.function.Predicate;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;
import rtx.kimiko.api.events.impl.game.TickEvent;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.settings.impl.BindSetting;
import rtx.kimiko.api.modules.settings.impl.SelectSetting;
import rtx.kimiko.mixin.accessor.KeyMappingAccessor;
import rtx.kimiko.utils.chat.ChatMessage;
import rtx.kimiko.utils.inventory.HotbarSwapper;
import rtx.kimiko.utils.inventory.InventoryItems;

public final class AutoSwap
extends Module {
    private static final String TOTEM = "\u0422\u043e\u0442\u0435\u043c";
    private static final String SPHERE = "\u0421\u0444\u0435\u0440\u0430";
    private static final long PRE_SWAP_STOP_MS = 50L;
    private static final long POST_SWAP_STOP_MS = 60L;
    private final SelectSetting firstItem = this.register(new SelectSetting("\u041e\u0441\u043d\u043e\u0432\u043d\u043e\u0439 \u043f\u0440\u0435\u0434\u043c\u0435\u0442", "\u0412\u044b\u0431\u0435\u0440\u0438\u0442\u0435 \u043f\u0435\u0440\u0432\u044b\u0439 \u043f\u0440\u0435\u0434\u043c\u0435\u0442 \u0434\u043b\u044f \u043e\u0431\u043c\u0435\u043d\u0430.").value("\u0422\u043e\u0442\u0435\u043c", "\u0421\u0444\u0435\u0440\u0430").selected("\u0422\u043e\u0442\u0435\u043c"));
    private final SelectSetting secondItem = this.register(new SelectSetting("\u0412\u0442\u043e\u0440\u0438\u0447\u043d\u044b\u0439 \u043f\u0440\u0435\u0434\u043c\u0435\u0442", "\u0412\u044b\u0431\u0435\u0440\u0438\u0442\u0435 \u0432\u0442\u043e\u0440\u043e\u0439 \u043f\u0440\u0435\u0434\u043c\u0435\u0442 \u0434\u043b\u044f \u043e\u0431\u043c\u0435\u043d\u0430.").value("\u0422\u043e\u0442\u0435\u043c", "\u0421\u0444\u0435\u0440\u0430").selected("\u0421\u0444\u0435\u0440\u0430"));
    private final BindSetting bind = this.register(new BindSetting("\u041a\u043d\u043e\u043f\u043a\u0430 \u0441\u0432\u0430\u043f\u0430", "\u0421\u0432\u0430\u043f\u0430\u0435\u0442 \u043f\u0440\u0435\u0434\u043c\u0435\u0442 \u0432 \u043e\u0444\u0444\u0445\u0435\u043d\u0434 \u043f\u0440\u0438 \u043d\u0430\u0436\u0430\u0442\u0438\u0438.").setKey(-1));
    private boolean lastDown;
    private AutoSwap.State state = AutoSwap.State.IDLE;
    private long stateTimer;
    private boolean keysOverridden;

    public AutoSwap() {
        super("Auto Swap", "\u0421\u0432\u0430\u043f\u0430\u0435\u0442 \u0431\u043e\u0435\u0432\u043e\u0439 \u043f\u0440\u0435\u0434\u043c\u0435\u0442 \u0432 \u043e\u0444\u0444\u0445\u0435\u043d\u0434 \u043f\u043e \u0431\u0438\u043d\u0434\u0443 \u043e\u0434\u043d\u0438\u043c \u043a\u043b\u0438\u043a\u043e\u043c, \u0431\u0435\u0437 \u043e\u0442\u043a\u0440\u044b\u0442\u0438\u044f \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u044f.", Category.UTILS);
    }

    private void resetState() {
        this.state = AutoSwap.State.IDLE;
        this.stateTimer = 0L;
    }

    private Predicate<ItemStack> predicateFor(String string) {
        return SPHERE.equals(string) ? InventoryItems::isPlayerHead : InventoryItems::isEnchantedTotem;
    }

    private void maintainMovementStop() {
        this.mc.options.forwardKey.setPressed(false);
        this.mc.options.backKey.setPressed(false);
        this.mc.options.leftKey.setPressed(false);
        this.mc.options.rightKey.setPressed(false);
        this.mc.options.jumpKey.setPressed(false);
        this.mc.options.sprintKey.setPressed(false);
        if (this.mc.player.isSprinting()) {
            this.mc.player.setSprinting(false);
        }
    }

    private void processSwap() {
        switch (this.state.ordinal()) {
            case 1: {
                if (System.currentTimeMillis() - this.stateTimer < 50L) {
                    return;
                }
                Predicate<ItemStack> predicate = this.resolveTarget();
                ItemStack itemStack = HotbarSwapper.find(predicate);
                if (HotbarSwapper.swapToOffhand(predicate) && !itemStack.isEmpty()) {
                    ChatMessage.brandmessage((Text)Text.literal((String)"Auto Swap \u0441\u0432\u0430\u043f\u043d\u0443\u043b \u043d\u0430 ").formatted(Formatting.WHITE).append((Text)itemStack.getName().copy().formatted(itemStack.getRarity().getFormatting())));
                }
                this.state = AutoSwap.State.WAIT_AFTER_SWAP;
                this.stateTimer = System.currentTimeMillis();
                break;
            }
            case 2: {
                if (System.currentTimeMillis() - this.stateTimer < 60L) {
                    return;
                }
                this.restoreMovement();
                this.resetState();
                break;
            }
        }
    }

    private Predicate<ItemStack> resolveTarget() {
        Predicate<ItemStack> predicate = this.predicateFor(this.firstItem.getValue());
        Predicate<ItemStack> predicate2 = this.predicateFor(this.secondItem.getValue());
        ItemStack itemStack = this.mc.player.getOffHandStack();
        if (HotbarSwapper.has(predicate) && !predicate.test(itemStack)) {
            return predicate;
        }
        return predicate2;
    }

    @Override
    protected void onDisable() {
        this.restoreMovement();
        this.resetState();
        this.lastDown = false;
    }

    @EventHandler
    private void onTick(TickEvent tickEvent) {
        boolean bl;
        if (!tickEvent.isPre()) {
            return;
        }
        if (this.mc.player == null || this.mc.world == null || this.mc.getWindow() == null || this.mc.currentScreen != null) {
            this.restoreMovement();
            this.resetState();
            this.lastDown = false;
            return;
        }
        if (this.state != AutoSwap.State.IDLE) {
            this.maintainMovementStop();
            this.processSwap();
            return;
        }
        boolean bl2 = bl = this.bind.isBound() && this.bind.getValue().isDown(this.mc.getWindow().getHandle());
        if (bl && !this.lastDown) {
            this.beginSwap();
        }
        this.lastDown = bl;
    }

    private void restoreMovement() {
        if (!this.keysOverridden) {
            return;
        }
        this.resync(this.mc.options.forwardKey);
        this.resync(this.mc.options.backKey);
        this.resync(this.mc.options.leftKey);
        this.resync(this.mc.options.rightKey);
        this.resync(this.mc.options.jumpKey);
        this.resync(this.mc.options.sprintKey);
        this.keysOverridden = false;
    }

    private boolean isPhysicallyDown(KeyBinding keyBinding) {
        if (this.mc.getWindow() == null) {
            return false;
        }
        try {
            InputUtil.Key key = InputUtil.fromTranslationKey(keyBinding.getBoundKeyTranslationKey());
            if (key.getCategory() == InputUtil.Type.MOUSE) {
                return GLFW.glfwGetMouseButton((long)this.mc.getWindow().getHandle(), (int)key.getCode()) == 1;
            }
            return InputUtil.isKeyPressed((Window)(Object)this.mc.getWindow(), (int)key.getCode());
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    private void beginSwap() {
        if (!HotbarSwapper.has(this.predicateFor(this.firstItem.getValue())) && !HotbarSwapper.has(this.predicateFor(this.secondItem.getValue()))) {
            return;
        }
        this.keysOverridden = true;
        this.maintainMovementStop();
        this.state = AutoSwap.State.WAIT_BEFORE_SWAP;
        this.stateTimer = System.currentTimeMillis();
    }

    private void resync(KeyBinding keyBinding) {
        keyBinding.setPressed(this.isPhysicallyDown(keyBinding));
    }


    public static enum State {
        IDLE,
        WAIT_BEFORE_SWAP,
        WAIT_AFTER_SWAP;
    
    }
}

