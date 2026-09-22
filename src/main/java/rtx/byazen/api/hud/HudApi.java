package rtx.byazen.api.hud;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import rtx.byazen.api.drags.Position;
import rtx.byazen.api.events.EventBus;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.render.HudRenderEvent;
import rtx.byazen.utils.logs.ClientLog;
import rtx.byazen.utils.storage.RepositoryStorage;

/**
 * HUD API для сторонних модов (идея №178 из IDEAS.md).
 * <p>
 * Любой мод может нарисовать свой элемент в HUD ByAzen: достаточно зарегистрировать элемент с
 * уникальным id, названием и методом отрисовки. Позиция запоминается клиентом, поэтому элемент
 * можно двигать мышью в режиме перетаскивания HUD, как обычные элементы ByAzen.
 * <pre>
 *   HudApi.register(new HudApi.Element() {
 *       public String id() { return "example:ping"; }
 *       public String title() { return "Пинг"; }
 *       public float width() { return 70; }
 *       public float height() { return 12; }
 *       public void render(DrawContext context, float x, float y, float alpha) { ... }
 *   });
 * </pre>
 * Сторонние моды так же могут подписываться на {@link EventBus}, чтобы получать игровые события
 * (тик, отрисовку, нажатия клавиш) — см. {@link rtx.byazen.api.events.EventHandler}.
 */
public final class HudApi {

    private static final Map<String, HudApi.Entry> ELEMENTS = new LinkedHashMap<String, HudApi.Entry>();
    private static boolean subscribed;
    private static boolean loaded;
    private static boolean enabled = true;

    private HudApi() {
    }

    /** Что должен уметь элемент стороннего мода. */
    public interface Element {
        String id();

        String title();

        float width();

        float height();

        void render(DrawContext context, float x, float y, float alpha);
    }

    private static final class Entry {
        Element element;
        final boolean ours;
        float x;
        float y;
        boolean visible = true;

        Entry(Element element, boolean ours, float x, float y) {
            this.element = element;
            this.ours = ours;
            this.x = x;
            this.y = y;
        }
    }

    /** Регистрирует элемент в HUD. Повторная регистрация с тем же id заменяет элемент. */
    public static synchronized void register(Element element) {
        if (element == null || element.id() == null || element.id().isBlank()) {
            ClientLog.warn("HUD API: элемент без id пропущен");
            return;
        }
        HudApi.load();
        Entry entry = ELEMENTS.get(element.id());
        if (entry == null) {
            float[] spot = HudApi.freeSpot(element);
            ELEMENTS.put(element.id(), new Entry(element, false, spot[0], spot[1]));
            ClientLog.info("HUD API: зарегистрирован элемент «" + element.title() + "» (" + element.id() + ")");
        }
        else {
            entry.element = element;
        }
        HudApi.ensureSubscribed();
    }

    /** То же, но для наших собственных модулей: позиция берётся из настроек элемента. */
    public static synchronized void registerInternal(String id, String title, float width, float height,
                                                    float x, float y, HudApi.Render render) {
        HudApi.load();
        HudApi.Element element = new HudApi.Element() {
            @Override
            public String id() {
                return id;
            }

            @Override
            public String title() {
                return title;
            }

            @Override
            public float width() {
                return width;
            }

            @Override
            public float height() {
                return height;
            }

            @Override
            public void render(DrawContext context, float renderX, float renderY, float alpha) {
                render.render(context, renderX, renderY, alpha);
            }
        };
        ELEMENTS.put(id, new Entry(element, true, x, y));
        HudApi.ensureSubscribed();
    }

    /** Простой функциональный интерфейс отрисовки. */
    public interface Render {
        void render(DrawContext context, float x, float y, float alpha);
    }

    public static synchronized boolean unregister(String id) {
        return ELEMENTS.remove(id) != null;
    }

    /** Модуль «HUD API» может запретить сторонним модам рисовать. */
    public static synchronized void setEnabled(boolean value) {
        enabled = value;
    }

    public static synchronized boolean enabled() {
        return enabled;
    }

    public static synchronized void setVisible(String id, boolean visible) {
        Entry entry = ELEMENTS.get(id);
        if (entry != null) {
            entry.visible = visible;
        }
    }

    public static synchronized void move(String id, float x, float y) {
        Entry entry = ELEMENTS.get(id);
        if (entry == null) {
            return;
        }
        entry.x = Math.max(0.0f, Math.min(Position.screenWidth() - entry.element.width(), x));
        entry.y = Math.max(0.0f, Math.min(Position.screenHeight() - entry.element.height(), y));
        HudApi.save();
    }

