# ByAzen

ByAzen - Custom client for Minecraft 1.21.11 (Fabric)

## Собранные JAR

В этом репозитории собраны:

- **ByAzen-1.0.0.jar** (9.2M) - Полноценный Fabric мод для Minecraft 1.21.11, основанный на WexSide. Переименован из `wexside-client-1.0.0.jar` (из `Wexside-1.21.11-Main.rar`). Готов к установке в `mods/` папку.
  - `id: byazen`, `name: ByAzen`, `version: 1.0.0`
  - Зависимости: `fabricloader >=0.19.2`, `minecraft ~1.21.11`, `java >=21`, `fabric-api *`
  - Включает все фичи WexSide: HUD, модули, команды, и т.д.
  - Просто переименуйте `WexSide` в `ByAzen` в `fabric.mod.json`

- **ByAzen-minimal-1.0.0.jar** (3.3K) - Минимальный пример мода ByAzen, собранный с помощью ECJ (Eclipse Compiler) и JRE 21 (jdk4py) без Minecraft зависимостей. Демонстрирует возможность сборки без Gradle.
  - Содержит `byazen.ByAzen` (ModInitializer + ClientModInitializer)
  - Скомпилирован с `ECJ 3.44.0` (поддержка Java 21) и `JRE 21.0.8`

- **Исходники**:
  - `Kimiko_Fix_Src.zip` (50M) - Kimiko 1.0.0 для 1.21.11 (Fabric Loom 1.17.19, Yarn 1.21.11+build.6)
  - `Wexside-1.21.11-Main.rar` (18M) - WexSide 1.21.11 (Fabric Loom 1.17, Yarn, включая `wexside-client-1.0.0.jar`)
  - `zenith-source.rar` (51M) - Zenith (Z3nith By K3ntSofts) для 1.21.11 (Fabric Loom 1.14.10)
  - `KillEffect 1.21.11.jar` (759K) - KillEffect мод
  - `Pulse-Cosmetics-1.21.11-1.0.0.jar` (23M) - Pulse Cosmetics

## Установка

1. Установите Fabric Loader 0.19.3+ для Minecraft 1.21.11
2. Установите Fabric API 0.141.6+1.21.11
3. Скопируйте `ByAzen-1.0.0.jar` в папку `mods/`
4. Запустите игру

## Сборка из исходников

### Требования

- Java 21 (JRE 21.0.8 от jdk4py или любой JDK 21)
- ECJ 3.44.0 (Eclipse Compiler, уже скачан в `/tmp/ecj_new.jar`)
- Gradle 9.5.1 (требует `https://services.gradle.org` - заблокирован в этом окружении, поэтому используется ECJ напрямую)

### Быстрая сборка (ECJ, без Gradle)

```bash
export JAVA_HOME=/usr/local/lib/python3.11/dist-packages/jdk4py/java-runtime
export PATH=$JAVA_HOME/bin:$PATH
ECJ_JAR=/tmp/ecj_new.jar

# Распаковать исходники
unzip -q Kimiko_Fix_Src.zip -d /tmp/kimiko
unrar x Wexside-1.21.11-Main.rar /tmp/wexside/
unrar x zenith-source.rar /tmp/zenith/

# Скомпилировать с заглушками (stubs) для Minecraft
# Генерация заглушек для net.minecraft.class_* уже выполнена в /tmp/wexside_stubs
java -jar $ECJ_JAR -source 21 -target 21 -d /tmp/classes -cp /tmp/wexside_stubs:libs/* src/main/java
```

### Полная сборка (Gradle, требует сети)

```bash
# Для Kimiko
cd /tmp/kimiko
./gradlew build

# Для WexSide
cd /tmp/wexside/WexSideSRC
./gradlew build  # уже есть wexside-client-1.0.0.jar

# Для Zenith
cd /tmp/zenith/zenith-source
./gradlew build
```

**Ограничения в этом окружении:**
- `https://maven.fabricmc.net`, `https://repo.maven.apache.org`, `https://piston-data.mojang.com` заблокированы (только `pypi.org`, `files.pythonhosted.org`, `github.com`, `api.github.com`, `codeload.github.com` разрешены)
- Поэтому Gradle не может скачать зависимости (minecraft, yarn, fabric-loader, fabric-api)
- Решение: использовать ECJ + заглушки, или скачать зависимости вручную через `codeload.github.com` (например, Fabric Loader из `FabricMC/fabric-loader` via `codeload`)
- Альтернатива: использовать уже собранный `wexside-client-1.0.0.jar` и ребрендить его в `ByAzen` (что и сделано)

## Структура ByAzen (ребренд WexSide)

- `fabric.mod.json`: `id` изменён с `wexside` на `byazen`, `name` с `WexSide` на `ByAzen`
- Все классы остаются в `ru.wexside.*` (можно переименовать в `byazen.*` при желании)
- Миксины: `wexside.mixins.json` (совместим, не требует переименования)
- Ресурсы: `assets/wexside/*` (можно переименовать в `assets/byazen/*`)

## Проверка JAR

```bash
unzip -l ByAzen-1.0.0.jar | head -n 20
cat fabric.mod.json
java -jar ByAzen-1.0.0.jar  # не запускается напрямую, это Fabric мод
```

## Следующие шаги

- Переименовать пакет `ru.wexside` в `byazen` во всех Java файлах (опционально)
- Добавить свои модули в `byazen` пакет
- Собрать через `./build.sh`
- Протестировать в игре 1.21.11

## Лицензия

См. `WexSideSRC/LICENSE`

## Автор

Собрано для https://github.com/gggvkvh405-rgb/ByAzen
Дата: 2026-09-20

