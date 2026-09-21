package rtx.byazen.api.modules.impl.Utils;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.gui.screen.ChatScreen;
import rtx.byazen.api.drags.DragSystem;
import rtx.byazen.api.drags.Draggable;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.impl.Interface.NotificationsModule;
import rtx.byazen.api.modules.settings.impl.BindSetting;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;

/**
 * Идеи №33 и №190 из IDEAS.md: «чистый экран» и кнопка «паника» —
 * одной клавишей спрятать весь HUD (для скриншотов и проверок), второй раз — вернуть как было.
 */
public final class QuickHideModule
extends Module {

    public final BindSetting keybind = this.register(new BindSetting("Клавиша", "Клавиша скрытия и возврата HUD.").setKey(72));
    public final BooleanSetting announce = this.register(new BooleanSetting("Уведомление", "Показывать тост при скрытии и показе HUD.", true));

    private final Map<String, Boolean> previous = new LinkedHashMap<String, Boolean>();
    private boolean hidden;
    private boolean keyDown;

    public QuickHideModule() {
        super("Quick Hide", "Прячет весь HUD по клавише (по умолчанию H) и возвращает его обратно.", Category.UTILS);
    }

    public boolean hidden() {
        return this.hidden;
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre() || this.mc.player == null || this.mc.currentScreen instanceof ChatScreen) {
            return;
        }
        boolean down = this.keybind.isBound() && this.keybind.getValue().isDown(this.mc.getWindow().getHandle());
        if (down && !this.keyDown) {
            this.keyDown = true;
            this.toggleHud();
            return;
        }
        if (!down) {
            this.keyDown = false;
        }
    }

    /** Скрыть или вернуть HUD. */
    public void toggleHud() {
        if (this.hidden) {
            for (Draggable draggable : DragSystem.get().getAll()) {
                Boolean visible = this.previous.get(draggable.getId());
                if (visible != null) {
                    draggable.setVisible(visible);
                }
            }
            this.previous.clear();
            this.hidden = false;
        }
        else {
            this.previous.clear();
            for (Draggable draggable : DragSystem.get().getAll()) {
                this.previous.put(draggable.getId(), draggable.isVisible());
                draggable.setVisible(false);
            }
            this.hidden = true;
        }
        if (this.announce.getValue()) {
            NotificationsModule.notify(this.hidden ? "HUD скрыт — нажми " + this.keybind.getValue().getName() + " ещё раз" : "HUD возвращён", 1800L);
        }
    }

    public void show() {
        if (this.hidden) {
            this.toggleHud();
        }
    }

    @Override
    protected void onDisable() {
        this.show();
    }
}
