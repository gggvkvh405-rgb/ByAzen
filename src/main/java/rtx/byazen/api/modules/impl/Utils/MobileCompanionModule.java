package rtx.byazen.api.modules.impl.Utils;

import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.api.ui.WebCompanionScreen;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.web.LocalHttp;
import rtx.byazen.utils.web.WebBridge;
import rtx.byazen.utils.web.WebService;

/**
 * Мобильный компаньон (идея №175 из IDEAS.md).
 * <p>
 * Клиент поднимает маленький веб-сервер и показывает QR-код: телефон в той же сети открывает
 * страницу со статистикой, координатами, здоровьем и управлением плеером. Доступ закрыт ключом в
 * ссылке, наружу в интернет ничего не уходит — сервер живёт только пока включён модуль.
 */
public final class MobileCompanionModule
extends Module {

    public final SliderSetting port = this.register(new SliderSetting("Порт", "Порт локального веб-сервера клиента.").range(1024.0f, 65535.0f).increment(1.0f).setValue(8765.0f));
    private final BooleanSetting lan = this.register(new BooleanSetting("Доступ из домашней сети", "Разрешить открывать страницу с телефона в той же сети (иначе только этот компьютер)."));
    private final ButtonSetting start = this.register(new ButtonSetting("Запустить заново", "Поднять веб-сервер с текущими настройками.").label("Запустить").onClick(MobileCompanionModule::restart));
    private final ButtonSetting stop = this.register(new ButtonSetting("Остановить", "Выключить веб-сервер до следующего включения модуля.").label("Стоп").onClick(WebService::stop));
    private final ButtonSetting qr = this.register(new ButtonSetting("Показать QR-код", "Окно со ссылкой и QR-кодом для телефона.").label("QR-код").onClick(WebCompanionScreen::open));
    private final ButtonSetting link = this.register(new ButtonSetting("Ссылка в чат", "Напечатать ссылку, чтобы отправить себе в мессенджер.").label("Ссылка").onClick(MobileCompanionModule::printLink));
    private final ButtonSetting rotate = this.register(new ButtonSetting("Новый ключ доступа", "Старые ссылки с QR-кода перестанут работать.").label("Сменить ключ").onClick(MobileCompanionModule::rotateToken));

    public MobileCompanionModule() {
        super("Mobile Companion", "Страница для телефона: статистика, координаты, музыка и лента событий.", Category.UTILS);
    }

    @Override
    protected void onEnable() {
        WebService.refresh();
        WebBridge.ensure();
    }

    @Override
    protected void onDisable() {
        WebService.refresh();
    }

    /** Порт из настроек: нужен и серверу, и окну с QR-кодом. */
    public static int port() {
        MobileCompanionModule module = ModuleManager.get().get(MobileCompanionModule.class);
        return module == null ? 8765 : (int)module.port.getFloat();
    }

    public static boolean lanAllowed() {
        MobileCompanionModule module = ModuleManager.get().get(MobileCompanionModule.class);
        return module != null && module.lan.getValue();
    }

    private static void restart() {
        WebService.stop();
        WebService.refresh();
        ChatMessage.send("§b" + LocalHttp.get().status());
    }

    private static void printLink() {
        if (!LocalHttp.get().running()) {
            WebService.refresh();
        }
        ChatMessage.send("§bКомпаньон: §f" + LocalHttp.get().link());
        if (LocalHttp.get().lan()) {
            WebBridge.warnLanOnce();
        }
    }

    private static void rotateToken() {
        String token = LocalHttp.get().rotateToken();
        ChatMessage.send("§bНовый ключ доступа: §f" + token + " §7(старые ссылки больше не работают)");
    }
}
