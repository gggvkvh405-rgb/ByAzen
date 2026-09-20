package rtx.kimiko.api.modules.impl.Utils;
import rtx.kimiko.api.events.EventHandler;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;
import rtx.kimiko.api.events.impl.game.TickEvent;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;

public final class AutoSprint
extends Module {
    private boolean heldByUs;

    public AutoSprint() {
        super("AutoSprint", "\u0423\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0435\u0442 \u0441\u043f\u0440\u0438\u043d\u0442 \u043f\u0440\u0438 \u0434\u0432\u0438\u0436\u0435\u043d\u0438\u0438 \u0432\u043f\u0435\u0440\u0451\u0434", Category.UTILS);
    }

    private boolean isPressed(KeyBinding keyBinding) {
        InputUtil.Key key = InputUtil.fromTranslationKey((String)keyBinding.getBoundKeyTranslationKey());
        long l = this.mc.getWindow().getHandle();
        return switch (key.getCategory()) {
            case KEYSYM, SCANCODE -> InputUtil.isKeyPressed((Window)(Object)this.mc.getWindow(), (int)key.getCode());
            case MOUSE -> GLFW.glfwGetMouseButton(l, key.getCode()) == 1;
            default -> false;
        };
    }

    @Override
    protected void onDisable() {
        this.releaseSprintKey();
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre()) {
            return;
        }
        if (this.mc.player == null || this.mc.options == null || this.mc.getWindow() == null) {
            this.releaseSprintKey();
            return;
        }
        boolean bl = this.isPressed(this.mc.options.forwardKey);
        if (bl) {
            this.mc.options.sprintKey.setPressed(true);
            this.heldByUs = true;
        } else {
            this.releaseSprintKey();
        }
    }

    private void releaseSprintKey() {
        if (this.heldByUs) {
            this.heldByUs = false;
            if (this.mc.options != null) {
                this.mc.options.sprintKey.setPressed(this.isPressed(this.mc.options.sprintKey));
            }
        }
    }
}

