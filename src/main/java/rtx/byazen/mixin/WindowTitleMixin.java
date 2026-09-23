package rtx.byazen.mixin;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.util.MonitorTracker;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.byazen.api.ui.window.MainWindow;

@Mixin(Window.class)

public abstract class WindowTitleMixin {
    @Shadow
    @Final
    private long handle;

    @ModifyVariable(method="<init>", at=@At(value="HEAD"), argsOnly=true, ordinal=0, require = 0)
    private static String byazen_forceInitialTitle(String original) {
        return MainWindow.getTitle();
    }

    @ModifyVariable(method="setTitle", at=@At(value="HEAD"), argsOnly=true, require = 0)
    private String byazen_forceTitle(String original) {
        return MainWindow.getTitle();
    }

    @Inject(method="<init>", at={@At(value="TAIL")}, require = 0)
    private void byazen_applyDarkTitleBar(WindowEventHandler eventHandler, MonitorTracker screenManager, WindowSettings displayData, String preferredFullscreenVideoMode, String title, CallbackInfo ci) {
        // Окно только что создано — это самый «хрупкий» момент запуска с нативными вызовами,
        // поэтому в безопасном режиме тёмную полосу заголовка не трогаем.
        if (rtx.byazen.utils.startup.StartTrace.safeMode()) {
            return;
        }
        MainWindow.applyDarkMode(this.handle);
    }
}

