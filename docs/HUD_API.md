# HUD API ByAzen — свои элементы в нашем HUD

ByAzen даёт сторонним модам два простых входа: **рисовать свой элемент в HUD** и **получать игровые события**.
Никаких зависимостей от внутренних классов клиента не нужно — только публичный API.

## 1. Свой элемент в HUD

```java
import net.minecraft.client.gui.DrawContext;
import rtx.byazen.api.hud.HudApi;
import rtx.byazen.utils.render.render2d.Render2D;

public final class PingElement implements HudApi.Element {

    public PingElement() {
        HudApi.register(this);   // один раз при инициализации мода
    }

    @Override
    public String id() {          // уникальный id: лучше с префиксом своего мода
        return "example:ping";
    }

    @Override
    public String title() {       // подпись для окна «HUD API» в клиенте
        return "Пинг";
    }

    @Override
    public float width() {        // размер в пикселях интерфейса
        return 76.0f;
    }

    @Override
    public float height() {
        return 13.0f;
    }

    @Override
    public void render(DrawContext context, float x, float y, float alpha) {
        int color = rtx.byazen.api.ui.theme.ClientAccent.rgba(0xEBF0FA, 255.0f * alpha);
        Render2D.msdfText("montserrat-bold", "Пинг " + ping() + " мс", x + 3.0f, y + 3.0f, 7.0f, color);
    }

    private int ping() {
        return 42; // здесь ваш настоящий пинг
    }
}
```

Что клиент делает сам:

* **Позиция.** По умолчанию элемент встаёт у правого края первым свободным местом, а дальше позиция
  запоминается в конфиге (`hudapi`). Переместить программно: `HudApi.move("example:ping", x, y)`.
* **Изоляция ошибок.** Если в `render` вылетит исключение, элемент отключается (`HudApi.setVisible(id, false)`),
  остальные продолжают рисоваться, а причина попадает в лог клиента — игра не падает.
* **Скрытие.** `HudApi.setVisible("example:ping", false)` и `HudApi.unregister("example:ping")`.
* **Список.** Модуль «HUD API» в клиенте показывает все элементы, кто их отдал, и умеет сбросить позиции.

## 2. События клиента

```java
import rtx.byazen.api.events.EventBus;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;

public final class MyListener {

    public void init() {
        EventBus.get().subscribe(this);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (!event.isPre()) {
            return;
        }
        // ваш код: раз в тик
    }
}
```

Полезные события: `TickEvent` (начало/конец тика), `HudRenderEvent` (отрисовка HUD, даёт `DrawContext`),
`WorldRenderEvent` (отрисовка мира), `PacketEvent` (пакеты), `AttackEntityEvent`, `DeathScreenEvent`.

Правила вежливости: не держите в обработчике долгие операции, не отменяйте чужие события без причины,
а если ваш мод выключают — вызывайте `EventBus.get().unsubscribe(this)`.

## 3. Ограничения

* API стабилен в пределах ветки 1.8.x: добавляем новое, старое не ломаем.
* Клиент не проверяет «читерность» чужих элементов — за это отвечает сам мод и правила сервера.
* Если элемент рисуется поверх важного (например, счётчика HP) — это решает игрок: он может сдвинуть
  или скрыть элемент через клиент либо отключить модуль «HUD API» целиком.
