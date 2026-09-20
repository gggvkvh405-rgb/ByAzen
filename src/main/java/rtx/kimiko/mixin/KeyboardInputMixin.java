package rtx.kimiko.mixin;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.util.PlayerInput;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import rtx.kimiko.api.events.EventBus;
import rtx.kimiko.api.events.impl.input.InputEvent;
import rtx.kimiko.api.modules.impl.Visuals.emotions.EmotionWheelScreen;
import rtx.kimiko.api.ui.UI;
import rtx.kimiko.api.ui.crosshair.CrosshairEditorScreen;

@Mixin(net.minecraft.client.input.KeyboardInput.class)

public abstract class KeyboardInputMixin {
    @ModifyExpressionValue(method="tick", at={@At(value="NEW", target="(ZZZZZZZ)Lnet/minecraft/class_10185;")}, require = 0)
    private PlayerInput kimiko_tickHook(PlayerInput original) {
        EventBus bus;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc != null && mc.currentScreen != null) {
            original = UI.isOpen() && !UI.isSearchTyping() || mc.currentScreen instanceof CrosshairEditorScreen || mc.currentScreen instanceof EmotionWheelScreen ? this.kimiko_readRawMovement() : new PlayerInput(false, false, false, false, false, false, false);
        }
        if (!(bus = EventBus.get()).hasListeners(InputEvent.class)) {
            return original;
        }
        InputEvent event = bus.post(new InputEvent(original));
        return event.getInput();
    }

    @Unique
    private PlayerInput kimiko_readRawMovement() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.getWindow() == null) {
            return new PlayerInput(false, false, false, false, false, false, false);
        }
        return new PlayerInput(this.kimiko_isRawKeyDown(mc, mc.options.forwardKey), this.kimiko_isRawKeyDown(mc, mc.options.backKey), this.kimiko_isRawKeyDown(mc, mc.options.leftKey), this.kimiko_isRawKeyDown(mc, mc.options.rightKey), this.kimiko_isRawKeyDown(mc, mc.options.jumpKey), this.kimiko_isRawKeyDown(mc, mc.options.sneakKey), this.kimiko_isRawKeyDown(mc, mc.options.sprintKey));
    }

    @Unique
    private boolean kimiko_isRawKeyDown(MinecraftClient mc, KeyBinding mapping) {
        InputUtil.Key key = InputUtil.fromTranslationKey((String)mapping.getBoundKeyTranslationKey());
        return switch (key.getCategory()) {
            case InputUtil.Type.KEYSYM -> InputUtil.isKeyPressed((Window)mc.getWindow(), (int)key.getCode());
            case InputUtil.Type.MOUSE -> {
                if (GLFW.glfwGetMouseButton((long)mc.getWindow().getHandle(), (int)key.getCode()) == 1) {
                    yield true;
                }
                yield false;
            }
            default -> false;
        };
    }
}

