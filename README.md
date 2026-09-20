# ByAzen

Универсальный клиент для Minecraft 1.21.11 (Fabric) — объединяет **Kimiko**, **KillEffect**, **Pulse Cosmetics**, **WexSide** и **Z3nith** в один модуль.

Бренд переименован: все видимые надписи теперь **ByAzen**, новый фирменный шрифт `byazen` (Montserrat Black, MSDF-атлас с кернингом).

## Состав

| Часть | Как включена |
|---|---|
| Kimiko (ядро) | исходный код, `rtx.kimiko.*` |
| KillEffect | готовый jar внутри: `META-INF/jars/KillEffect-1.21.11.jar` |
| Pulse Cosmetics | готовый jar внутри: `META-INF/jars/Pulse-Cosmetics-1.21.11-1.0.0.jar` |
| WexSide | исходный код: `ru.wexside.*` (862 файла) |
| Z3nith | исходный код: `org.zenith.*`, `com.darkmagician6.*`, `project.weye.*` (976 файлов) |

Точка входа клиента: `rtx.kimiko.Kimiko` + `ru.wexside.WexSideClient` + `org.zenith.init.ZenithInitializer` (все три в `entrypoints.client` в `fabric.mod.json`). Mixin-конфиги: `kimiko.mixins.json`, `wexside.mixins.json`, `zenithdlc.mixins.json` (+ конфигурации из внутренних jar).

## Фирменный шрифт ByAzen

- `src/main/resources/assets/kimiko/fonts/byazen/byazen.{json,png}` — MSDF-атлас (96 px, spread 4, кернинг из GPOS Montserrat, 1548 пар).
- Зарегистрирован в `MsdfFonts` как `"byazen"` и в enum `Fonts` как `BYAZEN`.
- Используется в: водяном знаке (`WatermarkComp`), заголовке GUI-панели (`UI`, `RemoteGuiPanelRenderer`), HP-тексте таргета (`TargetHudComp`).

Важно про формат атласа (согласовано с рендерером `MsdfFontLoader` / `msdf_text.fsh`):
- `atlasBounds` — пиксели, **y отсчитывается от НИЗА изображения** (`bottom < top`);
- `planeBounds` — em, **y вверх от базлиня**;
- глифы в PNG стоят «правильно вверх», внутри → 1, снаружи → 0 (`value = 0.5 − d / (2·spread)`).

## Сборка

Требуется **JDK 21** (на этой машине уже настроен `run_gradle.bat`).

```bat
rem Windows:
run_gradle.bat build

rem или прямо:
gradlew build
```

Готовый модуль появится в `build/libs/` и кладётся в папку `mods/`.

Внешние зависимости (выкачиваются Gradle'ом):
- `net.java.dev.jna:jna:5.15.0` — Discord RPC (был как inner jar, теперь maven);
- `com.electronwill.night-config` core/toml 3.8.3 — конфиги;
- Twelvemonkeys ImageIO 3.12.0 (WebP для WexSide);
- для Z3nith: `PlayerAnimationLibFabric 1.1.6+mc.1.21.11`, `dev.babbaj:nether-pathfinder:1.6`, netty proxy/socks 4.2.7.Final;
- `libs/shaded-runtime.jar` распаковывается в jar (без `org/apache/commons/lang3`, `io/netty` и подписей META-INF — MC их даёт сам);
- `libs/baritone-meteor`, `libs/zenith-addon-api`, `libs/zenith-addon-runtime` пакуются в `META-INF/jars`.

## Замечания

- Код WexSide и Z3nith получен декомпиляцией (CFR). Если при сборке вылезут ошибки в этих пакетах — это артефакты декомпиляции, чинить точечно (имена/типы), логика сохранена.
- Mixin'ы собираются штатным AP loom 1.17: для каждого конфига генерируется свой refmap (`kimiko-refmap.json`, `wexside-refmap.json`, `zenithdlc-refmap.json`) — имена совпадают с конвенцией/явным полем `"refmap"` в конфигах. Блок `loom.mixin { useLegacyMixinAp }` НЕ добавлять: он пишет один общий refmap и сломает остальные два конфига.
- `ClientPresence` по-прежнему детектирует пользователей по сигнатурам (логика не менялась).
- AccessWidener: `kimiko.accesswidener` (87 записей Kimiko + 117 записей Z3nith, дубликаты исключены).
- Ресурсы четырёх модов не конфликтуют (проверено по путям); `fabric.mod.json` остался один — общий.
