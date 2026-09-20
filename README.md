# ByAzen

**ByAzen** — клиент для Minecraft **1.21.11** (Fabric). Полностью переименованная (ребрендинг) версия проекта
Kimiko: все пакеты, классы, ресурсы, мод-ид и строки интерфейса теперь носят имя **ByAzen**.

| | |
|---|---|
| Mod ID | `byazen` |
| Имя в игре | ByAzen |
| Версия | 1.0.0 |
| Minecraft | 1.21.11 |
| Fabric Loader | 0.19.3+ |
| Fabric API | 0.141.6+1.21.11 |
| Java | 21 |
| Java-пакет | `rtx.byazen` |
| Главный класс | `rtx.byazen.ByAzen` |

## Что было переименовано

* `rtx.kimiko` → `rtx.byazen` (пакеты и все `import` в ~920 файлах)
* `Kimiko.java` → `ByAzen.java`, `KimikoMixinPlugin` → `ByAzenMixinPlugin`
* `kimiko.mixins.json` → `byazen.mixins.json`, `kimiko.accesswidener` → `byazen.accesswidener`
* `assets/kimiko/**` → `assets/byazen/**` (шейдеры, модели, текстуры, шрифты, gif, звуки)
* `LICENSE_kimiko` → `LICENSE_byazen`, файлы `META-INF/services/rtx.kimiko.*` → `rtx.byazen.*`
* Строки интерфейса, названия потоков, ключи Discord RPC, namespace `kimiko:` → `byazen:`
* `gradle.properties`, `settings.gradle`, `fabric.mod.json` — мод-ид, группа, имя архива (`ByAzen-1.0.0.jar`)

Не тронуты только внешние ссылки оригинального проекта (`https://t.me/kimikodlc`,
`https://kimiko_tech/...`) — это чужие рабочие адреса, переименование сломало бы кнопки Discord RPC.

## Сборка

### GitHub Actions (проще всего)

Workflow `.github/workflows/build.yml` собирает мод на раннере GitHub, где доступны Maven/Fabric-репозитории,
и публикует готовые jar обратно в корень репозитория:

* `ByAzen-1.0.0.jar` — готовый мод (кладите в `.minecraft/mods`)
* `ByAzen-1.0.0-sources.jar` — исходники
* `ByAzen-1.0.0-dev.jar` — dev-версия (для отладки, не для игры)

Запуск: push в ветку `arena/**` или `main`, либо вручную (Actions → Build ByAzen → Run workflow).

### Локально (Windows / Linux)

```bash
./gradlew build          # Linux/macOS
gradlew.bat build        # Windows (JDK 21 должен быть в PATH)
```

Готовый файл появится в `build/libs/ByAzen-1.0.0.jar`.

## Установка

1. Установите Fabric Loader 0.19.3+ для Minecraft 1.21.11.
2. Положите Fabric API (`fabric-api-0.141.6+1.21.11.jar`) в `.minecraft/mods`.
3. Положите `ByAzen-1.0.0.jar` в `.minecraft/mods`.
4. Запустите игру.

## Правила проекта

1. **Красивый рендер, никакой пикселизации.** Всё, что добавляется в ByAzen, рисуется сглаженно:
   шейдеры/SDF, сглаженные скругления, градиенты, MSAA/суперсэмплинг, интерполяция координат и альфы
   (`float`, а не округлённые пиксели), мип-мапы и `LinearFilter`. Никакого pixel-art, `NEAREST`-фильтра,
   шага по целым пикселям и нарочито «пиксельных» текстур/иконок.
2. Все новые файлы, пакеты и ресурсы — в пространстве имён `byazen` (`rtx.byazen.*`, `assets/byazen/**`).
3. Никаких упоминаний старого имени в коде, ресурсах и UI.