    public static synchronized List<String> ids() {
        return new ArrayList<String>(ELEMENTS.keySet());
    }

    public static synchronized List<String> titles() {
        ArrayList<String> list = new ArrayList<String>();
        for (Entry entry : ELEMENTS.values()) {
            list.add(entry.element.title() + " (" + entry.element.id() + (entry.ours ? ", ByAzen" : ", сторонний мод") + ")");
        }
        return list;
    }

    public static synchronized int count() {
        return ELEMENTS.size();
    }

    public static synchronized int externalCount() {
        int count = 0;
        for (Entry entry : ELEMENTS.values()) {
            if (!entry.ours) {
                ++count;
            }
        }
        return count;
    }

    /** Возвращает позицию элемента: используется окном диагностики. */
    public static synchronized float[] positionOf(String id) {
        Entry entry = ELEMENTS.get(id);
        return entry == null ? null : new float[]{entry.x, entry.y};
    }

    public static synchronized void resetPositions() {
        int index = 0;
        for (Entry entry : ELEMENTS.values()) {
            entry.x = Position.screenWidth() - entry.element.width() - 8.0f;
            entry.y = 30.0f + (float)index * 22.0f;
            ++index;
        }
        HudApi.save();
    }

    public static synchronized void clearExternal() {
        ELEMENTS.entrySet().removeIf(entry -> !entry.getValue().ours);
    }

    private static float[] freeSpot(Element element) {
        float x = Position.screenWidth() - element.width() - 8.0f;
        float y = 30.0f;
        for (Entry entry : ELEMENTS.values()) {
            y = Math.max(y, entry.y + entry.element.height() + 4.0f);
        }
        return new float[]{Math.max(2.0f, x), Math.min(y, Position.screenHeight() - element.height() - 2.0f)};
    }

    private static void ensureSubscribed() {
        if (subscribed) {
            return;
        }
        subscribed = true;
        try {
            EventBus.get().subscribe(HudApi.Holder.INSTANCE);
        }
        catch (Throwable throwable) {
            ClientLog.warn("HUD API: не удалось подписаться на события HUD");
        }
    }

    /** Отдельный объект-слушатель: элементы не должны сами ловить события. */
    public static final class Holder {
        static final Holder INSTANCE = new Holder();

        private Holder() {
        }

        @EventHandler
        public void onHud(HudRenderEvent hudRenderEvent) {
            HudApi.render(hudRenderEvent.getGraphics());
        }
    }

    private static synchronized void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || context == null || ELEMENTS.isEmpty()) {
            return;
        }
        if (client.options != null && client.options.hudHidden) {
            return;
        }
        if (!HudApi.enabled) {
            return;
        }
        for (Entry entry : new ArrayList<Entry>(ELEMENTS.values())) {
            if (!entry.visible) {
                continue;
            }
            try {
                entry.element.render(context, entry.x, entry.y, 1.0f);
            }
            catch (Throwable throwable) {
                entry.visible = false;
                ClientLog.error("HUD API: элемент «" + entry.element.title() + "» отключён из-за ошибки: "
                        + throwable.getClass().getSimpleName());
            }
        }
    }

    private static void load() {
        if (loaded) {
            return;
        }
        loaded = true;
        try {
            JsonObject root = RepositoryStorage.readObject("hudapi");
            if (!root.has("positions")) {
                return;
            }
            JsonObject positions = root.getAsJsonObject("positions");
            for (String id : positions.keySet()) {
                JsonObject spot = positions.getAsJsonObject(id);
                Entry entry = ELEMENTS.get(id);
                if (entry == null) {
                    continue;
                }
                entry.x = spot.get("x").getAsFloat();
                entry.y = spot.get("y").getAsFloat();
            }
        }
        catch (Throwable ignored) {
        }
    }

    private static void save() {
        try {
            JsonObject root = new JsonObject();
            JsonObject positions = new JsonObject();
            for (Entry entry : ELEMENTS.values()) {
                JsonObject spot = new JsonObject();
                spot.addProperty("x", entry.x);
                spot.addProperty("y", entry.y);
                positions.add(entry.element.id(), spot);
            }
            root.add("positions", positions);
            RepositoryStorage.write("hudapi", root);
        }
        catch (Throwable ignored) {
        }
    }
}
