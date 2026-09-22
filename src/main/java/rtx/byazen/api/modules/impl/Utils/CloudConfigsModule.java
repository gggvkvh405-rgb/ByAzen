package rtx.byazen.api.modules.impl.Utils;

import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.api.ui.CloudConfigsScreen;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.config.CloudConfigs;

/**
 * Облачные конфиги (идея №151 из IDEAS.md).
 * <p>
 * Клиент сам сохраняет версии настроек: перед сменой профиля, при выходе из мира и по таймеру.
 * Любую версию можно откатить — или выгрузить коротким кодом, чтобы перенести оформление и модули
 * на другой компьютер. История ограничена по количеству, поэтому папка не растёт бесконечно.
 */
public final class CloudConfigsModule
extends Module {

    private final BooleanSetting autoSave = this.register(new BooleanSetting("Автосохранение", "Сохранять версию при выходе из мира и раз в несколько минут.").setValue(true));
    private final SliderSetting interval = this.register(new SliderSetting("Интервал, мин", "Как часто оставлять версию при автосохранении.").range(1.0f, 30.0f).increment(1.0f).setValue(10.0f).visible(this.autoSave::getValue));
    private final BooleanSetting tellInChat = this.register(new BooleanSetting("Писать в чат", "Сообщать о сохранённой версии.").setValue(false));
    private final ButtonSetting saveNow = this.register(new ButtonSetting("Сохранить версию", "Оставить снимок текущих настроек прямо сейчас.").label("Сохранить").onClick(() -> CloudConfigsModule.snapshot("вручную")));
    private final ButtonSetting rollback = this.register(new ButtonSetting("Откатить последнюю", "Вернуться к предыдущей сохранённой версии.").label("Откатить").onClick(CloudConfigsModule::rollback));
    private final ButtonSetting open = this.register(new ButtonSetting("Список версий", "Показать все версии настроек с датами и размером.").label("Открыть").onClick(CloudConfigsModule::open));

    private long nextAuto;
    private long lastWorldExit;

    public CloudConfigsModule() {
        super("Cloud Configs", "Версии настроек с откатом: снимки, история, выгрузка коротким кодом.", Category.UTILS);
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (!event.isPre() || !this.isEnabled()) {
            return;
        }
        long now = System.currentTimeMillis();
        boolean inWorld = this.mc.player != null && this.mc.world != null;
        if (this.lastWorldExit != 0L && !inWorld && now - this.lastWorldExit < 1500L) {
            return;
        }
        if (!inWorld) {
            if (this.lastWorldExit == 0L) {
                this.lastWorldExit = now;
                if (this.autoSave.getValue()) {
                    CloudConfigs.snapshot("выход");
                }
            }
            return;
        }
        this.lastWorldExit = 0L;
        if (!this.autoSave.getValue()) {
            return;
        }
        if (this.nextAuto == 0L) {
            this.nextAuto = now + (long)(this.interval.getFloat() * 60000.0f);
            return;
        }
        if (now >= this.nextAuto) {
            this.nextAuto = now + (long)(this.interval.getFloat() * 60000.0f);
            CloudConfigs.snapshot("авто");
        }
    }

    public static void snapshot(String label) {
        CloudConfigs.Version version = CloudConfigs.snapshot(label);
        if (version != null) {
            ChatMessage.send("Версия настроек сохранена: " + version.timeText() + " · " + CloudConfigs.count() + " шт. в истории");
        }
    }

    public static void rollback() {
        CloudConfigs.Version version = CloudConfigs.last();
        if (version == null) {
            ChatMessage.error("Сохранённых версий пока нет");
            return;
        }
        int applied = CloudConfigs.restore(version);
        if (applied <= 0) {
            ChatMessage.error("Версию не удалось применить");
            return;
        }
        ChatMessage.send("Настройки откатились к " + version.timeText() + ": модулей " + applied);
    }

    public static void open() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.setScreen(new CloudConfigsScreen());
        }
    }
}
