# Сборка ByAzen в jar

Исходники ребренда лежат в **`ByAzenSRC/`** (бывший WexSide: mod id `byazen`,
пакет `ru.byazen`, конфиги `*.byazen`, логотип и вотермарка «byazen»).

> `wByAzen.jar` в корне — это **старая** сборка под брендом WexSide
> (переименован из `wexside-client-1.0.0.jar`, оставлен как проверенный артефакт
> аудита, см. `SECURITY_REPORT.md`). Внутри — бренд WexSide, а не ByAzen!
> Для бренда ByAzen собери jar из `ByAzenSRC/` по инструкции ниже.

Пересобрать jar в этой песочнице **технически невозможно** (нет JDK и заблокирован
доступ к Maven/Gradle/Mojang) — собирай на своём ПК.

## Требования

- **JDK 21** (Temurin/Oracle, именно 21 — `sourceCompatibility = 21`)
- Интернет (Gradle скачает дистрибутив 9.5.1, Loom 1.17, Minecraft 1.21.11, маппинги, Fabric)
- ~6 ГБ свободного места (кэш Gradle + Minecraft)

## Шаги (Windows)

```bat
:: 1. Открой папку с исходниками
cd ByAzenSRC

:: 2. Собери (первый запуск скачает Gradle 9.5.1 и все зависимости, ~5-15 минут)
gradlew.bat build

:: 3. Готовый jar:
::    build\libs\byazen-client-1.0.0.jar   (remapJar, уже ремапнутый под intermediary)
```

Положи `byazen-client-1.0.0.jar` в `mods/` (нужны Fabric Loader >= 0.19.2,
MC 1.21.11, Java 21, Fabric API).

## Шаги (Linux/macOS)

В проекте есть только `gradlew.bat`. На Linux проще поставить системный Gradle 9.x
или сгенерировать wrapper:

```bash
cd ByAzenSRC
# вариант А: системный gradle
gradle build
# вариант Б: докачать wrapper-скрипт (нужен интернет):
#   gradle wrapper --gradle-version 9.5.1
#   ./gradlew build
# готовый jar: build/libs/byazen-client-1.0.0.jar
```

## Особенности этого проекта

- `mappings loom.layered { }` **пустой** — это нормально для данного сорса:
  код декомпилированный (CFR) и использует intermediary-имена (`net.minecraft.class_310`),
  поэтому собирается против немапленного Minecraft. Ничего дописывать не нужно.
- `processResources` подставляет версию только если в `fabric.mod.json` есть плейсхолдер;
  сейчас там захардкожено `1.0.0-recovered` — так было в оригинальной сборке.
- `libs/*.jar` (TwelveMonkeys WebP) подключаются через `fileTree` и **не** шейдятся
  в итоговый jar — соответствует оригиналу.
- Ожидаемый результат сборки: `build/libs/byazen-client-1.0.0.jar` (~9.6 МБ),
  внутри 958 классов `ru/byazen/*`, манифест с `Fabric-Loom-Version`.
- Папка конфигов в игре: `config/byazen/`, файлы `*.byazen`.

## Что переименовано при ребренде

`WexSide`/`Wexside`/`wexside` → `ByAzen`/`byazen` везде: пакет `ru.byazen`,
классы `ByAzenClient`/`ByAzenScreen`/`ByAzenHitParticles`/`ByAzenGlobals`,
mod id, entrypoint, миксины (`byazen.mixins.json`, методы `byazen$*`),
ассеты `assets/byazen/`, `*.byazen`-конфиги, вотермарка, префикс чата,
`VERSION_LABEL`, профиль по умолчанию. Ноль остатков старого бренда в текстовых
файлах (проверено grep'ом), все импорты/миксины/ассеты резолвятся.

Шрифт бренда: вотермарка переведена с Inter SemiBold на фирменный
MSDF-шрифт клиента `brandText` (Maison Neue Extended Bold, сглаженный вектор —
никаких пикселей); логотип меню `logotypes.png` и иконка мода `icon.png`
перерисованы с «byazen»/«B» (Montserrat ExtraBold, сглаживание 4x).
В `fabric.mod.json` добавлено поле `icon`.
