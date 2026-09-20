# ByAzen

**ByAzen** — клиент для Minecraft **1.21.11** (Fabric). Полностью переименованная (ребренд) версия проекта
Kimiko: все пакеты, классы, ресурсы, мод-ид и строки интерфейса теперь носят имя **ByAzen**.

| | |
|---|---|
| Готовый мод | **`ByAzen-1.0.0.jar`** (в корне репозитория, 54.8 МБ) |
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

* `rtx.kimiko` → `rtx.byazen` — пакеты, классы и все `import` в ~920 файлах
* `Kimiko.java` → `ByAzen.java`, `KimikoMixinPlugin` → `ByAzenMixinPlugin`
* `kimiko.mixins.json` → `byazen.mixins.json`, `kimiko.accesswidener` → `byazen.accesswidener`
* `assets/kimiko/**` → `assets/byazen/**` — шейдеры, модели, текстуры, шрифты, gif, звуки
* `LICENSE_kimiko` → `LICENSE_byazen`, `META-INF/services/rtx.kimiko.*` → `rtx.byazen.*`
* namespace `kimiko:` → `byazen:` (шейдеры `#moj_import <byazen:common.glsl>`, текстуры, gif, буферы, потоки)
* `fabric.mod.json`, `gradle.properties`, `settings.gradle` — id, группа, имя архива (`ByAzen-1.0.0.jar`)

Не тронуты только две внешние ссылки оригинального проекта (это чужие рабочие адреса, переименование
сделало бы кнопки нерабочими):

* `src/main/java/rtx/byazen/utils/discord/rpc/DiscordRPCManager.java` — `https://t.me/kimikodlc`
* `src/main/java/rtx/byazen/utils/profile/ProfileIdentity.java` — `https://kimiko_tech/api/account/avatar/`

Исходный архив `Kimiko_Fix_Src.zip` удалён из рабочей ветки — его содержимое распаковано и переименовано прямо
в репозитории. Сам архив по-прежнему доступен в истории git (`git show 4c5fd8d:Kimiko_Fix_Src.zip`).

## Сборка

### GitHub Actions (использовалась для этого jar)

Workflow [`.github/workflows/build.yml`](.github/workflows/build.yml) собирает мод на раннере GitHub (там
доступны `maven.fabricmc.net`, `libraries.minecraft.net`, Maven Central) и публикует готовый jar обратно
в корень ветки. Запуск: push в `arena/**` / `main` или вручную (Actions → *Build ByAzen* → Run workflow).
Jar пишется детерминированно, поэтому повторная сборка без изменений в коде не создаёт лишних коммитов.

### Локально (Windows / Linux)

```bash
gradlew.bat build        # Windows (JDK 21 в PATH)
./gradlew build          # Linux / macOS
```

Готовый файл: `build/libs/ByAzen-1.0.0.jar`.

> В песочнице, где делался ребренд, наружу открыт только GitHub/PyPI, поэтому Maven-репозитории недоступны —
> сборка выполнялась на GitHub Actions, а не локально.

## Установка

1. Установите Fabric Loader 0.19.3+ для Minecraft 1.21.11.
2. Положите Fabric API (`fabric-api-0.141.6+1.21.11.jar`) в `.minecraft/mods`.
3. Положите `ByAzen-1.0.0.jar` в `.minecraft/mods`.
4. Запустите игру.

## Правила проекта

1. **Красивый рендер, никакой пикселизации.** Всё, что добавляется в ByAzen, рисуется сглаженно:
   шейдеры/SDF, плавные скругления, градиенты, MSAA/суперсэмплинг, интерполяция координат и альфы
   (`float`, а не округлённые пиксели), мип-мапы и линейная фильтрация. Никакого pixel-art, `NEAREST`-фильтра,
   шага по целым пикселям и нарочито «пиксельных» текстур/иконок.
2. Все новые файлы, пакеты и ресурсы — в пространстве имён `byazen` (`rtx.byazen.*`, `assets/byazen/**`).
3. Никаких упоминаний старого имени в коде, ресурсах и UI.
