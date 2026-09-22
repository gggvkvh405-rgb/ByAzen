package rtx.byazen.utils.web;

import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Utils.MobileCompanionModule;
import rtx.byazen.api.modules.impl.Utils.WebDashboardModule;

/**
 * Согласование модулей веб-страниц (идеи №175 и №176 из IDEAS.md).
 * <p>
 * Сервер один на клиент: «Мобильный компаньон» и «Веб-дашборд» — две страницы одного сервера.
 * Здесь решается, поднимать ли его, какие страницы отдавать и когда выключать: если выключены оба
 * модуля, сервер останавливается и порт освобождается.
 */
public final class WebService {

    private WebService() {
    }

    public static void refresh() {
        boolean companion = WebService.enabled(MobileCompanionModule.class);
        boolean dashboard = WebService.enabled(WebDashboardModule.class);
        LocalHttp http = LocalHttp.get();
        if (!companion && !dashboard) {
            if (http.running()) {
                http.stop();
                WebBridge.pushEvent("web", "Веб-сервер остановлен");
            }
            return;
        }
        int port = MobileCompanionModule.port();
        boolean lan = MobileCompanionModule.lanAllowed();
        boolean wasRunning = http.running();
        String result = http.start(port, lan, dashboard);
        WebBridge.ensure();
        if (!wasRunning) {
            WebBridge.pushEvent("web", dashboard && !companion ? "Дашборд запущен" : "Компаньон запущен");
        }
        if (lan) {
            WebBridge.warnLanOnce();
        }
        rtx.byazen.utils.logs.ClientLog.info("веб-страницы: " + result);
    }

    public static void stop() {
        LocalHttp.get().stop();
        WebBridge.pushEvent("web", "Веб-сервер выключен вручную");
    }

    private static boolean enabled(Class<? extends Module> type) {
        Module module = ModuleManager.get().get(type);
        return module != null && module.isEnabled();
    }

    public static String status() {
        LocalHttp http = LocalHttp.get();
        return http.running() ? http.status() : "веб-страницы выключены";
    }
}
