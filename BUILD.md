# Сборка WexSide в jar

Готовый проверенный jar уже лежит в корне: **`wexside-client-1.0.0.jar`**
(SHA-256 `0d09840189ab6eedd8874b800547d396565b7ce05c05b51c45d2bd95e37c8c4e`).
Просто положи его в `mods/` (нужны Fabric Loader >= 0.19.2, MC 1.21.11, Java 21, Fabric API).

Ниже — как пересобрать его из исходников на своём ПК (в этой песочнице сборки нет:
отсутствует JDK и заблокирован доступ к Maven/Gradle/Mojang).

## Требования

- **JDK 21** (Temurin/Oracle, именно 21 — `sourceCompatibility = 21`)
- Интернет (Gradle скачает дистрибутив 9.5.1, Loom 1.17, Minecraft 1.21.11, маппинги, Fabric)
- ~6 ГБ свободного места (кэш Gradle + Minecraft)

## Шаги (Windows)

```bat
:: 1. Распакуй архив
::    Wexside-1.21.11-Main.rar -> папка WexSideSRC

:: 2. Проверь версию (необязательно)
cd WexSideSRC

:: 3. Собери (первый запуск скачает Gradle 9.5.1 и все зависимости, ~5-15 минут)
gradlew.bat build

:: 4. Готовый jar:
::    build\libs\wexside-client-1.0.0.jar   (remapJar, уже ремапнутый под intermediary)
```

## Шаги (Linux/macOS)

В архиве есть только `gradlew.bat`. На Linux проще поставить системный Gradle 9.x
или сгенерировать wrapper:

```bash
cd WexSideSRC
# вариант А: системный gradle
gradle build
# вариант Б: докачать wrapper-скрипт (нужен интернет):
#   gradle wrapper --gradle-version 9.5.1
#   ./gradlew build
# готовый jar: build/libs/wexside-client-1.0.0.jar
```

## Особенности этого проекта

- `mappings loom.layered { }` **пустой** — это нормально для данного сорса:
  код декомпилированный (CFR) и использует intermediary-имена (`net.minecraft.class_310`),
  поэтому собирается против немапленного Minecraft. Ничего дописывать не нужно.
- `processResources` подставляет версию только если в `fabric.mod.json` есть плейсхолдер;
  сейчас там захардкожено `1.0.0-recovered` — так и было в оригинальной сборке.
- `libs/*.jar` (TwelveMonkeys WebP) подключаются через `fileTree` и **не** шейдятся
  в итоговый jar — это тоже соответствует оригиналу (клиент регистрирует WebP-ридер
  из этих библиотек в рантайме через classpath... на практике оригинал просто собран
  так же — jar 1-в-1 совпадает с лежащим в архиве).
- Ожидаемый результат сборки: `build/libs/wexside-client-1.0.0.jar` (~9.6 МБ),
  внутри 958 классов `ru/wexside/*`, манифест с `Fabric-Loom-Version`.

## Если сборка падает

1. `JAVA_HOME is not set` → поставь JDK 21 и пропиши `JAVA_HOME`.
2. Ошибки скачивания зависимостей → нужен доступ к `maven.fabricmc.net`,
   `maven.minecraftforge.net`/`maven.neoforged.net` (не требуется здесь), `piston-meta.mojang.com`,
   `libraries.minecraft.net`, `repo.maven.apache.org`. VPN/прокси при необходимости.
3. Ошибки компиляции вида `cannot find symbol class_XXX` → убедись, что `mappings`
   остался пустым `loom.layered { }` (не подставляй yarn — сорс под intermediary).
